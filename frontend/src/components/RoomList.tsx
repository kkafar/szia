import { RoomInfo } from "../types";
import Room from "./Room";
import '../styles/RoomList.css'

type RoomListProps = {
  rooms: Array<RoomInfo>,
};

export default function RoomList(props: RoomListProps) {
  return (
    <div className="RoomList">
      {props.rooms.map((room, id) => {
        return <Room key={id} name={room.name}  settings={room.settings} state={room.state} />
      })}
    </div>
  );
}
