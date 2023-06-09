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

import com.reactific.sbt.ReactificPlugin.autoImport.privateNexusResolver
import sbt._
import sbt.Keys._

object ResolversHelper extends AutoPluginHelper {


  override def projectSettings: Seq[Setting[_]] = {
    import ReactificPlugin.autoImport._
    privateNexusResolver := None
  }

  private final val releases = "releases"

  val jgitRepo =
    "eclipse-jgit".at("http://download.eclipse.org/jgit/maven")

  private val standardResolvers: Seq[Resolver] = Seq[Resolver](
    Resolver.sonatypeRepo(releases),
    Resolver.bintrayRepo("typesafe", "ivy-releases"),
    Resolver.jcenterRepo,
    Resolver.sonatypeRepo("snapshots")
  )

  def standard(project: Project): Project = {
    project.settings(
      resolvers := Seq.empty[Resolver],
      externalResolvers := {
        Seq[Resolver](
          Resolver.file(
            "local",
            _root_.sbt.file(Path.userHome.absolutePath + "/.ivy2/local")
          )(Resolver.ivyStylePatterns),
          Resolver.mavenLocal,
          privateNexusResolver.value.getOrElse(Resolver.mavenCentral)
        ) ++ standardResolvers
      }
    )
  }

  def sbt(project: Project): Project = {
    project
      .configure(standard)
        .settings(
          externalResolvers +=
            Resolver.bintrayRepo("sbt", "sbt-plugin-releases")
        )
  }

  def scala(project: Project): Project = {
    project
      .configure(sbt)
      .settings(
        externalResolvers ++= Seq(
          Resolver.typesafeIvyRepo(releases),
          Resolver.bintrayRepo("scalaz", releases)
        )
      )
  }

  def all(project: Project): Project = {
    project
      .configure(scala)
  }
}
