/*
 * Copyright 2015-2023 Reactific Software LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.reactific.sbt

import sbt.Keys._
import sbt._
import scala.sys.process.Process

/** Commands Added To The Build */
object CommandsHelper extends AutoPluginHelper {

  def handyAliases(project: Project): Project = {
    project.settings {
      Seq(
        addCommandAlias("tq", "test-quick"),
        addCommandAlias("to", "test-only"),
        addCommandAlias("cq", "compile-quick"),
        addCommandAlias("copmile", "compile"),
        addCommandAlias("tset", "test"),
        addCommandAlias("TEST", "; clean ; test"),
        addCommandAlias("tc", "test:compile"),
        addCommandAlias("ctc", "; clean ; test:compile"),
        addCommandAlias(
          "cov",
          "; clean ; coverage ; test ; coverageAggregate ; reload"
        ),
        addCommandAlias("!", "sh")
      ).flatten
    }
  }

  private[sbt] val printClasspath = TaskKey[File](
    "print-class-path",
    "Print the project's compilation class path."
  )

  private[sbt] val printTestClasspath = TaskKey[File](
    "print-test-class-path",
    "Print the project's testing class path."
  )

  private[sbt] val printRuntimeClasspath = TaskKey[File](
    "print-runtime-class-path",
    "Print the project's runtime class path."
  )

  private[sbt] val compileOnly =
    TaskKey[File]("compile-only", "Compile just the specified files")

  def classPathCommands(project: Project): Project = {
    project.settings(
      printClasspath := { print_class_path.value },
      printTestClasspath := { print_test_class_path.value },
      printRuntimeClasspath := { print_runtime_class_path.value },
    )
  }

  def shellCommands(project: Project): Project = {
    project.settings(commands ++= Seq(shell_command, bang_command))
  }

  def allCommands(project: Project): Project = {
    project
      .configure(shellCommands)
      .configure(classPathCommands)
      .configure(handyAliases)
  }

  override def projectSettings: Seq[Setting[_]] = {
    Seq.empty[Setting[_]]
  }

  private def addCommandAliases(m: (String, String)*): Unit = {
    val s = m.map(p => addCommandAlias(p._1, p._2)).reduce(_ ++ _)
    (_: Project).settings(s: _*)
  }

  private def print_class_path: Def.Initialize[Task[File]] = Def.task {
    val out = target.value
    val cp = (fullClasspath in Compile).value
    println("----- Compile: " + out.getCanonicalPath + ": FILES:")
    println(cp.files.map(_.getCanonicalPath).mkString("\n"))
    println("----- END")
    out
  }

  private def print_test_class_path: Def.Initialize[Task[File]] = Def.task {
    val out = target.value
    val cp = (fullClasspath in Test).value
    println("----- Test: " + out.getCanonicalPath + ": FILES:")
    println(cp.files.map(_.getCanonicalPath).mkString("\n"))
    println("----- END")
    out
  }

  private def print_runtime_class_path: Def.Initialize[Task[File]] = Def.task {
    val out = target.value
    val cp = (fullClasspath in Runtime).value
    println("----- Runtime: " + out.getCanonicalPath + ": FILES:")
    println(cp.files.map(_.getCanonicalPath).mkString("\n"))
    println("----- END")
    out
  }

  private def compile_only: Def.Initialize[Task[File]] = Def.task {
    val out = target.value
    // val comp = (compile in Compile).value
    println("Not Implemented Yet.")
    out
  }

  private def shell_command: Command = {
    Command.args("sh", "Invoke a system shell and pass arguments to it") {
      (state, args) =>
        Process(args).!; state
    }
  }

  private def bang_command: Command = {
    Command.args("!", "Invoke a system shell and pass arguments to it") {
      (state, args) =>
        Process(args).!; state
    }
  }
}
