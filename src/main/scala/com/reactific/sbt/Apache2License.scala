package com.reactific.sbt

import de.heikoseeberger.sbtheader.HeaderPattern
import de.heikoseeberger.sbtheader.license.License

object Apache2License extends License {
  import HeaderPattern._

  val xmlBlockComment = """(?s)(<!--(?!--).*?-->(?:\n|\r|\r\n)+)(.*)""".r

  override def apply(yyyy: String, copyrightOwner: String, commentStyle: String = "*") = {
    commentStyle match {
      case "*" =>
        (
          cStyleBlockComment,
          s"""|/*
              | * Copyright © $yyyy $copyrightOwner. All Rights Reserved.
              | *
              | * Licensed under the Apache License, Version 2.0 (the "License");
              | * you may not use this file except in compliance with the License.
              | * You may obtain a copy of the License at
              | *
              | *     http://www.apache.org/licenses/LICENSE-2.0
              | *
              | * Unless required by applicable law or agreed to in writing, software
              | * distributed under the License is distributed on an "AS IS" BASIS,
              | * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
              | * See the License for the specific language governing permissions and
              | * limitations under the License.
              | */
              |
              |""".stripMargin
          )
      case "#" =>
        (
          hashLineComment,
          s"""|# Copyright © $yyyy $copyrightOwner. All Rights Reserved.
              |#
              |# Licensed under the Apache License, Version 2.0 (the "License");
              |# you may not use this file except in compliance with the License.
              |# You may obtain a copy of the License at
              |#
              |#     http://www.apache.org/licenses/LICENSE-2.0
              |#
              |# Unless required by applicable law or agreed to in writing, software
              |# distributed under the License is distributed on an "AS IS" BASIS,
              |# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
              |# See the License for the specific language governing permissions and
              |# limitations under the License.
              |
              |""".stripMargin
          )
      case "<" ⇒
        (
          xmlBlockComment,
          s"""|<!--
              | - Copyright © $yyyy $copyrightOwner. All Rights Reserved.
              | -
              | - Licensed under the Apache License, Version 2.0 (the "License");
              | - you may not use this file except in compliance with the License.
              | - You may obtain a copy of the License at
              | -
              | -     http://www.apache.org/licenses/LICENSE-2.0
              | -
              | - Unless required by applicable law or agreed to in writing, software
              | - distributed under the License is distributed on an "AS IS" BASIS,
              | - WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
              | - See the License for the specific language governing permissions and
              | - limitations under the License.
              | -->
              |
              |""".stripMargin
          )
      case _ =>
        throw new IllegalArgumentException(s"Comment style '$commentStyle' not supported")
    }
  }
}
