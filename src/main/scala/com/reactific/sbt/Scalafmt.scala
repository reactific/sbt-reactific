package com.reactific.sbt

import scala.concurrent.duration._

import org.scalafmt.sbt.ScalafmtPlugin
import sbt._
import sbt.Keys._
import sbt.io.syntax.File

object Scalafmt extends AutoPluginHelper {

  override def autoPlugins: Seq[AutoPlugin] = {
    Seq(ScalafmtPlugin)
  }

  val scalafmt_path: String = "/reactific/public/master/.scalafmt.conf"
  val scalafmt_conf: String = ".scalafmt.conf"

  def autoUpdateScalaFmtConf(
    scalafmtPath: String = scalafmt_path,
    maxAge: FiniteDuration = 1.day
  )(project: Project
  ): Project = {
    project.settings(
      update := {
        val log = streams.value.log
        val localFile: File = baseDirectory.value / scalafmt_conf
        Miscellaneous
          .updateFromPublicRepository(localFile, scalafmtPath, maxAge, log)
        update.value
      }
    )
  }
}
