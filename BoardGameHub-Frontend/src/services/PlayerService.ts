import api from "./api";

export const playerService = {
  // Post a new player
  async createPlayer(playerData: any) {
    try {
      const response = await api.post("/players", playerData);
      return response;
    } catch (error: any) {
      throw error.response.data.errors[0];
    }
  },

  // Login a player
  async loginPlayer(playerData: any) {
    try {
      const response = await api.post("/players/login", playerData);
      return response;
    } catch (error: any) {
      console.log(error);
      throw error.response.data.errors[0];
    }
  },

  // Get player by ID
  async getPlayerById(playerId: number) {
    try {
      const response = await api.get(`/players/${playerId}`);
      return response;
    } catch (error: any) {
      throw error.response.data.errors[0];
    }
  },

  // Update player by ID
  async updatePlayer(playerId: number, playerData: any) {
    try {
      const response = await api.put(`/players/${playerId}`, playerData);
      return response;
    } catch (error: any) {
      throw error.response.data.errors[0];
    }
  },
};
