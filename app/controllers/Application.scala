package controllers

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._

import play.api._
import play.api.mvc._
import models.audio.AudioPlayer

object Application extends Controller {

  /**
   * Serve the Application.
   */
  def index = Action {
    Ok(views.html.index())
  }

  /**
   * Endpoint to ping when a user leaves the page. Will eventually be replaced
   * with a message for when the socket disconnects (possibly).
   */
  def connect = Action { request =>
    val playerId = request.getQueryString("playerId")

    if (playerId != None) {
      BadRequest("playerId required")
    } else {

      // Make this async so if there's ops on the player's map the request
      // won't block
      val addPlayerFuture: Future[String] = Future[String] {
        AudioPlayer.addPlayer(playerId.get)
      }

      Async {
        addPlayerFuture.map {res =>
          res match {
            case null => BadRequest("ERR_ID_EXISTS")
            case _ => Ok(res)
          }
        }
      }

    }
  }

  /**
   * Establishes the websocket connection for sending
   * musical control messages to the server.
   *
   * @todo Implement in OSC.
   */
  // def init = WebSocket.using[String] { request =>

  //   val in = Iteratee.forEach[String] { msg =>
  //     msg match {
  //       case "playNote" =>

  //     }
  //   }

  // }

}
