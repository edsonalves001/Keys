import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import Header from "../../components/Header";
import PasswordField from "../../components/PasswordField";
import useTitle from "../../hooks/useTitle";
import auth from "../../service/auth";
import "./login.css";
import imagemlogin from "../../assets/fotoLogin.png"

export default function Login() {
  useTitle("Acesse o Keys");
  const navigate = useNavigate();
  const [email, setEmail] = useState(() => localStorage.getItem("emailLogin") || "")
  const [senha, setSenha] = useState("");
  const [carregando, setCarregando] = useState(false);

  async function entrar(e) {
    e.preventDefault();
    setCarregando(true);
    try {
      const response = await auth.post("/login", {
        nm_email_usuario: email,
        nm_senha_usuario: senha,
      });
      const token = response.data.token;
      if (!token) {
        throw new Error("O servidor não retornou um token.");
      }
      localStorage.setItem("token", token);
      alert("Login realizado com sucesso!");
      console.log("Token armazenado:", localStorage.getItem("token"));
      navigate("/cronograma");

    } catch (error) {
      console.error("Erro ao realizar login:", error);
      alert("Não foi possível realizar o login. Verifique seu e-mail e senha.");

    } finally {
      setCarregando(false);
    }
  }

  return (
    <>
      <Header />
      <main className="c p-login">
        <div className="login">
          <div className="ph">
            <img id="fotoLogin" src={imagemlogin}></img>
          </div>
          <form className="fm" onSubmit={entrar}>
            <h1>Bem-vindo de volta</h1>
            <p>Entre para ver as reservas do dia.</p>
            <label htmlFor="em">E-mail</label>
            <div className="f">
              <input id="em" type="email" placeholder="nome@instituicao.edu.br" required value={email} onChange={(e) => setEmail(e.target.value)}/>
            </div>
            <label htmlFor="pw">Senha</label>
            <PasswordField id="pw" placeholder="Digite sua senha" required value={senha} onChange={(e) => setSenha(e.target.value)}/>
            <div className="row">
              <Link className="lk" to="/recuperar">Esqueci minha senha</Link>
              <Link className="lk" to="/registro">Adquirir o Keys</Link>
            </div>
            <button className="btn" type="submit" disabled={carregando}>{carregando ? "Entrando..." : "Entrar"}</button>
          </form>
        </div>
      </main>
    </>
  );
}
