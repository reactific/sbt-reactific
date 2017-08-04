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
import sbtunidoc.ScalaUnidocPlugin

/** Plugin Settings For UniDoc, since it is not an AutoPlugin */
object Unidoc extends AutoPluginHelper {

  /** The AutoPlugins that we depend upon */
  override def autoPlugins: Seq[AutoPlugin] = Seq(ScalaUnidocPlugin)

  def knownApiMappings = Map (
    ("org.scala-lang", "scala-library") → url(s"http://www.scala-lang.org/api/$scalaVersion/"),
    ("com.typesafe.akka", "akka-actor") → url(s"http://doc.akka.io/api/akka/"),
    ("com.typesafe", "config") → url("http://typesafehub.github.io/config/latest/api/"),
    ("joda-time", "joda-time") → url("http://joda-time.sourceforge.net/apidocs/")
  )

  override def projectSettings = ScalaUnidocPlugin.projectSettings ++ Seq(
    apiURL := Some(url("https://github.com/reactific/" + normalizedName.value + "/api/")),
    autoAPIMappings := true,
    apiMappings ++= {
      val cp: Seq[Attributed[File]] = (fullClasspath in Compile).value
      def findManagedDependency(organization: String, name: String): Option[File] = {
        ( for {
            entry <- cp
            module <- entry.get(moduleID.key)
            if module.organization == organization
            if module.name.startsWith(name)
            jarFile = entry.data
          } yield jarFile
        ).headOption
      }
      for (
        ((org,lib), url) <- knownApiMappings ;
        dep = findManagedDependency(org, lib) if dep.isDefined
      ) yield {
        dep.get -> url
      }
    }
  )
}
