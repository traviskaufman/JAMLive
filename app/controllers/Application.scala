package controllers

import play.api._
import play.api.mvc._
import models.audio.AudioPlayer

object Application extends Controller {

  /**
   * Serve the Application.
   */
  def index = Action { implicit request =>
    // TODO: Async add player to system. (Right now just sync)
    AudioPlayer.
    Ok(views.html.index())
  }

  /**
   * Establishes the websocket connection for sending
   * musical control messages to the server.
   *
   * @todo Implement in OSC.
   */
  def init = WebSocket.using[String] { request =>

    val in = Iteratee.forEach[String] { msg =>
      msg match {
        case "playNote" =>

      }
    }

  }

}
