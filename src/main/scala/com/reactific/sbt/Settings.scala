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

import scala.language.postfixOps

/** Title Of Thing.
  *
  * Description of thing
  */
object Settings extends PluginSettings {

  val filter = { (ms: Seq[(File, String)]) =>
    ms filter {
      case (file, path) =>
        path != "logback.xml" && !path.startsWith("toignore") && !path.startsWith("samples")
    }
  }

  object devnull extends ProcessLogger {
    def info(s: => String) {}

    def error(s: => String) {}

    def buffer[T](f: => T): T = f
  }

  def currBranch = (
    ("git status -sb" lines_! devnull headOption)
      getOrElse "-" stripPrefix "## ")

  def buildShellPrompt(version: String) = {
    (state: State) => {
      val currProject = Project.extract(state).currentProject.id
      "%s : %s : %s> ".format( currProject, currBranch, version )
    }
  }


  override def projectSettings : Seq[sbt.Def.Setting[_]] = Defaults.coreDefaultSettings ++
    Seq(
      scalacOptions in (Compile, doc) ++= Opts.doc.title(ProjectPlugin.autoImport.titleForDocs.value),
      scalacOptions in (Compile, doc) ++= Opts.doc.version(version.value),
      fork in Test := false,
      logBuffered in Test := false,
      ivyScala := ivyScala.value map {_.copy(overrideScalaVersion = true)},
      shellPrompt := buildShellPrompt(version.value),
      mappings in(Compile, packageBin) ~= filter,
      mappings in(Compile, packageSrc) ~= filter,
      mappings in(Compile, packageDoc) ~= filter
    )
}
