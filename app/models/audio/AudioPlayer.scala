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
 * @param playerCount The number of current connected players.
 *
 * @todo removePlayer
 */
object AudioPlayer {
  implicit val synth: Synthesizer = JSyn.createSynthesizer
  implicit val lineOut: LineOut = new LineOut
  var playerCount: Int = 0

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
    synth.add(lineOut)
    lineOut.start()

    System.setOut(stdout)
    Logger.info(captureBuf.toString)
    captureStream.close
  }

  /**
   * Adds a player to the group of players, giving him/her an instrument.
   *
   * @param playerId The unique Id of the player currently playing.
   *
   * @return The playerId that was added successfully, or null if a player
   *    could not be added.
   */
  def addPlayer(playerId: String): String = {
    Logger.info("Attempting to add player %s".format(playerId))

    if (players.contains(playerId)) {
      Logger.warn("Failed to add player %s: that Id already exists".format(
        playerId
      ))
      return null
    }

    val playerVoice = new WaveShapingVoice
    connectToOutput(playerVoice)
    synth.add(playerVoice)
    players += (playerId -> playerVoice)
    playerCount += 1

    Logger.info("Player %s connected!".format(playerId))
    playerId
  }

  /**
   * Removes a player from the group of players.
   *
   * @param playerId The unique Id of the player to be removed.
   *
   * @return The UnitVoice of the player that was removed if successful, null
   *    otherwise.
   */
  def removePlayer(playerId: String): UnitVoice = {
    Logger.info("Attempting to remove player %s".format(playerId))
    val playerVoice = getPlayerVoice(playerId)

    if (playerVoice != null) {
      playerVoice.noteOff(synth.createTimeStamp)
      players -= playerId
      playerCount -= 1
      Logger.info("Player %s successfully removed".format(playerId))
    } else {
      Logger.warn("Attempted to remove %s but playerId wasn't found".format(
        playerId
      ))
    }

    playerVoice
  }

  /**
   * Get the voice associated with the playerId.
   *
   * @param playerId The player ID the voice is associated with.
   *
   * @return The voice associated with the playerId, or null if there is
   *    no voice associated with that id.
   */
  def getPlayerVoice(playerId: String): UnitVoice = {
    players.getOrElse(playerId, null)
  }

  /**
   * Plays a voice.
   *
   * @param playerId The ID of the player whos voice should be played.
   */
  def play(playerId: String) {
    val voice = getPlayerVoice(playerId)

    if (voice == null) {
      Logger.warn("cannot play voice of %s since no voice for that player " +
                  "exists".format(playerId))
      return
    }

    val amplitude = 1.toDouble / (if (playerCount > 0) playerCount else 1)

    // Play for 500 ms a tone of 250Hz at amplitude 0.4
    voice.noteOn(
      250,
      amplitude,
      synth.createTimeStamp
    )
    voice.noteOff(
      synth.createTimeStamp.makeRelative(0.5)
    )
  }

  /**
   * Shutdown the System.
   */
  def shutdown() {
    val removed = players.keysIterator.map(removePlayer).filter { v =>
      v match {
        case null => false
        case _ => true
      }
    }

    Logger.info("removed %d current players".format(removed.length))
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
