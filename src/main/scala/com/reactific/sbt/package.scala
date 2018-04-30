package com.reactific

import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.time.Instant
import scala.concurrent.duration.FiniteDuration
import scala.io.Source

import _root_.sbt._
import _root_.sbt.internal.util.ManagedLogger

package object sbt {

  /** A convenience type for configuration functions */
  type P2P = Project ⇒ Project

  def updateFromPublicRepository(
    local: File,
    remote: String, // start with github organization or user
    maxAge: FiniteDuration,
    log: ManagedLogger
  ): Unit = {
    val url = new URL("https", "raw.githubusercontent.com", remote)
    val conn: HttpURLConnection =
      url.openConnection().asInstanceOf[HttpURLConnection]
    val timeout = 30000
    conn.setConnectTimeout(timeout)
    conn.setReadTimeout(timeout)
    conn.setInstanceFollowRedirects(true)
    conn.connect()
    val status = conn.getResponseCode
    val message = conn.getResponseMessage
    val lastModified =
      conn.getHeaderFieldDate("Last-Modified", Instant.now().toEpochMilli)

    if (status != 200) {
      log.warn(
        s"Failed to get ${url.toExternalForm}: HTTP $status: $message"
      )
    }
    val cutOff = Instant.now().toEpochMilli - maxAge.toMillis
    if (!local.exists() || lastModified > cutOff) {
      Option(conn.getContent()) match {
        case Some(is: InputStream) =>
          val content = Source.fromInputStream(is).mkString
          val pw = new java.io.PrintWriter(local)
          try {
            pw.write(content)
          } finally {
            pw.close()
          }
          log.info(
            s"Updated .scalafmt.conf from ${url.toExternalForm
            }: HTTP $status $message ${Instant.ofEpochMilli(lastModified)}"
          )
        case Some(x: Any) =>
          throw new IllegalStateException(
            s"Wrong content type for ${url.toExternalForm}: ${x.getClass}."
          )
        case None =>
          throw new IllegalStateException(
            s"No content from ${url.toExternalForm}"
          )
      }
    }
  }
}
