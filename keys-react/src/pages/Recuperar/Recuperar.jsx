import { useState } from "react";
import { Link } from "react-router-dom";
import Header from "../../components/Header";
import useTitle from "../../hooks/useTitle";
import api from "../../api/api.js";
import "./recuperar.css";

export default function Recuperar() {
  useTitle("Recuperar senha – Keys");
  const [email, setEmail] = useState("");
  const [enviando, setEnviando] = useState(false);
  const [mensagem, setMensagem] = useState("");
  const [erro, setErro] = useState(false);
  const enviar = async (e) => {
    e.preventDefault();
    setMensagem("");
    setErro(false);
    if (!email.trim()) {
      setErro(true);
      setMensagem("Informe seu endereço de e-mail.");
      return; 
    }
    setEnviando(true);
    try {
      const response = await api.post("/auth/esqueci-senha",
        {
          email: email.trim(),
        }
      );

      setMensagem(response.data?.mensagem || "Se o e-mail estiver cadastrado, você receberá um link para redefinir sua senha.");
      setEmail("");

    } catch (error) {
      console.error("Erro ao solicitar recuperação:", error);
      setErro(true);
      setMensagem(error.response?.data?.mensagem ||"Não foi possível solicitar a recuperação. Tente novamente." + error);

    } finally {
      setEnviando(false);
    }
  };

  return (
    <>
      <Header />
      <main className="c p-recuperar">
        <form className="card navy" style={{ maxWidth: 520 }} onSubmit={enviar}>
          <div className="ico">
            <svg width="34" height="34" viewBox="0 0 24 24" fill="none" stroke="#0a1a2e" strokeWidth="3" strokeLinecap="round">
              <circle cx="8" cy="16" r="3.5" />
              <path d="M11 13l8-8M16 8l2.5 2.5" />
            </svg>
          </div>
          <h1>Esqueceu sua senha?</h1>
          <p className="s">
            Informe seu e-mail e enviaremos um link
            para criar uma nova senha.
          </p>
          <label htmlFor="re">E-mail</label>
          <div className="f">
            <input id="re" type="email" placeholder="nome@instituicao.edu.br" required value={email}
              onChange={(e) => {
                setEmail(e.target.value);
                setMensagem("");
                setErro(false);
              }}
              disabled={enviando}
            />
          </div>

          {mensagem && (
            <p className={erro ? "mensagem-erro" : "mensagem-sucesso"} role="status">{mensagem}</p>
          )}

          <button className="btn" type="submit" disabled={enviando}>{enviando ? "Enviando..." : "Enviar link"}</button>
          <p style={{ textAlign: "center", margin: "26px 0 0",}}>
            <Link className="lk" to="/login" style={{ fontSize: 20 }}>Voltar ao login</Link>
          </p>
        </form>
      </main>
    </>
  );
}
