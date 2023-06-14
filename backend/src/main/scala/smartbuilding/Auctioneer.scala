package smartbuilding

import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior, Scheduler}
import akka.util.Timeout
import org.slf4j.LoggerFactory
import smartbuilding.RoomAgent.OfferResult
import smartbuilding.SimulationManager.RoomResponse
import smartbuilding.SmartBuildingApp.actorSystem.executionContext

import java.time.Instant
import scala.annotation.tailrec
import scala.concurrent.duration.{Duration, SECONDS}
import scala.concurrent.{Await, Future}
import scala.util.{Success, Try}

object Auctioneer {
  sealed trait Command

  case class AuctionOffer(id: String, sell: Boolean, volume: Double, price: Int, meta: SimulationManager.RoomResponse) extends Command

  private val logger = LoggerFactory.getLogger("ExpLog")

  def apply(
             epochDuration: Long,
             roomAgents: List[ActorRef[RoomAgent.Command]],
             settings: SimulationSettings,
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

          roomAgents.zip(responses).foreach { case (agent, offer) =>
            if (offer.sell && offer.price <= price) agent ! OfferResult(auctionNumber, -offer.volume)
            else if (!offer.sell && offer.price >= price) agent ! OfferResult(auctionNumber, offer.volume)
            else agent ! OfferResult(auctionNumber, 0.0)
          }

          val now = Instant.now()
          if (now.isBefore(deadline)) Thread.sleep(deadline.toEpochMilli - now.toEpochMilli)
          context.log.info(s"Finished auction $auctionNumber with clearing price $price.")

          val metric = calculateMetric(responses.map(_.meta))
          logger.info(s"$auctionNumber,${settings.buildingSettings.thermalCapacity},${settings.buildingSettings.thermalResistance},${metric}")

          work(auctionNumber + 1)
        case _ =>
          context.log.error("Unable to finish the auction")
          work(auctionNumber + 1)
      }
    }

    def calculateMetric(responses: Iterable[RoomResponse]) = {
      val n = responses.size
      val desiredTemperatures = responses.map(_.settings.desiredTemperature)
      val actualTemperatures = responses.map(_.state.temperature)
      val avgDesired = desiredTemperatures.foldLeft(0.0)(_ + _) / n
      val avgActual = actualTemperatures.foldLeft(0.0)(_ + _) / n
      val variance =
        desiredTemperatures
          .zip(actualTemperatures)
          .map { case (actual, desired) =>
            Math.pow((actual - desired) - (avgActual - avgDesired), 2)
          }
          .foldLeft(0.0)(_ + _) / n
      Math.sqrt(variance)
    }

    def findClearingPrice(offers: List[AuctionOffer]): Int = {
      val prices = offers.map(_.price)
      val differences = for {
        p <- prices
        (sellOffers, buyOffers) = offers.partition(_.sell)
        sellSum = sellOffers.filter(_.price <= p).foldLeft(0.0)(_ + _.volume)
        buySum = buyOffers.filter(_.price >= p).foldLeft(0.0)(_ + _.volume)
      } yield (sellSum - buySum).abs

      prices.zip(differences).minBy(_._2)._1
    }

    work(1)
  }

}
