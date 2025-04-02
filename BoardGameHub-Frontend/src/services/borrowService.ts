import api from "./api";

export const borrowRequestService = {
  async getRequestsByRequesterId(playerId: number) {
    try {
      const response = await api.get(`/borrow-requests/requester/${playerId}`);
      return response.data;
    } catch (error: any) {
      console.error("Error fetching borrow requests:", error);
      throw error.response.data.errors[0];
    }
  },

  async cancelBorrowRequest(requestId: string, userId: number) {
    try {
      const response = await api.delete(`/borrow-requests/${requestId}?userId=${userId}`);
      return response.data;
    } catch (error: any) {
      console.error("Error cancelling borrow request:", error);
      throw error.response.data.errors[0];
    }
  },
};
