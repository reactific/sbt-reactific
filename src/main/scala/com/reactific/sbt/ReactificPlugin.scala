package com.reactific.sbt

import com.typesafe.sbt.GitPlugin
import sbt.Keys.organization
import sbt.{
  settingKey,
  taskKey,
  url,
  AllRequirements,
  AutoPlugin,
  Def,
  File,
  Project,
  Resolver,
  Setting,
  SettingKey,
  TaskKey,
  URL
}

/** ReactificPlugin Implementation */
object ReactificPlugin extends AutoPlugin {

  /** A convenience type for configuration functions */
  type P2P = Project ⇒ Project

  /** All Requirements Must Be Met */
  override def allRequirements = {
    // must have all requirements satisfied to activate
    AllRequirements
  }

  /** Require the AutoPlugin to be manually activated */
  override def trigger = {
    // requires manual activation via "enablePlugin(ProjectPlugin)
    noTrigger
  }

  /** The list of helper objects in this package */
  val helpers: Seq[AutoPluginHelper] = {
    Seq(
      Commands,
      // CompileQuick,
      Compiler,
      Miscellaneous,
      Site,
      Unidoc,
      BuildInfo,
      Header,
      Packaging,
      Publishing,
      Release
    )
  }

  /** Extract the AutoPlugins needed from the Helpers */
  val autoPlugins: Seq[AutoPlugin] = {
    helpers.flatMap(_.autoPlugins) :+ GitPlugin
  }

  override def requires = {
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
    autoImport.developerUrl := url("http://reactific.com/")
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

}
