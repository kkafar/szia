import { SimulationSettings } from "../types";
import config from '../config.json'
import axios from "axios";

export class SimulationService {
  async getInitialConfiguration(): Promise<SimulationSettings> {
    let response;

    try {
      response = await axios({
        method: 'get',
        baseURL: config.backendEndpointUrl,
        url: '/config',
        headers: {
          'Access-Control-Allow-Origin': 'localhost:8080',
        }
      });
    } catch (error) {
      console.error(error)
      throw new Error("Some error occured while tryincg to fetch data. Maybe backed server is down?"); 
    }

    const responseData: SimulationSettings = response.data;
    return responseData;
  }
}
