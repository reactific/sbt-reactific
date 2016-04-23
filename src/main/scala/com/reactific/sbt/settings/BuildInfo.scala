package com.reactific.sbt.settings

import com.reactific.sbt.AutoPluginHelper
import sbt.Keys._
import sbt._
import sbtbuildinfo.BuildInfoKeys._
import sbtbuildinfo._

/** The BuildInfo AutoPlugin
  * This sets up the BuildInfo plugin
  */
object BuildInfo extends AutoPluginHelper {

  /** The AutoPlugins that we depend upon */
  override def autoPlugins: Seq[AutoPlugin] = Seq.empty[AutoPlugin]

  override def projectSettings : Seq[Setting[_]] = Seq(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := codePackage.value,
    buildInfoObject := {
      val s = codePackage.value.split('.').map { s => s.head.toString.toUpperCase + s.tail }.mkString
      s.head.toString.toUpperCase + s.tail + "Info"
    },
    buildInfoKeys := Seq[BuildInfoKey](
      name, normalizedName, description, homepage, licenses, organization, organizationHomepage,
      apiURL, version, scalaVersion, isSnapshot,
      codePackage,
      titleForDocs,
      copyrightHolder,
      copyrightYears,
      developerUrl
    ),
    buildInfoOptions := Seq(BuildInfoOption.ToMap, BuildInfoOption.ToJson, BuildInfoOption.BuildTime)
  )

}
