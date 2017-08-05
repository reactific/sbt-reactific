
name := "hello-test"

scalaVersion := "2.11.8"

organization := "com.reactific"

maxErrors := 50

unmanagedResourceDirectories in compile := Seq(baseDirectory.value / "src/resources")

enablePlugins(ReactificPlugin)

copyrightHolder := "Reactific Software LLC"

startYear  := Some(2015)

developerUrl := url("http://reactific.com/")

titleForDocs := "Yo!"

codePackage := "com.reactific.yo"

