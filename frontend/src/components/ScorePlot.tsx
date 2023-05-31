import axios from 'axios';
import { useEffect, useState } from 'react';
import Plot from 'react-plotly.js';
import config from '../config.json';
import { timeStamp } from 'console';


interface ScorePlotProps { };
interface PlotData {
  times: Array<number>;
  stds: Array<number>;
}

export default function ScorePlot(props: ScorePlotProps) {
  const [plotData, setPlotData] = useState<PlotData>({ times: [], stds: [] });

  const refreshData = async () => {
    axios({
      method: 'get',
      baseURL: config.backendEndpointUrl,
      url: '/metric',
      headers: {
        'Access-Control-Allow-Origin': 'localhost:8080',
      }
    })
      .then(response => {
        setPlotData((oldPlotData) => {
          return {
            times: oldPlotData.times.concat(new Date().getTime()),
            stds: oldPlotData.stds.concat(response.data),
          }
        })
      })
      .catch(e => console.log(e))
  }

  useEffect(() => {
    const intervalHandle = setInterval(() => {
      refreshData();
    }, config.requestInterval);

    return () => {
      clearInterval(intervalHandle);
    };
  }, []);


  return (
    <div>
      <Plot
        style={{ paddingLeft: "5%", paddingRight: "5%", paddingTop: "5%" }}
        data={[
          {
            x: plotData.times,
            y: plotData.stds,
            type: 'scatter',
            mode: 'lines+markers',
            marker: { color: 'red' },
          },
        ]}
        layout={{ autosize: true, title: 'Cost function' }}
      />
    </div>
  );
}
