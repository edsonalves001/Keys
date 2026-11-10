import axios from "axios";

export const api = axios.create({
  baseURL: "http://localhost:8080/", // prefixo todas urls
  timeout: 10000,                     // 10s e cancela se b.o
  headers: { "Content-Type": "application/json" },
});