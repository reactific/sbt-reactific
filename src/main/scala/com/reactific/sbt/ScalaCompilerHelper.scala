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
object ScalaCompilerHelper extends AutoPluginHelper {

  import ReactificPlugin.autoImport._

  val scalac_common_options = Seq(
    "-encoding", "UTF-8", // Yes, this is 2 args
    "-language:existentials", // Turn on existentials feature
    "-language:higherKinds", // Turn on higher kinds feature
    "-language:implicitConversions", // Turn on implicit conversions feature
    "-deprecation", // Warn about deprecated features
    "-explaintypes", // Explain type errors in more detail
    "-feature", // Warn about use of features that should be imported
    "-unchecked",
    // Enable additional warnings where generated code depends on assumptions.
    "-Xlint", // Turn on all linting warnings
    "-Xfuture", // Turn on future language features
    "-Ywarn-numeric-widen", // Warn when numeric types are widened
    "-Ywarn-value-discard", // Warn when non-Unit values are discarded
    "-Ywarn-dead-code", // Warn when dead code is identified.
    "-Ywarn-nullary-override",
    // Warn when non-nullary overrides nullary,
    // e.g. `def foo()` over `def foo`.
    "-Ywarn-nullary-unit" // Warn when nullary methods return Unit.
  )

  val scalac_2_10_options: Seq[String] =
    scalac_common_options ++ Seq("-target:jvm-1.7")

  val scalac_2_11_options: Seq[String] =
    scalac_common_options ++ Seq(
      "-target:jvm-1.8",
      "-Ywarn-infer-any", // Warn when a type argument is inferred to be `Any`.
      "-Ywarn-unused", // Warn about unused variables
      "-Ywarn-unused-import" // Warn about unused imports
    )

  val scalac_2_12_options: Seq[String] = scalac_common_options ++ Seq(
    "-Yno-adapted-args",
    // Do not adapt an argument list (either by inserting () or creating a
    // tuple) to match the receiver.
    "-Ypartial-unification",
    // Enable partial unification in type constructor inference
    "-Ywarn-extra-implicit",
    // Warn when more than one implicit parameter section is defined.
    "-Ywarn-inaccessible",
    // Warn about inaccessible types in method signatures.
    "-Ywarn-infer-any", // Warn when a type argument is inferred to be `Any`.
    "-Ywarn-unused:imports", // Warn if an import selector is not referenced.
    "-Ywarn-unused:implicits", // Warn if an implicit parameter is unused.
    "-Ywarn-unused:imports", // Warn if an import selector is not referenced.
    "-Ywarn-unused:locals", // Warn if a local definition is unused.
    "-Ywarn-unused:params", // Warn if a value parameter is unused.
    "-Ywarn-unused:patvars", // Warn if a variable bound in a pattern is unused.
    "-Ywarn-unused:privates" // Warn if a private member is unused.
  )

  def common(project: Project): Project = {
    project
      .settings(
        warningsAreErrors := true,
        scalaVersion := "2.12.14",
        scalacOptions ++= {
          {
            if (scalaVersion.value.startsWith("2.10")) {
              scalac_2_10_options
            } else if (scalaVersion.value.startsWith("2.11")) {
              scalac_2_11_options
            } else if (scalaVersion.value.startsWith("2.12")) {
              scalac_2_12_options
            } else {
              scalac_common_options
            }
          } ++ {
            if (warningsAreErrors.value) {
              Seq("-Xfatal-warnings")
            } else {
              Seq.empty[String]
            }
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
            "-language:implicitConversions" // Turn on implicit conversions
          )
        }
      )
  }
}
