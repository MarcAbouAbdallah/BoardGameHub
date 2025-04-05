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

  async getPendingRequestsByRequesteeId(playerId: number) {
    try {
      const response = await api.get(`/borrow-requests/requestee/${playerId}?status=PENDING`);
      return response.data;
    } catch (error: any) {
      console.error("Error fetching borrow requests:", error);
      throw error.response.data.errors[0];
    }
  },

  async respondToBorrowRequest(requestId: string, response: 'ACCEPTED' | 'DECLINED') {
    try {
      return await api.put(`/borrow-requests/${requestId}/status`, {
        status: response
      });
    } catch (error) {
      console.error("Error responding to request:", error);
      throw error;
    }
  },

  async createBorrowRequest(
    data: {gameCopyId: string; requesteeId: string; startDate: string; endDate: string; comment: string; requesterId: number },
  ) {
    try {
      const response = await api.post(`/borrow-requests`, data);
      return response.data;
    } catch (error: any) {
      console.error("Error creating borrow request:", error);
      throw error.response.data.errors[0];
    }
  },

  async getAcceptedRequestsByRequesterId(playerId: number) {
    try {
      const response = await api.get(`/borrow-requests/requester/${playerId}?status=ACCEPTED`);
      return response.data;
    } catch (error: any) {
      console.error("Error fetching borrow requests:", error);
      throw error.response.data.errors[0];
    }
  },

  async getAcceptedRequestsByRequesteeId(playerId: number) {
    try {
      const response = await api.get(`/borrow-requests/requestee/${playerId}?status=ACCEPTED`);
      return response.data;
    } catch (error: any) {
      console.error("Error fetching borrow requests:", error);
      throw error.response.data.errors[0];
    }
  },

};
