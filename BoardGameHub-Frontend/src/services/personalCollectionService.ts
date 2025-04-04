import api from "./api";

export const addGameToCollection = async (playerId: number, gameId: number) => {
  const response = await api.post("/gamecopies", {
    playerId,
    gameId,
  });
  return response.data;
};

export async function getPlayerCollection(playerId: number) {
  const response = await api.get(`/gamecopies/players/${playerId}`);
  return response.data;
}

