export type RoomSettings = {
  initialEnergy: number,
  defaultTemperature: number,
  desiredTemperature: number,
}

export type RoomState = {
  powerAvailable: number,
  powerConsumed: number,
  temperature: number,
}

export type RoomInfo = {
  name: string,
  settings: RoomSettings,
  state: RoomState,
}
