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

import java.net.{ InetSocketAddress, Socket }
import javax.net.SocketFactory

/**
 * @author siuming
 */
class TcpDestination(host: String, port: Int, val settings: TcpSettings) extends Destination {

  @volatile var socket: Socket = _

  override def send(bytes: Array[Byte]) = {
    if (!socket.isClosed) {
      socket = SocketFactory.getDefault.createSocket()
      socket.connect(new InetSocketAddress(host, port))
    }
    socket.getOutputStream.write(bytes)
    socket.getOutputStream.flush()
  }

  protected def preSend(socket: Socket, bytes: Array[Byte]): Unit = {
  }

  protected def postSend(socket: Socket, bytes: Array[Byte]): Unit = {
  }
}
