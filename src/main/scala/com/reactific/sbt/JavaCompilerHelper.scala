/*
 * Copyright 2015-2017 Reactific Software LLC
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

import sbt.Keys._
import sbt._

/** Compiler Settings Needed */
object JavaCompilerHelper extends AutoPluginHelper {

  import com.reactific.sbt.ReactificPlugin.autoImport._

  val java_compile_options: Seq[String] = Seq[String](
    "-g",
    "-deprecation",
    "-encoding", "UTF-8",
    "-Xlint",
    "-Xdoclint:all",
    "-Xmaxerrs", "50",
    "-Xmaxwarns", "50",
    "-Xprefer:source"
  )

  def typical(project: Project): Project = {
    project
      .settings(
        warningsAreErrors := true,
        javaOptions in test ++= Seq("-Xmx512m"),
        javacOptions ++= java_compile_options ++ {
          if (warningsAreErrors.value) Seq("-Werror") else Seq.empty[String]
        }
      )
  }

}
