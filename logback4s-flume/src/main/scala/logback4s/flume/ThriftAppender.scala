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

package logback4s.flume

import ch.qos.logback.classic.spi.ILoggingEvent
import logback4s.PipelineAppender

/**
 * @author siuming
 */
class ThriftAppender extends PipelineAppender[ILoggingEvent] {
  override val defaultHost = "127.0.0.1"
  override val defaultPort = 4141

  override protected def newRouter(
    connections: String,
    strategy: String,
    maxRetries: Int,
    maxFails: Int,
    failTimeout: Long) = {
    ???
  }
}
