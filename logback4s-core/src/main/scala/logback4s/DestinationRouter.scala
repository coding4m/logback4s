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

import java.io.Closeable

/**
 * @author siuming
 */
final class DestinationRouter(
  destinations: Seq[Destination],
  strategy: DestinationStrategy,
  maxRetries: Int,
  maxFails: Int,
  failTimeout: Long) extends Closeable {

  @volatile private var hints = Seq.empty[BackupHint]
  @volatile private var backups = Seq.empty[Backup]
  @volatile private var actives = Seq(destinations: _*)
  @volatile private var closed: Boolean = false

  def send(bytes: Array[Byte]): Unit = {
    if (closed) {
      throw new IllegalStateException("router already closed.")
    }
    sendInternal(maxRetries, bytes)
  }

  private def sendInternal(retryTimes: Int, bytes: Array[Byte]): Unit = {
    upgradeDestination()
    val d = selectDestination()
    try {
      d.send(bytes)
    } catch {
      case e: Throwable =>
        downgradeDestination(d)
        if (retryTimes <= 1) {
          throw e
        }
        sendInternal(retryTimes - 1, bytes)
    }
  }

  private def selectDestination() = {
    val _actives = actives
    if (_actives.isEmpty) {
      throw new NotAvailableDestinationException
    }
    strategy.select(_actives)
  }

  private def upgradeDestination(): Unit = {
    val _backups = backups
    if (_backups.nonEmpty) {
      this.synchronized {
        backups.filter(_.until <= System.currentTimeMillis()).foreach { it =>
          backups = backups.filterNot(_.destination.id != it.destination.id)
          actives = actives.filterNot(_.id != it.destination.id) :+ it.destination
        }
      }
    }
  }

  private def downgradeDestination(destination: Destination): Unit = {
    this.synchronized {
      val hint = hints.find(_.id == destination.id).getOrElse(BackupHint(destination.id, 0))
      if (hint.fails >= maxFails - 1) {
        backups = backups.filterNot(_.destination.id != hint.id) :+ Backup(destination, System.currentTimeMillis() + failTimeout)
        actives = actives.filterNot(_.id != hint.id)
        hints = hints.filterNot(_.id != hint.id)
      } else {
        hints = hints.filterNot(_.id != hint.id) :+ hint.copy(fails = hint.fails + 1)
      }
    }
  }

  override def close() = {
    this.synchronized {
      closed = true
      destinations.foreach(_.close())
    }
  }
}
