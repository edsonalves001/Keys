import { useEffect } from "react";
import { Navigate, Route, Routes, useLocation } from "react-router-dom";
import Landing from "./pages/Landing/Landing";
import Login from "./pages/Login/Login";
import Recuperar from "./pages/Recuperar/Recuperar";
import NovaSenha from "./pages/NovaSenha/NovaSenha";
import Cronograma from "./pages/Cronograma/Cronograma";
import Reserva from "./pages/Reserva/Reserva";
import Usuarios from "./pages/Usuarios/Usuarios";
import AdicionarUsuario from "./pages/AdicionarUsuario/AdicionarUsuario";
import EditarReserva from "./pages/EditarReserva/EditarReserva";
import Registro from "./pages/Registro/Registro";
import VLibras from "../src/components/VLibras";
import AuthGuard from "./service/AuthGuard";

function ScrollTopo() {
  const { pathname } = useLocation();
  useEffect(() => {
    window.scrollTo(0, 0);
  }, [pathname]);
  return null;
}

export default function App() {
  return (
    <>
      <ScrollTopo />
      <VLibras />
      <Routes>
        <Route path="/" element={<Landing />} />
        <Route path="/login" element={<Login />} />
        <Route path="/recuperar" element={<Recuperar />} />
        <Route path="/nova-senha" element={<NovaSenha />} />
        <Route path="/registro" element={<Registro />} />

        <Route element={<AuthGuard />}>
          <Route path="/cronograma" element={<Cronograma />} />
          <Route path="/reserva" element={<Reserva />} />
          <Route path="/usuarios" element={<Usuarios />} />
          <Route path="/adicionar-usuario" element={<AdicionarUsuario />} />
          <Route path="/editar-reserva/:id" element={<EditarReserva />} />
        </Route>

        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </>
  );
}
