package smartbuilding

import akka.actor.typed.ActorSystem
import com.typesafe.config.ConfigFactory

object SmartBuildingApp extends App {
  val config = ConfigFactory.load()
  val settings = SimulationSettings.fromConfig(config)
  val actorSystem: ActorSystem[SimulationManager.Command] =
    ActorSystem(SimulationManager(settings), "SmartBuilding")
}
