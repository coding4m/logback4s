/*
 * Copyright 2015 - 2016 Forever High Tech <http://www.foreverht.com> - all rights reserved.
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

package logback4s

import ch.qos.logback.core.encoder.EncoderBase

/**
 * @author siuming
 */
trait PipelineEncoder[E] extends EncoderBase[E] {

  private var tag: String = _
  private var source: String = _
  private var version: String = _
  private var timezone: String = "Asia/Shanghai"
  private var includeMdcFields: String = _
  private var includeAllMdcFields: Boolean = true

  override def headerBytes() =
    Array.emptyByteArray

  override def footerBytes() =
    Array.emptyByteArray

  final def getTag(): String = {
    this.tag
  }

  final def setTag(tags: String): Unit = {
    this.tag = tags
  }

  final def getSource(): String = {
    this.source
  }

  final def setSource(source: String): Unit = {
    this.source = source
  }

  final def getVersion(): String = {
    this.version
  }

  final def setVersion(version: String): Unit = {
    this.version = version
  }

  final def getTimezone(): String = {
    this.timezone
  }

  final def setTimezone(timezone: String): Unit = {
    this.timezone = timezone
  }

  final def getIncludeMdcFields(): String = {
    this.includeMdcFields
  }

  final def setIncludeMdcFields(includeFields: String): Unit = {
    this.includeMdcFields = includeFields
  }

  final def getIncludeAllMdcFields(): Boolean = {
    this.includeAllMdcFields
  }

  final def setIncludeAllMdcFields(includeAll: Boolean): Unit = {
    this.includeAllMdcFields = includeAll
  }
}
