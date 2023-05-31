package smartbuilding

import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior, PostStop, Scheduler}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.core.util.StatusPrinter
import org.slf4j.{Logger, LoggerFactory}
import smartbuilding.RoomAgent.GetInfo

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.util.{Failure, Success}

object SimulationManager extends JsonSupport {
  // TODO: Fix this, try logging in auctioneer or implement additional logging actor that calculates metric periodically
  // once a time tick.
  val logger = LoggerFactory.getLogger("ExpLog")
  var time_tick = 0

  sealed trait Message

  private final case class StartFailed(cause: Throwable) extends Message

  private final case class Started(binding: ServerBinding) extends Message

  case object Stop extends Message

  sealed trait Response

  case class RoomResponse(name: String, state: RoomState, settings: RoomSettings) extends Response

  def apply(settings: SimulationSettings): Behavior[Message] =
    Behaviors.setup { context =>
      implicit val system: ActorSystem[Nothing] = context.system
      implicit val timeout: Timeout = Timeout(3 seconds)
      implicit val scheduler: Scheduler = context.system.scheduler

      val logContext = LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext]
      StatusPrinter.print(logContext)

      val roomAgents = settings.roomSettings.map { case (id, roomSettings) =>
        (id, context.spawn(RoomAgent(id, settings.buildingSettings, roomSettings), id))
      }
      val auctioneer =
        context.spawn(Auctioneer(settings.epochDuration, roomAgents.values.toList), "auctioneer")

      val routes = cors() {
        get {
          concat(
            pathPrefix("room" / Remaining) { id =>
              roomAgents.get(id) match {
                case Some(agent) =>
                  onComplete(agent.ask(GetInfo)) {
                    case Failure(exception) => failWith(exception)
                    case Success(response @ RoomResponse(name, state, settings)) =>
                      complete(response)
                  }
                case None => complete(StatusCodes.NotFound)
              }
            },
            path("metric") {
              val roomInfos = roomAgents.values.map(_.ask(GetInfo))
              onComplete(Future.sequence(roomInfos)) {
                case Failure(exception) => failWith(exception)
                case Success(responses: Iterable[RoomResponse]) =>
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

                  time_tick += 1
                  logger.info(s"$time_tick,${settings.buildingSettings.thermalCapacity},${settings.buildingSettings.thermalResistance},${Math.sqrt(variance)}")

                  complete(Math.sqrt(variance).toString)
              }
            }
          )
        }
      }

      val serverBinding: Future[Http.ServerBinding] =
        Http().newServerAt(settings.serverSettings.host, settings.serverSettings.port).bind(routes)
      context.pipeToSelf(serverBinding) {
        case Success(binding) => Started(binding)
        case Failure(ex)      => StartFailed(ex)
      }

      starting(false)
    }

  def running(binding: ServerBinding): Behavior[Message] =
    Behaviors
      .receive[Message] { (ctx, msg) =>
        msg match {
          case Stop =>
            ctx.log.info(
              "Stopping server http://{}:{}/",
              binding.localAddress.getHostString,
              binding.localAddress.getPort
            )
            Behaviors.stopped
        }
      }
      .receiveSignal { case (_, PostStop) =>
        binding.unbind()
        Behaviors.same
      }

  def starting(wasStopped: Boolean): Behaviors.Receive[Message] =
    Behaviors.receive[Message] { (ctx, msg) =>
      msg match {
        case StartFailed(cause) =>
          throw new RuntimeException("Server failed to start", cause)
        case Started(binding) =>
          ctx.log.info(
            "Server online at http://{}:{}/",
            binding.localAddress.getHostString,
            binding.localAddress.getPort
          )
          if (wasStopped) ctx.self ! Stop
          running(binding)
        case Stop =>
          starting(wasStopped = true)
      }
    }

}
