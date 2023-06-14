package smartbuilding

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import smartbuilding.SimulationManager.RoomResponse

object RoomAgent {

  import Auctioneer.AuctionOffer
  import RoomState._

  sealed trait Command

  case class AuctionInitialized(auctioneer: ActorRef[Auctioneer.Command]) extends Command

  case class OfferResult(volume: Double) extends Command

  case class GetInfo(replyTo: ActorRef[SimulationManager.Response]) extends Command

  case class SetTargetTemp(temp: Float) extends Command

  def apply(
             id: String,
             buildingSettings: BuildingSettings,
             initialRoomSettings: RoomSettings
           ): Behavior[Command] =
    Behaviors.setup { context =>
      val capacity = buildingSettings.thermalCapacity
      val resistance = buildingSettings.thermalResistance

      def work(state: RoomState, settings: RoomSettings): Behavior[Command] =
        Behaviors.receiveMessage {
          case AuctionInitialized(auctioneer) =>
            val offer = makeOffer(state, settings)
            context.log.info(s"Agent $id sending $offer")
            auctioneer ! offer
            work(state, settings)
          case OfferResult(volume) =>
            work(updateState(state, settings, volume), settings)
          case GetInfo(replyTo) =>
            replyTo ! RoomResponse(id, state, settings)
            work(state, settings)
          case SetTargetTemp(temp) =>
            work(state, RoomSettings(settings.initialEnergy, settings.defaultTemperature, temp))
        }

      def makeOffer(state: RoomState, settings: RoomSettings) = {
        val diff = settings.desiredTemperature - state.temperature
        val sell = !shouldHeat(state, settings)
        val volume = 3 * (diff / 20.0)
        val price = if (sell) 10 else 10 // 7.2 - deleting the temperature dependency
        AuctionOffer(id, sell, volume, price, RoomResponse(id, state, settings))
      }

      def updateState(state: RoomState, settings: RoomSettings, volume: Double) = {
        val powerAvailable = state.powerAvailable + volume
        val output = updateControllerOutput(state, settings)
        val powerConsumed = Math.min(output, powerAvailable)
        val temperature = updateTemperature(state, settings, -powerConsumed)
        context.log.info(s"Agent $id was offered $volume of heat, pa: $powerAvailable, output: $output, pc: $powerConsumed")
        RoomState(powerAvailable, powerConsumed, temperature)
      }

      def updateControllerOutput(state: RoomState, settings: RoomSettings) = {
        val diff = settings.desiredTemperature - state.temperature
        if (diff > 0) Math.min(MaxControllerOutput, diff)
        else Math.max(MinControllerOutput, diff)
      }

      def updateTemperature(state: RoomState, settings: RoomSettings, powerConsumed: Double) = {
        val paceFactor = 1 / (1 + (1 / (resistance * capacity)))
        val oldTemperature = state.temperature
        val newTemperature = paceFactor * (state.temperature + ((settings.defaultTemperature / resistance) - powerConsumed) / capacity)
        context.log.info(s"Agent $id consumed $powerConsumed to update temp by ${newTemperature - oldTemperature}")
        newTemperature
      }

      def shouldHeat(state: RoomState, settings: RoomSettings) = state.temperature < settings.desiredTemperature

      work(RoomState(initialRoomSettings.initialEnergy, 0, initialRoomSettings.defaultTemperature), initialRoomSettings)
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
