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

  // PATCH /gamecopies/{gameCopyId} - update availability
  async updateGameCopyAvailability(gameCopyId: number, dto: any) {
    try {
      const response = await api.patch(`/gamecopies/${gameCopyId}`, dto);
      return response.data;
    } catch (error) {
      console.error("Error updating game copy availability:", error);
      throw error;
    }
  },

  // DELETE /gamecopies/{gameCopyId}?userId={userId}
  async removeGameCopy(gameCopyId: number, userId: number) {
    try {
      const response = await api.delete(`/gamecopies/${gameCopyId}?userId=${userId}`);
      return response.data;
    } catch (error) {
      console.error("Error removing game copy:", error);
      throw error;
    }
  },
};