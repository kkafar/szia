export type SimulationSettings = {
  epochDuration: number,
  buildingSettings: {
    thermalCapacity: number,
    thermalResistance: number,
  },
  roomSettings: Array<RoomSettings>
}

export type RoomSettings = {
  id: string,
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
