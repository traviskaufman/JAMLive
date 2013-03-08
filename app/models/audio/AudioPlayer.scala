package models.audio

import scala.collection.JavaConversions._
import scala.collection.concurrent.{Map => ConcurrentMap}

import java.io.{ByteArrayOutputStream, PrintStream}
import java.util.concurrent.ConcurrentHashMap

import play.api.Logger

import com.jsyn._
import com.jsyn.instruments.WaveShapingVoice
import com.jsyn.unitgen.LineOut
import com.jsyn.unitgen.UnitVoice

/**
 * Responsible for handling all triggering and playback of audio
 * requested by messages received by the server.
 *
 * @param synth The synthesizer object used to play the audio.
 * @param lineOut The stereo output that the whole system connects to.
 * @param players A map of each player and his/her voice.
 */
object AudioPlayer {
  implicit val synth: Synthesizer = JSyn.createSynthesizer
  implicit val lineOut: LineOut = new LineOut

  private val players: ConcurrentMap[String,
                          UnitVoice] = new ConcurrentHashMap[String, UnitVoice]

  /**
   * Initializes the Audio Player.
   *
   * This should be called before any other method.
   */
  def init() {
    Logger.info("Starting JSyn Synthesizer")

    // Get rid of this stupid fucking stdout logging JSyn does.
    // It's ridiculous that I have to do this crap.
    val stdout = System.out
    val captureBuf = new ByteArrayOutputStream
    val captureStream = new PrintStream(captureBuf)
    System.setOut(captureStream)

    synth.start() // Bootstrap JSyn

    System.setOut(stdout)
    Logger.info(captureBuf.toString)
    captureStream.close

    synth.add(lineOut)
    lineOut.start()
  }

  /**
   * Adds a player to the group of players, giving him/her an instrument.
   *
   * @param playerId The unique Id of the player currently playing.
   */
  def addPlayer(playerId: String) {
    Logger.info("Adding player %s".format(playerId))
    val playerVoice = new WaveShapingVoice
    connectToOutput(playerVoice)
    synth.add(playerVoice)
    players += (playerId -> playerVoice)
  }

  /**
   * Plays a voice.
   *
   * @param playerId The ID of the player whos voice should be played.
   */
  def play(playerId: String) {
    val voice = players.getOrElse(playerId, null)

    if (voice == null) {
      return
    }

    // Play for 500 ms a tone of 250Hz at amplitude 0.4
    voice.noteOn(
      250, 0.4, synth.createTimeStamp
    )
    voice.noteOff(
      synth.createTimeStamp.makeRelative(0.5)
    )
  }

  /**
   * Shutdown the System.
   */
  def shutdown() {
    lineOut.stop()
    synth.stop()
  }

  /**
   * Connects a voice to the stereo output.
   *
   * @param voice The voice to connect.
   * @protected
   */
  protected def connectToOutput(voice: UnitVoice) {
    voice.getOutput.connect(
      0, lineOut.input, 0
    )
    voice.getOutput.connect(
      0, lineOut.input, 1
    )
  }
}
