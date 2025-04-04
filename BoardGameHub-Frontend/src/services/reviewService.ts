import api from "./api";

export const reviewService = {
  async getReviewsByPlayerId(playerId: number) {
    try {
      const response = await api.get(`/reviews/player/${playerId}`);
      return response.data;
    } catch (error: any) {
      console.error("Error fetching reviews by player ID:", error);
      throw error.response.data.errors[0];
    }
  },

  async updateReview(
    reviewId: string,
    userId: number,
    data: { rating?: number; comment?: string },
  ) {
    try {
      const response = await api.put(`/reviews/${reviewId}?userId=${userId}`, data);
      return response.data;
    } catch (error: any) {
      console.error("Error updating review:", error);
      throw error.response.data.errors[0];
    }
  },

  async deleteReview(reviewId: number, userId: number) {
    try {
      const response = await api.delete(`/reviews/${reviewId}?userId=${userId}`);
      return response.data;
    } catch (error: any) {
      console.error("Error deleting review:", error);
      throw error.response.data.errors[0];
    }
  },

  async getReviewsByGameName(gameName: string) {
    try {
      const response = await api.get(`/reviews/game/${gameName}`);
      return response.data;
    }
    catch (error: any) {
      console.error("Error fetching reviews by game name:", error);
      throw error.response.data.errors[0];
    }
  },

  async createReview(
    data: { rating?: number; comment?: string; gameName: String; reviewerId: number},
  ) {
    try {
      const response = await api.post(`/reviews`, data);
      return response.data;
    } catch (error: any) {
      console.error("Error creating review:", error);
      throw error.response.data.errors[0];
    }
  },

};
