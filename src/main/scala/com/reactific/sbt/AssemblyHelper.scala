/*
 * Copyright 2015-2023 Reactific Software LLC
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
