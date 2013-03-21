/**
 * @package test
 */
package test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json.Json.parse

/**
 * Spec for the application controller. Should be pretty straightforward.
 */
class ApplicationSpec extends Specification {

  "Application" should {

    "send 404 on a bad request" in {
      running(FakeApplication()) {
        route(FakeRequest(GET, "/boum")) must beNone
      }
    }

    "render the index page" in {
      running(FakeApplication()) {
        val home = route(FakeRequest()).get

        status(home) must equalTo(OK)
        contentType(home) must beSome.which(_ == "text/html")
        contentAsString(home) must contain ("JAMLive!")
      }
    }

    "connect a user to the audio server if s/he has a unique ID" in {
      val playerId = "Bob"

      running(FakeApplication()) {
        val connectRequest = route(FakeRequest("POST", "/connect").withFormUrlEncodedBody(
          ("playerId" -> playerId)
        )).get

        status(connectRequest) must equalTo(OK)
        contentType(connectRequest) must beSome.which(_ == "application/json")

        val resp = parse(contentAsString(connectRequest))
        (resp \ "status").as[Int] must equalTo(OK)
        (resp \ "playerId").as[String] must equalTo(playerId)
      }
    }
  }
}
