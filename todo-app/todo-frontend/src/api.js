import axios from "axios";

// Zentrale API-Konfiguration — URL aus Env-Variable, Fallback auf localhost
const API_URL = process.env.REACT_APP_API_URL || "http://localhost:8085";

// Axios-Instanz mit Basis-URL — keine hartcodierten URLs mehr im Code
const api = axios.create({
  baseURL: API_URL,
  headers: { "Content-Type": "application/json" },
});

export const todoApi = {
  getAll: () => api.get("/todos"),
  create: (title) => api.post("/todos", { title, done: false }),
  delete: (id) => api.delete(`/todos/${id}`),
  toggleDone: (id) => api.put(`/todos/${id}/done`),
  updateTitle: (id, newTitle) =>
    api.put(`/todos/${id}/title`, newTitle, {
      headers: { "Content-Type": "text/plain" },
    }),
};

export default api;