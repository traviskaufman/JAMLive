/**
 * @package test
 */
package test

import org.specs2.mutable._

import play.api.mvc.{Result, AsyncResult}
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json.Json.parse

/**
 * Holds some fixtures and convenience methods for testing the application.
 *
 * @param playerId The player ID used for testing.
 */
object ApplicationSpec {
  val playerId: String = "Bob"

  /**
   * Creates a request that would hypothetically be sent by a client back to the server when a user
   * wanted to connect, and returns the reponse from that request.
   *
   * @return a Result instance representing that request.
   */
  def getConnectResponse: Result = await[Result](
    route(FakeRequest(POST, "/connect").withFormUrlEncodedBody(
      ("playerId" -> playerId)
    )).get.asInstanceOf[AsyncResult].result
  )
}

/**
 * Spec for the application controller. Should be pretty straightforward.
 */
class ApplicationSpec extends Specification {
  import ApplicationSpec._

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
      running(FakeApplication()) {
        val connectResp = getConnectResponse

        status(connectResp) must equalTo(OK)
        contentType(connectResp) must beSome.which(_ == "application/json")

        val json = parse(contentAsString(connectResp))
        (json \ "status").as[Int] must equalTo(OK)
        (json \ "playerId").as[String] must equalTo(playerId)
      }
    }

    "send back an error if a player tries to connect with a taken id" in {
      running(FakeApplication()) {
        val _ = getConnectResponse
        val attempt2 = getConnectResponse
        val json = parse(contentAsString(attempt2))

        (json \ "status").as[Int] must equalTo(BAD_REQUEST)
        (json \ "error").as[String] must equalTo(s"""ID $playerId is already taken.""")
      }
    }
  }
}
