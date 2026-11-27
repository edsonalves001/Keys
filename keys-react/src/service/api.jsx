import axios from "axios";

const api = axios.create({
    baseURL: "http://localhost:8080/api",
    headers: {
        "Content-Type": "application/json"
    }
});

api.interceptors.request.use(
    config => {
        const token = localStorage.getItem("token");

        if (!token) {
            window.location.href = "/login";
            return Promise.reject(new Error("Token não encontrado"));
        }

        config.headers = config.headers || {};
        config.headers.Authorization = `Bearer ${token}`;

        return config;
    },
    error => Promise.reject(error)
);


export default api;