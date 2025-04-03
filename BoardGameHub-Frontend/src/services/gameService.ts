import api from "./api";

const getAllGames = async () => {
  const response = await api.get("/games");
  return response.data;
};

const getGameById = async (id: number) => {
  const response = await api.get(`/games/${id}`);
  return response.data;
};

const createGame = async (gameData: any) => {
  const response = await api.post("/games", gameData);
  return response.data;
};

const updateGame = async (id: number, gameData: any) => {
  const response = await api.put(`/games/${id}`, gameData);
  return response.data;
};

const deleteGame = async (id: number) => {
  await api.delete(`/games/${id}`);
};

export default {
  getAllGames,
  getGameById,
  createGame,
  updateGame,
  deleteGame,
};

