package com.reactific.sbt

import com.reactific.sbt.ReactificPlugin.autoImport.privateNexusResolver
import sbt._
import sbt.Keys.externalResolvers

object ResolversHelper extends AutoPluginHelper {


  override def projectSettings: Seq[Setting[_]] = {
    import ReactificPlugin.autoImport._
    privateNexusResolver := None
  }

  private final val releases = "releases"

  val jgitRepo =
    "eclipse-jgit".at("http://download.eclipse.org/jgit/maven")

  private val standardResolvers: Seq[Resolver] = Seq[Resolver](
    Resolver.sonatypeRepo(releases),
    Resolver.bintrayRepo("typesafe", "ivy-releases"),
    Resolver.jcenterRepo,
    Resolver.sonatypeRepo("snapshots")
  )

  def standard(project: Project): Project = {
    project.settings(
      externalResolvers := {
        Seq[Resolver](
          Resolver.file(
            "local",
            _root_.sbt.file(Path.userHome.absolutePath + "/.ivy2/local")
          )(Resolver.ivyStylePatterns),
          Resolver.mavenLocal
        ) ++ {
          Seq(privateNexusResolver.value.getOrElse(Resolver.mavenCentral))
        } ++ standardResolvers
      }
    )
  }

  def sbt(project: Project): Project = {
    project
      .configure(standard)
        .settings(
          externalResolvers +=
            Resolver.bintrayRepo("sbt", "sbt-plugin-releases")
        )
  }

  def scala(project: Project): Project = {
    project
      .configure(sbt)
      .settings(
        externalResolvers ++= Seq(
          Resolver.bintrayRepo("sbt", "sbt-plugin-releases"),
            Resolver.typesafeIvyRepo(releases),
        Resolver.bintrayRepo("scalaz", releases)
        )
      )
  }

  def all(project: Project): Project = {
    project
      .configure(scala)
  }
}
