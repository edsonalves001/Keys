import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import Avatar from "../../components/Avatar";
import Header from "../../components/Header";
import { useToast } from "../../context/ToastContext";
import useTitle from "../../hooks/useTitle";
import { corSala, dtxt } from "../../utils/helpers";
import api  from "../../api/api.js";
import "./usuarios.css";

const pl = (n) => n + (n === 1 ? " reserva" : " reservas");
const reservaFinalizada = (reserva) => reserva.statusReserva === "FINALIZADA";
const formatarHorario = (data) => {
  if (!data) return "--:--";
  const horario = data.split("T")[1];
  return horario ? horario.substring(0, 5) : "--:--";
};

const formatarData = (data) => {
  if (!data) return "Data não informada";
  return dtxt(data.split("T")[0]);
};

const formatarStatus = (status) => {
  switch (status) {
    case "ESPERANDO_EFETIVACAO":
      return "Pendente";

    case "EFETIVADA":
      return "Efetivada";

    case "FINALIZADA":
      return "Finalizada";

    case "CANCELADA":
      return "Cancelada";

    default:
      return status ?? "Não informado";
  }
};

const obterEmailDoToken = (token) => {
  try {
    const partePayload = token?.split(".")[1];
    if (!partePayload) return null;
    const base64 = partePayload.replace(/-/g, "+").replace(/_/g, "/");
    const payload = JSON.parse(atob(base64));
    return payload.sub || payload.nm_email_usuario || payload.email || null;
  } catch {
    return null;
  }
};

