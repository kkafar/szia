package smartbuilding

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

object SimulationManager {
  sealed trait Command

  def apply(settings: SimulationSettings): Behavior[Command] =
    Behaviors.setup { context =>
      val roomAgents = settings.roomSettings.map { case (id, roomSettings) =>
        (id, context.spawn(RoomAgent(id, settings.buildingSettings, roomSettings), id))
      }
      val auctioneer =
        context.spawn(Auctioneer(settings.epochDuration, roomAgents.values.toList), "auctioneer")

      Behaviors.unhandled
    }

}
