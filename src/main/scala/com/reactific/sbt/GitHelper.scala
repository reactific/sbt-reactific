package com.reactific.sbt

import com.typesafe.sbt.GitPlugin
import sbt._

object GitHelper extends AutoPluginHelper {

  def command(project: Project): Project = {
    project
      .enablePlugins(GitPlugin)
  }
}
