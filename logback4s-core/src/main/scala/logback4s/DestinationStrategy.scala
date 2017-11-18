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

import java.util.concurrent.atomic.AtomicLong

import scala.util.Random

/**
 * @author siuming
 */
trait DestinationStrategy {
  def select(destinations: Seq[Destination]): Destination
}
object RandomStrategy extends RandomStrategy {
  val Name = "random"
}
trait RandomStrategy extends DestinationStrategy {
  private val random = new Random()
  override def select(destinations: Seq[Destination]) = {
    destinations(random.nextInt(destinations.size))
  }
}
object RoundRobinStrategy extends RoundRobinStrategy {
  val Name = "roundrobin"
}
trait RoundRobinStrategy extends DestinationStrategy {
  val counter = new AtomicLong()

  override def select(destinations: Seq[Destination]) = {
    destinations((counter.getAndIncrement() % destinations.size).toInt)
  }
}
