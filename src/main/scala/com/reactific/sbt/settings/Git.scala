package com.reactific.sbt.settings

import com.reactific.sbt.AutoPluginHelper
import com.typesafe.sbt.GitPlugin
import sbt.{Setting, AutoPlugin}

/** An extension of GitPlugin to set it up */
object Git extends AutoPluginHelper {
  /** The AutoPlugins that we depend upon */
  override def autoPlugins: Seq[AutoPlugin] = Seq(GitPlugin)

  override def projectSettings : Seq[Setting[_]] = Seq (

  )
}
