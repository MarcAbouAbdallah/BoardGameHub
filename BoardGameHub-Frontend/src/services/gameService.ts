import api from "./api";

const gameService = {
  // GET /games
  async getAllGames() {
    try {
      const response = await api.get("/games");
      return response.data;
    } catch (error) {
      console.error("Error fetching games:", error);
      throw error;
    }
  },

  // GET /games/{id}
  async getGameById(gameId: number) {
    try {
      const response = await api.get(`/games/${gameId}`);
      return response.data;
    } catch (error) {
      console.error("Error fetching game:", error);
      throw error;
    }
  },

  // POST /games
  async createGame(gameData: any) {
    try {
      const response = await api.post("/games", gameData);
      return response.data;
    } catch (error: any) {
      console.error("Error creating game:", error);
      throw error.response.data.errors[0];
    }
  },

  // PUT /games/{id}
  async updateGame(gameId: number, gameData: any) {
    try {
      const response = await api.put(`/games/${gameId}`, gameData);
      return response.data;
    } catch (error: any) {
      console.error("Error updating game:", error);
      throw error.response.data.errors[0];
    }
  },

  // DELETE /games/{id}
  async deleteGame(gameId: number) {
    try {
      const response = await api.delete(`/games/${gameId}`);
      return response.data;
    } catch (error: any) {
      console.error("Error deleting game:", error);
      throw error.response.data.errors[0];
    }
  },
};

export default gameService;