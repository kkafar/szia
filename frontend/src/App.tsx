/* eslint-disable react-hooks/exhaustive-deps */
import { useEffect, useState } from 'react';
import './App.css';

import { SimulationSettings } from './types';
import Dashboard from './components/Dashboard';
import { SimulationService } from './services/SimulationService';


function App() {
  const [simSettings, setSimSettings] = useState<SimulationSettings>({
    epochDuration: 1,
    buildingSettings: {
      thermalCapacity: 10.0,
      thermalResistance: 10.0,
    },
    roomSettings: [],
  });


  useEffect(() => {
    new SimulationService().getInitialConfiguration()
      .then((response: SimulationSettings) => {
        setSimSettings(response);
      })
      .catch(console.warn)
  }, []);

  return (
    <Dashboard simulationSettings={simSettings} />
  );
}

export default App;
