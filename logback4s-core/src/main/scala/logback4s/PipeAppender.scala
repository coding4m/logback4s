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
import ch.qos.logback.core.spi.DeferredProcessingAware

/**
 * @author siuming
 */
object PipeAppender {
  val DefaultMaxRetries = 1
  val DefaultMaxFails = 1
  val DefaultFailTimeout = 10000
}
abstract class PipeAppender[E] extends AppenderBase[E] {
  import PipeAppender._

  val defaultHost: String
  val defaultPort: Int

  private var router: DestinationRouter = _
  private var encoder: Encoder[E] = _

  private var maxRetries: Int = DefaultMaxRetries
  private var maxFails: Int = DefaultMaxFails
  private var failTimeout: Long = DefaultFailTimeout

  private var destinations: String = _
  private var destinationStrategy: String = RandomStrategy.Name

  final override def append(eventObject: E) = {
    val event = processEvent(eventObject)
    router.send(encoder.headerBytes() ++ encoder.encode(event) ++ encoder.footerBytes())
    postProcessEvent(eventObject)
  }

  protected def processEvent(eventObject: E): E = eventObject match {
    case deferred: DeferredProcessingAware =>
      deferred.prepareForDeferredProcessing()
      eventObject
    case _ =>
      eventObject
  }

  protected def postProcessEvent(eventObject: E): Unit = {
  }

  final override def start() = {
    preStart()
    router = newRouter(destinations, destinationStrategy, maxRetries, maxFails, failTimeout)
    postStart()
    super.start()
  }

  protected def preStart(): Unit = {
  }

  protected def postStart(): Unit = {
  }

  protected def newRouter(
    destinations: String,
    destinationStrategy: String,
    maxRetries: Int,
    maxFails: Int,
    failTimeout: Long): DestinationRouter

  final override def stop() = {
    preStop()
    try {
      router.close()
    } catch {
      case _: Throwable =>
    }
    postStop()
    super.stop()
  }

  protected def preStop(): Unit = {
  }

  protected def postStop(): Unit = {
  }

  final def getEncoder(): Encoder[E] = {
    this.encoder
  }

  final def setEncoder(encoder: Encoder[E]): Unit = {
    this.encoder = encoder
  }

  final def getMaxRetries(): Int = {
    this.maxRetries
  }

  final def setMaxRetries(maxRetries: Int): Unit = {
    this.maxRetries = maxRetries
  }

  final def getMaxFails(): Int = {
    this.maxFails
  }

  final def setMaxFails(maxFails: Int): Unit = {
    this.maxFails = maxFails
  }

  final def getFailTimeout(): Long = {
    this.failTimeout
  }

  final def setFailTimeout(failTimeout: Long): Unit = {
    this.failTimeout = failTimeout
  }

  final def getDestinations(): String = {
    this.destinations
  }

  final def setDestinations(destinations: String): Unit = {
    this.destinations = destinations
  }

  final def getDestinationStrategy(): String = {
    this.destinationStrategy
  }

  final def setDestinationStrategy(strategy: String): Unit = {
    this.destinationStrategy = strategy
  }
}
