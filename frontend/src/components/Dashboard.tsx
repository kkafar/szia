/* eslint-disable react-hooks/exhaustive-deps */
import { useEffect, useState } from 'react';
import '../App.css';

import { RoomInfo, SimulationSettings } from '../types';
import axios from 'axios';
import RoomList from './RoomList';
import config from '../config.json';
import ScorePlot from './ScorePlot';

type DashboardProps = {
  simulationSettings: SimulationSettings
};

function Dashboard(props: DashboardProps) {
  const [roomList, setRoomList] = useState<Array<RoomInfo>>([]);

  const getData = async () => {
    try {
      const result = await axios.all(
        props.simulationSettings.roomSettings.map((settings) => {
          return axios({
            method: 'get',
            baseURL: config.backendEndpointUrl,
            url: '/room/' + settings.id,
            headers: {
              'Access-Control-Allow-Origin': 'localhost:8080',
            }
          })
        })
      )
      setRoomList(result.map((response) => response.data));
    } catch (e) {
      console.log(e);
    }
  };

  useEffect(() => {
    const intervalHandle = setInterval(() => {
      getData();
    }, config.requestInterval);

    return () => {
      clearInterval(intervalHandle);
    };
  }, []);

  return (
    <div className="App">
      <RoomList rooms={roomList} />
      <ScorePlot />
    </div>
  );
}

export default Dashboard;
