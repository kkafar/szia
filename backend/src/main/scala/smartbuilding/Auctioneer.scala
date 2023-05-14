package smartbuilding

import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior, Scheduler}
import akka.util.Timeout
import smartbuilding.SmartBuildingApp.actorSystem.executionContext

import java.time.Instant
import scala.annotation.tailrec
import scala.concurrent.duration.{Duration, SECONDS}
import scala.concurrent.{Await, Future}
import scala.util.{Random, Success, Try}

object Auctioneer {
  sealed trait Command
  case class AuctionOffer(id: String, sell: Boolean, volume: Double, price: Int) extends Command

  private val rand = new Random()

  def apply(
      epochDuration: Long,
      roomAgents: List[ActorRef[RoomAgent.Command]]
  ): Behavior[Command] = Behaviors.setup { context =>
    implicit val timeout: Timeout = Timeout(epochDuration, SECONDS)
    implicit val scheduler: Scheduler = context.system.scheduler

    @tailrec
    def work(auctionNumber: Int): Behavior[Command] = {
      context.log.info(s"Beginning auction no. $auctionNumber")
      val deadline = Instant.now.plusSeconds(epochDuration)
      val futureResponses =
        for (agent <- roomAgents) yield agent.ask(RoomAgent.AuctionInitialized)
      // TODO: asynchronize

      Try(Await.result(Future.sequence(futureResponses), Duration(epochDuration, SECONDS))) match {
        case Success(responses: List[AuctionOffer]) =>
          val price = findClearingPrice(responses)
          roomAgents.foreach(_ ! RoomAgent.AuctionFinished(price))
          val now = Instant.now()
          if (now.isBefore(deadline)) Thread.sleep(deadline.toEpochMilli - now.toEpochMilli)
          context.log.info(s"Finished auction $auctionNumber with clearing price $price.")
          work(auctionNumber + 1)
        case _ =>
          context.log.error("Unable to finish auction")
          work(auctionNumber + 1)
      }
    }

    def findClearingPrice(offers: List[AuctionOffer]): Int = {
      offers.map(_.price).head
    }

    work(1)
  }

}
