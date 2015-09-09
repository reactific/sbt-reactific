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

import com.typesafe.sbt.SbtScalariform._

import scalariform.formatter.preferences.AlignSingleLineCaseStatements.MaxArrowIndent
import scalariform.formatter.preferences._

/** Settings For Scalariform */
object Scalariform extends PluginSettings {


  override def projectSettings = scalariformSettings ++ Seq(
    ScalariformKeys.preferences := {
      FormattingPreferences().
        // Align parameters on different lines in the same column
        setPreference(AlignParameters, false).
        // Align the arrows of consecutive single-line case statements
        setPreference(AlignSingleLineCaseStatements, true).
        // Enable Compact Control Readability style
        setPreference(CompactControlReadability, false).
        // Omit spaces when formatting a '+' operator on String literals
        setPreference(CompactStringConcatenation, false).
        // Double indent either a class's parameters or its inheritance list
        setPreference(DoubleIndentClassDeclaration, true).
        // Format XML literals
        setPreference(FormatXml, true).
        // Indent local defs an extra level
        setPreference(IndentLocalDefs, false).
        // Indent package blocks
        setPreference(IndentPackageBlocks, true).
        // Number of spaces to use for indentation
        setPreference(IndentSpaces, 2).
        // Use a tab character for indentation
        setPreference(IndentWithTabs, false).
        // Maximum number of spaces inserted before an arrow to align case statements
        setPreference(MaxArrowIndent, 16).
        // Start multiline Scaladoc comment body on same line as the opening '/**'
        setPreference(MultilineScaladocCommentsStartOnFirstLine, true).
        // Place Scaladoc asterisks beneath the second asterisk in the opening '/**', as opposed to the first
        setPreference(PlaceScaladocAsterisksBeneathSecondAsterisk, true).
        // Preserve a space before a parenthesis argument
        setPreference(PreserveSpaceBeforeArguments, true).
        // Allow a newline before a ')' in an argument expression
        setPreference(PreserveDanglingCloseParenthesis, true).
        // Replace arrow tokens with unicode equivalents: => with ⇒, and <- with ←
        setPreference(RewriteArrowSymbols, true).
        // Add a space before colons
        setPreference(SpaceBeforeColon, true).
        // Require a space after '(' and before ')'
        setPreference(SpaceInsideParentheses, false).
        // Require a space after '[' and before ']'
        setPreference(SpaceInsideBrackets, false).
        // Add a space around the @ token in pattern binders
        setPreference(SpacesWithinPatternBinders, true)
    }
  )
}
