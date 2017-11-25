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

import com.typesafe.sbt.SbtNativePackager.{Universal, UniversalDocs, UniversalSrc}
import com.typesafe.sbt.packager.SettingsHelper
import com.typesafe.sbt.packager.archetypes.JavaServerAppPackaging
import com.typesafe.sbt.packager.archetypes.systemloader.SystemVPlugin
import com.typesafe.sbt.packager.debian.DebianPlugin
import com.typesafe.sbt.packager.rpm.RpmPlugin
import com.typesafe.sbt.packager.universal.UniversalPlugin
import com.typesafe.sbt.packager.universal.UniversalPlugin.autoImport._
import sbt.Keys._
import sbt.{addArtifact, _}

/** Enable PublishUniversalPlugin to make universal artifacts publishable */
object Packaging {

  def linuxServerPackaging(project: Project): Project = {
    import ReactificPlugin.autoImport._
  
    project.enablePlugins(
        JavaServerAppPackaging,
        UniversalPlugin,
        SystemVPlugin,
        RpmPlugin,
        DebianPlugin
    ).settings(
      SettingsHelper
        .makeDeploymentSettings(Universal, dist in Universal, "zip") ++
        SettingsHelper.makeDeploymentSettings(
          UniversalSrc,
          dist in UniversalSrc,
          "zip"
        ) ++
        SettingsHelper.makeDeploymentSettings(
          UniversalDocs,
          dist in UniversalDocs,
          "zip"
        ) ++
        // --add the artifact so it is included in the publishing tasks
        addArtifact(artifact in (Universal, packageZip), packageZip in Universal)
    ).settings(
      // --hard coded result of "universal:packageBin"
      packageZip :=
        (baseDirectory in Compile).value / "target" / "universal" /
          s"${name.value}-${version.value}.zip",
      // --label the zip artifact as a zip instead of the default jar
      // 0.13:
      artifact in (Universal, packageZip) ~= { (art: Artifact) =>
        art.withType("zip").withExtension("zip")
      },
      /* 1.0:
      artifact in (Universal, packageZip) ~= { art =>
        art.withType("zip").withExtension("zip")
      }, */
      // --make sure the zip gets made before the publishing commands for the
      // added artifacts
      publish := { publish.dependsOn(dist in Universal).value },
      publishM2 := { publishM2.dependsOn(dist in Universal).value },
      publishLocal := { publishLocal.dependsOn(dist in Universal).value }
    )
  }
}
