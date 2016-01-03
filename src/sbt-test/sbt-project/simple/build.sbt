import com.reactific.sbt.ProjectPlugin.autoImport._

name := "hello-test"

scalaVersion := "2.11.6"

organization := "com.reactific"

maxErrors := 50

unmanagedResourceDirectories in compile := Seq(baseDirectory.value / "src/resources")

enablePlugins(ProjectPlugin)

copyrightHolder := "Reactific Software LLC"

copyrightYears := Seq(2015)

developerUrl := url("http://reactific.com/")

titleForDocs := "Yo!"

codePackage := "com.reactific.yo"

