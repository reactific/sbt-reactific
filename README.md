[![License](http://img.shields.io/:license-Apache%202-red.svg)](http://www.apache.org/licenses/LICENSE-2.0.txt)
[![Build Status](https://travis-ci.org/reactific/sbt-release.svg?branch=master)](https://travis-ci.org/reactific/sbt-reactific)
[![Release](https://img.shields.io/github/release/reactific/sbt-reactific.svg?style=flat)](https://github.com/reactific/sbt-reactific/releases)
[![Join the chat at https://gitter.im/reactific/sbt-reactific](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/reactific/sbt-reactific?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

# sbt-reactific
This is an SBT plugin we use at Reactific Software LLC to pull together often used plugins for 
Scala based Reactific software projects. This plugin was developed in an effort to keep SBT 
incantations DRY (Don't Repeat Yourself) and reduce workload on starting new projects. Most 
of the projects Reactific develops are similar in nature and use a common set of plugins and 
options to manage them. To elimnate redundancy and errors, this plugin brings together the 
common plugins, provides sane defaults and makes some decisions about the structure of the 
project. Users can still override the settings but if you agree with the defaults then it 
is very simple to use. 

# sbt 1.0 targeted
The sbt-reactific plugin is targeted towards the 1.0 release of sbt. That is, 
it expects you to define your project in an `build.sbt` file at the top level
of the project's directory structure. Currently, sbt-reactific is based on 
the 0.13.16 release of sbt using scala 2.10.6. When sbt 1.0 is released and 
all the plugins it depends on also support that release, sbt-reactific will 
be updated to meet all requirements of sbt 1.0. For example, it will at least
move from using scala 2.10 to using scala 2.12. Other changes may be 
necessary as well, depending on sbt 1.0's requirements. 


## Using The Plugin
This package builds an sbt autoplugin. You use it like you would any 
autoplugin, by putting something like this in your `project/plugins.sbt` file:

```sbtshell
addSbtPlugin("com.reactific" % "sbt-reactific" % "2.0.4" )
```

This should be resolvable on the Maven Central repository.

## Plugins Enabled
When you include and enable the ReactificPlugin, you will enable a variety of 
other plugins too. That set of plugins may determine whether you want to use 
this plugin or not. For Reactific, these are all required. For other 
companies, you probably want to provide your own plugin that adjusts the 
options provided by this one to simplify build specification in your projects.

Here are the plugins enabled by sbt-reactific: 
```sbtshell
addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.5")
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "2.0")
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.1")
addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.9.3")
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.7.0")
addSbtPlugin("com.eed3si9n" % "sbt-unidoc" % "0.4.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "1.2.1")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.2.0")
addSbtPlugin("de.heikoseeberger" % "sbt-header" % "2.0.0")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")
addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.1.0")
```

And those plugins endow your project with these features:
* sbt-release: Run a release procedure to release your open source software 
* sbt-sonatype: Publish your project to sonatype repository with "release" command
* sbt-git: Run git commands directly from sbt
* sbt-pgp: Certification and signing support for publishing releases to Sonatype
* sbt-buildinfo: Get the build time, date, version, build number and other details into your software
* sbt-unidoc: Scala and Java documentation unification
* sbt-compile-quick-plugin: Adds a quick compile (qc) command
* sbt-license-report: Find out what licenses your project utilizes 
* sbt-site: Site generation for your project
* sbt-native-packager: Packaging for various machines
* sbt-header: Update file headers for open source licensing
* sbt-scoverage: Scala test coverage support
* sbt-coveralls: Report scala test coverage to coveralls

Additionally, sbt-reactific provides its own features:
* Running arbitrary shell commands from sbt using the "sh" or "!" command aliases
* Providing an sbt prompt that includes the release and git branch in it
* Printing the run time, test time, and compile time class paths for diagnosing classpath issues
* Apache 2.0 license header support for scala, java, properties, conf, and xml file suffixes


## Configuring A One-Project Build
If your build has just one project, you should add something like the 
following into your `build.sbt`:

```sbtshell

name := "my-project"
scalaVersion := "2.12.2"
organization := "com.company"
copyrightHolder := "My Company, Inc."
startYear  := Some(2015)
developerUrl := url("http://company.com/")
titleForDocs := "Company Project"
codePackage := "com.company.project"
enablePlugins(ReactificPlugin)
```

## Configuring a Multi-Project Build
If you want to have multiple subprojects in your build then do something like 
this instead:
```sbtshell
val commonSettings = Seq(
  copyrightHolder := "My Company, Inc.",
  startYear  := Some(2015),
  developerUrl := url("http://company.com/"),
  titleForDocs := "Documentation For Project At Company",
  codePackage := "com.company.project"
)

lazy val p1 = (project in file("p1"))
  .enablePlugins(ReactificPlugin)
  .settings(commonSettings)
  
lazy val p2 = (project in file("p2"))
  .enablePlugins(ReactificPlugin)
  .dependsOn(p1)
  .settings(commonSettings)

```

## Configuration Keys Provided By sbt-reactific

Several keys are defined by the sbt-reactific project for convenience.

### codePackage (String)
This should be set to the main, top level Scala package name that contains 
all of the project's code. This is used in several places with the plugins.

### titleForDocs (String)
This is the title that should be used for the project's generated Java and 
Scala documentation.

### copyrightHolder (String)
This is the legal name of the entity or person that holds the copyright for 
the software. This is used in the license header for the software, and in the
documentation. This is separate from the sbt provided `organization` setting.

### developerUrl (URL)
This should be set to an URL that refers to the developer's home page.

### publishSnapshotsTo (Resolver)
This should be set to the URL where snapshot versions are published to. The 
default for this points to Reactific's repository on oss.sonatype.org and 
since you won't know the password for it, you'll need to set this to the 
repository where your snapshots ought to be published. 

### publishReleasesTo (Rsolver)
This should be set to the URL where release versions are published to. The 
default for this points to Reactific's repository on oss.sonatype.org and 
since you won't know the password for it, you'll need to set this to the 
repository where your release ought to be published. 

### warningsAreErrors (Boolean)
Here at Reactific we don't like warnings in our software so we have this key
defaulted to true. It causes all Scala and Java warnings to be treated as 
errors by the compiler. In other words, it will be impossible to build 
or release software that have any warnings in it. This has helped to produce 
higher quality software as many warnings are actually bugs waiting to happen.

### checkScalaStyle (Boolean)
When this flag is true, the `scalastyle` task will be run during the release
process to check the source code for style issues. Default is true.

### checkHeaders (Boolean)
When this flag is true, the `headerCheck` task will be run during the release
process to check that all source files have correct headers. Default is true.

### checkTests (Boolean)
When this flag is true, the `test` task will be run during the release 
process to check that all tests pass. Default is true.    

### runScalafmtWhenReleases (Boolean)
When this flag is true, the `scalafmt` task will be run during the release
process to reformat code to the standard form. Any changes will be checked in
with the version checkin. Default is false.  

### additionalCheckSteps (Seq[ReleaseStep])
Additional steps to add to the release process to check the artifact's 
correctness. These steps should generate errors if the checks fail and should
not modify the code in any way. 

## Prompt Style
The sbt-reactific plugin changes the sbt plugin to contain useful information.
For example, one of the test cases produces this prompt:

```
hello-test(simple) : master...origin/master : 0.1.2-SNAPSHOT>
``` 
* `hello-test` is the name of the project as specified in the build.sbt
* `simple` is the name of the root project
* `master...origin/master` is the git branch your project is using
* `0.1.2-SNAPSHOT` is the version you're currently building towards

This helps to make sure you know where you are when you're building or 
releasing the software.

## Commands & Aliases
The sbt-reactific plugin adds various commands and command aliases that you 
can use from the sbt prompt. We find these to be handy.

### git
Run any git command from within sbt. This allows you to manipulate the git 
repository without leaving sbt. 
```
hello-test(simple) : master...origin/master : 0.1.2-SNAPSHOT>git version
[info] git version 2.12.2
hello-test(simple) : master...origin/master : 0.1.2-SNAPSHOT>
```

### sh
Run any shell command from within sbt. This allows you to get information 
from the shell without leaving sbt. For example:
```
hello-test(simple) : master...origin/master : 0.1.2-SNAPSHOT>sh ls
build.sbt
project
src
target
test
version.sbt
```
### printClassPath
Prints the project's classpath 

```
hello-test(simple) : master...origin/master : 0.1.2-SNAPSHOT>printClassPath
[info] Compiling 1 Scala source to /Users/reid/Code/sbt-reactific/src/sbt-test/sbt-project/simple/target/scala-2.11/classes...
----- Compile: /Users/reid/Code/sbt-reactific/src/sbt-test/sbt-project/simple/target: FILES:
/Users/reid/Code/sbt-reactific/src/sbt-test/sbt-project/simple/target/scala-2.11/classes
/Users/reid/.ivy2/cache/org.scala-lang/scala-library/jars/scala-library-2.11.8.jar
----- /Users/reid/Code/sbt-reactific/src/sbt-test/sbt-project/simple/target: All Binary Dependencies:
/Library/Java/JavaVirtualMachines/jdk1.8.0_73.jdk/Contents/Home/jre/lib/rt.jar
/Users/reid/.ivy2/cache/org.scala-lang/scala-library/jars/scala-library-2.11.8.jar
----- END
[success] Total time: 1 s, completed Aug 5, 2017 1:36:40 PM
```

### printTestClassPath
Similar to printClassPath but prints the testing class path.

### printRuntimeClassPath
Similar to printClassPath but prints the runtime class path.

### tq
An alias for `test-quick` to re-test only failing test cases

### to
An alias for `test-only` to test specific SPEC tests

### cq
An alias for `compile-quick` (currently not implemented)

### copmile
An alias for `compile` because it gets mis-typed by this author so much.

### tset
An alias for `test` because it gets mis-typed by this author so much.

### TEST
An alias for `; clean ; test` 

### tc
An alias for `test:compile` to compile the test code and its dependencies

### ctc
An alias for `; clean ; test:compile` to do a clean recompilation of the test
code.

### cov
An alias for `; lean ; coverage ; test ; coverageAggregate ; reload` which is
 a sequence of commands needed for getting coverage data.

