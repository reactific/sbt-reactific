package com.reactific.sbt

import com.reactific.sbt.settings._
import com.typesafe.sbt.{GitPlugin}
import sbt.AutoPlugin

object SubProjectPlugin extends SubProjectPluginTrait

trait SubProjectPluginTrait extends AutoPluginHelper {

  /** The list of AutoPlugins that a SubProject Needs */
  def autoPlugins : Seq[AutoPlugin] = Seq(
    Commands, CompileQuick, settings.Compiler,
    JavaVersionCheck, GitPlugin, Miscellaneous, Site, Unidoc
  )
}
