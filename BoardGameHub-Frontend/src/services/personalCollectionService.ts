import api from "./api";

export const addGameToCollection = async (playerId: number, gameId: number) => {
  const response = await api.post("/gamecopies", {
    playerId,
    gameId,
  });
  return response.data;
};

