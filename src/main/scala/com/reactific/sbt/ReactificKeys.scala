package com.reactific.sbt

import sbt._
import sbt.Keys._
import sbt.internal.inc.ReflectUtilities
import de.heikoseeberger.sbtheader.HeaderPlugin

trait ReactificKeys {

  val codePackage: SettingKey[String] = settingKey[String](
    "The main, top level Scala package name that contains the project's code"
  )

  val titleForDocs: SettingKey[String] = settingKey[String](
    "The name of the project as it should appear in documentation."
  )

  val gitHubGroup: SettingKey[String] = settingKey[String](
    "The github group corresponding to the organization owning the code"
  )

  val copyrightHolderId: SettingKey[String] = settingKey[String](
    "The user id or email address for contacting the copyright holder"
  )

  val copyrightHolderName: SettingKey[String] = settingKey[String](
    "The name of the copyright holder for this project."
  )

  val developerUrl: SettingKey[URL] = settingKey[URL](
    "The URL for the project developer's home page"
  )

  val privateNexusResolver : SettingKey[Option[Resolver]] =
    settingKey[Option[Resolver]](
    "Set to the (probably internal) resolver you want to search " +
    "first. Defaults to None but if set to Some[Resolver], it will be " +
    "replace the Maven Central resolver so needs to include that if needed."
  )

  val publishSnapshotsTo: SettingKey[Resolver] = settingKey[Resolver](
    "The Sonatype Repository to which snapshot versions are " +
      " published. Defaults to the OSS Sonatype Repository for snapshot."
  )

  val publishReleasesTo: SettingKey[Resolver] = settingKey[Resolver](
    "The Sonatype Repository to which release versions are " +
      "published. Defaults to the Sonatype stagine repository for OSS. To " +
      "publish releases, you must configure your account and password into " +
      "the SBT credentials file: ~/.sbt/1.0/sonatype.sbt"
  )

  val warningsAreErrors: SettingKey[Boolean] = settingKey[Boolean](
    "Cause compiler warnings to be errors instead. Default=true"
  )

  val checkScalaStyle: SettingKey[Boolean] = settingKey[Boolean](
    "Cause scalastyle to be run when releasing"
  )

  val checkHeaders: SettingKey[Boolean] = settingKey[Boolean](
    "Cause headerCheck to be run when releasing"
  )

  def rootProject(): Project = {
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
        shellPrompt := MiscellaneousHelper.buildShellPrompt.value,
      )
      .aggregate(aggregates: _*)
  }
}
