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

import com.typesafe.sbt.JavaVersionCheckPlugin.autoImport._
import com.typesafe.sbt.{GitPlugin, JavaVersionCheckPlugin}
import de.heikoseeberger.sbtheader.HeaderPlugin
import sbt.Keys._
import sbt._
import sbtbuildinfo.BuildInfoKeys._
import sbtbuildinfo._
import sbtrelease.ReleasePlugin
import sbtsh.ShPlugin

/** The ProjectPlugin to add to Reactific Scala projects so they share a common set of build characteristics */
object ProjectPlugin extends AutoPlugin {

  val autoplugins : Seq[AutoPlugin] = Seq(JavaVersionCheckPlugin,
    GitPlugin, HeaderPlugin, ShPlugin, ReleasePlugin )

  override def requires = {
    // Enable all the AutoPlugin instances listed in autoplugins
    autoplugins.foldLeft(empty) { (b,plugin) => b && plugin }
  }

  // Not AutoPlugins Yet: CompileQuick, Unidoc, SbtSite, SbtGhPages, SbtUnidocPlugin :(

  /** Settings For Plugins that are not yet AutoPlugins so we can mimic them.
    * This trait provides the same settings methods as an AutoPlugin. See [[sbt.AutoPlugin]]
    * This is used to override settings in both AutoPlugins and regular Plugins.
    */
  val pluginSettings : Seq[PluginSettings] = Seq(
    CompileQuick, Compiler, Settings, Unidoc, SonatypePublishing, Site, Release
  )

  override def trigger = noTrigger

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

    val compileOnly = TaskKey[File]("compile-only", "Compile just the specified files")
    val printClasspath = TaskKey[File]("print-class-path", "Print the project's compilation class path.")
    val printTestClasspath = TaskKey[File]("print-test-class-path", "Print the project's testing class path.")
    val printRuntimeClasspath = TaskKey[File]("print-runtime-class-path", "Print the project's runtime class path.")
  }

  val standardResolvers = Seq(
    "BinTray-typesafe" at "https://dl.bintray.com/typesafe/ivy-releases",
    "BinTray-sbt" at "https://dl.bintray.com/sbt/sbt-plugin-releases",
    "Bintray-scalaz" at "http://dl.bintray.com/scalaz/releases",
    "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/",
    "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
  )

  // Get all the autoImport keys into this namespace for easier reference

  import autoImport._

  /**
   * Define the values of the settings
   */
  override def projectSettings: Seq[Setting[_]] = {
    Defaults.coreDefaultSettings ++
      autoplugins.foldLeft(Seq.empty[Setting[_]]) { (s, p) => s ++ p.projectSettings } ++
      pluginSettings.foldLeft(Seq.empty[Setting[_]]) { (s, p) => s ++ p.projectSettings } ++
      Seq (
        resolvers := standardResolvers,
        javaVersionPrefix in javaVersionCheck := Some("1.8"),
        ivyScala := ivyScala.value map {_.copy(overrideScalaVersion = true)},
        buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
        buildInfoPackage := codePackage.value,
        buildInfoObject := {
          val s = codePackage.value.split('.').map { s => s.head.toString.toUpperCase + s.tail }.mkString
          s.head.toString.toUpperCase + s.tail + "Info"
        },
        buildInfoKeys := Seq[BuildInfoKey](
          name, normalizedName, description, homepage, licenses, organization, organizationHomepage,
          apiURL, version, scalaVersion, isSnapshot, codePackage, titleForDocs, copyrightHolder,
          copyrightYears, developerUrl
        ),
        buildInfoOptions := Seq(BuildInfoOption.ToMap, BuildInfoOption.ToJson, BuildInfoOption.BuildTime),
        printClasspath <<= Commands.print_class_path,
        printTestClasspath <<= Commands.print_test_class_path,
        printRuntimeClasspath <<= Commands.print_runtime_class_path,
        compileOnly <<= Commands.compile_only,
        libraryDependencies ++= Seq(
          "org.specs2" %% "specs2-core" % "3.6.1" % "test",
          "org.specs2" %% "specs2-junit" % "3.6.1" % "test"
        )
      )
  }

  override def buildSettings : Seq[Setting[_]] = Defaults.buildCore ++
    Seq(
      baseDirectory := thisProject.value.base,
      target := baseDirectory.value / "target"
    ) ++
    Commands.aliases ++
    autoplugins.foldLeft(Seq.empty[Setting[_]]) { (s,p) => s ++ p.buildSettings } ++
    pluginSettings.foldLeft(Seq.empty[Setting[_]]) { (s,p) => s ++ p.buildSettings }
}






