export default function Usuarios() {
  useTitle("Usuários – Keys");
  const navigate = useNavigate();
  const toast = useToast();
  const [usersBanco, setUsersBanco] = useState([]);
  const [usuarioExibido, setUsuarioExibido] = useState(null);
  const [reservasUsuario, setReservasUsuario] = useState([]);
  const [busca, setBusca] = useState("");
  const [nomeUser, setNomeUser] = useState("");
  const [emailUser, setEmailUser] = useState("");
  const [abrirModalEditar, setAbrirModalEditar] = useState(false);
  const [abrirModalDeletar, setAbrirModalDeletar] = useState(false);
  const [senhaExclusao, setSenhaExclusao] = useState("");
  const [erroExclusao, setErroExclusao] = useState("");
  const [mostrarSenhaExclusao, setMostrarSenhaExclusao] = useState(false);
  const [excluindoUsuario, setExcluindoUsuario] = useState(false);
  const [carregando, setCarregando] = useState(false);
  const [carregandoReservas, setCarregandoReservas] = useState(false);
  const [abrirModalCancelarReserva, setAbrirModalCancelarReserva] = useState(false);
  const [senhaCancelamento, setSenhaCancelamento] = useState("");
  const [erroCancelamento, setErroCancelamento] = useState("");
  const [reservaSelecionada, setReservaSelecionada] = useState(null);
  const [cancelandoReserva, setCancelandoReserva] = useState(false);
  const [mostrarSenhaCancelamento, setMostrarSenhaCancelamento] = useState(false);

  async function buscarUsers() {
    const token = localStorage.getItem("token");
    try { 
      setCarregando(true);
      const resp = await api.get("/usuarios", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      if (resp.status === 200) {
        setUsersBanco(resp.data);
        setUsuarioExibido((atual) => {
          if (!resp.data.length) return null;
          if (atual) {
            return (resp.data.find((user) => user.id === atual.id) ?? resp.data[0]);
          }
          return resp.data[0];
        });
      }
    } catch (error) {
      console.error("Erro ao buscar usuários:", error);
      toast("Não foi possível carregar os usuários.");
    } finally {
      setCarregando(false);
    }
  }

  async function buscarReservasUsuario(usuario) {
    if (!usuario) {
      setReservasUsuario([]);
      return;
    }
    const token = localStorage.getItem("token");
    try {
      setCarregandoReservas(true);
      setReservasUsuario([]);
      const resposta = await api.get(`/VerReservaDesseUsuario/${usuario.id}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      const reservas = Array.isArray(resposta.data) ? resposta.data : [];
      setReservasUsuario(reservas);
    } catch (error) {
      console.error("Erro ao buscar reservas do usuário:", error);
      setReservasUsuario([]);
      toast("Não foi possível carregar as reservas.");
    } finally {
      setCarregandoReservas(false);
    }
  }

  useEffect(() => { buscarUsers();}, []);
  useEffect(() => { buscarReservasUsuario(usuarioExibido);}, [usuarioExibido]);

  const lista = usersBanco.filter((user) => (user.nome ?? "").toLowerCase().includes(busca.toLowerCase()));
  const reservasAtivas = reservasUsuario.filter((reserva) => !reservaFinalizada(reserva)).sort((a, b) => new Date(a.inicioReserva) - new Date(b.inicioReserva));
  const reservasFinalizadas = reservasUsuario.filter((reserva) => reservaFinalizada(reserva)).sort((a, b) =>  new Date(b.inicioReserva) - new Date(a.inicioReserva));

  function selecionarUsuario(usuario) {
    setUsuarioExibido(usuario);
  }

  function abrirCancelamento(reserva) {
    setReservaSelecionada(reserva);
    setSenhaCancelamento("");
    setErroCancelamento("");
    setMostrarSenhaCancelamento(false);
    setAbrirModalCancelarReserva(true);
  }

  async function handleCancelarReserva() {
    if (!reservaSelecionada) return;
    if (!senhaCancelamento.trim()) {
      setErroCancelamento("Digite sua senha para cancelar a reserva.");
      return;
    }
    setErroCancelamento("");
    const token = localStorage.getItem("token");
    try {
      setCancelandoReserva(true);
      const resposta = await api.delete("/api/ApagarReserva",
        {
          params: {
            id: reservaSelecionada.cdreserva,
            senha: senhaCancelamento,
          },
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      toast(resposta.data || "Reserva cancelada com sucesso!");
      setAbrirModalCancelarReserva(false);
      setErroCancelamento("");
      setSenhaCancelamento("");
      setReservaSelecionada(null);
      await buscarReservasUsuario(usuarioExibido);
    } catch (error) {
      console.error("Erro ao cancelar reserva:", error);
      const mensagem = error.response?.data?.mensagem || error.response?.data?.erro || error.response?.data || "Não foi possível cancelar a reserva.";
      setErroCancelamento(typeof mensagem === "string" ? mensagem : "Não foi possível cancelar a reserva.");

    } finally {
      setCancelandoReserva(false);
    }
  }

  function abrirEdicao() {
    if (!usuarioExibido) return;
    setNomeUser(usuarioExibido.nome ?? "");
    setEmailUser(usuarioExibido.email_user ?? "");
    setAbrirModalEditar(true);
  }

  async function handleAtualizarUser() {
    const token = localStorage.getItem("token");
    if (nomeUser.trim().length === 0 || emailUser.trim().length === 0) {
      toast("Preencha todos os campos.");
      return;
    }

    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(emailUser)) {
      toast("Formato de e-mail inválido.");
      return;
    }

    try {
      const resp = await api.patch(`/api/usuarios/${usuarioExibido.id}`,
        {
          nm_usuario: nomeUser.trim(),
          nm_email_usuario: emailUser.trim(),
        },

        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (resp.status === 200) {
        setAbrirModalEditar(false);
        toast("Usuário atualizado com sucesso!");
        await buscarUsers();
      }

    } catch (error) {
      console.error("Erro ao atualizar usuário:", error);
      setAbrirModalEditar(false);
      toast("Não foi possível atualizar o usuário.");
    }

  }

  async function handleDeletarUser() {
    if (!usuarioExibido) return;
    if (!senhaExclusao.trim()) {
      setErroExclusao("Digite sua senha de administrador para excluir o usuário.");
      return;
    }

    setErroExclusao("");
    const token = localStorage.getItem("token");
    const emailAdministrador = obterEmailDoToken(token);

    if (!emailAdministrador) {
      setErroExclusao("Não foi possível identificar o administrador conectado. Entre novamente.");
      return;
    }

    try {
      setExcluindoUsuario(true);
      await api.post("/auth/login", {
        nm_email_usuario: emailAdministrador,
        nm_senha_usuario: senhaExclusao,
      });

    } catch (error) {
      console.error("Erro ao validar a senha do administrador:", error);
      const status = error.response?.status;
      if (status === 400 || status === 401 || status === 403) {
        setErroExclusao("Senha incorreta. Confira sua senha e tente novamente.");
      } else {
        setErroExclusao("Não foi possível validar sua senha. Tente novamente.");
      }
      return;
    } finally {
      setExcluindoUsuario(false);
    }
    try {
      setExcluindoUsuario(true);
      const resp = await api.delete(`/api/usuarios/${usuarioExibido.id}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (resp.status === 204) {
        const usuariosAtualizados = usersBanco.filter(
          (user) => user.id !== usuarioExibido.id
        );

        setUsersBanco(usuariosAtualizados);
        setUsuarioExibido(usuariosAtualizados[0] ?? null);
        setReservasUsuario([]);
        setAbrirModalDeletar(false);
        setErroExclusao("");
        setSenhaExclusao("");
        setMostrarSenhaExclusao(false);
        toast("Usuário excluído com sucesso!");
      }

    } catch (error) {
      console.error("Erro ao excluir usuário:", error);

      const mensagem = error.response?.data?.mensagem || error.response?.data?.erro || "Não foi possível excluir o usuário.";
      setErroExclusao(typeof mensagem === "string" ? mensagem : "Não foi possível excluir o usuário.");
    } finally {
      setExcluindoUsuario(false);
    }
  }

  function adicionarUsuario() {
    navigate("/addUser");
  }

function classeStatus(status) {
  switch (status) {
    case "ESPERANDO_EFETIVACAO":
      return "status-pendente";

    case "EFETIVADA":
      return "status-efetivada";

    case "FINALIZADA":
      return "status-finalizada";

    case "CANCELADA":
      return "status-cancelada";

    default:
      return "status-desconhecido";
  }
}

function exibirReserva(reserva) {
  const cor = corSala(reserva.nomeSala);
  return (
    <div className="rs" key={reserva.cdreserva}>
      <i className="k" style={{ background: cor,}}/>
      <div className="n">
        {formatarData(reserva.inicioReserva)}
        <small>
          {formatarHorario(reserva.inicioReserva)}
          {" – "}
          {formatarHorario(reserva.fimReserva)}
        </small>
      </div>

      <span className="tag nome-sala">{reserva.nomeSala}</span>

      <span  className={`tag status-reserva ${classeStatus(reserva.statusReserva)}`}>
        {formatarStatus(reserva.statusReserva)}
      </span>

      {reserva.statusReserva === "ESPERANDO_EFETIVACAO" && (
        <>
          <Link className="out" to={`/editar-reserva/${reserva.cdreserva}`} state={{reservaId: reserva.cdreserva,}}> Editar</Link>
          <button className="out d" type="button" onClick={() => abrirCancelamento(reserva)}>Cancelar</button>
        </>
      )}
    </div>
  );
}

  return (
    <>
      <Header variant="app" ativo="usuarios" />
      <main className="pg p-usuarios">
        <div className="title">
          <div>
            <h1>Usuários</h1>
            <p>Veja as reservas de cada pessoa e gerencie o acesso.</p>
          </div>
          <button type="button" className="btn-adicionar-reserva" onClick={() => navigate("/adicionar-usuario")}>
            <span>+</span>  Adicionar usuario</button>
        </div>

        <div className="cols2 u">
          <section className="pan">
            <input className="sf" type="search" placeholder="Pesquisar usuário" aria-label="Pesquisar usuário" style={{ marginTop: 0 }} value={busca} onChange={(e) => setBusca(e.target.value)}/>
            <div className="ul">
              {carregando && (
                <p className="empty">Carregando usuários...</p>
              )}

              {!carregando && usersBanco.length === 0 && (
                <p className="empty">Sem usuários adicionados ainda!</p>
              )}

              {!carregando &&
                lista.map((usuario) => (
                  <button key={usuario.id} type="button" className={usuarioExibido?.id === usuario.id ? "on" : undefined} onClick={() => selecionarUsuario(usuario)}>
                    <Avatar nome={usuario.nome} />
                    <span><strong>{usuario.nome}</strong></span>
                  </button>
                ))}

              {!carregando && usersBanco.length > 0 && lista.length === 0 && 
              (<p className="empty">Nenhum usuário encontrado.</p>)
              }
            </div>
          </section>
          <section className="pan" hidden={!usuarioExibido}>
            {usuarioExibido && (
              <>
                <div className="ph">
                  <Avatar nome={usuarioExibido.nome} big />
                  <div className="m">
                    <h2>{usuarioExibido.nome}</h2>
                    <p>{usuarioExibido.email_user}</p>
                    <div className="chips">
                      <span className="chip">{pl(reservasUsuario.length)}</span>
                    </div>
                  </div>
                  <div className="x">
                    <button className="out" type="button" onClick={abrirEdicao}>Editar usuário</button>
                    <button className="out d" type="button" onClick={() => {
                        setSenhaExclusao("");
                        setMostrarSenhaExclusao(false);
                        setAbrirModalDeletar(true);
                      }}>Excluir usuário</button>
                  </div>
                </div>

                <div className="sh" style={{ marginTop: 28 }}>
                  <span>Próximas reservas</span>
                  <span>{pl(reservasAtivas.length)}</span>
                </div>

                <div>
                  {carregandoReservas ? (
                    <p className="empty">Carregando reservas...</p>
                  ) : reservasAtivas.length > 0 ? (
                    reservasAtivas.map((reserva) =>  exibirReserva(reserva))
                  ) : (
                    <p className="empty">Não existem reservas pendentes ou futuras para este usuário.</p>
                  )}
                </div>

                <div className="sh" style={{ marginTop: 28 }}>
                  <span>Reservas finalizadas</span>
                  <span>{pl(reservasFinalizadas.length)}</span>
                </div>

                <div>
                  {carregandoReservas ? (
                    <p className="empty">Carregando reservas...</p>
                  ) : reservasFinalizadas.length > 0 ? (
                    reservasFinalizadas.map((reserva) => exibirReserva(reserva))
                  ) : (
                    <p className="empty">Nenhuma reserva finalizada.</p>
                  )}
                </div>
              </>
            )}
          </section>
        </div>

        {abrirModalCancelarReserva && (
          <div className="modal-overlay">
            <div className="modal">
              <h2>Cancelar reserva</h2>
              <p>Tem certeza de que deseja cancelar a reserva da sala{" "}
                <strong>{reservaSelecionada?.nomeSala}</strong>?
              </p>

              <p>Para confirmar, digite sua senha de administrador.</p>
              <div className="campo-senha">
                <input type={mostrarSenhaCancelamento ? "text" : "password"} placeholder="Digite sua senha" value={senhaCancelamento}
                  onChange={(e) => {
                    setSenhaCancelamento(e.target.value);
                    if (erroCancelamento) setErroCancelamento("");
                  }}
                  autoComplete="current-password"
                />

                <button type="button" className="ver-senha"
                  onClick={() =>
                    setMostrarSenhaCancelamento((atual) => !atual)
                  }
                  aria-label={
                    mostrarSenhaCancelamento ? "Ocultar senha" : "Mostrar senha"}

                  title={mostrarSenhaCancelamento ? "Ocultar senha" : "Mostrar senha"}
                >

                  {mostrarSenhaCancelamento ? (
                    // Olho Fechado
                    <svg
                      viewBox="0 0 24 24" width="21" height="21" fill="none" stroke="currentColor" strokeWidth="2"
                      strokeLinecap="round"
                      strokeLinejoin="round"
                    >
                      <path d="M3 3l18 18" />
                      <path d="M10.6 10.6a2 2 0 0 0 2.8 2.8" />
                      <path d="M9.9 5.2A10.8 10.8 0 0 1 12 5c5 0 9 5 10 7a13 13 0 0 1-3 3.8" />
                      <path d="M6.6 6.6C4.2 8 2.5 10.3 2 12c1 2 5 7 10 7a10 10 0 0 0 3-.5" />
                    </svg>
                  ) : (

                    // Olho aberto
                    <svg viewBox="0 0 24 24" width="21" height="21" fill="none" stroke="currentColor"
                      strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"
                    >
                      <path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7-10-7-10-7z" />
                      <circle cx="12" cy="12" r="3" /></svg>

                  )}
                </button>
              </div>
              {erroCancelamento && (
                <p className="erro-modal" role="alert"
                  style={{ color: "#b42318", background: "#fef3f2", padding: "10px 12px", borderRadius: 8, margin: "12px 0", fontSize: 14 }}
                >{erroCancelamento}</p>
              )}

              <div className="botoes-acoes">
                <button className="cancelarReserva" onClick={handleCancelarReserva} disabled={cancelandoReserva}>
                  {cancelandoReserva ? "Cancelando..." : "Confirmar cancelamento"}
                </button>

                <button className="manter" disabled={cancelandoReserva}
                  onClick={() => {
                    setAbrirModalCancelarReserva(false);
                    setErroCancelamento("");
                    setSenhaCancelamento("");
                    setMostrarSenhaCancelamento(false);
                    setReservaSelecionada(null);
                  }}>Voltar</button>
              </div>
            </div>
          </div>
        )}

        {abrirModalEditar && (
          <div className="modal-overlay">
            <div className="modal">
              <h2>Edite o usuário</h2>
              <input type="text" placeholder="Digite o novo nome" value={nomeUser} onChange={(e) => setNomeUser(e.target.value)} />
              <input type="email" placeholder="Digite o novo e-mail" value={emailUser} onChange={(e) => setEmailUser(e.target.value)}/>
              <div className="botoes-acoes">
                <button type="button" className="atualizar" onClick={handleAtualizarUser}>Atualizar</button>
                <button  className="out d" type="button" onClick={() => setAbrirModalEditar(false)}>Cancelar</button>
              </div>
            </div>
          </div>
        )}

        {abrirModalDeletar && (
          <div className="modal-overlay">
            <div className="modal">
              <h2>Excluir usuário</h2>
              <p>Tem certeza de que deseja excluir{" "}
                <strong>{usuarioExibido?.nome}</strong>?
              </p>
              <p>Para confirmar, digite sua senha de administrador.</p>
              <div className="campo-senha">
                <input type={mostrarSenhaExclusao ? "text" : "password"} placeholder="Digite sua senha" value={senhaExclusao}
                  onChange={(e) => {
                    setSenhaExclusao(e.target.value);
                    if (erroExclusao) setErroExclusao("");
                  }}
                  autoComplete="current-password"
                  disabled={excluindoUsuario}
                />

                <button type="button" className="ver-senha"
                  onClick={() => setMostrarSenhaExclusao((atual) => !atual)}
                  aria-label={mostrarSenhaExclusao ? "Ocultar senha" : "Mostrar senha"}
                  title={mostrarSenhaExclusao ? "Ocultar senha" : "Mostrar senha"}
                  disabled={excluindoUsuario}
                >
                  {mostrarSenhaExclusao ? (
                    <svg viewBox="0 0 24 24" width="21" height="21" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                      <path d="M3 3l18 18" />
                      <path d="M10.6 10.6a2 2 0 0 0 2.8 2.8" />
                      <path d="M9.9 5.2A10.8 10.8 0 0 1 12 5c5 0 9 5 10 7a13 13 0 0 1-3 3.8" />
                      <path d="M6.6 6.6C4.2 8 2.5 10.3 2 12c1 2 5 7 10 7a10 10 0 0 0 3-.5" />
                    </svg>
                  ) : (
                    <svg viewBox="0 0 24 24"  width="21" height="21" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                      <path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7-10-7-10-7z" />
                      <circle cx="12" cy="12" r="3" />
                    </svg>
                  )}
                </button>
              </div>

              {erroExclusao && (
                <p className="erro-modal" role="alert" style={{ color: "#b42318", background: "#fef3f2", padding: "10px 12px", borderRadius: 8, margin: "12px 0", fontSize: 14 }}>{erroExclusao}</p>
              )}

              <div className="botoes-acoes">
                <button className="deletar" type="button" onClick={handleDeletarUser} disabled={excluindoUsuario}>{excluindoUsuario ? "Validando / excluindo..." : "Confirmar exclusão"}</button>
                <button className="manter" type="button" disabled={excluindoUsuario} onClick={() => {setAbrirModalDeletar(false); setErroExclusao(""); setSenhaExclusao(""); setMostrarSenhaExclusao(false);}}>Voltar</button>
              </div>
            </div>
          </div>
        )}
      </main>
    </>
  );
}