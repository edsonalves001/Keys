import { useEffect, useState } from "react";
import { Navigate, Outlet } from "react-router-dom";

export default function AuthGuard() {
    const [autenticado, setAutenticado] = useState(
        () => !!localStorage.getItem("token")
    );

    useEffect(() => {
        function verificarToken() {
            const token = localStorage.getItem("token");
            setAutenticado(!!token);
        }

        // Detecta alterações no localStorage em outras abas
        function aoAlterarStorage(event) {
            if (event.key === "token" || event.key === null) {
                verificarToken();
            }
        }

        // Detecta alterações feitas na própria aba
        window.addEventListener("auth-change", verificarToken);

        // Detecta alterações feitas em outras abas
        window.addEventListener("storage", aoAlterarStorage);

        return () => {
            window.removeEventListener("auth-change", verificarToken);
            window.removeEventListener("storage", aoAlterarStorage);
        };
    }, []);

    if (!autenticado) {
        return <Navigate to="/login" replace />;
    }

    return <Outlet />;
}
