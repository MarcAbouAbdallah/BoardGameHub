import api from "./api";

export const playerService = {
  // Post a new player
  async createPlayer(playerData: any) {
    try {
      const response = await api.post("/players", playerData);
      return response;
    } catch (error) {
      console.error("Error creating player:", error);
      throw error;
    }
  },

  // Login a player
  async loginPlayer(playerData: any) {
    try {
      const response = await api.post("/players/login", playerData);
      return response;
    } catch (error) {
      console.error("Error logging in player:", error);
      throw error;
    }
  },

  // Get player by ID
  async getPlayerById(playerId: number) {
    try {
      const response = await api.get(`/players/${playerId}`);
      return response;
    } catch (error) {
      console.error("Error fetching player:", error);
      throw error;
    }
  },

  // Update player by ID
  async updatePlayer(playerId: number, playerData: any) {
    try {
      const response = await api.put(`/players/${playerId}`, playerData);
      return response;
    } catch (error) {
      console.error("Error updating player:", error);
      throw error;
    }
  },
};
