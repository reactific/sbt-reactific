/**********************************************************************************************************************
 *                                                                                                                    *
 * Copyright (c) 2015, Reactific Software LLC. All Rights Reserved.                                                   *
 *                                                                                                                    *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance     *
 * with the License. You may obtain a copy of the License at                                                          *
 *                                                                                                                    *
 *     http://www.apache.org/licenses/LICENSE-2.0                                                                     *
 *                                                                                                                    *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed   *
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for  *
 * the specific language governing permissions and limitations under the License.                                     *
 **********************************************************************************************************************/

package com.reactific.sbt.settings

import com.reactific.sbt.AutoPluginHelper
import sbt.Keys._
import sbt._

/** Compiler Settings Needed */
object Compiler extends AutoPluginHelper {

  object autoImport {
  }

  /** The AutoPlugins that we depend upon */
  override def autoPlugins: Seq[AutoPlugin] = Seq.empty[AutoPlugin]

  val java_compile_options = Seq[String](
    "-g",
    "-deprecation",
    "-encoding", "UTF-8",
    "-Xlint",
    "-Xdoclint:all",
    "-Xmaxerrs", "50",
    "-Xmaxwarns", "50",
    "-Xprefer:source"
  )

  val scalac_common_options = Seq(
    "-encoding", "UTF-8",             // Yes, this is 2 args
    "-language:existentials",         // Turn on existentials feature
    "-language:higherKinds",          // Turn on higher kinds feature
    "-language:implicitConversions",  // Turn on implicit conversions feature
    "-deprecation",                   // Warn about deprecated features
    "-explaintypes",                  // Explain type errors in more detail
    "-feature",                       // Warn about use of features that should be imported
    "-unchecked",                     // Enable additional warnings where generated code depends on assumptions.
    "-Xlint",                         // Turn on all linting warnings
    "-Xfuture",                       // Turn on future language features
    "-Ywarn-numeric-widen",           // Warn when numeric types are widened
    "-Ywarn-value-discard",           // Warn when non-Unit values are discarded
    "-Ywarn-dead-code",               // Warn when dead code is identified.
    "-Ywarn-nullary-override",        // Warn when non-nullary overrides nullary, e.g. `def foo()` over `def foo`.
    "-Ywarn-nullary-unit"             // Warn when nullary methods return Unit.
  )
  val scalac_2_10_options = scalac_common_options ++ Seq(
    "-target:jvm-1.7"
  )

  val scalac_2_11_options = scalac_common_options ++ Seq(
    "-target:jvm-1.8",
    "-Ywarn-infer-any",               // Warn when a type argument is inferred to be `Any`.
    "-Ywarn-unused",                  // Warn about unused variables
    "-Ywarn-unused-import"            // Warn about unused imports
  )

  override def projectSettings : Seq[Setting[_]] = Seq(
    warningsAreErrors := true,
    javaOptions in test ++= Seq("-Xmx512m"),
    javacOptions ++= java_compile_options ++ Seq(
      { if (warningsAreErrors.value) "-Werror" else "" }
    ),
    scalaVersion := "2.11.8",
    // ivyScala  := ivyScala.value map {_.copy(overrideScalaVersion = true)},
    scalacOptions ++= {
      {
        if (scalaVersion.value.startsWith("2.10")) scalac_2_10_options else scalac_2_11_options
      } ++ {
        if (warningsAreErrors.value) Seq("-Xfatal-warnings") else Seq.empty[String]
      }
    },
    scalacOptions in (Compile, doc) ++= {
      Opts.doc.title(titleForDocs.value) ++
      Opts.doc.version(version.value) ++ Seq(
        "-feature",
        "-unchecked",
        "-deprecation",
        "-diagrams",
        "-explaintypes",
        "-language:existentials", // Turn on existentials feature
        "-language:higherKinds", // Turn on higher kinds feature
        "-language:implicitConversions" // Turn on implicit conversions feature
      )
    }
   )
}
