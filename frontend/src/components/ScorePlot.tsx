import axios from 'axios';
import { useState } from 'react';
import Plot from 'react-plotly.js';
import config from '../config.json';
import { timeStamp } from 'console';


interface ScorePlotProps {};
interface PlotData {
  times: Array<number>;
  stds: Array<number>;
}

export default function ScorePlot(props: ScorePlotProps) {
  const [plotData, setPlotData] = useState<PlotData>({times: [], stds: []});

  const refreshData = () => {
    axios({
      method: 'get',
      baseURL: config.backendEndpointUrl,
      url: '/metric',
      headers: {
        'Access-Control-Allow-Origin': 'localhost:8080',
      }
    })
    .then(response => {
      setPlotData({
        times: plotData.times.concat(new Date().getTime()),
        stds: plotData.stds.concat(response.data),
      });
    })
    .catch(e => console.log(e))
  }

  
  return (
    <div>
      <Plot
      style={{paddingLeft: "5%", paddingRight: "5%", paddingTop: "5%"}}
      data={[
          {
          x: plotData.times,
          y: plotData.stds,
          type: 'scatter',
          mode: 'lines+markers',
          marker: {color: 'red'},
          },
      ]}
      layout={ {autosize: true, title: 'Cost function'} }
        />
      <button style={{width: "90%", marginLeft: "5%", marginRight: "5%", marginBottom: "5%"}} onClick={refreshData}> Refresh</button>
    </div>
  );
}
