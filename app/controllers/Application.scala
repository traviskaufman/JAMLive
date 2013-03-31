package controllers

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._

import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import play.api.libs.iteratee.{Iteratee, Enumerator}
import play.api.libs.json.Json

import models.audio.AudioPlayer
import models.audio.JsynExtensions._

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
  def index = Action { request =>
    Ok(views.html.index(request))
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
            )).withSession(request.session + ("playerId" -> res))
          }
        }
      }

    }
  }

  /**
   * Retrieve information about a certain player's instrument.
   *
   * @param pId The id of the player you're getting the instrument for.
   */
  def instrument(pId: String) = Action {
    val instrument = AudioPlayer.getPlayerVoice(pId)

    if (instrument == null) {
      BadRequest(Json.obj(
        "status" -> http.Status.BAD_REQUEST,
        "error" -> "No player associated with that playerId"
      ))
    } else {
      Ok(Json.toJson(instrument.getUnitGenerator.inputPortsToMaps))
    }
  }

  /**
   * Establishes the websocket connection for sending
   * musical control messages to the server.
   *
   * @todo Implement with WAMP.
   */
  def jamSession = WebSocket.using[String] { request =>
    val in = Iteratee.foreach[String] { msg =>
      msg match {
        case str if str.startsWith("playNote:") =>
          val (pId, freq) = {
            val sp = str.split(":")
            (sp(1), sp(2).toInt)
          }

          AudioPlayer.play(pId, freq)
      }
    } mapDone { _ =>
      AudioPlayer.removePlayer(request.session("playerId"))
      Logger.info("Player Disconnected!")
    }

    val out = Enumerator[String]("")  // TODO: Fix this (WAMP Welcome Msg, etc.).

    (in, out)
  }

}
