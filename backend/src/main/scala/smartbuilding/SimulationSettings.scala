package smartbuilding

import com.typesafe.config.Config

import scala.jdk.CollectionConverters.CollectionHasAsScala

case class BuildingSettings(thermalCapacity: Double, thermalResistance: Double)

case class RoomSettings(defaultTemperature: Double, desiredTemperature: Double)

case class SimulationSettings(
    epochDuration: Long,
    buildingSettings: BuildingSettings,
    roomSettings: Map[String, RoomSettings]
)

object SimulationSettings {
  def fromConfig(config: Config): SimulationSettings = {
    val epochDuration = config.getLong("epochDuration")

    val buildingObject = config.getConfig("building")
    val buildingSettings =
      BuildingSettings(
        buildingObject.getDouble("thermalCapacity"),
        buildingObject.getDouble("thermalResistance")
      )

    val roomsList = config.getConfigList("rooms")
    val roomSettings = roomsList.asScala.map { c =>
      c.getString("id") -> RoomSettings(
        c.getDouble("defaultTemperature"),
        c.getDouble("desiredTemperature")
      )
    }.toMap

    SimulationSettings(epochDuration, buildingSettings, roomSettings)
  }
}