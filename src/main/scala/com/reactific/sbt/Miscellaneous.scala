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

/** General settings for the project */
object Miscellaneous extends AutoPluginHelper {

  def filter(ms: Seq[(File, String)]): Seq[(File,String)] = {
    ms.filter {
      case (_, path) =>
        path != "logback.xml" &&
          !path.startsWith("toignore") &&
          !path.startsWith("samples")
    }
  }
  
  def currBranch: String = {
    import com.typesafe.sbt.git.JGit
    val jgit = JGit(new File("."))
    jgit.branch
  }
  
  def buildShellPrompt = Def.setting { (state: State) =>
    {
      val id = Project.extract(state).currentProject.id
      s"${name.value}($id) : $currBranch : ${version.value}>"
    }
  }

  def standardResolvers: Seq[Resolver] = Seq[Resolver](
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots"),
    Resolver.bintrayRepo("typesafe", "ivy-releases"),
    Resolver.bintrayRepo("sbt", "sbt-plugin-releases"),
    Resolver.bintrayRepo("scalaz", "releases")
  )

  override def projectSettings: Seq[sbt.Def.Setting[_]] =
    Defaults.coreDefaultSettings ++
      Seq(
        baseDirectory := thisProject.value.base,
        target := baseDirectory.value / "target",
        resolvers := standardResolvers,
        logLevel := Level.Info,
        fork in Test := false,
        logBuffered in Test := false,
        shellPrompt := buildShellPrompt.value,
        unmanagedJars in Compile :=  {
          baseDirectory.map { base =>
            (base / "libs" ** "*.jar").classpath
          }.value
        },
        unmanagedJars in Runtime := {
          baseDirectory.map { base =>
            (base / "libs" ** "*.jar").classpath
          }.value
        },
        unmanagedJars in Test := {
          baseDirectory.map { base =>
            (base / "libs" ** "*.jar").classpath
          }.value
        },
        mappings in (Compile, packageBin) ~= filter,
        mappings in (Compile, packageSrc) ~= filter,
        mappings in (Compile, packageDoc) ~= filter
      )
}
