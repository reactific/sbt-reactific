[![License](http://img.shields.io/:license-Apache%202-red.svg)](http://www.apache.org/licenses/LICENSE-2.0.txt)
[![Build Status](https://travis-ci.org/reactific/sbt-project.svg?branch=master)](https://travis-ci.org/reactific/sbt-project)
[![Release](https://img.shields.io/github/release/reactific/sbt-project.svg?style=flat)](https://github.com/reactific/sbt-project/releases)
[![Join the chat at https://gitter.im/reactific/sbt-project](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/reactific/sbt-project?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

# sbt-project
An SBT plugin to pull together often used plugins for Scala based Reactific software projects. This plugin was 
developed in an effort to keep SBT incantations DRY (Don't Repeat Yourself).  Most of the projects Reactific developers 
are similar in nature and use a common set of plugins to manage them. To elimnate redundancy and errors, this plugin 
brings together the common plugins, provides sane defaults and makes some decisions about the structure of the 
project. Users can still override the settings but if you agree with the defaults then it is very simple to use, 
just add this to your build.sbt:

```scala
import com.reactific.sbt.ProjectPlugin.autoImport._
enablePlugins(ProjectPlugin)
copyrightHolder := "Name Of Person Or Company Holding Copyrights"

copyrightYears := Seq(2015)

developerUrl := url("http://example.com/")

titleForDocs := "Your Documentation Title Here"

codePackage := "com.example.yo"
```

This bundles together the following sbt plugins for convenience: 
```scala
"org.scalatest" %% "scalatest" % "2.2.4" % "test",
"com.eed3si9n" % "sbt-buildinfo" % "0.5.0",
"com.eed3si9n" % "sbt-unidoc" % "0.3.3"
"com.etsy" % "sbt-compile-quick-plugin" % "0.5.3"
"com.github.gseitz" % "sbt-release" % "1.0.1"
"com.jsuereth" % "sbt-pgp" % "1.0.0"
"com.typesafe.sbt" % "sbt-git" % "0.8.4"
"com.typesafe.sbt" % "sbt-license-report" % "1.0.0"
"com.typesafe.sbt" % "sbt-site" % "0.8.2"
"de.heikoseeberger" % "sbt-header" % "1.5.1"
"org.scoverage" % "sbt-scoverage" % "1.0.1"
"org.scoverage" % "sbt-coveralls" % "1.0.0"
"org.xerial.sbt" % "sbt-sonatype" % "1.0"
```

And those plugins endow your project with these features:
* scalatest: behavior driven testing for scala
* sbt-buildinfo: Get the build time, date, version and other details into your software 
* sbt-unidoc: Scala and Java documentation unification
* sbt-compile-quick-plugin: Compile
* sbt-pgp: Certification and signing support for publishing releases
* sbt-git: Run git commands directly from sbt
* sbt-license-report: Find out what licenses your project utilizes 
* sbt-site: Site generation for your project
* sbt-header: Update file headers for open source licensing
* sbt-scoverage: Scala test coverage support
* sbt-coveralls: Report scala test coverage to coveralls
* sbt-sonatype: Publish your project to sonatype repository with "release" command

Additionally, sbt-project provides its own features:
* Running arbitrary shell commands from sbt using the "sh" or "!" command aliases
* Printing the run time, test time, and compile time class paths for diagnosing classpath issues
* Apache 2.0 license header support for scala, java, properties, conf, and xml file suffixes
