import { RoomInfo } from "../types";
import '../styles/Room.css';
import uparrowImage from '../assets/up-arrow.svg';
import downarrowImage from '../assets/down-arrow.svg';
import { useEffect } from "react";

type ListItemProps = {
  title: string,

};

function ListItem(props: ListItemProps) {

};

export default function Room(props: RoomInfo) {

  useEffect(() => {

  }, [props.state.temperature]);

  function resolveBgColor() {
    if (Math.abs(props.state.temperature - props.settings.desiredTemperature) / props.settings.desiredTemperature <= 0.1) {
      return '#a8c256';
    } else {
      return '#ff6e52';
    }
  }

  return (
    <div className="Room">
      <div className="Room-content" style={{ backgroundColor: resolveBgColor() }}>
        <div className="Room-list">
          <p>ID: {props.name ?? "Unknown"}</p>
          <ul>
            <li>Default temperature: {props.settings.defaultTemperature.toFixed(2)}</li>
            <li>Target temperature: {props.settings.desiredTemperature.toFixed(2)}</li>
            <li>Power available: {props.state.powerAvailable.toFixed(2)}</li>
            <li>Power consumed: {props.state.powerConsumed.toFixed(2)}</li>
            <li>Current temperature: {props.state.temperature.toFixed(2)}</li>
          </ul>
        </div>
      </div>
      <div className="Room-filler">

      </div>
    </div>
  );
}
