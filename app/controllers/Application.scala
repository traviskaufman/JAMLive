package controllers

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._

import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import play.api.libs.json.Json

import models.audio.AudioPlayer

/**
 * Main Controller for JAMLive!
 *
 * @param playerForm Very simple; what the user initially uses to sign up.
 */
object Application extends Controller {
  val playerForm = Form(
    "playerId" -> text
  )

  /**
   * Serve the Application.
   */
  def index = Action {
    Ok(views.html.index())
  }

  /**
   * Endpoint to ping when a user connects to the page and enters an id for
   * himself/herself.
   */
  def connect = Action { implicit request =>
    val pId = playerForm.bindFromRequest.get

    // Make this async so if there's ops on the players map the request
    // won't block
    val addPlayerFuture: Future[String] = Future[String] {
      AudioPlayer.addPlayer(pId)
    }

    Async {

      addPlayerFuture.map {res =>
        res match {
          case null => BadRequest(Json.obj(
            "status" -> http.Status.BAD_REQUEST,
            "error" -> "ID %s is already taken.".format(pId)
          ))
          case _ => {
            AudioPlayer.play(res)
            Ok(Json.obj(
              "status" -> http.Status.OK,
              "playerId" -> res
            ))
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
