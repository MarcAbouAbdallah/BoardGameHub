import api from "./api";

export const gameCopyService = {
  // GET /gamecopies/games/{id} - fetch all copies for a specific game
  async getGameCopiesForGame(gameId: number) {
    try {
      const response = await api.get(`/gamecopies/games/${gameId}`);
      return response.data;
    } catch (error) {
      console.error("Error fetching game copies for game:", error);
      throw error;
    }
  },

  // Optionally, GET available game copies for a player
  async getAvailableGameCopies(playerId: number) {
    try {
      const response = await api.get("/gamecopies/available", { data: { playerId } });
      return response.data;
    } catch (error) {
      console.error("Error fetching available game copies:", error);
      throw error;
    }
  },

  // GET /gamecopies/{gameCopyId}
  async getGameCopyById(gameCopyId: number) {
    try {
      const response = await api.get(`/gamecopies/${gameCopyId}`);
      return response.data;
    } catch (error) {
      console.error("Error fetching game copy by id:", error);
      throw error;
    }
  },

  // POST /gamecopies - add a game copy to a player's personal collection
  async addGameCopy(playerId: number, gameId: number) {
    try {
      const response = await api.post("/gamecopies", { playerId, gameId });
      return response.data;
    } catch (error) {
      console.error("Error adding game copy:", error);
      throw error;
    }
  },

  async getPlayerCollection(playerId: number) {
    try {
      const response = await api.get(`/gamecopies/players/${playerId}`);
      return response.data;
    } catch (error: any) {
      console.error("Error fetching player collection:", error);
      throw error.response.data.errors[0];
    }
  },

  // DELETE /gamecopies/{gameCopyId}?userId={userId}
  async removeGameCopy(gameCopyId: number, userId: number) {
    try {
      const response = await api.delete(`/gamecopies/${gameCopyId}?userId=${userId}`);
      return response.data;
    } catch (error: any) {
      console.error("Error removing game copy:", error);
      throw error.response.data.errors[0];
    }
  },
};