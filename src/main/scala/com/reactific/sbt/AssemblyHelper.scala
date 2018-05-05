package com.reactific.sbt

import sbt._

object AssemblyHelper extends AutoPluginHelper {
  
  def all: Project => Project = { (project: Project) =>
    project
      .enablePlugins(AssemblyPlugin)
      .settings(assemblyMergeStrategy in assembly := {
        case PathList("META-INF", "io.netty.versions.properties") =>
          MergeStrategy.discard
        case x =>
          val oldStrategy = (assemblyMergeStrategy in assembly).value
          oldStrategy(x)
      })
  }
}
