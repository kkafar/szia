package smartbuilding

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import smartbuilding.SimulationManager.{AllRoomsResponse, ConfigResponse, ModifyDesiredTemperatureRequest, RoomResponse}
import spray.json.DefaultJsonProtocol
import spray.json.RootJsonFormat

trait JsonSupport extends SprayJsonSupport {
  import DefaultJsonProtocol._

  implicit val roomSettingsFormat: RootJsonFormat[RoomSettings] = jsonFormat5(RoomSettings.apply)
  implicit val roomStateFormat: RootJsonFormat[RoomState] = jsonFormat3(RoomState.apply)
  implicit val roomResponseFormat: RootJsonFormat[RoomResponse] = jsonFormat3(RoomResponse.apply)
  implicit val putDesiredTempFormat: RootJsonFormat[ModifyDesiredTemperatureRequest] = jsonFormat1(ModifyDesiredTemperatureRequest.apply)
  implicit val buildingSettingsFormat: RootJsonFormat[BuildingSettings] = jsonFormat2(BuildingSettings.apply)
  implicit val configResponseFormat: RootJsonFormat[ConfigResponse] = jsonFormat3(ConfigResponse.apply)
  implicit val allRoomResponseFormat: RootJsonFormat[AllRoomsResponse] = jsonFormat1(AllRoomsResponse.apply)
}