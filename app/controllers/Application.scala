package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {

  /**
   * Serve the Application.
   */
  def index = Action {
    Ok(views.html.index())
  }

}
