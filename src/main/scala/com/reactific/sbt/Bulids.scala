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
    organization := "com.reactific",
    copyrightHolder := "Reactific Software LLC",
    copyrightYears := Seq(2016),
    developerUrl := url("http://reactific.com/")
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

  def rootName : Option[String] = None

  lazy val root : Option[Project] = {
    val aggregates = projects.filterNot(_.base == file(".")).map { p => p.project }
    val root_file = file(".")
    val root_name = rootName match {
      case Some(nm) ⇒ nm
      case None ⇒
        root_file.getCanonicalFile.getName + "-root"
    }
    Some(
      Build.defaultProject(root_name, root_file).
        settings(
          name := root_name,
          publishArtifact := false, // no artifact to publish for the virtual root project
          publish := {}, // just to be sure
          publishLocal := {}, // and paranoid
          shellPrompt :=  Miscellaneous.buildShellPrompt.value
        ).aggregate(aggregates:_*)
    )
  }

  override def projectDefinitions(base: File) = {
    root match {
      case Some(project) =>
        project +: super.projectDefinitions(base)
      case None =>
        throw new Exception("Failed to lazy init root project definition")
    }
  }

  final override def rootProject = {
    root match {
      case Some(project) => Some(project)
      case None =>
        throw new Exception("Failed to lazy init root project definition")
    }
  }
}
