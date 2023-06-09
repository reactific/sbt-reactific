/*
 * Copyright 2015-2023 Reactific Software LLC
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

import sbt._
import sbt.Keys._
import sbt.internal.inc.ReflectUtilities
import de.heikoseeberger.sbtheader.HeaderPlugin
import sbtrelease.ReleasePlugin.autoImport.ReleaseStep

trait ReactificKeys {
  
  sealed trait ArtifactKind
  case object ZipFileArtifact extends ArtifactKind
  case object DebianServerArtifact extends ArtifactKind
  case object RPMScalaServerArtifact extends ArtifactKind
  case object DockerServerArtifact extends ArtifactKind
  
  val artifactKinds: SettingKey[Seq[ArtifactKind]] =
    settingKey[Seq[ArtifactKind]](
      "The kinds of artifacts the project should produce. " +
        "Defaults to just to ZipFileArtifact"
  )
  
  val codePackage: SettingKey[String] = settingKey[String](
    "The main, top level Scala package name that contains the project's code"
  )

  val titleForDocs: SettingKey[String] = settingKey[String](
    "The name of the project as it should appear in documentation."
  )

  val organizationGitHubGroup: SettingKey[String] = settingKey[String](
    "The github group corresponding to the organization owning the code"
  )

  val copyrightHolderId: SettingKey[String] = settingKey[String](
    "The user id or email address for contacting the copyright holder"
  )

  val copyrightHolderName: SettingKey[String] =
    settingKey[String]("The name of the copyright holder for this project.")

  val developerUrl: SettingKey[URL] =
    settingKey[URL]("The URL for the project developer's home page")

  val privateNexusResolver: SettingKey[Option[Resolver]] =
    settingKey[Option[Resolver]](
      "Set to the (probably internal) resolver you want to search " +
        "first. Defaults to None but if set to Some[Resolver], it will be " +
        "replace the Maven Central resolver so needs to include that if " +
        "needed. Also becomes the destination of the release, otherwise " +
        "Sonatype OSS Nexus. "
    )

  val publishSnapshotsTo: SettingKey[Resolver] = settingKey[Resolver](
    "The Sonatype Repository to which snapshot versions are " +
      "published. Defaults to the OSS Sonatype Repository for snapshot."
  )

  val publishReleasesTo: SettingKey[Resolver] = settingKey[Resolver](
    "The Sonatype Repository to which release versions are " +
      "published. Defaults to the Sonatype OSS staging repository. To " +
      "publish releases, you must configure your account and password into " +
      "the SBT credentials file: ~/.sbt/1.0/sonatype.sbt"
  )

  val warningsAreErrors: SettingKey[Boolean] = settingKey[Boolean](
    "Cause compiler warnings to be errors instead. Default=true"
  )

  val checkScalaStyle: SettingKey[Boolean] = settingKey[Boolean](
    "Cause scalastyle to be run when releasing. Default is true"
  )

  val checkHeaders: SettingKey[Boolean] = settingKey[Boolean](
      "Cause headerCheck to be run when releasing. Default is true")
  
  val checkTests: SettingKey[Boolean] = settingKey[Boolean](
    "Cause tests to be run when releasing. Default is true"
  )
  
  val runScalafmtWhenReleasing: SettingKey[Boolean] = settingKey[Boolean](
    "Reformat scala source coe with Scalafmt as part of releasing. " +
      "Default is false"
  )
  
  val additionalCheckSteps: SettingKey[Seq[ReleaseStep]] =
    settingKey[Seq[ReleaseStep]](
      "Additional steps in release process to check the artifact's correctness"
    )
  
  private val thisDir = file(".")
  def theProject: Project = {
    val id = thisDir.getCanonicalFile.getName
    Project
      .apply(id.replaceAll(" ","-"), thisDir)
      .settings(
        name := id
      )
  }

  def rootProject: Project = {
    val projects: Seq[Project] = {
      ReflectUtilities.allVals[Project](this).values.toSeq
    }
    val aggregates = projects.filterNot(_.base == thisDir).map { p =>
      p.project
    }
    val id = thisDir.getCanonicalFile.getName + "-root"
    Project
      .apply(id, thisDir)
      .settings(
        name := id,
        aggregate in update := false,
        aggregate in HeaderPlugin.autoImport.headerCheck := false,
        aggregate in HeaderPlugin.autoImport.headerCreate := false,
        publishArtifact := false, // no artifact to publish for the virtual root project
        publish := {}, // just to be sure
        publishLocal := {}, // and paranoid
        publishTo := Some(Resolver.defaultLocal),
        shellPrompt := MiscellaneousHelper.buildShellPrompt.value
      )
      .aggregate(aggregates: _*)
  }
}
