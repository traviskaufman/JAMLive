/**
 * @package models.audio
 */
package models.audio

import scala.collection.JavaConversions._
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
     * about an input port of the unitGenerator. Namely, the map will contain the following keys:
     * <ul>
     *   <li><strong>name</strong>: The name of the input port.
     *   <li><strong>default</strong>: The port's default value.
     *   <li><strong>max</strong>: The maximum value that the port can have.
     *   <li><strong>min</strong>: The minimum value that the port can have.
     * </ul>
     *
     * @return An Iterable containing maps with information for each input port.
     */
    def inputPortsToMap: Map[String, Map[String, String]] = ug.getPorts.filter (
      _.isInstanceOf[UnitInputPort]

    ).map (
      _.asInstanceOf[UnitInputPort]

    ).foldLeft(Map.empty[String, Map[String, String]]) (
      (mem: Map[String, Map[String, String]], p: UnitInputPort) => mem + (
        p.getName -> Map[String, String](
          "default" -> p.getDefault.toString,
          "max" -> p.getMaximum.toString,
          "min" -> p.getMinimum.toString
      ))
    )
  }
}
