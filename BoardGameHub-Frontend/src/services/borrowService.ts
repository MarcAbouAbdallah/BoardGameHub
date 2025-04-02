import api from "./api";

export const borrowRequestService = {

  async getRequestsByRequesterId(playerId: string) {
    try {
      const response = await api.get(`/borrow-requests/requester/${playerId}`);
      return response.data;
    } catch (error) {
      console.error("Error fetching borrow requests:", error);
      throw error;
    }
  },

  async cancelBorrowRequest(requestId: string, userId: string) {
    try {
      const response = await api.delete(`/borrow-requests/${requestId}?userId=${userId}`);
      return response.data;
    } catch (error) {
      console.error("Error cancelling borrow request:", error);
      throw error;
    }
  },
};
