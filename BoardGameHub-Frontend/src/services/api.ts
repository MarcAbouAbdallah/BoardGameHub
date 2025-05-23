import axios from "axios";

const api = axios.create({
  baseURL: import.meta.env.PROD ? "/api" : "http://localhost:8080",
  headers: {
    "Content-Type": "application/json",
  },
});

export default api;
