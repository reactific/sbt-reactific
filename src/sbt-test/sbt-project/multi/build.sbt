

maxErrors := 5


lazy val p1 = (project in file("p1"))
  .enablePlugins(ReactificPlugin)
  .settings(
    titleForDocs := "Yo!",
    codePackage := "com.reactific.yo"
  )
  
lazy val p2 = (project in file("p2"))
  .enablePlugins(ReactificPlugin)
  .dependsOn(p1)
  .settings(
      titleForDocs := "Yo!",
      codePackage := "com.reactific.yo"
    )
