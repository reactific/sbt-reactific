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

import sbt._
import sbt.Keys._
import com.typesafe.sbt.GitPlugin

/** ReactificPlugin Implementation */
object ReactificPlugin extends AutoPlugin {

  /** The list of helper objects in this package */
  val helpers: Seq[AutoPluginHelper] = {
    Seq(
      BuildInfoHelper,
      CommandsHelper,
      GitHelper,
      HeaderHelper,
      JavaCompilerHelper,
      MiscellaneousHelper,
      PackagingHelper,
      PublishingHelper,
      ReleaseHelper,
      ResolversHelper,
      ScalaCompilerHelper,
      ScalafmtHelper,
      ScalastyleHelper,
      SiteHelper,
      UnidocHelper
    )
  }

  /** Extract the AutoPlugins needed from the Helpers */
  val autoPlugins: Seq[AutoPlugin] = {
    helpers.flatMap(_.autoPlugins) :+ GitPlugin
  }

  override def requires: Plugins = {
    autoPlugins.foldLeft(empty) { (b, plugin) =>
      b && plugin
    }
  }

  object autoImport extends ReactificKeys {
    object config {
      val BuildInfo: BuildInfoHelper.type = BuildInfoHelper
      val Commands: CommandsHelper.type = CommandsHelper
      val Git: GitHelper.type = GitHelper
      val Header: HeaderHelper.type = HeaderHelper
      val JavaC: JavaCompilerHelper.type = JavaCompilerHelper
      val Miscellaneous: MiscellaneousHelper.type = MiscellaneousHelper
      val Packaging: PackagingHelper.type = PackagingHelper
      val Publishing: PublishingHelper.type = PublishingHelper
      val Release: ReleaseHelper.type = ReleaseHelper
      val Resolvers: ResolversHelper.type = ResolversHelper
      val ScalaC: ScalaCompilerHelper.type = ScalaCompilerHelper
      val Scalafmt: ScalafmtHelper.type = ScalafmtHelper
      val Scalastyle: ScalastyleHelper.type = ScalastyleHelper
      val Site: SiteHelper.type = SiteHelper
      val Unidoc: UnidocHelper.type = UnidocHelper
    }
  }

  def identificationSettings: Seq[Setting[_]] = {
    Seq(
      organization := "com.reactific",
      autoImport.copyrightHolderId := "admin@reactific.com",
      autoImport.copyrightHolderName := "Reactific Software LLC",
      autoImport.developerUrl := url("http://github.com/reactific")
    )
  }

  override def projectSettings: Seq[Def.Setting[_]] = {
    Defaults.coreDefaultSettings ++ helpers.flatMap(_.projectSettings)
  }

  override def buildSettings: Seq[Def.Setting[_]] = {
    super.buildSettings ++ identificationSettings ++
      helpers.flatMap(_.buildSettings)
  }

  override def globalSettings: Seq[Def.Setting[_]] = {
    super.globalSettings ++ helpers.flatMap(_.globalSettings)
  }
}
