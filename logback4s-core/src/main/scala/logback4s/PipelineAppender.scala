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

import java.util.concurrent.Executors

import ch.qos.logback.core.AppenderBase
import ch.qos.logback.core.encoder.Encoder
import ch.qos.logback.core.spi.DeferredProcessingAware
import com.lmax.disruptor.{ EventFactory, EventHandler, EventTranslatorOneArg, ExceptionHandler }
import com.lmax.disruptor.dsl.Disruptor

/**
 * @author siuming
 */
object PipelineAppender {
  val DefaultMaxBufferSize = 1024
  val DefaultMaxRetries = 1
  val DefaultMaxFails = 1
  val DefaultFailTimeout = 10000

  private[logback4s] class LoggingEvent[E] {
    var event: E = _
    def set(event: E): Unit = this.event = event
  }

  private[logback4s] class LoggingEventFactory[E] extends EventFactory[LoggingEvent[E]] {
    override def newInstance() = new LoggingEvent[E]
  }

  private[logback4s] class LoggingEventTranslator[E] extends EventTranslatorOneArg[LoggingEvent[E], E] {
    override def translateTo(event: LoggingEvent[E], sequence: Long, arg0: E): Unit = event.set(arg0)
  }
}
abstract class PipelineAppender[E] extends AppenderBase[E] {
  import PipelineAppender._

  val defaultHost: String
  val defaultPort: Int

  private val factory = new LoggingEventFactory[E]
  private val translator = new LoggingEventTranslator[E]
  private var disruptor: Disruptor[LoggingEvent[E]] = _

  private var router: DestinationRouter = _
  private var encoder: Encoder[E] = _

  private var maxBufferSize: Int = DefaultMaxBufferSize
  private var maxRetries: Int = DefaultMaxRetries
  private var maxFails: Int = DefaultMaxFails
  private var failTimeout: Long = DefaultFailTimeout

  private var destinations: String = _
  private var destinationStrategy: String = RandomStrategy.Name

  final override def append(eventObject: E): Unit = {
    val event = processEvent(eventObject)
    if (disruptor.getRingBuffer.tryPublishEvent(translator, event)) {
      postProcessEvent(eventObject)
    } else addError("publish logging event failed.")
  }

  /**
   *
   * @param eventObject
   * @return
   */
  protected def processEvent(eventObject: E): E = eventObject match {
    case deferred: DeferredProcessingAware =>
      deferred.prepareForDeferredProcessing()
      eventObject
    case _ =>
      eventObject
  }

  /**
   *
   * @param eventObject
   */
  protected def postProcessEvent(eventObject: E): Unit = {
  }

  final override def start(): Unit = {
    preStart()
    encoder.start()

    val strategy = destinationStrategy.toLowerCase match {
      case RoundRobinStrategy.Name => RoundRobinStrategy
      case _                       => RandomStrategy
    }
    router = new DestinationRouter(newDestinations(destinations), strategy, maxRetries, maxFails, failTimeout)
    disruptor = new Disruptor[LoggingEvent[E]](factory, maxBufferSize, Executors.defaultThreadFactory())
    disruptor.handleEventsWith(new EventHandler[LoggingEvent[E]] {
      override def onEvent(le: LoggingEvent[E], sequence: Long, endOfBatch: Boolean): Unit = {
        router.send(encoder.headerBytes() ++ encoder.encode(le.event) ++ encoder.footerBytes())
      }
    })
    disruptor.setDefaultExceptionHandler(new ExceptionHandler[LoggingEvent[E]] {
      override def handleOnStartException(ex: Throwable): Unit = throw ex
      override def handleOnShutdownException(ex: Throwable): Unit = throw ex
      override def handleEventException(ex: Throwable, sequence: Long, event: LoggingEvent[E]): Unit = addError("handle logging event failed.", ex)
    })
    disruptor.start()
    postStart()
    super.start()
  }

  protected def preStart(): Unit = {
  }

  protected def postStart(): Unit = {
  }

  final override def stop(): Unit = {
    preStop()
    try {
      disruptor.shutdown()
    } catch {
      case _: Throwable =>
    }
    try {
      router.close()
    } catch {
      case _: Throwable =>
    }
    try {
      encoder.stop()
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

  /**
   *
   * @param connections
   * @return
   */
  protected def newDestinations(connections: String): Seq[Destination]

  final def getEncoder(): Encoder[E] = {
    this.encoder
  }

  final def setEncoder(encoder: Encoder[E]): Unit = {
    this.encoder = encoder
  }

  final def getMaxBufferSize(): Int = {
    this.maxBufferSize
  }

  final def setMaxBufferSize(maxBufferSize: Int): Unit = {
    this.maxBufferSize = maxBufferSize
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
