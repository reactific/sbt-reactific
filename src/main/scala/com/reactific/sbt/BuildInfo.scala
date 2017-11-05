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

import java.util.Calendar

import sbt.Keys._
import sbt._
import sbtbuildinfo.BuildInfoKeys._
import sbtbuildinfo._

/** The BuildInfo AutoPlugin
 * This sets up the BuildInfo plugin
 */
object BuildInfo extends AutoPluginHelper {

  override def autoPlugins: Seq[AutoPlugin] = {
    Seq(BuildInfoPlugin)
  }

  import com.reactific.sbt.ReactificPlugin.autoImport._

  override def projectSettings: Seq[Setting[_]] = {
    val now: Calendar = Calendar.getInstance()
    val year = now.get(Calendar.YEAR)
    Seq(
      buildInfoKeys := Seq[BuildInfoKey](
        name,
        version,
        scalaVersion,
        sbtVersion
      ),
      buildInfoPackage := codePackage.value,
      buildInfoObject := {
        val pieces = codePackage.value
          .split('.')
          .map { s =>
            s.head.toString.toUpperCase + s.tail
          }
          .mkString
        pieces.head.toString.toUpperCase + pieces.tail + "Info"
      },
      buildInfoKeys := Seq[BuildInfoKey](
        name,
        normalizedName,
        description,
        homepage,
        licenses,
        organization,
        organizationHomepage,
        apiURL,
        version,
        scalaVersion,
        sbtVersion,
        isSnapshot,
        codePackage,
        titleForDocs,
        copyrightHolder,
        developerUrl,
        buildInfoBuildNumber,
        BuildInfoKey.action[String]("copyrightYears")(
          startYear.value.get + "-" + year.toString
        )
      ),
      buildInfoOptions :=
        Seq(
          BuildInfoOption.ToMap,
          BuildInfoOption.ToJson,
          BuildInfoOption.BuildTime
        )
    )
  }

}
