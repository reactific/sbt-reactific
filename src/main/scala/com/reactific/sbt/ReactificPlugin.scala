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

import com.typesafe.sbt.GitPlugin
import de.heikoseeberger.sbtheader.HeaderPlugin
import sbt._
import sbt.Keys._
import sbt.internal.inc.ReflectUtilities

/** ReactificPlugin Implementation */
object ReactificPlugin extends AutoPlugin {

  /** The list of helper objects in this package */
  val helpers: Seq[AutoPluginHelper] = {
    Seq(
      BuildInfo,
      Commands,
      Compiler,
      Header,
      Miscellaneous,
      Publishing,
      Release,
      Scalastyle,
      Site,
      Unidoc,
    )
  }

  /** Extract the AutoPlugins needed from the Helpers */
  val autoPlugins: Seq[AutoPlugin] = {
    helpers.flatMap(_.autoPlugins) :+ GitPlugin
  }

  object conf {
    val useClassPathJar: P2P = Miscellaneous.useClassPathJar
    val linuxServerPackaging: P2P = Packaging.linuxServerPackaging
  }

  override def requires: Plugins = {
    autoPlugins.foldLeft(empty) { (b, plugin) =>
      b && plugin
    }
  }

  object autoImport {

    val codePackage: SettingKey[String] = settingKey[String](
      "The main, top level Scala package name that contains the project's code"
    )

    val titleForDocs: SettingKey[String] = settingKey[String](
      "The name of the project as it should appear in documentation."
    )

    val copyrightHolder: SettingKey[String] =
      settingKey[String]("The name of the copyright holder for this project.")

    val developerUrl: SettingKey[URL] =
      settingKey[URL]("The URL for the project developer's home page")

    val publishSnapshotsTo: SettingKey[Resolver] = settingKey[Resolver](
      "The Sonatype Repository to which snapshot versions are published"
    )

    val publishReleasesTo: SettingKey[Resolver] = settingKey[Resolver](
      "The Sonatype Repository to which release versions are published"
    )

    val warningsAreErrors: SettingKey[Boolean] = settingKey[Boolean](
      "Cause compiler warnings to be errors instead. Default=true"
    )

    val packageZip: TaskKey[File] = taskKey[File]("package-zip")

    val compileOnly =
      TaskKey[File]("compile-only", "Compile just the specified files")

    val printClasspath = TaskKey[File](
      "print-class-path",
      "Print the project's compilation class path."
    )

    val printTestClasspath = TaskKey[File](
      "print-test-class-path",
      "Print the project's testing class path."
    )

    val printRuntimeClasspath = TaskKey[File](
      "print-runtime-class-path",
      "Print the project's runtime class path."
    )
  }

  def identificationSettings: Seq[Setting[_]] = Seq(
    organization := "com.reactific",
    autoImport.copyrightHolder := "Reactific Software LLC",
    autoImport.developerUrl := url("http://github.com/reactific")
  )

  override def projectSettings: Seq[Def.Setting[_]] = {
    super.projectSettings ++ helpers.flatMap(h ⇒ h.projectSettings)
  }

  override def buildSettings: Seq[Def.Setting[_]] = {
    super.buildSettings ++ identificationSettings ++ helpers.flatMap(
      h ⇒ h.buildSettings
    )
  }

  override def globalSettings: Seq[Def.Setting[_]] = {
    super.globalSettings ++ helpers.flatMap(h ⇒ h.globalSettings)
  }

  def makeRootProject(): Project = {
    val projects: Seq[Project] = {
      ReflectUtilities.allVals[Project](this).values.toSeq
    }
    val aggregates = projects.filterNot(_.base == file(".")).map { p =>
      p.project
    }
    val base = file(".")
    val id = base.getCanonicalFile.getName + "-root"
    Project
      .apply(id, base)
      .settings(
        name := id,
        aggregate in update := false,
        aggregate in HeaderPlugin.autoImport.headerCheck := false,
        aggregate in HeaderPlugin.autoImport.headerCreate := false,
        publishArtifact := false, // no artifact to publish for the virtual root project
        publish := {}, // just to be sure
        publishLocal := {}, // and paranoid
        publishTo := Some(Resolver.defaultLocal),
        shellPrompt := Miscellaneous.buildShellPrompt.value,
      )
      .aggregate(aggregates: _*)
  }

}
