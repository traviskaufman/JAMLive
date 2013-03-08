/**
 * Global Settings for the JAMLive! Application.
 */
import play.api._
import models.audio.AudioPlayer

object Global extends GlobalSettings {
  override def onStart(app: Application) {
    Logger.info("Bootstrapping Audio Player")
    AudioPlayer.init()
  }

  override def onStop(app: Application) {
    Logger.info("Shutting down the Audio Player")
    AudioPlayer.shutdown()
  }
}
