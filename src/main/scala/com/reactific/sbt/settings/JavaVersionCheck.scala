package com.reactific.sbt.settings

import com.reactific.sbt.AutoPluginHelper
import com.typesafe.sbt.JavaVersionCheckPlugin
import com.typesafe.sbt.JavaVersionCheckPlugin.autoImport._
import sbt.{Setting, AutoPlugin}

/** Unit Tests For JavaVersionCheck */
object JavaVersionCheck extends AutoPluginHelper {

  /** The AutoPlugins that we depend upon */
  override def autoPlugins: Seq[AutoPlugin] = Seq(JavaVersionCheckPlugin)

  override def projectSettings : Seq[Setting[_]] = Seq(
    javaVersionPrefix in javaVersionCheck := Some("1.8")
  )
}
