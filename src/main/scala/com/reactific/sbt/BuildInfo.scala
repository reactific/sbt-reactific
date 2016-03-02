package com.reactific.sbt

import sbt.Keys._
import sbt._

import sbtbuildinfo._
import sbtbuildinfo.BuildInfoKeys._

object BuildInfo extends PluginSettings {

  override def projectSettings : Seq[Setting[_]] = Seq(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := ProjectPlugin.autoImport.codePackage.value,
    buildInfoObject := {
      val s = ProjectPlugin.autoImport.codePackage.value.split('.').map { s => s.head.toString.toUpperCase + s.tail }.mkString
      s.head.toString.toUpperCase + s.tail + "Info"
    },
    buildInfoKeys := Seq[BuildInfoKey](
      name, normalizedName, description, homepage, licenses, organization, organizationHomepage,
      apiURL, version, scalaVersion, isSnapshot,
      ProjectPlugin.autoImport.codePackage,
      ProjectPlugin.autoImport.titleForDocs,
      ProjectPlugin.autoImport.copyrightHolder,
      ProjectPlugin.autoImport.copyrightYears,
      ProjectPlugin.autoImport.developerUrl
    ),
    buildInfoOptions := Seq(BuildInfoOption.ToMap, BuildInfoOption.ToJson, BuildInfoOption.BuildTime)
  )

}
