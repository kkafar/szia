package smartbuilding

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

object RoomAgent {
  import Auctioneer.AuctionOffer
  import RoomState._

  sealed trait Command
  case class AuctionInitialized(auctioneer: ActorRef[Auctioneer.Command]) extends Command
  case class OfferResult(volume: Double) extends Command

  def apply(
      id: String,
      buildingSettings: BuildingSettings,
      roomSettings: RoomSettings
  ): Behavior[Command] =
    Behaviors.setup { context =>
      val capacity = buildingSettings.thermalCapacity
      val resistance = buildingSettings.thermalResistance

      def work(state: RoomState): Behavior[Command] =
        Behaviors.receiveMessage {
          case AuctionInitialized(auctioneer) =>
            val offer = makeOffer(state)
            context.log.info(s"Agent $id sending $offer")
            auctioneer ! offer
            work(state)
          case OfferResult(volume) =>
            work(updateState(state, volume))

        }

      def makeOffer(state: RoomState) = {
        val diff = roomSettings.desiredTemperature - state.temperature
        val sell = !shouldHeat(state)
        val volume = 3 * (diff / 20.0)
        val price = if (sell) 10 else 100 // 7.2 - deleting the temperature dependency
        AuctionOffer(id, sell, volume, price)
      }

      def updateState(state: RoomState, volume: Double) = {
        val powerAvailable = state.powerAvailable + volume
        val output = updateControllerOutput(state)
        val powerConsumed = Math.min(output, powerAvailable)
        val temperature = updateTemperature(state, powerConsumed)
        RoomState(powerAvailable, powerConsumed, temperature)
      }

      def updateControllerOutput(state: RoomState) = {
        val diff = roomSettings.desiredTemperature - state.temperature
        if (diff > 0) Math.min(MaxControllerOutput, diff)
        else Math.max(MinControllerOutput, diff)
      }

      def updateTemperature(state: RoomState, powerConsumed: Double) = {
        val paceFactor = 1 / (1 + (1 / (resistance * capacity)))
        paceFactor * (state.temperature + ((roomSettings.defaultTemperature / resistance) - powerConsumed) / capacity)
      }

      def shouldHeat(state: RoomState) = state.temperature < roomSettings.desiredTemperature

      work(RoomState(3, 0, roomSettings.defaultTemperature))
    }
}

case class RoomState(
    powerAvailable: Double,
    powerConsumed: Double,
    temperature: Double
)

object RoomState {
  val MaxControllerOutput = 3
  val MinControllerOutput = -3
}