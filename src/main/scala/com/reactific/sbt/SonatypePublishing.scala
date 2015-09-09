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
import ProjectPlugin.autoImport._
import xerial.sbt.Sonatype

/** Settings For SonatypePublishing Plugin */
object SonatypePublishing extends PluginSettings {


  def targetRepository: Def.Initialize[Option[Resolver]] = Def.setting {
    val nexus = "https://oss.sonatype.org/"
    val snapshotsR = "snapshots" at nexus + "content/repositories/snapshots"
    val releasesR  = "releases"  at nexus + "service/local/staging/deploy/maven2"
    val resolver = if (isSnapshot.value) snapshotsR else releasesR
    Some(resolver)
  }

  val defaultScmInfo = Def.setting {
    val gitUrl = "//github.com/scrupal/" + normalizedName.value + ".git"
    ScmInfo(url("https:" ++ gitUrl), "scm:git:" ++ gitUrl, Some("https:" ++ gitUrl) )
  }

  override def projectSettings = Sonatype.sonatypeSettings ++ Seq(
    Sonatype.SonatypeKeys.sonatypeProfileName := "org.scrupal",
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false },
    licenses := Seq("Apache2" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    homepage := Some(new URL("http://modules.scrupal.org/" + normalizedName.value)),
    pomExtra in Global := {
      <scm>
        <url>{scmInfo.value.getOrElse(defaultScmInfo.value).browseUrl.toString}</url>
        <connection>{scmInfo.value.getOrElse(defaultScmInfo.value).connection}</connection>
      </scm>
      <developers>
        <developer>
          <id>{scrupalCopyrightHolder.value}</id>
          <name>{scrupalCopyrightHolder.value}</name>
          <url>{scrupalDeveloperUrl.value}</url>
        </developer>
      </developers>
    }
  )
}
