import api from "./api";

export const reviewService = {
    async getReviewsByPlayerId(playerId: string) {
      try {
        const response = await api.get(`/reviews/player/${playerId}`);
        return response.data;
      } catch (error) {
        console.error("Error fetching reviews by player ID:", error);
        throw error;
      }
    },
  }