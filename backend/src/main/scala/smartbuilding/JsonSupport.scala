package smartbuilding

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import smartbuilding.SimulationManager.RoomResponse
import spray.json.DefaultJsonProtocol
import spray.json.RootJsonFormat

trait JsonSupport extends SprayJsonSupport {
  import DefaultJsonProtocol._

  implicit val roomSettingsFormat: RootJsonFormat[RoomSettings] = jsonFormat2(RoomSettings.apply)
  implicit val roomStateFormat: RootJsonFormat[RoomState] = jsonFormat3(RoomState.apply)
  implicit val roomResponseFormat: RootJsonFormat[RoomResponse] = jsonFormat3(RoomResponse.apply)
}