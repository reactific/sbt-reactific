/**********************************************************************************************************************
 *                                                                                                                    *
 * Copyright (c) 2015, Reactific Software LLC. All Rights Reserved.                                                   *
 *                                                                                                                    *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance     *
 * with the License. You may obtain a copy of the License at                                                          *
 *                                                                                                                    *
 *     http://www.apache.org/licenses/LICENSE-2.0                                                                     *
 *                                                                                                                    *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed   *
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for  *
 * the specific language governing permissions and limitations under the License.                                     *
 **********************************************************************************************************************/
package com.reactific.sbt

import sbt.Keys._
import sbt._
import scala.sys.process.Process

/** Commands Added To The Build */
object Commands extends AutoPluginHelper {

  import com.reactific.sbt.ReactificPlugin.autoImport._

  override def projectSettings: Seq[Setting[_]] = {
    Seq[Setting[_]](
      compileOnly := { compile_only.value },
      printClasspath := { print_class_path.value },
      printTestClasspath := { print_test_class_path.value },
      printRuntimeClasspath := { print_runtime_class_path.value },
      Keys.commands ++= Seq(shell_command, bang_command)
    )
  }

  def addCommandAliases(m: (String, String)*) = {
    val s = m.map(p => addCommandAlias(p._1, p._2)).reduce(_ ++ _)
    (_: Project).settings(s: _*)
  }

  override def globalSettings: Seq[Def.Setting[_]] = {
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

  def print_class_path = Def.task {
    val out = target.value
    val cp = (fullClasspath in Compile).value
    //val analysis= (compile in Compile).value
    println("----- Compile: " + out.getCanonicalPath + ": FILES:")
    println(cp.files.map(_.getCanonicalPath).mkString("\n"))
//    println("----- " + out.getCanonicalPath + ": All Binary Dependencies:")
//    println(analysis.readSourceInfos().getAllSourceInfos.asScala.values.head.)
//    println(analysis.readCompilations().getAllCompilations.toSeq.mkString("\n"))
    // println(analysis.relations.allBinaryDeps.toSeq.mkString("\n"))
    println("----- END")
    out
  }

  def print_test_class_path = Def.task {
    val out = target.value
    val cp = (fullClasspath in Test).value
    println("----- Test: " + out.getCanonicalPath + ": FILES:")
    println(cp.files.map(_.getCanonicalPath).mkString("\n"))
    println("----- END")
    out
  }

  def print_runtime_class_path = Def.task {
    val out = target.value
    val cp = (fullClasspath in Runtime).value
    println("----- Runtime: " + out.getCanonicalPath + ": FILES:")
    println(cp.files.map(_.getCanonicalPath).mkString("\n"))
    println("----- END")
    out
  }

  def compile_only = Def.task {
    val out = target.value
    // val comp = (compile in Compile).value
    println("Not Implemented Yet.")
    out
  }

  def shell_command = {
    Command.args("sh", "Invoke a system shell and pass arguments to it") {
      (state, args) =>
        Process(args).!; state
    }
  }

  def bang_command = {
    Command.args("!", "Invoke a system shell and pass arguments to it") {
      (state, args) =>
        Process(args).!; state
    }
  }
}
