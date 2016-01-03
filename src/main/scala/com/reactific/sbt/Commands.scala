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

/** Commands Added To The Build */
object Commands {

  def aliases : Seq[Def.Setting[(State => State)]] = {
    Seq(
      addCommandAlias("tq", "test-quick"),
      addCommandAlias("to", "test-only"),
      addCommandAlias("cq", "compile-quick"),
      addCommandAlias("copmile", "compile"),
      addCommandAlias("tset", "test"),
      addCommandAlias("cov", "; clean ; coverage ; test ; coverageAggregate"),
      addCommandAlias("!", "sh")
    ).flatten
  }

  def print_class_path = (target, fullClasspath in Compile, compile in Compile) map { (out, cp, analysis) =>
    println("----- Compile: " + out.getCanonicalPath + ": FILES:")
    println(cp.files.map(_.getCanonicalPath).mkString("\n"))
    println("----- " + out.getCanonicalPath + ": All Binary Dependencies:")
    println(analysis.relations.allBinaryDeps.toSeq.mkString("\n"))
    println("----- END")
    out
  }

  def print_test_class_path = (target, fullClasspath in Test).map { (out, cp) =>
    println("----- Test: " + out.getCanonicalPath + ": FILES:")
    println(cp.files.map(_.getCanonicalPath).mkString("\n"))
    println("----- END")
    out
  }
  def print_runtime_class_path = (target, fullClasspath in Runtime).map { (out, cp) =>
    println("----- Runtime: " + out.getCanonicalPath + ": FILES:")
    println(cp.files.map(_.getCanonicalPath).mkString("\n"))
    println("----- END")
    out
  }

  def compile_only = (target, compile in Compile) map { (out, compile) =>
    println("Not Implemented Yet.")
    out
  }

  def shell_command = Command.args("sh", "Invoke a system shell and pass arguments to it") { (state, args) =>
    args.mkString(" ").! ; state
  }
}
