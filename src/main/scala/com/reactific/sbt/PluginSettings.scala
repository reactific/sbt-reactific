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

import sbt._

/** Basic configuration of a plugin */
trait PluginSettings {
  /** The [[sbt.Configuration]]s to add to each project that activates this [[sbt.AutoPlugin]].*/
  def projectConfigurations: Seq[Configuration] = Nil

  /** The [[sbt.Setting]]s to add in the scope of each project that activates this [[sbt.AutoPlugin]]. */
  def projectSettings: Seq[Setting[_]] = Nil

  /** The [[sbt.Setting]]s to add to the build scope for each project that activates this [[sbt.AutoPlugin]].
    * The settings returned here are guaranteed to be added to a given build scope only once
    * regardless of how many projects for that build activate this AutoPlugin. */
  def buildSettings: Seq[Setting[_]] = Nil
}
