
import com.reactific.sbt.PublishUniversalPlugin
import com.reactific.sbt.ProjectPlugin
import com.reactific.sbt.AggregatingRootBuild
import com.reactific.sbt.settings._
import sbt.Keys._
import sbt._
import sbt.dsl._

object MultiBuild extends AggregatingRootBuild {

  override def rootName : Option[String] = Some("MultiBuildTest")

  override def settings = super.settings ++ Seq(
    maxErrors := 5,
    titleForDocs := "Yo!",
    codePackage := "com.reactific.yo"
  )
  lazy val p1 = project.in(file("p1")).
    enablePlugins(PublishUniversalPlugin)

  lazy val p2 = project.in(file("p2")).
    enablePlugins(PublishUniversalPlugin).
    dependsOn(p1)
}

