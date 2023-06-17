/* eslint-disable react-hooks/exhaustive-deps */
import { useEffect, useState } from 'react';
import '../App.css';

import { RoomInfo, SimulationSettings } from '../types';
import RoomList from './RoomList';
import config from '../config.json';
import ScorePlot from './ScorePlot';
import { SimulationService } from '../services/SimulationService';

type DashboardProps = {
  simulationSettings: SimulationSettings
};

function Dashboard(props: DashboardProps) {
  const [roomList, setRoomList] = useState<Array<RoomInfo>>([]);

  const getData = async () => {
    try {
      const result = await SimulationService.getAllRoomsInformation();
      setRoomList(result.roomSettings);
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
