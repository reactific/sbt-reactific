package com.reactific.sbt.settings

import scala.util.matching.Regex

import de.heikoseeberger.sbtheader.HeaderPattern
import de.heikoseeberger.sbtheader.license.{Apache2_0, CommentBlock, License}

object Apache2License extends License {
  import HeaderPattern._

  val xmlBlockComment = """(?s)(<!--(?!--).*?-->(?:\n|\r|\r\n)+)(.*)""".r
  val xmlStyle = new CommentBlock("<!--", "  --", "-->")

  def createLicenseText(yyyy: String, copyrightOwner: String): String = {
    Apache2_0.createLicenseText(yyyy, copyrightOwner)
  }

  override def apply(
    yyyy: String, copyrightOwner: String, commentStyle: String = "*"
  ) : (Regex, String) = {
    val text = createLicenseText(yyyy, copyrightOwner)
    commentStyle match {
      case "*" => (cStyleBlockComment, CommentBlock.cStyle(text))
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
