/*
 * Copyright (c) 2015, Reactific Software LLC. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed  on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for  the specific language governing permissions and limitations under the License.
 */

import java.time.Year

import ReleaseTransformations._
import de.heikoseeberger.sbtheader.HeaderPlugin
import de.heikoseeberger.sbtheader.License.ALv2

val defaultScmInfo = Def.setting {
  val gitUrl = "//github.com/reactific/" + normalizedName.value + ".git"
  ScmInfo(
    url("https:" ++ gitUrl),
    "scm:git:" ++ gitUrl,
    Some("https:" ++ gitUrl)
  )
}

// Libraries for the project we plug into
val dependencies = Seq("org.apache.commons" % "commons-lang3" % "3.5")

enablePlugins(
  SbtPgp,
  GitPlugin,
  Sonatype,
  ReleasePlugin,
  ScriptedPlugin,
  ScalastylePlugin,
  HeaderPlugin
)

sbtPlugin := true
name := "sbt-reactific"
organization := "com.reactific"
scalaVersion := "2.12.4"
scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-Xlint")
logLevel := Level.Info
resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots"),
  Resolver.bintrayRepo("sbt", "sbt-plugin-releases"),
  Resolver.typesafeIvyRepo("releases"),
  "eclipse-jgit".at("http://download.eclipse.org/jgit/maven")
)
libraryDependencies ++= Seq(
  "org.apache.commons" % "commons-lang3" % "3.5",
  "org.slf4j" % "slf4j-simple" % "1.7.25"
)
// Scripted - sbt plugin tests
scriptedLaunchOpts := {
  scriptedLaunchOpts.value ++
    Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
}
scriptedBufferLog := false
// Scalastyle
scalastyleConfig :=
  baseDirectory.value / "project" / "scalastyle-config.xml"
// Header
startYear := Some(2015)
headerLicense := {
  val years = startYear.value.get.toString + "-" + Year.now().toString
  Some(ALv2(years, "Reactific Software LLC"))
}
// Release process
releaseUseGlobalVersion := true
releaseVersionBump := sbtrelease.Version.Bump.Bugfix
releasePublishArtifactsAction := PgpKeys.publishSigned.value
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  releaseStepCommand("scripted"),
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommand("packageBin"),
  publishArtifacts,
  setNextVersion,
  commitNextVersion,
  releaseStepCommand("sonatypeReleaseAll"),
  pushChanges
)
// Publishing to sonatype
Sonatype.SonatypeKeys.sonatypeProfileName := "com.reactific"
publishTo := {
  val nexus = "https://oss.sonatype.org/"
  val snapshotsR =
    "snapshots".at(nexus + "content/repositories/snapshots")
  val releasesR =
    "releases".at(nexus + "service/local/staging/deploy/maven2")
  val resolver = if (isSnapshot.value) snapshotsR else releasesR
  Some(resolver)
}
publishMavenStyle := true
publishArtifact in Test := false
pomIncludeRepository := { _ =>
  false
}
licenses :=
  Seq("Apache2" -> url("http://www.apache.org/licenses/LICENSE-2.0"))
homepage :=
  Some(new URL("https://github.com/reactific/" + normalizedName.value))
pomExtra in Global := {
  // scalastyle:off
  <developers>
    <developer>
      <id>reid-spencer</id>
      <name>Reid Spencer</name>
      <url>https://github.com/reid-spencer</url>
    </developer>
  </developers>
  // scalastyle:on
}

addSbtPlugin("com.github.gseitz" % "sbt-release" % Ver.sbt_release)
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % Ver.sbt_sonatype)
addSbtPlugin("com.jsuereth" % "sbt-pgp" % Ver.sbt_pgp)
addSbtPlugin("com.typesafe.sbt" % "sbt-git" % Ver.sbt_git)
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % Ver.sbt_buildinfo)
addSbtPlugin("com.eed3si9n" % "sbt-unidoc" % Ver.sbt_unidoc)
addSbtPlugin("com.typesafe.sbt" % "sbt-site" % Ver.sbt_site)
addSbtPlugin(
  "com.typesafe.sbt" % "sbt-native-packager" % Ver.sbt_native_packager
)
addSbtPlugin("de.heikoseeberger" % "sbt-header" % Ver.sbt_header)
addSbtPlugin(
  "org.scalastyle" %% "scalastyle-sbt-plugin" % Ver.scalastyle_sbt_plugin
)
addSbtPlugin("org.scoverage" % "sbt-scoverage" % Ver.sbt_scoverage)
addSbtPlugin("org.scoverage" % "sbt-coveralls" % Ver.sbt_coveralls)
addSbtPlugin("com.geirsson" % "sbt-scalafmt" % Ver.sbt_scalafmt)
