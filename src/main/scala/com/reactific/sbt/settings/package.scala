package com.reactific.sbt

import sbt._

/** Definitions Of Settings And Keys We Add */

package object settings {

  val codePackage = settingKey[String]("The main, top level Scala package name that contains the project's code")
  val titleForDocs = settingKey[String]("The name of the project as it should appear in documentation.")
  val copyrightHolder = settingKey[String]("The name of the copyright holder for this project.")
  val copyrightYears = settingKey[Seq[Int]]("The years in which the copyright was in place for this project.")
  val developerUrl = settingKey[URL]("The URL for the project developer's home page")
  val publishSnapshotsTo = settingKey[Resolver]("The Sonatype Repository to which snapshot versions are published")
  val publishReleasesTo = settingKey[Resolver]("The Sonatype Repository to which release versions are published")
  val warningsAreErrors = settingKey[Boolean]("Cause compiler warnings to be errors instead. Default=true")
  val packageZip = taskKey[File]("package-zip")

}
