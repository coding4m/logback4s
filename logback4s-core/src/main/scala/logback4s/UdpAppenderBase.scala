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

/**
 * @author siuming
 */
abstract class UdpAppenderBase[E] extends DestinationAppender[E] {

  override protected def newRouter(
    destinations: String,
    destinationStrategy: String,
    maxRetries: Int,
    maxFails: Int,
    failTimeout: Long) = {
    import Destination._
    val connections = destinations.split(",|;").collect {
      case HostAndPort(host, port) => new UdpDestination(host, port.toInt)
      case Host(host)              => new UdpDestination(host, defaultPort)
    }

    val strategy = destinationStrategy.toLowerCase match {
      case RoundRobinStrategy.Name => RoundRobinStrategy
      case _                       => RandomStrategy
    }

    new DestinationRouter(connections, strategy, maxRetries, maxFails, failTimeout)
  }
}
