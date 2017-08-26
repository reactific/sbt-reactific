package com.reactific.sbt

import sbt._
import sbt.Keys._
import org.scalastyle.sbt.ScalastylePlugin
import org.scalastyle.sbt.ScalastylePlugin.autoImport._

/** Unit Tests For Scalastyle */
object Scalastyle  extends AutoPluginHelper {
  
  /** The AutoPlugins that we depend upon */
  override def autoPlugins: Seq[AutoPlugin] = {
    Seq(ScalastylePlugin)
  }
  
  /** The Settings to add in the scope of each project that activates this
   * AutoPlugin.
   */
  override def projectSettings: Seq[Setting[_]] = {
    Seq(
      scalastyleConfig :=
        baseDirectory.value / "project" / "scalastyle-config.xml",
      scalastyleFailOnError := true
    )
  }
}
