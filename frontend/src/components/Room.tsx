import { RoomInfo } from "../types";
import '../styles/Room.css';
import uparrowImage from '../assets/up-arrow.svg';
import downarrowImage from '../assets/down-arrow.svg';
import { ChangeEvent, useEffect, useRef, useState } from "react";
import Plot from "react-plotly.js";
import config from '../config.json';
import axios from "axios";

type ListItemProps = {
  title: string,

};

function ListItem(props: ListItemProps) {

};

export default function Room(props: RoomInfo) {
  const [data, setData] = useState<Array<number>>([])
  const [targetTemp, setTargetTemp] = useState<number>(props.settings.desiredTemperature);
  const inputRef = useRef<HTMLInputElement>(null);

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

  const handleInputChange = (event: ChangeEvent<HTMLInputElement>) => {
    console.log(event.target.value)
    // setTargetTemp(event.target.value);
  };

  const handleTempChange = async () => {
    const temperature = parseFloat(inputRef.current?.value!);
    setTargetTemp(temperature);
    axios({
      method: 'put',
      baseURL: config.backendEndpointUrl,
      url: '/room/' + props.name,
      data: {
        desiredTemperature: temperature,
      },
      headers: {
        'Access-Control-Allow-Origin': 'localhost:8080',
      }
    })
  };

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
        <div className="Room-input-wrapper">
          <input type="text" ref={inputRef} onChange={handleInputChange}></input>
          <button onClick={handleTempChange}>Confirm</button>
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
