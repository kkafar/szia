package smartbuilding

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

import scala.annotation.tailrec
import scala.util.Random

object RoomAgent {
  import Auctioneer.AuctionOffer

  sealed trait Command
  case class AuctionInitialized(auctioneer: ActorRef[Auctioneer.Command]) extends Command
  case class AuctionFinished(price: Int) extends Command

  private val rand = new Random()

  def apply(id: String, buildingSettings: BuildingSettings, roomSettings: RoomSettings): Behavior[Command] =
    Behaviors.setup { context =>
      def work(state: RoomState): Behavior[Command] =
        Behaviors.receiveMessage {
          case AuctionInitialized(auctioneer) =>
            val offer = AuctionOffer(id, rand.nextBoolean(), rand.between(0.0, 1.0), rand.between(1, 100))
            context.log.info(s"Agent $id sending $offer")
            auctioneer ! offer
            work(state)
          case AuctionFinished(price) => work(state)
        }

      work(RoomState(1, 1, 1, 20.0, 100))
    }

}

case class RoomState(
    powerConsumed: Int,
    powerAvailable: Int,
    heatLevel: Int,
    temperature: Double,
    money: Int
)
