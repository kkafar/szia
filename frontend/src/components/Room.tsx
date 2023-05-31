import { RoomInfo } from "../types";
import '../styles/Room.css';
import uparrowImage from '../assets/up-arrow.svg';
import downarrowImage from '../assets/down-arrow.svg';
import { useEffect, useState } from "react";
import Plot from "react-plotly.js";

type ListItemProps = {
  title: string,

};

function ListItem(props: ListItemProps) {

};

export default function Room(props: RoomInfo) {
  const [data, setData] = useState<Array<number>>([])

  useEffect(() => {
    setData((data) => [...data, props.state.temperature]);
  }, [props.state.temperature]);

  function resolveBgColor() {
    if (Math.abs(props.state.temperature - props.settings.desiredTemperature) / props.settings.desiredTemperature <= 0.1) {
      return '#a8c256';
    } else {
      return '#ff6e52';
    }
  }

  return (
    <div className="Room" style={{ flexDirection: 'row' }}>
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
      <div>
        <Plot
          className="Room-plot"
          data={[
            {
              x: data.map((d, i) => i),
              y: data.map((d: number) => d.toFixed(2)),
              type: 'scatter',
              mode: 'lines+markers',
              marker: { color: 'red' },
              name: 'Temp',
            },
            {
              x: data.map((d, i) => i), 
              y: Array(data.length).fill(props.settings.desiredTemperature),
              mode: 'lines',
              name: 'Target',
            }
          ]}
          layout={{ title: 'Temperature' }}
        />
      </div>
    </div>
  );
}
