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

/** Compiler Settings Needed */
object Compiler extends PluginSettings {
  override def projectSettings : Seq[Setting[_]] = Seq(
    scalaVersion := "2.11.7",
    javaOptions in test ++= Seq("-Xmx512m"),
    scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation", "-target:jvm-1.8"),
    scalacOptions in(Compile, doc) ++=
      Seq("-feature", "-unchecked", "-deprecation", "-diagrams", "-implicits", "-skip-packages", "samples")

  )
}
