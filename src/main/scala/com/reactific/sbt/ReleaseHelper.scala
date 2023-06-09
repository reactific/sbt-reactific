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

import com.typesafe.sbt.packager.universal.UniversalPlugin
import com.typesafe.sbt.pgp.PgpKeys
import sbt._
import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._
import sbtrelease.ReleasePlugin.autoImport._
import sbtrelease.{ ReleasePlugin, Version }
import ReactificPlugin.autoImport._
import sbt.KeyRanks.APlusTask
import sbt.Keys.publish

object ReleaseHelper extends AutoPluginHelper {

  /** The AutoPlugins that we depend upon */
  override def autoPlugins: Seq[AutoPlugin] = Seq(ReleasePlugin)
  
  override def projectSettings: Seq[sbt.Setting[_]] = {
    Seq[sbt.Setting[_]](
      runScalafmtWhenReleasing := false,
      additionalCheckSteps := Seq.empty[ReleaseStep],
      checkTests := true,
      checkScalaStyle := true,
      checkHeaders := true
    )
  }

  def initialSteps: Seq[ReleaseStep] =
    Seq[ReleaseStep](checkSnapshotDependencies, inquireVersions, runClean)

  def checkingSteps(
    checkHeaders: Boolean,
    checkScalastyle: Boolean,
    checkTests: Boolean,
    additionalCheckSteps: Seq[ReleaseStep]
  ): Seq[ReleaseStep] = {
    additionalCheckSteps ++ {
      if (checkHeaders) {
        Seq[ReleaseStep](releaseStepCommand("headerCheck"))
      } else {
        Seq.empty[ReleaseStep]
      }
    } ++ {
      if (checkScalastyle) {
        Seq[ReleaseStep](releaseStepCommand("scalastyle"))
      } else {
        Seq.empty[ReleaseStep]
      }
    } ++ {
      if (checkTests) {
        Seq[ReleaseStep](releaseStepCommand("test"))
      } else {
        Seq.empty[ReleaseStep]
      }
    }
  }
  
  def taggingSteps(runScalafmt: Boolean): Seq[ReleaseStep] = {
    {
      if (runScalafmt) {
        Seq[ReleaseStep](releaseStepCommand("scalafmt"))
      } else {
        Seq.empty[ReleaseStep]
      }
    } ++
      Seq[ReleaseStep](
        setReleaseVersion,
        commitReleaseVersion,
        tagRelease
      )
  }
  
  def packagingSteps(artifactKinds: Seq[ArtifactKind]): Seq[ReleaseStep] = {
    artifactKinds.map[ReleaseStep,Seq[ReleaseStep]] {
      case ZipFileArtifact ⇒
        releaseStepTask(UniversalPlugin.autoImport.dist)
      case DebianServerArtifact ⇒
        releaseStepCommand("deb:packageBin")
      case RPMScalaServerArtifact ⇒
        releaseStepCommand("rpm:packageBin")
      case DockerServerArtifact ⇒
        releaseStepCommand("docker:stage")
    }
  }
  
  
  def finalSteps(releaseOSS: Boolean): Seq[ReleaseStep] = {
    Seq[ReleaseStep](
      publishArtifacts,
      setNextVersion,
      commitNextVersion
    ) ++ {
      if (releaseOSS) {
        Seq[ReleaseStep](releaseStepCommand("sonatypeReleaseAll"))
      } else {
        Seq.empty[ReleaseStep]
      }
    } :+ pushChanges
  }
  
  val choosePublishTask = TaskKey[Unit](
    "choose-publish-signed", "Choose which publishing task to use", APlusTask
  )
  
  
  def releasePublishTask(
    isOSS: Boolean
  ): Def.Initialize[Task[Unit]] = Def.taskDyn[Unit] {
    if (isOSS) { // releasing open source
      PgpKeys.publishSigned
    } else {
      publish
    }
  }
  
  def process(project: Project): Project = {
    import ReactificPlugin.autoImport._
    project
      .enablePlugins(ReleasePlugin)
      .settings(
        releaseUseGlobalVersion := true,
        releaseVersionBump := Version.Bump.Bugfix,
        choosePublishTask :=
          releasePublishTask(privateNexusResolver.value.isEmpty),
        releasePublishArtifactsAction := choosePublishTask.value,
        releaseProcess := {
          initialSteps ++
            checkingSteps(
              checkHeaders.value, checkScalaStyle.value,
              checkTests.value, additionalCheckSteps.value
            ) ++
            taggingSteps(runScalafmtWhenReleasing.value) ++
            packagingSteps(artifactKinds.value) ++
            finalSteps(privateNexusResolver.value.isEmpty)
        }
      )
  }
}
