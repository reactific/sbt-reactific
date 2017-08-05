package com.reactific.sbt

import java.time.Instant
import java.time.temporal.ChronoField
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
      buildInfoKeys := Seq[BuildInfoKey](name,
                                         version,
                                         scalaVersion,
                                         sbtVersion),
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
        Seq(BuildInfoOption.ToMap,
            BuildInfoOption.ToJson,
            BuildInfoOption.BuildTime)
    )
  }

}
