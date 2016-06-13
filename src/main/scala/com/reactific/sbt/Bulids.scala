package com.reactific.sbt

import com.reactific.sbt.settings._
import sbt.Keys._
import sbt._

/** SingleProjectBuild
  * This is the simple case where the top directory contains the single project to build.
  * The subclass should define its project and assign it to the rootProject member.
  * This class just sets the shell prompt for the build.
  */
class SingleProjectBuild extends Build {

  override def settings = super.settings ++ Seq(
    shellPrompt := Miscellaneous.buildShellPrompt.value,
    organization := "com.reactific",
    copyrightHolder := "Reactific Software LLC",
    copyrightYears := Seq(2016),
    developerUrl := url("http://reactific.com/"),
    codePackage := s"com.reactific.${name.value}"
  )
}

/** Aggregating Root Build
  * This is intended for a multi-project scenario where each project is in a subdirectory.
  * This build precludes setting the rootProject member because SBT will automatically set
  * up a root project that aggregates the contained projects. In this case, there must be
  * no project assigned to the "." directory; they must all be in subdirectories of the
  * top level. This class also sets up the shell prompt, gives the root projct the name
  * "root" and ensures it doesn't publish any artifacts.
  */
class AggregatingRootBuild extends SingleProjectBuild {

  final override def rootProject: Option[Project] = None

  override def settings: scala.Seq[sbt.Def.Setting[_]] = {
    super.settings ++ Seq(
      name := "root",
      publish := {}
    )
  }
}
