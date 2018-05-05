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

import scala.xml._

import sbt.Keys._
import sbt.Keys.pomExtra
import sbt.Keys.pomIncludeRepository
import sbt._
import xerial.sbt.Sonatype._
import xerial.sbt.{Sonatype => SonatypePlugin}

/** Settings For SonatypePublishing Plugin */
object PublishingHelper extends AutoPluginHelper {

  import com.reactific.sbt.ReactificPlugin.autoImport._

  /** The AutoPlugins that we depend upon */
  override def autoPlugins: Seq[AutoPlugin] = Seq(SonatypePlugin)

  // scalastyle:off
  def makeLicense(name: String, url: String): Elem = {
    <license>
      <name>{name}</name>
      <url>{url}</url>
      <distribution>repo</distribution>
    </license>
  }

  def publishAsMaven(project: Project): Project = {
    project
      .settings(
        publishMavenStyle := true,
        pomExtra := {
          val devs =
            <developers>
              <developer>
                <id>{copyrightHolderId.value}</id>
                <name>{copyrightHolderName.value}</name>
                <url>{developerUrl.value}</url>
              </developer>
            </developers>
          val liclist = {
            licenses.value.map {
              case (nm, url) => makeLicense(nm, url.toExternalForm)
            }
          }
          val lics = <licenses>{liclist}</licenses>
          NodeSeq.fromSeq(Seq(devs, lics))
        },
        pomIncludeRepository := { _ =>
          false
        }
      )
  }
  // scalastyle:on

  def publishToSonaType(project: Project): Project = {
    project
      .enablePlugins(SonatypePlugin)
      .settings(sonatypeSettings)
      .settings(
        SonatypeKeys.sonatypeProfileName := organization.value,
        publishSnapshotsTo := Resolver.sonatypeRepo("snapshots"),
        publishReleasesTo :=
          MavenRepository(
            "Sonatype Maven Staging",
            "https://oss.sonatype.org/service/local/staging/deploy/maven2"
          ),
        homepage := Some(
          new URL(
            s"https://github.com/${organizationGitHubGroup
              .value}/${normalizedName.value}"
          )
        )
      )
      .configure(publishAsMaven)
  }

  def publishToPrivate(project: Project): Project = {
    project
      .settings(
        // TODO: set publishTo ?
      )
      .configure(publishAsMaven)
  }

  override def projectSettings: Seq[Setting[_]] = {
    Seq(
      organizationGitHubGroup := "reactific",
      licenses := Seq(
        "Apache2" -> url("http://www.apache.org/licenses/LICENSE-2.0")
      ),
      publishSnapshotsTo := Resolver.defaultLocal,
      publishReleasesTo := Resolver.defaultLocal,
      publishArtifact in Test := false,
      publishTo := {
        if (isSnapshot.value) {
          Some(publishSnapshotsTo.value)
        } else {
          Some(publishReleasesTo.value)
        }
      },
      scmInfo := {
        val gitUrl =
          s"//github.com/${organizationGitHubGroup.value}/${normalizedName
            .value}"
        Some(
          ScmInfo(
            url("https:" + gitUrl),
            "scm:git:" + gitUrl + ".git",
            Some("https:" + gitUrl)
          )
        )
      }
    )
  }
}
