package smartbuilding

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import smartbuilding.SimulationManager.{RoomResponse, SetDesiredTempRequest}
import spray.json.DefaultJsonProtocol
import spray.json.RootJsonFormat

trait JsonSupport extends SprayJsonSupport {
  import DefaultJsonProtocol._

  implicit val roomSettingsFormat: RootJsonFormat[RoomSettings] = jsonFormat3(RoomSettings.apply)
  implicit val roomStateFormat: RootJsonFormat[RoomState] = jsonFormat3(RoomState.apply)
  implicit val roomResponseFormat: RootJsonFormat[RoomResponse] = jsonFormat3(RoomResponse.apply)
  implicit val putDesiredTempFormat: RootJsonFormat[SetDesiredTempRequest] = jsonFormat1(SetDesiredTempRequest.apply)
}