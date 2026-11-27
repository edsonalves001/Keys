import { useRef, useState } from "react";
import Abas from "../../components/Abas";
import Avatar from "../../components/Avatar";
import Header from "../../components/Header";
import PlanilhaPanel from "../../components/PlanilhaPanel";
import { useToast } from "../../context/ToastContext";
import useTitle from "../../hooks/useTitle";
import { emailOk } from "../../utils/helpers";
import api from "../../api/api.js";
import "./adicionarUsuario.css";

const VAZIO = {nm: "", em2: "", id: "", senha: "", confirmarSenha: "",};
const senhaOk = (s) => s.length >= 8 && /[A-Z]/.test(s) && /[a-z]/.test(s) && /\d/.test(s) && /[@#$%^&+=!]/.test(s);

const Olho = () => (
  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor"strokeWidth="2" aria-hidden="true">
    <path d="M1.5 12S5.5 5 12 5s10 7 10.5 7-4 7-10.5 7S1.5 12 1.5 12z" />
    <circle cx="12" cy="12" r="3" />
  </svg>
);

function CampoSenha({id, label, valor, onChange, placeholder, erro,}) {
  const [mostrar, setMostrar] = useState(false);
  return (
    <div className="fld">
      <label htmlFor={id}>{label}</label>
      <div className="w pw">
        <input id={id} type={mostrar ? "text" : "password"} autoComplete="new-password" placeholder={placeholder} className={erro ? "bad" : undefined} aria-invalid={!!erro} value={valor} onChange={onChange}/>
        <button className="eyeb" type="button" aria-label={(mostrar ? "Ocultar" : "Mostrar") + " senha"} aria-pressed={mostrar} onClick={() => setMostrar((m) => !m)}><Olho /></button>
      </div>
      <p className="err" role="alert">{erro}</p>
    </div>
  );
}

export default function AdicionarUsuario() {
  useTitle("Adicionar usuário – Keys");
  const toast = useToast();
  const nomeRef = useRef();
  const [aba, setAba] = useState("m");
  const [f, setF] = useState(VAZIO);
  const [erros, setErros] = useState({});
  const [fila, setFila] = useState([]);
  const [ed, setEd] = useState(-1);
  const [enviando, setEnviando] = useState(false);
  const campo = (k) => (e) => {
    setF((x) => ({ ...x, [k]: e.target.value }));
    setErros((x) => ({ ...x, [k]: "" }));
  };

  const duplicado = (k, valor) =>
    fila.some((u, i) => i !== ed && u[k].trim().toLowerCase() === valor.trim().toLowerCase());
  const validar = () => {
    const nm = f.nm.trim();
    const em = f.em2.trim();
    const id = f.id.trim();
    const e = {
      nm: nm ? "" : "Informe o nome completo.",
      em2: !emailOk(em) ? "Informe um e-mail válido." : duplicado("e", em) ? "Este e-mail já está na fila." : "",
      id: !id ? "Informe o identificador." : duplicado("i", id) ? "Este identificador já está na fila." : "",
      senha: senhaOk(f.senha) ? "" : "A senha ainda não atende aos requisitos.",
      confirmarSenha: f.confirmarSenha && f.confirmarSenha === f.senha ? "" : "As senhas não coincidem.",
    };
    setErros(e);
    return Object.values(e).every((m) => !m);
  };

  const limpar = () => {
    setEd(-1);
    setF(VAZIO);
    setErros({});
  };

  const enviar = (e) => {
    e.preventDefault();
    if (enviando || !validar()) return;
    const u = {
      n: f.nm.trim(),
      e: f.em2.trim(),
      i: f.id.trim(),
      senha: f.senha,
      confirmarSenha: f.confirmarSenha,
    };

    if (ed >= 0) {
      setFila((l) =>
        l.map((x, i) => (i === ed ? u : x))
      );
      toast("Usuário atualizado.");

    } else {
      setFila((l) => [...l, u]);
      toast("Usuário adicionado à fila.");

    }
    limpar();
  };

  const editar = (i) => {
    const u = fila[i];
    setEd(i);
    setF({
      nm: u.n,
      em2: u.e,
      id: u.i,
      senha: u.senha,
      confirmarSenha: u.confirmarSenha,
    });

    setErros({});
    nomeRef.current?.focus();
  };

  const remover = (i) => {
    setFila((l) => l.filter((_, k) => k !== i));

    if (ed === i) {
      limpar();
    } else if (ed > i) {
      setEd((atual) => atual - 1);
    }
  };

  const salvar = async () => {
    if (fila.length === 0) {
      toast("Não há usuários a serem criados.");
      return;
    }

    if (enviando) return;
    setEnviando(true);
    const token = localStorage.getItem("token");

    if (!token) {
      toast("Sessão expirada. Faça login novamente.");
      setEnviando(false);
      return;
    }

    let cadastrados = 0;
    try {
      for (const u of fila) {
        await api.post("/auth/criarUser",
          {
            cd_usuario: u.i,
            nm_usuario: u.n,
            nm_email_usuario: u.e,
            nm_senha_usuario: u.senha,
            nm_confirmar_senha: u.confirmarSenha,
            cd_cnpj_empresa: "generico",
          },
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        cadastrados++;
      }
      toast( cadastrados + (cadastrados > 1 ? " usuários cadastrados com sucesso." : " usuário cadastrado com sucesso."));
      setFila([]);
      limpar();
    } catch (erro) {
      console.error("Erro ao cadastrar usuários:", erro);
      toast(erro?.response?.data?.mensagem || erro?.response?.data?.erro || erro?.response?.data?.message || "Não foi possível cadastrar todos os usuários.");

    } finally {
      setEnviando(false);
    }
  };

  const resumo = fila.length ? `${fila.length} ${fila.length > 1 ? "usuários" : "usuário"} na fila` : "Nenhum usuário na fila.";

  const regras = [
    ["Mínimo de 8 caracteres", f.senha.length >= 8],
    ["Uma letra maiúscula", /[A-Z]/.test(f.senha)],
    ["Uma letra minúscula", /[a-z]/.test(f.senha)],
    ["Um número", /\d/.test(f.senha)],
    ["Um caractere especial (@#$%^&+=!)", /[@#$%^&+=!]/.test(f.senha),],
  ];

  return (
    <>
      <Header variant="app" ativo="usuarios" />
      <main className="pg p-adicionar-usuario">
        <div className="title">
          <div>
            <h1>Adicionar usuário</h1>
            <p>Cadastre uma pessoa por vez ou importe várias de uma só vez.</p>
          </div>
          <Abas aba={aba} onChange={setAba} />
        </div>

        <div hidden={aba !== "m"}>
          <div className="cols2">
            <section className="pan">
              <form onSubmit={enviar} noValidate>
                <div className="sh">
                  <span>Dados do usuário</span>
                </div>
                <div className="fld">
                  <label htmlFor="nm">Nome completo</label>
                  <input id="nm" ref={nomeRef} autoComplete="off" placeholder="Ex.: Ronaldo Pereira" className={erros.nm ? "bad" : undefined} aria-invalid={!!erros.nm} value={f.nm}  onChange={campo("nm")}/>
                  <p className="err" role="alert">{erros.nm}</p>
                </div>
                <div className="fld" style={{ marginTop: 4 }}>
                  <label htmlFor="em2">E-mail institucional</label>
                  <input id="em2" type="email" autoComplete="off" placeholder="nome@instituicao.edu.br" className={erros.em2 ? "bad" : undefined} aria-invalid={!!erros.em2} value={f.em2} onChange={campo("em2")}/>
                  <p className="err" role="alert">{erros.em2}</p>
                </div>
                <div className="fld" style={{ marginTop: 4 }}>
                  <label htmlFor="id">Identificador</label>
                  <input id="id" autoComplete="off" placeholder="Matrícula ou código" className={erros.id ? "bad" : undefined} aria-invalid={!!erros.id} value={f.id} onChange={campo("id")}/>
                  <p className="err" role="alert">{erros.id}</p>
                </div>
                <div className="sh">
                  <span>Acesso</span>
                </div>

                <CampoSenha id="senha" label="Senha" valor={f.senha} placeholder="Crie uma senha" onChange={campo("senha")} erro={erros.senha}/>

                <CampoSenha id="confirmarSenha" label="Confirmar senha" valor={f.confirmarSenha} placeholder="Repita a senha" onChange={campo("confirmarSenha")} erro={erros.confirmarSenha}/>

                <ul className="rules">
                  {regras.map(([t, v]) => (
                    <li key={t} className={v ? "ok" : undefined}>
                      <i></i>
                      {t}
                    </li>
                  ))}
                </ul>

                <p className="hint">A senha será cadastrada junto com os dados do usuário.</p>

                <button className="btn2 full" type="submit" disabled={enviando}>
                  {ed >= 0 ? "Atualizar usuário" : "Adicionar à fila"}
                </button>

                {ed >= 0 && (
                  <button className="out" type="button" style={{width: "100%", marginTop: 10, padding: 12,}} onClick={limpar}>Cancelar edição</button>
                )}

              </form>
            </section>

            <section className="pan">
              <div className="hd">
                <h2>Fila de cadastro{" "}
                  <span className="count">{fila.length ? `(${fila.length})` : ""}</span>
                </h2>
              </div>

              {fila.length > 0 ? (
                <div className="tw" style={{ marginTop: 12 }}>
                  <table className="dt">
                    <thead>
                      <tr>
                        <th>Usuário</th>
                        <th>Identificador</th>
                        <th>
                          <span className="sr">Ações</span>
                        </th>
                      </tr>
                    </thead>

                    <tbody>
                      {fila.map((u, i) => (
                        <tr key={u.i} className={i === ed ? "ed" : undefined}>
                          <td>
                            <div className="u">
                              <Avatar nome={u.n} />
                              <span>{u.n}<small>{u.e}</small></span>
                            </div>
                          </td>
                          <td data-l="Identificador">{u.i}</td>
                          <td className="a">
                            <button className="out" type="button" onClick={() => editar(i)} disabled={enviando}>Editar</button>
                            <button className="out d" type="button" onClick={() => remover(i)} disabled={enviando}>Excluir</button>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              ) : (
                <p className="empty">
                  Os usuários adicionados aparecem aqui para você
                  revisar antes de salvar.</p>
              )}

              <div className="foot">
                <p aria-live="polite">{resumo}</p>
                <button className="btn2" type="button" disabled={!fila.length || enviando} onClick={salvar}>{enviando ? "Criando..." : "Salvar usuários"}</button>
              </div>
            </section>
          </div>
        </div>
        <div hidden={aba !== "x"}>
          <PlanilhaPanel tipo="usr" titulo="Importar usuários via Excel" nota="Use o modelo para preencher nome, identificador e e-mail de cada pessoa."/>
        </div>
      </main>
    </>
  );
}
