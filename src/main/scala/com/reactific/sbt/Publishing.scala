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

import sbt.Keys._
import sbt._
import xerial.sbt.Sonatype._
import xerial.sbt.{Sonatype ⇒ SonatypePlugin}

/** Settings For SonatypePublishing Plugin */
object Publishing extends AutoPluginHelper {

  import com.reactific.sbt.ReactificPlugin.autoImport._

  /** The AutoPlugins that we depend upon */
  override def autoPlugins: Seq[AutoPlugin] = Seq(SonatypePlugin)

  val defaultScmInfo = Def.setting {
    val gitUrl = "//github.com/reactific/" + normalizedName.value + ".git"
    ScmInfo(
      url("https:" ++ gitUrl),
      "scm:git:" ++ gitUrl,
      Some("https:" ++ gitUrl)
    )
  }

  override def projectSettings = sonatypeSettings ++ Seq(
    SonatypeKeys.sonatypeProfileName := "com.reactific",
    publishSnapshotsTo := Resolver.sonatypeRepo("snapshots"),
    publishReleasesTo :=
      MavenRepository(
        "Sonatype Maven Staging",
        "https://oss.sonatype.org/service/local/staging/deploy/maven2"
      ),
    publishTo := {
      if (isSnapshot.value)
        Some(publishSnapshotsTo.value)
      else
        Some(publishReleasesTo.value)
    },
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { _ =>
      false
    },
    licenses := Seq(
      "Apache2" -> url("http://www.apache.org/licenses/LICENSE-2.0")
    ),
    homepage := Some(
      new URL("https://github.com/reactific/" + normalizedName.value)
    ),
    pomExtra in Global := {
      <developers>
        <developer>
          <id>{copyrightHolder.value}</id>
          <name>{copyrightHolder.value}</name>
          <url>{developerUrl.value}</url>
        </developer>
      </developers>
    }
  )

}
