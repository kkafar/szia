/* eslint-disable react-hooks/exhaustive-deps */
import { useEffect, useState } from 'react';
import './App.css';

import { SimulationSettings } from './types';
import Dashboard from './components/Dashboard';
import { SimulationService } from './services/SimulationService';


function App() {
  const [initialDataFetched, setInitialDataFetched] = useState(false);

  const [simSettings, setSimSettings] = useState<SimulationSettings>({
    epochDuration: 1,
    buildingSettings: {
      thermalCapacity: 10.0,
      thermalResistance: 10.0,
    },
    roomSettings: [],
  });


  useEffect(() => {
    if (!initialDataFetched) {
      const intervalHandle = setInterval(() => {
        new SimulationService().getInitialConfiguration()
          .then((response: SimulationSettings) => {
            console.log(JSON.stringify(response));
            setSimSettings(response);
            setInitialDataFetched(true);
          })
          .catch(console.error)
      });
      return () => clearInterval(intervalHandle);
    }
  }, []);
  
  if (initialDataFetched === true) {
    return (
      <Dashboard simulationSettings={simSettings} />
    );
  } else {
    return (<div>Loading...</div>);
  }

}

export default App;
