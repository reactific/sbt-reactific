package com.reactific.sbt

import sbt.mavenint.PomExtraDependencyAttributes
import sbt._

/** A Little Help For AutoPlugins
  * This trait just provides some definitions and makes it easier to set up the plugin requirements. Just list
  * the plugins upon which your plugin is dependent in the autoPlugins method and the rest is taken care of.
  */
trait AutoPluginHelper extends AutoPlugin {

  /** A convenience type for configuration functions */
  type P2P = Project ⇒ Project

  /** The AutoPlugins that we depend upon */
  def autoPlugins : Seq[AutoPlugin]

  /** All Requirements Must Be Met */
  override def allRequirements = AllRequirements // must have all requirements satisfied to activate

  /** Require the AutoPlugin to be manually activated */
  override def trigger = noTrigger // requires manual activation via "enablePlugin(ProjectPlugin)

  override def requires = {
    // Depend upon all the AutoPlugin instances listed in autoPlugins
    autoPlugins.foldLeft(empty) { (b,plugin) => b && plugin }
  }

  def defaultSbtVersion = "0.13"
  def defaultScalaVersion = "2.10"

  def pluginModuleID(m: ModuleID, sbtV: String = defaultSbtVersion, scalaV: String = defaultScalaVersion) : ModuleID = {
    m.extra(
      PomExtraDependencyAttributes.SbtVersionKey -> sbtV,
      PomExtraDependencyAttributes.ScalaVersionKey -> scalaV
    ).
      copy(crossVersion = CrossVersion.Disabled)
  }
}
