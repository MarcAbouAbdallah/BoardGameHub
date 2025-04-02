// src/services/registrationService.ts
import api from "./api";

export const registrationService = {
  // POST /registrations/{eventId}/{playerId}
  async registerToEvent(eventId: number, playerId: number) {
    try {
      const response = await api.post(`/registrations/${eventId}/${playerId}`);
      return response.data;
    } catch (error) {
      console.error("Error registering to event:", error);
      throw error;
    }
  },

  // GET /registrations
  async findAllRegistrations() {
    try {
      const response = await api.get("/registrations");
      return response.data;
    } catch (error) {
      console.error("Error fetching all registrations:", error);
      throw error;
    }
  },

  // GET /registrations/event/{eventId}
  async findRegistrationsByEvent(eventId: number) {
    try {
      const response = await api.get(`/registrations/event/${eventId}`);
      return response.data;
    } catch (error) {
      console.error("Error fetching registrations by event:", error);
      throw error;
    }
  },

  // GET /registrations/player/{playerId}
  async findRegistrationsByPlayer(playerId: number) {
    try {
      const response = await api.get(`/registrations/player/${playerId}`);
      return response.data;
    } catch (error) {
      console.error("Error fetching registrations by player:", error);
      throw error;
    }
  },

  // GET /registrations/{eventId}/{playerId}
  async findRegistration(eventId: number, playerId: number) {
    try {
      const response = await api.get(`/registrations/${eventId}/${playerId}`);
      return response.data;
    } catch (error) {
      console.error("Error fetching registration:", error);
      throw error;
    }
  },

  // DELETE /registrations/{eventId}/{playerId}
  async unregisterFromEvent(eventId: number, playerId: number) {
    try {
      await api.delete(`/registrations/${eventId}/${playerId}`);
    } catch (error) {
      console.error("Error unregistering from event:", error);
      throw error;
    }
  },
};