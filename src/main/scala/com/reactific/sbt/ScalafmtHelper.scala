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

import scala.concurrent.duration._

import org.scalafmt.sbt.ScalafmtPlugin
import sbt._
import sbt.Keys._
import sbt.io.syntax.File

object ScalafmtHelper extends AutoPluginHelper {

  override def autoPlugins: Seq[AutoPlugin] = {
    Seq(ScalafmtPlugin)
  }

  val scalafmt_path: String = "/reactific/public/master/.scalafmt.conf"
  val scalafmt_conf: String = ".scalafmt.conf"

  def autoUpdateScalaFmtConf(
    scalafmtPath: String = scalafmt_path,
    maxAge: FiniteDuration = 1.day
  )(project: Project): Project = {
    project
      .enablePlugins(ScalafmtPlugin)
      .settings(
        update := {
          val log = streams.value.log
          val localFile: File = baseDirectory.value / scalafmt_conf
          updateFromPublicRepository(localFile, scalafmtPath, maxAge, log)
          update.value
        }
      )
  }

  def all(project: Project): Project = {
    project.configure(autoUpdateScalaFmtConf())
  }
}
