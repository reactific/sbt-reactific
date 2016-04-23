package com.reactific.sbt.settings

import com.reactific.sbt.AutoPluginHelper
import de.heikoseeberger.sbtheader.HeaderPlugin
import sbt._

object Header extends AutoPluginHelper {

  /** The AutoPlugins that we depend upon */
  override def autoPlugins: Seq[AutoPlugin] = Seq(HeaderPlugin)

  override def projectSettings: Seq[Setting[_]] = {
    Seq (
      HeaderPlugin.autoImport.headers := Map(
        "scala" -> Apache2License("2015,2016", "Reactific Software LLC"),
        "java" -> Apache2License("2015,2016", "Reactific Software LLC"),
        "conf" -> Apache2License("2015,2016", "Reactific Software LLC", "#"),
        "sbt" → Apache2License("2015,2016", "Reactific Software LLC", "#"),
        "properties" → Apache2License("2015,2016", "Reactific Software LLC", "#"),
        "xml" → Apache2License("2015,2016", "Reactific Software LLC", "<")
      )
    )
  }

}
