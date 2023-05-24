import { RoomInfo } from "./types";

export default class RoomInfoService {
  serverAddr: string;
  callback: (data: Array<RoomInfo>) => void;

  constructor(serverAddr: string, callback: (data: Array<RoomInfo>) => void) {
    this.serverAddr = serverAddr;
    this.callback = callback;
  }

  // startFetching

}
