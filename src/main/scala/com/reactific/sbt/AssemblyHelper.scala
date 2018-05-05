package com.reactific.sbt

import sbt._
import sbtassembly.AssemblyPlugin
import sbtassembly.AssemblyPlugin.autoImport._
import sbtassembly.MergeStrategy
import sbtassembly.PathList

object AssemblyHelper extends AutoPluginHelper {
  
  def all: Project => Project = { (project: Project) =>
    project
      .enablePlugins(AssemblyPlugin)
      .settings(assemblyMergeStrategy in assembly := {
        case PathList("META-INF", "io.netty.versions.properties") =>
          MergeStrategy.discard
        case x: Any =>
          val oldStrategy = (assemblyMergeStrategy in assembly).value
          oldStrategy(x)
      })
  }
}
