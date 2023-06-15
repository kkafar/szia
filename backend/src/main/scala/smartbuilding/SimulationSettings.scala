package smartbuilding

import com.typesafe.config.Config

import scala.jdk.CollectionConverters.CollectionHasAsScala

case class BuildingSettings(thermalCapacity: Double, thermalResistance: Double)

case class RoomSettings(
	id: String,
    initialEnergy: Double,
    defaultTemperature: Double,
    desiredTemperature: Double,
    powerAvailableRatio: Double
)

case class ServerSettings(host: String, port: Int)

case class SimulationSettings(
    serverSettings: ServerSettings,
    epochDuration: Long,
    buildingSettings: BuildingSettings,
    roomSettings: Map[String, RoomSettings]
)

object SimulationSettings {
  def fromConfig(config: Config): SimulationSettings = {
    val serverObject = config.getConfig("server")
    val serverSettings = ServerSettings(serverObject.getString("host"), serverObject.getInt("port"))

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
        c.getString("id"),
        c.getDouble("initialEnergy"),
        c.getDouble("defaultTemperature"),
        c.getDouble("desiredTemperature"),
        c.getDouble("powerAvailableRatio")
      )
    }.toMap

    SimulationSettings(serverSettings, epochDuration, buildingSettings, roomSettings)
  }
}
