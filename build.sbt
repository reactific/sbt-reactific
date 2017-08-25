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

import ReleaseTransformations._


val defaultScmInfo = Def.setting {
  val gitUrl = "//github.com/reactific/" + normalizedName.value + ".git"
  ScmInfo(url("https:" ++ gitUrl), "scm:git:" ++ gitUrl, Some("https:" ++ gitUrl) )
}



// Libraries for the project we plug into
val dependencies = Seq (
  "org.apache.commons"  % "commons-lang3" % "3.5"
)


lazy val root = {
  (project in file("."))
    .enablePlugins(SbtPgp,GitPlugin,Sonatype,ScriptedPlugin,ReleasePlugin)
    .settings(
      name            := "sbt-reactific",
      sbtPlugin       := true,
      organization    := "com.reactific",
      scalaVersion    := "2.12.3",
      scalacOptions   ++= Seq("-deprecation", "-unchecked", "-feature", "-Xlint"),
      logLevel        := Level.Info,
      resolvers       ++= Seq(
        Resolver.sonatypeRepo("releases"),
        Resolver.sonatypeRepo("snapshots"),
        Resolver.bintrayRepo("sbt","sbt-plugin-releases"),
        Resolver.typesafeIvyRepo("releases"),
        "eclipse-jgit" at "http://download.eclipse.org/jgit/maven"
      ),
      libraryDependencies ++= Seq (
        "org.apache.commons"  % "commons-lang3" % "3.5",
        "org.slf4j" % "slf4j-simple" % "1.7.25"
      ),
  
      // Scripted - sbt plugin tests
      scriptedLaunchOpts := { scriptedLaunchOpts.value ++
        Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
      },
      scriptedBufferLog := false,
  
      // Release process
      releaseUseGlobalVersion := true,
      releaseVersionBump := sbtrelease.Version.Bump.Bugfix,
      releasePublishArtifactsAction := PgpKeys.publishSigned.value,
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
      ),
  
      // Publishing to sonatype
      Sonatype.SonatypeKeys.sonatypeProfileName := "com.reactific",
      publishTo := {
        val nexus = "https://oss.sonatype.org/"
        val snapshotsR = "snapshots" at nexus + "content/repositories/snapshots"
        val releasesR  = "releases"  at nexus + "service/local/staging/deploy/maven2"
        val resolver = if (isSnapshot.value) snapshotsR else releasesR
        Some(resolver)
      },
      publishMavenStyle := true,
      publishArtifact in Test := false,
      pomIncludeRepository := { _ => false },
      licenses := Seq("Apache2" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
      homepage := Some(new URL("https://github.com/reactific/" + normalizedName.value)),
      pomExtra in Global := {
        <developers>
          <developer>
            <id>reid-spencer</id>
            <name>Reid Spencer</name>
            <url>https://github.com/reid-spencer</url>
          </developer>
        </developers>
      }
    )
}

addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.6")
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "2.0")
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.1.0-M1")
addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.9.3")
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.7.0")
addSbtPlugin("com.eed3si9n" % "sbt-unidoc" % "0.4.1")
// addSbtPlugin("com.etsy" % "sbt-compile-quick-plugin" % "1.3.0")
// addSbtPlugin("com.typesafe.sbt" % "sbt-license-report" % "1.2.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "1.3.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.2.2")
addSbtPlugin("de.heikoseeberger" % "sbt-header" % "3.0.1")
// addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.0")
// addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.1.0")
