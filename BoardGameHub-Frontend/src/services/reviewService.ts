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

    async updateReview(
        reviewId: string,
        userId: string,
        data: { rating?: number; comment?: string }
      ) {
        try {
          const response = await api.put(`/reviews/${reviewId}?userId=${userId}`, data);
          return response.data;
        } catch (error) {
          console.error("Error updating review:", error);
          throw error;
        }
      },

      async deleteReview(reviewId: string, userId: string) {
        try {
          const response = await api.delete(`/reviews/${reviewId}?userId=${userId}`);
          return response.data;
        } catch (error) {
          console.error("Error deleting review:", error);
          throw error;
        }
      },
  }