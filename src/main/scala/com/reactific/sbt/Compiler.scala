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

package com.reactific.sbt

import sbt.Keys._
import sbt._
import com.typesafe.sbt.JavaVersionCheckPlugin.autoImport._

/** Compiler Settings Needed */
object Compiler extends PluginSettings {
  val scalac_2_10_options = Seq(
    "-encoding", "UTF-8",             // Yes, this is 2 args
    "-language:existentials",         // Turn on existentials feature
    "-language:higherKinds",          // Turn on higher kinds feature
    "-language:implicitConversions",  // Turn on implicit conversions feature
    "-deprecation",                   // Warn about deprecated features
    "-explaintypes",                  // Explain type errors in more detail
    "-feature",                       // Warn about use of features that should be imported
    "-unchecked",                     // Enable additional warnings where generated code depends on assumptions.
    "-Xlint",                         // Turn on all linting warnings
    "-Xfatal-warnings",               // Warnings are fatal
    "-Xfuture",                       // Turn on future language features
    "-Ywarn-numeric-widen",           // Warn when numeric types are widened
    "-Ywarn-value-discard"            // Warn when non-Unit values are discarded
  )

  override def projectSettings : Seq[Setting[_]] = Seq(
    javaVersionPrefix in javaVersionCheck := Some("1.8"),
    javaOptions in test ++= Seq("-Xmx512m"),
    scalaVersion := "2.11.7",
    scalacOptions ++= scalac_2_10_options ++ Seq(
      "-target:jvm-1.8",                // Let's be modern
      "-Ywarn-unused",                  // Warn about unused variables
      "-Ywarn-unused-import"            // Warn about unused imports
    ),
    scalacOptions in (Compile, doc) ++=
      Seq("-feature", "-unchecked", "-deprecation", "-diagrams", "-implicits", "-skip-packages", "samples")
  )
}
