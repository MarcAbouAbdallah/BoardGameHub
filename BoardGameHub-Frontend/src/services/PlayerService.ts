import api from "./api";
import { useToast } from "@/components/ui/toast";

const { toast } = useToast();

export const playerService = {
  // Post a new player
  async createPlayer(playerData: any) {
    try {
      console.log("Creating player with data:", playerData);
      const response = await api.post("/players", playerData);
      toast({
        title: "Success",
        description: "Player created successfully. Please login using your credentials.",
        variant: "default",
      });
      console.log("Player created successfully:", response.data);

      return response.data;
    } catch (error: any) {
      if (error.response.status === 409) {
        toast({
          title: "Error",
          description:
            "Player with this email already exists. Please login or use a different email.",
          variant: "destructive",
        });
      } else {
        toast({
          title: "Error",
          description: error.response.data.errors[0],
          variant: "destructive",
        });
      }
      throw error;
    }
  },

  // Login a player
  async loginPlayer(playerData: any) {
    try {
      console.log("Logging in player with data:", playerData);
      const response = await api.post("/players/login", playerData);
      console.log("Player logged in successfully:", response.data);
      toast({
        title: "Success",
        description: "Player logged in successfully.",
        variant: "default",
      });
      return response.data;
    } catch (error: any) {
      if (error.response.status === 401) {
        toast({
          title: "Error",
          description: "Invalid email or password. Please try again.",
          variant: "destructive",
        });
      } else {
        toast({
          title: "Error",
          description: error.response.data.errors[0],
          variant: "destructive",
        });
      }
      throw error;
    }
  },

  // Get player by ID
  async getPlayerById(playerId: number) {
    try {
      const response = await api.get(`/players/${playerId}`);
      console.log("Player fetched successfully:", response.data);
      toast({
        title: "Success",
        description: "Player fetched successfully.",
        variant: "default",
      });
      return response.data;
    } catch (error: any) {
      if (error.response.status === 404) {
        toast({
          title: "Error",
          description: "Player not found.",
          variant: "destructive",
        });
      } else {
        toast({
          title: "Error",
          description: error.response.data.errors[0],
          variant: "destructive",
        });
      }
      throw error;
    }
  },

  // Update player by ID
  async updatePlayer(playerId: number, playerData: any) {
    try {
      const response = await api.put(`/players/${playerId}`, playerData);
      console.log("Player updated successfully:", response.data);
      toast({
        title: "Success",
        description: "Player updated successfully.",
        variant: "default",
      });
      return response.data;
    } catch (error: any) {
      if (error.response.status === 404) {
        toast({
          title: "Error",
          description: "Player not found.",
          variant: "destructive",
        });
      } else {
        toast({
          title: "Error",
          description: error.response.data.errors[0],
          variant: "destructive",
        });
      }
      throw error;
    }
  },
};
