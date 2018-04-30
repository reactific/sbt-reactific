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

import com.typesafe.sbt.pgp.PgpKeys
import sbt._
import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._
import sbtrelease.ReleasePlugin.autoImport._
import sbtrelease.{ReleasePlugin, Version}

object ReleaseHelper extends AutoPluginHelper {

  /** The AutoPlugins that we depend upon */
  override def autoPlugins: Seq[AutoPlugin] = Seq(ReleasePlugin)

  def process(project: Project): Project = {
    import ReactificPlugin.autoImport._
    project
      .enablePlugins(ReleasePlugin)
      .settings(
        releaseUseGlobalVersion := true,
        releaseVersionBump := Version.Bump.Bugfix,
        releasePublishArtifactsAction := PgpKeys.publishSigned.value,
        releaseProcess := Seq[ReleaseStep](
          checkSnapshotDependencies,
          inquireVersions,
          runClean
        ) ++ {
          if (checkScalaStyle.value)
            Seq[ReleaseStep](releaseStepCommand("scalastyle"))
          else
            Seq.empty[ReleaseStep]
        } ++ {
          if (checkHeaders.value)
              Seq[ReleaseStep](releaseStepCommand("headerCheck"))
          else
            Seq.empty[ReleaseStep]
        } ++ Seq[ReleaseStep](
          runTest,
          setReleaseVersion,
          commitReleaseVersion,
          tagRelease
        ) ++
          PackagingHelper.packagingReleaseSteps.value
        ++ Seq[ReleaseStep](
          publishArtifacts,
          setNextVersion,
          commitNextVersion,
          releaseStepCommand("sonatypeReleaseAll"),
          pushChanges
        )
      )
  }
}
