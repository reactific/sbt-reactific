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

import scala.language.postfixOps

/** General settings for the project */
object Miscellaneous extends AutoPluginHelper {

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

  def standardResolvers : Seq[Resolver] = Seq[Resolver](
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots"),
    Resolver.bintrayRepo("typesafe", "ivy-releases"),
    Resolver.bintrayRepo("sbt", "sbt-plugin-releases"),
    Resolver.bintrayRepo("scalaz", "releases")
  )

  override def projectSettings : Seq[sbt.Def.Setting[_]] = Defaults.coreDefaultSettings ++
    Seq(
      baseDirectory := thisProject.value.base,
      target := baseDirectory.value / "target",
      resolvers := standardResolvers,
      logLevel  := Level.Info,
      fork in Test := false,
      logBuffered in Test := false,
      shellPrompt := buildShellPrompt(version.value),
      mappings in(Compile, packageBin) ~= filter,
      mappings in(Compile, packageSrc) ~= filter,
      mappings in(Compile, packageDoc) ~= filter,
      libraryDependencies ++= Seq(
        "org.specs2" %% "specs2-core" % "3.6.6" % "test",
        "org.specs2" %% "specs2-junit" % "3.6.6" % "test"
      )
    )

  /** The AutoPlugins that we depend upon */
  override def autoPlugins: Seq[AutoPlugin] = Seq.empty[AutoPlugin]
}
