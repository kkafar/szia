import React, { useEffect, useState } from 'react';
import logo from './logo.svg';
import './App.css';

import { RoomInfo, RoomState, RoomSettings } from './types';
import axios from 'axios';

type RoomListProps = {
  rooms: Array<RoomInfo>,
};

function RoomList(props: RoomListProps) {
  return (
    <div>
      {props.rooms.map((room) => {
        return <p>{room.settings.desiredTemperature}</p>
      })}
    </div>
  );
}

const SERVER_URL = "http://localhost:8080";


function App() {
  const roomIds: Array<string> = ["alfa", "beta", "gamma"];
  const [roomList, setRoomList] = useState<Array<RoomInfo>>([]);

  function updateState(data: RoomInfo) {
    console.log("Updating state");
    setRoomList([data]);
  }

  const getData = async () => {
    console.log("Fetching data");
    try {
      const result = await axios({
        method: 'get',
        baseURL: SERVER_URL,
        url: '/room/alfa',
        headers: {
          'Access-Control-Allow-Origin': 'localhost:8080',
        }
      });

      updateState(result.data as RoomInfo);
    } catch (e) {
      console.log(e);
    }
  };

  useEffect(() => {
    const intervalHandle = setInterval(() => {
      getData();
    }, 2000);

    return () => {
      clearInterval(intervalHandle);
    };
  }, []);

  const MockRoom: RoomInfo = {
    settings: {
      defaultTemperature: 20.0,
      desiredTemperature: 25.0,
    },
    state: {
      powerAvailable: 300.0,
      powerConsumed: -30.0,
      temperature: 19.0,
    }
  };

  return (
    <div className="App">
      Hello world
      <RoomList rooms={roomList} />
    </div>
  );
}

export default App;
