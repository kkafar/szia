import { RoomInfo } from "./types";

export default class RoomInfoService {
  serverAddr: string;
  dataHandler: (data: Array<RoomInfo>) => void;

  constructor(serverAddr: string, dataHandler: (data: Array<RoomInfo>) => void) {
    this.serverAddr = serverAddr;
    this.dataHandler = dataHandler;
  }

  // startFetching

}
