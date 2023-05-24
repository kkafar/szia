import { useEffect, useState } from 'react';
import './App.css';

import { RoomInfo } from './types';
import axios from 'axios';
import RoomList from './components/RoomList';
import config from './config.json';


function App() {
  const roomIds: Array<string> = ["alfa", "beta", "gamma"];
  const [roomList, setRoomList] = useState<Array<RoomInfo>>([]);

  function updateState(data: Array<RoomInfo>) {
    console.log("Updating state");
    data.forEach(data => console.log(data));
    setRoomList(data);
  }

  const getData = async () => {
    console.log("Fetching data");
    try {
      const result = await axios.all(
        roomIds.map((id) => {
          return axios({
            method: 'get',
            baseURL: config.backendEndpointUrl,
            url: '/room/' + id,
            headers: {
              'Access-Control-Allow-Origin': 'localhost:8080',
            }
          })
        })
      )

      updateState(result.map((response) => response.data))
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

  return (
    <div className="App">
      <RoomList rooms={roomList} />
    </div>
  );
}

export default App;
