/**********************************************************************************************************************
 *                                                                                                                    *
 * Copyright (c) 2015, Reactific Software LLC. All Rights Reserved.                                                   *
 *                                                                                                                    *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance     *
 * with the License. You may obtain a copy of the License at                                                          *
 *                                                                                                                    *
 *     http://www.apache.org/licenses/LICENSE-2.0                                                                     *
 *                                                                                                                    *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed   *
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for  *
 * the specific language governing permissions and limitations under the License.                                     *
 **********************************************************************************************************************/

package com.reactific.sbt

import com.typesafe.sbt.SbtNativePackager._
import com.typesafe.sbt.packager.universal.UniversalPlugin
import com.typesafe.sbt.{GitPlugin, JavaVersionCheckPlugin}
import de.heikoseeberger.sbtheader.HeaderPlugin
import sbt._
import sbt.Keys._
import sbtrelease.ReleasePlugin

/** The ProjectPlugin to add to Reactific Scala projects so they share a common set of build characteristics */
object ProjectPlugin extends ProjectPluginTrait

object SubProjectPlugin extends SubProjectPluginTrait

trait SubProjectPluginTrait extends AutoPluginHelper with PluginSettings {

  /** The list of AutoPlugins that a SubProject Needs */
  def autoPlugins : Seq[AutoPlugin] = Seq(
    JavaVersionCheckPlugin, GitPlugin, HeaderPlugin
  )

  /** Settings For Plugins that are not yet AutoPlugins so we can mimic them.
    * This trait provides the same settings methods as an AutoPlugin.
    * This is used to override settings in both AutoPlugins and regular Plugins.
    */
  def pluginSettings : Seq[PluginSettings] = Seq(
    Commands, CompileQuick, Compiler, Settings, Unidoc, SonatypePublishing, Site, Release
  )

  /** The Configurations to add to each project that activates this AutoPlugin.*/
  override def projectConfigurations: Seq[Configuration] = Nil

  /** The [[sbt.Setting]]s to add in the scope of each project that activates this AutoPlugin. */
  override def projectSettings: Seq[Setting[_]]  = {
    Defaults.coreDefaultSettings ++
    pluginSettings.foldLeft(Seq.empty[Setting[_]]) { (s, p) => s ++ p.projectSettings } ++
    Seq(
      resolvers := standardResolvers,
      ivyScala  := ivyScala.value map {_.copy(overrideScalaVersion = true)},
      logLevel  := Level.Info
    )
  }

  /** The [[sbt.Setting]]s to add to the build scope for each project that activates this AutoPlugin.
    * The settings returned here are guaranteed to be added to a given build scope only once
    * regardless of how many projects for that build activate this AutoPlugin. */
  override def buildSettings : Seq[Setting[_]] = {
    Defaults.buildCore ++
    pluginSettings.foldLeft(Seq.empty[Setting[_]]) { (s, p) => s ++ p.buildSettings } ++
    Seq(
      baseDirectory := thisProject.value.base,
      target := baseDirectory.value / "target"
    )
  }

  /** The [[sbt.Setting]]s to add to the global scope exactly once if any project activates this AutoPlugin. */
  override def globalSettings: Seq[Setting[_]] = {
    pluginSettings.foldLeft(Seq.empty[Setting[_]]) { (s, p) => s ++ p.globalSettings }
  }

  def standardResolvers : Seq[Resolver] = Seq[Resolver](
    "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/",
    "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
    "BinTray Typesafe Releases" at "https://dl.bintray.com/typesafe/ivy-releases",
    "BinTray Sbt Plugin Releases" at "https://dl.bintray.com/sbt/sbt-plugin-releases",
    "Bintray Scalaz Releases" at "http://dl.bintray.com/scalaz/releases"
  )
}

trait ProjectPluginTrait extends SubProjectPluginTrait {

  override def autoPlugins : Seq[AutoPlugin] = super.autoPlugins ++ Seq(
    ReleasePlugin, UniversalPlugin
  )

  override def pluginSettings : Seq[PluginSettings] = super.pluginSettings ++ Seq(
    BuildInfo, Header
  )

  /**
   * Defines all settings/tasks that get automatically imported,
   * when the plugin is enabled
   */
  object autoImport {
    val codePackage = settingKey[String]("The main, top level Scala package name that contains the project's code")
    val titleForDocs = settingKey[String]("The name of the project as it should appear in documentation.")
    val copyrightHolder = settingKey[String]("The name of the copyright holder for this project.")
    val copyrightYears = settingKey[Seq[Int]]("The years in which the copyright was in place for this project.")
    val developerUrl = settingKey[URL]("The URL for the project developer's home page")
    val publishSnapshotsTo = settingKey[Resolver]("The Sonatype Repository to which snapshot versions are published")
    val publishReleasesTo = settingKey[Resolver]("The Sonatype Repository to which release versions are published")
    val warningsAreErrors = settingKey[Boolean]("Cause compiler warnings to be errors instead. Default=true")
    //--a dummy task to hold the result of the universal:packageBin to stop the circular dependency issue
    val packageZip = taskKey[File]("package-zip")
  }

  /**
   * Define the values of the settings
   */
  override def projectSettings: Seq[Setting[_]] = super.projectSettings ++ Seq(
    //--hard coded result of "universal:packageBin"
    autoImport.packageZip :=
      (baseDirectory in Compile).value / "target" / "universal" / s"${name.value}-${version.value}.zip",

    //--label the zip artifact as a zip instead of the default jar
    artifact in (Universal, autoImport.packageZip) ~= { (art:Artifact) =>
      art.copy(`type` = "zip", extension = "zip")
    },

    //--make sure the zip gets made before the publishing commands for the added artifacts
    publish <<= (publish) dependsOn (packageBin in Universal),
    publishM2 <<= (publishM2) dependsOn (packageBin in Universal),
    publishLocal <<= (publishLocal) dependsOn (packageBin in Universal),

    autoImport.warningsAreErrors := true,
    autoImport.publishSnapshotsTo :=
      MavenRepository("Sonatype Snapshots", "https://oss.sonatype.org/content/repositories/snapshots"),
    autoImport.publishReleasesTo :=
      MavenRepository("Sonatype Maven Staging", "https://oss.sonatype.org/service/local/staging/deploy/maven2"),
    libraryDependencies ++= Seq(
      "org.specs2" %% "specs2-core" % "3.6.6" % "test",
      "org.specs2" %% "specs2-junit" % "3.6.6" % "test"
    )
  ) ++
    //--add the artifact so it is included in the publishing tasks
    addArtifact(artifact in (Universal, autoImport.packageZip), autoImport.packageZip in Universal)
}






















