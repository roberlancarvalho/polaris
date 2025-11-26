import axios from "axios";
import { Capacitor } from "@capacitor/core";

const MEU_IP = "10.0.2.2";

let coreBase = "http://localhost:8080/api/v1";
let authBase = "http://localhost:8081/api/v1";

if (Capacitor.getPlatform() === "android") {
  coreBase = `http://${MEU_IP}:8080/api/v1`;
  authBase = `http://${MEU_IP}:8081/api/v1`;
  console.log(`RESET: Usando IP da Rede: ${MEU_IP}`);
}

const api = axios.create({
  baseURL: coreBase,
  headers: { "Content-Type": "application/json" },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("polaris_token");
  if (token) config.headers.Authorization = `Bearer ${token}`;

  if (
    config.url &&
    (config.url.includes("/auth/login") ||
      config.url.includes("/auth/register"))
  ) {
    config.baseURL = "";
    config.url = `${authBase}${config.url}`;
  }
  return config;
});

export default api;
