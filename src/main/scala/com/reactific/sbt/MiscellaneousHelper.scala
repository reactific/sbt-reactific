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
import com.typesafe.sbt.packager.Keys.scriptClasspath
import com.typesafe.sbt.SbtNativePackager.Universal
import sbt.IO
import sbt.Keys._
import sbt._

import com.reactific.sbt.ReactificPlugin.autoImport._

/** General settings for the project */
object MiscellaneousHelper extends AutoPluginHelper {

  private def currBranch: String = {
    import com.typesafe.sbt.git.JGit
    val jgit = JGit(new File("."))
    jgit.branch
  }

  private[sbt] def buildShellPrompt: Def.Initialize[State => String] = {
    Def.setting { (state: State) =>
      val id = Project.extract(state).currentProject.id
      s"${name.value}($id) : $currBranch : ${version.value}>"
    }
  }

  private def filter(ms: Seq[(File, String)]): Seq[(File, String)] = {
    ms.filter {
      case (_, path) =>
        path != "logback.xml" &&
          !path.startsWith("toignore") &&
          !path.startsWith("samples")
    }
  }

  def useUnmanagedJarLibs(project: Project): Project = {
    project
      .settings(
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

  override def projectSettings: Seq[sbt.Def.Setting[_]] = {
    Seq(
      baseDirectory := thisProject.value.base,
      target := baseDirectory.value / "target",
      logLevel := Level.Info,
      fork in Test := false,
      logBuffered in Test := false,
      shellPrompt := buildShellPrompt.value,
      artifactKinds := { Seq[ArtifactKind](ZipFileArtifact) }
    )
    
  }

  private val classpath_jar = "classpath.jar"

  private def makeRelativeClasspathNames(
    mappings: Seq[(File, String)]
  ): Seq[String] = {
    for {
      (_, name) <- mappings
    } yield {
      // Here we want the name relative to the lib/ folder...
      // For now we just cheat...
      if (name.startsWith("lib/")) name.drop(4) else "../" + name
    }
  }

  private def makeClasspathJar(
    classPath: Seq[String],
    target: File
  ): Seq[String] = {
    val manifest = new java.util.jar.Manifest()
    manifest.getMainAttributes
      .putValue("Class-Path", classPath.mkString(" "))
    val classpathJar = target / "lib" / classpath_jar
    IO.jar(Seq.empty, classpathJar, manifest)
    Seq(classpath_jar)
  }

  def useClassPathJar(project: Project): Project = {
    project.settings(Seq[Def.SettingsDefinition](scriptClasspath := {
      import com.typesafe.sbt.SbtNativePackager._
      import com.typesafe.sbt.packager.Keys._
      MiscellaneousHelper.makeClasspathJar(
        scriptClasspathOrdering
          .map(MiscellaneousHelper.makeRelativeClasspathNames)
          .value,
        (target in Universal).value
      )
    }, mappings in Universal += {
      (target in Universal).value / "lib" / "classpath.jar" ->
        "lib/classpath.jar"
    }): _*)
  }
}
