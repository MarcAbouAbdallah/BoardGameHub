import api from "./api";

export const eventService = {
  // GET /events
  async getAllEvents() {
    try {
      const response = await api.get("/events");
      return response.data;
    } catch (error) {
      console.error("Error fetching events:", error);
      throw error;
    }
  },

  // GET /events/{eventId}
  async getEvent(eventId: number) {
    try {
      const response = await api.get(`/events/${eventId}`);
      return response.data;
    } catch (error) {
      console.error("Error fetching event:", error);
      throw error;
    }
  },

  // POST /events
  async createEvent(eventData: any) {
    try {
      const response = await api.post("/events", eventData);
      return response.data;
    } catch (error) {
      console.error("Error creating event:", error);
      throw error;
    }
  },

  // PUT /events/{eventId}?userId={userId}
  async updateEvent(eventId: number, eventData: any) {
    try {
      // We expect eventData to have a userId prop for the query param:
      const response = await api.put(
        `/events/${eventId}?userId=${eventData.userId}`,
        eventData
      );
      return response.data;
    } catch (error) {
      console.error("Error updating event:", error);
      throw error;
    }
  },

  // DELETE /events/{eventId}?userId={userId}
  async deleteEvent(eventId: number, userId: number) {
    try {
      const response = await api.delete(`/events/${eventId}?userId=${userId}`);
      return response.data;
    } catch (error) {
      console.error("Error deleting event:", error);
      throw error;
    }
  },
};