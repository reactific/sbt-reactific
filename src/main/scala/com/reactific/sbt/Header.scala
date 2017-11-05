/*
 * Copyright 2015-2017 Reactific Software LLC
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

object Header extends AutoPluginHelper {

  /** The AutoPlugins that we depend upon */
  override def autoPlugins: Seq[AutoPlugin] = Seq(HeaderPlugin)

  override def projectSettings: Seq[Setting[_]] = {
    val copyright = "Reactific Software LLC"
    import sbtheader.{CommentStyle, FileType}
    import HeaderPlugin.autoImport._
    Seq(
      startYear := Some(2015),
      headerLicense := {
        val years = startYear.value.get.toString + "-" + Year.now().toString
        Some(ALv2(years, copyright))
      },
      organizationName := ReactificPlugin.autoImport.copyrightHolder.value,
      headerMappings ++= Map[FileType, CommentStyle](
        FileType.sh → CommentStyle.HashLineComment,
        FileType(".sbt") → CommentStyle.CStyleBlockComment,
        FileType(".xml") → CommentStyle.XmlStyleBlockComment,
        FileType(".scala.html") → CommentStyle.TwirlStyleBlockComment,
        FileType(".conf") → CommentStyle.HashLineComment
      )
    )
  }
}
