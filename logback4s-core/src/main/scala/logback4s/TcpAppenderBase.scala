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

import ch.qos.logback.core.AppenderBase
import ch.qos.logback.core.encoder.Encoder

/**
 * @author siuming
 */
abstract class TcpAppenderBase[E] extends AppenderBase[E] {

  val defaultHost: String
  val defaultPort: Int

  private var router: DestinationRouter = _
  private var encoder: Encoder[E] = _
  private var destinations: Seq[Destination] = Seq.empty
  private var destinationStrategy: DestinationStrategy = RandomStrategy

  override def append(eventObject: E) = {
    router.send(encoder.headerBytes() ++ encoder.encode(eventObject) ++ encoder.footerBytes())
  }

  override def start() = {
    router = new DestinationRouter(destinations, destinationStrategy)
    super.start()
  }

  def setEncoder(encoder: Encoder[E]): Unit = {
    this.encoder = encoder
  }

  def setDestinations(destinations: String): Unit = {
    import Destination._
    this.destinations = destinations.split(",|;").collect {
      case HostAndPort(host, port) => new TcpDestination(host, port.toInt)
      case Host(host)              => new TcpDestination(host, defaultPort)
    }
  }

  def setDestinationStrategy(strategy: String): Unit = strategy.toLowerCase match {
    case RoundRobinStrategy.Type => destinationStrategy = RoundRobinStrategy
    case _                       => destinationStrategy = RandomStrategy
  }
}
