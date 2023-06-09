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

import java.time.Year

import de.heikoseeberger.sbtheader
import de.heikoseeberger.sbtheader.HeaderPlugin
import de.heikoseeberger.sbtheader.License.ALv2
import sbt.Keys._
import sbt._

object HeaderHelper extends AutoPluginHelper {

  /** The AutoPlugins that we depend upon */
  override def autoPlugins: Seq[AutoPlugin] = Seq(HeaderPlugin)

  private[sbt] val earliestStart = 2015

  def enable(project: Project): Project = {
    import ReactificPlugin.autoImport._
    val copyright = "Reactific Software LLC"
    import sbtheader.{CommentStyle, FileType}
    import HeaderPlugin.autoImport._
    project
      .enablePlugins(HeaderPlugin)
      .settings(
        Seq(
          checkHeaders := true,
          startYear := Some(earliestStart),
          headerLicense := {
            val years = startYear.value.get.toString + "-" + Year.now().toString
            Some(ALv2(years, copyright))
          },
          organizationName := copyrightHolderName.value,
          headerMappings ++= Map[FileType, CommentStyle](
            FileType.sh → CommentStyle.hashLineComment,
            FileType(".sbt") → CommentStyle.cStyleBlockComment,
            FileType(".xml") → CommentStyle.xmlStyleBlockComment,
            FileType(".scala.html") → CommentStyle.twirlStyleBlockComment,
            FileType(".conf") → CommentStyle.hashLineComment
          )
        )
      )
  }
}
