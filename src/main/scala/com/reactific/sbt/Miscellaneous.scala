/*
 * Copyright 2015-2017 Reactific Software LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.reactific.sbt

import java.io.File
import java.net.HttpURLConnection
import java.time.Instant
import scala.concurrent.duration._
import scala.io.Source

import com.typesafe.sbt.packager.Keys.scriptClasspath
import com.typesafe.sbt.SbtNativePackager.Universal
import sbt.IO
import sbt.Keys._
import sbt._
import sbt.internal.util.ManagedLogger
import sun.net.www.content.text.PlainTextInputStream

/** General settings for the project */
object Miscellaneous extends AutoPluginHelper {

  def filter(ms: Seq[(File, String)]): Seq[(File, String)] = {
    ms.filter {
      case (_, path) =>
        path != "logback.xml" &&
          !path.startsWith("toignore") &&
          !path.startsWith("samples")
    }
  }

  def currBranch: String = {
    import com.typesafe.sbt.git.JGit
    val jgit = JGit(new File("."))
    jgit.branch
  }

  def buildShellPrompt: Def.Initialize[State => String] = {
    Def.setting { (state: State) =>
      val id = Project.extract(state).currentProject.id
      s"${name.value}($id) : $currBranch : ${version.value}>"
    }
  }

  def updateFromPublicRepository(
    local: File,
    remote: String,
    maxAge: FiniteDuration,
    log: ManagedLogger
  ): Unit = {
    val url = new URL("https", "raw.githubusercontent.com", remote)
    val conn: HttpURLConnection =
      url.openConnection().asInstanceOf[HttpURLConnection]
    val timeout = 30000
    conn.setConnectTimeout(timeout)
    conn.setReadTimeout(timeout)
    conn.setInstanceFollowRedirects(true)
    conn.connect()
    val status = conn.getResponseCode
    val message = conn.getResponseMessage
    val lastModified =
      conn.getHeaderFieldDate("Last-Modified", Instant.now().toEpochMilli)

    if (status != 200) {
      log.warn(
        s"Failed to get ${url.toExternalForm}: HTTP $status: $message"
      )
    }
    val cutOff = Instant.now().toEpochMilli - maxAge.toMillis
    if (!local.exists() || lastModified > cutOff) {
      Option(conn.getContent()) match {
        case Some(is: PlainTextInputStream) =>
          val content = Source.fromInputStream(is).mkString
          val pw = new java.io.PrintWriter(local)
          try {
            pw.write(content)
          } finally {
            pw.close()
          }
          log.info(
            s"Updated .scalafmt.conf from ${url.toExternalForm
            }: HTTP $status $message ${Instant.ofEpochMilli(lastModified)}"
          )
        case Some(x: Any) =>
          throw new IllegalStateException(
            s"Wrong content type for ${url.toExternalForm}: ${x.getClass}."
          )
        case None =>
          throw new IllegalStateException(
            s"No content from ${url.toExternalForm}"
          )
      }
    }
  }

  def standardResolvers: Seq[Resolver] = Seq[Resolver](
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots"),
    Resolver.bintrayRepo("typesafe", "ivy-releases"),
    Resolver.bintrayRepo("sbt", "sbt-plugin-releases"),
    Resolver.bintrayRepo("scalaz", "releases")
  )

  val classpath_jar = "classpath.jar"

  def makeRelativeClasspathNames(mappings: Seq[(File, String)]): Seq[String] = {
    for {
      (_, name) <- mappings
    } yield {
      // Here we want the name relative to the lib/ folder...
      // For now we just cheat...
      if (name.startsWith("lib/")) name.drop(4) else "../" + name
    }
  }

  def makeClasspathJar(classPath: Seq[String], target: File): Seq[String] = {
    val manifest = new java.util.jar.Manifest()
    manifest.getMainAttributes
      .putValue("Class-Path", classPath.mkString(" "))
    val classpathJar = target / "lib" / classpath_jar
    IO.jar(Seq.empty, classpathJar, manifest)
    Seq(classpath_jar)
  }

  override def projectSettings: Seq[sbt.Def.Setting[_]] = {
    Defaults.coreDefaultSettings ++
      Seq(
        baseDirectory := thisProject.value.base,
        target := baseDirectory.value / "target",
        resolvers := standardResolvers,
        logLevel := Level.Info,
        fork in Test := false,
        logBuffered in Test := false,
        shellPrompt := buildShellPrompt.value,
        unmanagedJars in Compile := {
          baseDirectory.map { base =>
            (base / "libs" ** "*.jar").classpath
          }.value
        },
        unmanagedJars in Runtime := {
          baseDirectory.map { base =>
            (base / "libs" ** "*.jar").classpath
          }.value
        },
        unmanagedJars in Test := {
          baseDirectory.map { base =>
            (base / "libs" ** "*.jar").classpath
          }.value
        },
        mappings in (Compile, packageBin) ~= filter,
        mappings in (Compile, packageSrc) ~= filter,
        mappings in (Compile, packageDoc) ~= filter
      )
  }

  def useClassPathJar(project: Project): Project = {
    project.settings(Seq[Def.SettingsDefinition](scriptClasspath := {
      import com.typesafe.sbt.SbtNativePackager._
      import com.typesafe.sbt.packager.Keys._
      Miscellaneous.makeClasspathJar(
        scriptClasspathOrdering
          .map(Miscellaneous.makeRelativeClasspathNames)
          .value,
        (target in Universal).value
      )
    }, mappings in Universal += {
      (target in Universal).value / "lib" / "classpath.jar" ->
        "lib/classpath.jar"
    }): _*)
  }
}
