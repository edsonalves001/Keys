import { useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import Header from "../../components/Header";
import PasswordField from "../../components/PasswordField";
import useTitle from "../../hooks/useTitle";
import api  from "../../api/api.js";
import "./novaSenha.css";

export default function NovaSenha() {
  useTitle("Nova senha – Keys");
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const token = searchParams.get("token");
  const [senha, setSenha] = useState("");
  const [confirmar, setConfirmar] = useState("");
  const [validandoToken, setValidandoToken] = useState(true);
  const [tokenValido, setTokenValido] = useState(false);
  const [salvando, setSalvando] = useState(false);
  const [mensagem, setMensagem] = useState("");
  const [erro, setErro] = useState(false);
  const regras = [
    ["Mínimo de 8 caracteres", senha.length >= 8],
    ["Uma letra maiúscula", /[A-Z]/.test(senha)],
    ["Uma letra minúscula", /[a-z]/.test(senha)],
    ["Um número", /\d/.test(senha)],
    ["Um caractere especial (@#$%^&+=!)", /[@#$%^&+=!]/.test(senha)],
  ];
  const senhaValida = regras.every(([, ok]) => ok);
  const senhasIguais = senha === confirmar && confirmar.length > 0;
  useEffect(() => {
    let ativo = true;
    async function validarToken() {
      if (!token) {
        setTokenValido(false);
        setMensagem("Link de recuperação inválido ou incompleto.");
        setErro(true);
        setValidandoToken(false);
        return;
      }

      try {
        await api.get("api/auth/validar-token", {
          params: { token },
        });

        if (ativo) {
          setTokenValido(true);
        }
      } catch (error) {
        console.error("Erro ao validar token:", error);

        if (ativo) {
          setTokenValido(false);
          setErro(true);
          setMensagem(error.response?.data?.mensagem || "Este link de recuperação é inválido ou expirou. Solicite um novo link.");
        }

      } finally {
        if (ativo) {
          setValidandoToken(false);
        }
      }
    }
    validarToken();
    return () => {ativo = false;};
  }, [token]);

  const salvar = async (e) => {
    e.preventDefault();
    setMensagem("");
    setErro(false);
    if (!token || !tokenValido) {
      setErro(true);
      setMensagem("Seu link de recuperação não é mais válido.");
      return;
    }
    if (!senhaValida) {
      setErro(true);
      setMensagem("A senha não atende a todos os requisitos.");
      return;
    }
    if (!senhasIguais) {
      setErro(true);
      setMensagem("As senhas não coincidem.");
      return;
    }
    setSalvando(true);
    try {
      const response = await api.post("api/auth/nova-senha", {token, senha, senha2: confirmar,});
      setMensagem(response.data?.mensagem || "Senha atualizada com sucesso!");
      setTimeout(() => {navigate("/login");}, 1800);
    } catch (error) {
      console.error("Erro ao redefinir senha:", error);
      const dados = error.response?.data;
      setErro(true);
      setMensagem(dados?.erros?.join(" ") || dados?.mensagem ||"Não foi possível atualizar sua senha. Tente novamente.");

    } finally {
      setSalvando(false);
    }
  };

  return (
    <>
      <Header />
      <main className="c p-nova-senha">
        <form className="card navy" onSubmit={salvar}>
          <h1>Crie sua nova senha</h1>
          <p className="s">Escolha uma senha que você ainda não usou no Keys.</p>
          {validandoToken ? (
            <p className="mensagem-info">Validando seu link de recuperação...</p>
          ) : !tokenValido ? (
            <>
              <p className="mensagem-erro" role="alert">{mensagem}</p>
              <button type="button" className="btn" onClick={() => navigate("/recuperar")}>Solicitar novo link</button>
            </>
          ) : (
            <>
              <label htmlFor="n1">Nova senha</label>
              <PasswordField id="n1" placeholder="Nova senha" value={senha} onChange={(e) => setSenha(e.target.value)}/>
              <ul className="req">
                {regras.map(([texto, ok]) => (
                  <li key={texto} className={ok ? "ok" : undefined}>
                    <i></i>
                    {texto}
                  </li>
                ))}
              </ul>
              <label htmlFor="n2">Confirmar nova senha</label>
              <PasswordField id="n2" placeholder="Repita a nova senha" value={confirmar} onChange={(e) => setConfirmar(e.target.value)}/>
              {confirmar && (
                <p className={senhasIguais ? "mensagem-sucesso" : "mensagem-erro"}>{senhasIguais ? "As senhas coincidem." : "As senhas não coincidem."}</p>
              )}
              {mensagem && (
                <p className={erro ? "mensagem-erro" : "mensagem-sucesso"} role="alert">{mensagem}</p>
              )}
              <button className="btn" type="submit" disabled={salvando || !senhaValida || !senhasIguais}> {salvando ? "Salvando..." : "Salvar nova senha"}</button>
            </>
          )}
        </form>
      </main>
    </>
  );
}
