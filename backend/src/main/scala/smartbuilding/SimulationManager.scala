package smartbuilding

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior, PostStop}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives.complete

import scala.concurrent.Future
import scala.util.{Failure, Success}

object SimulationManager {
  sealed trait Message
  private final case class StartFailed(cause: Throwable) extends Message
  private final case class Started(binding: ServerBinding) extends Message
  case object Stop extends Message

  def apply(settings: SimulationSettings): Behavior[Message] =
    Behaviors.setup { context =>
      implicit val system: ActorSystem[Nothing] = context.system

      val roomAgents = settings.roomSettings.map { case (id, roomSettings) =>
        (id, context.spawn(RoomAgent(id, settings.buildingSettings, roomSettings), id))
      }
      val auctioneer =
        context.spawn(Auctioneer(settings.epochDuration, roomAgents.values.toList), "auctioneer")

      val routes = complete("server works!!1")

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
