/**
 * @package models.transport
 * WORK IN PROGRESS!!
 * @todo Finish this!
 */
package models.transport

import play.api.libs.json.Json

/**
 * Provides an interface for handling WAMP messages received from the client.
 *
 * @param WAMPMessage Enums for different types of WAMP Messages.
 */
object WAMP {

  object WAMPMessage extends Enumeration {
    type WAMPMessage = Value
    val WELCOME = Value
    val PREFIX = Value
    val CALL = Value
    val CALLRESULT = Value
    val CALLERROR = Value
    val SUBSCRIBE = Value
    val PUBLISH = Value
    val EVENT = Value
  }

  import WAMPMessage._

  /**
   * Parses a WAMP Message and gives back information about that message.
   * @param msg JSON String received as a WAMP message by the server.
   * @todo better dox.
   * @return Map containing information about this message.
   */
  def parseMessage(msg: String): Map[String, String] = {
    val mb = Map.newBuilder[String, String]
    val json = Json.parse(msg)
    val msgType = WAMPMessage(json(0).as[Int])

    mb += ("typeId" -> msgType)

    msgType match {
      case WELCOME => // Don't know why this would ever be incoming but whatevs
        mb += (
          "sessionId" -> json(1).as[String],
          "protocolVersion" -> json(2).as[Int],
          "serverIdent" -> json(3).as[String]
        )

      case PREFIX =>
        mb += (
          "prefix" -> json(1).as[String],
          "URI" -> json(2).as[String]
        )

      // TODO: Finish
    }

    mb.result()
  }
}
