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

import java.util.concurrent.locks.ReentrantReadWriteLock

/**
 * @author siuming
 */
final class DestinationRouter(
  destinations: Seq[Destination],
  strategy: DestinationStrategy,
  maxRetries: Int,
  maxFails: Int,
  failTimeout: Long) {

  private val lock = new ReentrantReadWriteLock()
  private var hints = Seq.empty[BackupHint]
  private var backups = Seq.empty[Backup]
  private var actives = Seq(destinations: _*)

  def send(bytes: Array[Byte]): Unit = {
    sendInternal(maxRetries, bytes)
  }

  private def sendInternal(retryTimes: Int, bytes: Array[Byte]): Unit = {
    val destination = selectDestination()
    try {
      destination.send(bytes)
    } catch {
      case e: Throwable =>
        downgradeDestination(destination)
        if (retryTimes <= 1) {
          throw e
        }
        sendInternal(retryTimes - 1, bytes)
    }
  }

  private def selectDestination() = {
    readLock {
      if (backups.nonEmpty) {
        upgradeDestination()
      }
      if (actives.isEmpty) {
        throw new NotAvailableDestinationException
      }

      strategy.select(actives)
    }
  }

  private def upgradeDestination(): Unit = {
    writeLock {
      backups.filter(_.until <= System.currentTimeMillis()).foreach { it =>
        backups = backups.filterNot(_.destination.id != it.destination.id)
        actives = actives.filterNot(_.id != it.destination.id) :+ it.destination
      }
    }
  }

  private def downgradeDestination(destination: Destination): Unit = {
    writeLock {
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

  private def readLock[T](body: => T): T = {
    try {
      lock.readLock().lock()
      body
    } finally {
      lock.readLock().unlock()
    }
  }

  private def writeLock[T](body: => T): T = {
    try {
      lock.writeLock().lock()
      body
    } finally {
      lock.writeLock().unlock()
    }
  }
}
