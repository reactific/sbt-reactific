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
import ProjectPlugin.autoImport._

/** Title Of Thing.
  *
  * Description of thing
  */
object Unidoc extends PluginSettings {
  import sbtunidoc.{Plugin => UnidocPlugin}
  override def projectSettings = UnidocPlugin.unidocSettings ++ Seq(
    scalacOptions in (Compile, doc) ++= Seq("-unchecked", "-deprecation", "-implicits"),
    scalacOptions in(Compile, doc) ++= Opts.doc.title(titleForDocs.value),
    scalacOptions in(Compile, doc) ++= Opts.doc.version(version.value),
    apiURL := Some(url("https://github.com/reactific/" + normalizedName.value + "/api/")),
    autoAPIMappings := true,
    apiMappings ++= {
      val cp: Seq[Attributed[File]] = (fullClasspath in Compile).value
      def findManagedDependency(organization: String, name: String): File = {
        ( for {
          entry <- cp
          module <- entry.get(moduleID.key)
          if module.organization == organization
          if module.name.startsWith(name)
          jarFile = entry.data
        } yield jarFile
          ).head
      }
      Map(
        findManagedDependency("org.scala-lang", "scala-library") → url(s"http://www.scala-lang.org/api/$scalaVersion/"),
        findManagedDependency("com.typesafe.akka", "akka-actor") → url(s"http://doc.akka.io/api/akka/"),
        findManagedDependency("com.typesafe", "config") → url("http://typesafehub.github.io/config/latest/api/"),
        findManagedDependency("joda-time", "joda-time") → url("http://joda-time.sourceforge.net/apidocs/")
      )
    }
  )
}
