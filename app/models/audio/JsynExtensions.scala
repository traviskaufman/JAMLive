/**
 * @package models.audio
 */
package models.audio

import scala.collection.JavaConversions._
import java.lang.ArrayIndexOutOfBoundsException
import com.jsyn.unitgen.UnitGenerator
import com.jsyn.ports.UnitInputPort

/**
 * Contains extensions for working with JSyn Classes.
 */
package object JsynExtensions {
  /**
   * Extensions to JSyn's UnitGenerator.
   * @param ug The UnitGenerator that we're extending.
   */
  implicit class UnitGeneratorExtensions(ug: UnitGenerator) {
    /**
     * Outputs an Iterable object containing a series of maps, each of which contains information
     * about an input port of the unitGenerator. Namely, the map will contain keys, each of which
     * corresponds to a parameter name, and a map of values as follows:
     * <ul>
     *   <li><strong>default</strong>: The port's default value.
     *   <li><strong>max</strong>: The maximum value that the port can have.
     *   <li><strong>min</strong>: The minimum value that the port can have.
     * </ul>
     *
     * @return An Iterable containing maps with information for each input port.
     */
    def inputPortsToMap: Map[String, Map[String, Double]] = ug.getPorts.filter (
      _.isInstanceOf[UnitInputPort]

    ).map (
      _.asInstanceOf[UnitInputPort]

    ).foldLeft(Map.empty[String, Map[String, Double]]) (
      (mem: Map[String, Map[String, Double]], p: UnitInputPort) => mem + (
        p.getName -> Map[String, Double](
          "default" -> p.getDefault,
          "max" -> p.getMaximum,
          "min" -> p.getMinimum
      ))
    )

    /**
     * Retrieve a value of a (input) parameter without jumping through tons of method hoops and/or
     * throwing exceptions.
     *
     * @param pName The name of the parameter to get.
     * @param partNum (optional) The port number to get. Useful for multi-input ports.
     *    Defaults to 0.
     *
     * @return An option containing the value of the port if it exists.
     */
    def getParamValue(pName: String, partNum: Integer = 0): Option[Double] = {
      ug.getPortByName(pName) match {
        case null => None
        case p =>
          try {
            Some[Double](p.asInstanceOf[UnitInputPort].get(partNum))
          } catch {
            case _: ArrayIndexOutOfBoundsException => None
          }
      }
    }
  }
}
