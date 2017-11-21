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

package logback4s.logstash

import org.scalatest.{ FunSpec, Matchers }
import org.slf4j.LoggerFactory

/**
 * @author siuming
 */
class LoggerSpec extends FunSpec with Matchers {

  val logger = LoggerFactory.getLogger(getClass)
  describe("just test") {
    it("should be true") {
      logger.warn("hello world")
      assert(1 == 1)
    }
  }
}
