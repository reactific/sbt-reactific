package com.reactific.sbt

import com.reactific.sbt.ReactificPlugin.helpers
import com.typesafe.sbt.GitPlugin
import de.heikoseeberger.sbtheader.HeaderPlugin
import de.heikoseeberger.sbtheader.License.ALv2
import sbt.Keys._
import sbt._

/*
object Apache2License extends License {

  val xmlBlockComment = """(?s)(<!--(?!--).*?-->(?:\n|\r|\r\n)+)(.*)""".r
  val xmlStyle = new ("<!--", "  --", "-->")

  CommentStyle.XmlStyleBlockComment

  override def apply(
    yyyy: String, copyrightOwner: String, commentStyle: String = "*"
  ) : CommentStyle = {
    val text = ALv2(yyyy, copyrightOwner).text
    commentStyle match {
      case "*" =>
        CommentStyle.CStyleBlockComment(text)
      case "#" => (hashLineComment, CommentBlock.hashLines(text))
      case "//" => (cppStyleLineComment, CommentBlock.cppStyle(text))
      case "<" ⇒ (xmlBlockComment, xmlStyle(text))
      case _ =>
        throw new IllegalArgumentException(
          s"Comment style '$commentStyle' not supported"
        )
    }
  }
}

 */

object Header extends AutoPluginHelper {

  /** The AutoPlugins that we depend upon */
  override def autoPlugins: Seq[AutoPlugin] = Seq(HeaderPlugin)

  override def projectSettings: Seq[Setting[_]] = {
    val years = "2015-2017"
    val copyright = "Reactific Software LLC"
    import HeaderPlugin.autoImport._
    Seq(
      headerLicense := Some(ALv2(years, copyright)),
      organizationName := ReactificPlugin.autoImport.copyrightHolder.value,
      startYear := Some(2015)
    )
  }
}
