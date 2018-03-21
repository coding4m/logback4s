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

package logback4s.logstash.encoder

import java.time.format.DateTimeFormatter
import java.time.{ Instant, ZoneId }

import ch.qos.logback.classic.spi.ILoggingEvent
import logback4s.PipelineEncoder
import org.json4s.jackson.Serialization

/**
 * @author siuming
 */
class JsonEncoder extends PipelineEncoder[ILoggingEvent] {
  import scala.collection.JavaConverters._
  private implicit val formats = org.json4s.DefaultFormats

  private val dtf = DateTimeFormatter.ISO_INSTANT
  private var encoding = "utf-8"
  private var separator = System.lineSeparator()

  override def encode(event: ILoggingEvent) = {
    var evt = Map.empty[String, Any]
    evt = evt + ("level" -> event.getLevel.levelStr)
    evt = evt + ("logger" -> event.getLoggerName)
    evt = evt + ("thread" -> event.getThreadName)
    evt = evt + ("message" -> event.getFormattedMessage)

    if (getIncludeAllMdcFields()) {
      evt = evt + ("@metadata" -> event.getMDCPropertyMap)
    } else {
      val fields = getIncludeMdcFields().split(",|;").map(_.trim)
      evt = evt + ("@metadata" -> event.getMDCPropertyMap.asScala.filterKeys(fields.contains))
    }

    if (null != getServiceName() && getServiceName().nonEmpty) {
      evt = evt + ("service_name" -> getServiceName())
    }
    if (null != getServiceHost() && getServiceHost().nonEmpty) {
      evt = evt + ("service_host" -> getServiceHost())
    }
    if (null != getServicePort() && getServicePort().nonEmpty) {
      evt = evt + ("service_port" -> getServicePort())
    }

    if (null != getTag() && getTag().nonEmpty) {
      evt = evt + ("@tag" -> getTag())
    }
    if (null != getVersion() && getVersion().nonEmpty) {
      evt = evt + ("@version" -> getVersion())
    }

    val zoneId = ZoneId.of(getTimezone())
    evt = evt + ("@timestamp" -> dtf.format(Instant.ofEpochMilli(event.getTimeStamp).atZone(zoneId)))
    Serialization.write(evt).getBytes(encoding)
  }

  override def footerBytes() = separator.getBytes

  final def setEncoding(encoding: String): Unit = {
    this.encoding = encoding
  }

  final def setSeparator(separator: String): Unit = {
    this.separator = separator
  }
}
