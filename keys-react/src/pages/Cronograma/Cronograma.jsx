import { useNavigate } from "react-router-dom";
import { useEffect, useMemo, useRef, useState } from "react";
import Header from "../../components/Header";
import { useToast } from "../../context/ToastContext";
import useTitle from "../../hooks/useTitle";
import { MESES, dtxt, iniciais, mesmoDia } from "../../utils/helpers";
import api from "../../service/api";
import "./cronograma.css";

const STATUS = {
  DISPONIVEL: "DISPONIVEL",
  INDISPONIVEL: "INDISPONIVEL",
};

const hora = (valor) => {
  if (!valor) return "";
  return String(valor).substring(0, 5);
};

const obterData = (reserva) => {
  if (!reserva.infosReserva) return "";
  return String(reserva.infosReserva).split("T")[0];
};

const obterHoraNumero = (valor) => {
  if (!valor) return 0;
  return Number(String(valor).split(":")[0]);
};

const plural = (n, um, varios) =>
  `${n} ${n === 1 ? um : varios}`;

const obterStatusReserva = (reserva) => {
  return (
    reserva.statusReserva ??
    reserva.StatusReserva ??
    reserva.status ??
    ""
  );
};

const informacoesStatus = (status) => {
  switch (status) {
    case "FINALIZADA":
      return {
        texto: "Finalizada",
        classe: "status-finalizada",
      };

    case "ESPERANDO_EFETIVACAO":
      return {
        texto: "Pendente",
        classe: "status-pendente",
      };

    case "EFETIVADA":
      return {
        texto: "Efetivada",
        classe: "status-efetivada",
      };

    default:
      return {
        texto: status || "Não informado",
        classe: "status-desconhecido",
      };
  }
};

function SalaDialog({ sala, onFechar, onSalvar }) {
  const ref = useRef(null);
  const [nome, setNome] = useState(sala.nome);
  const [desc, setDesc] = useState(sala.descricao);
  const [status, setStatus] = useState(sala.statusSala);
  const [erro, setErro] = useState("");
  const [salvando, setSalvando] = useState(false);

  useEffect(() => {
    ref.current?.showModal();
  }, []);

  const enviar = async (e) => {
    e.preventDefault();
    if (!nome.trim()) {
      setErro("Informe o nome da sala.");
      return;
    }
    setSalvando(true);
    setErro("");
    try {
      await onSalvar(
        sala.cd,
        nome.trim(),
        desc.trim(),
        status
      );
    } catch (error) {
      console.error("Erro ao editar sala:", error);
      setErro("Não foi possível salvar as alterações.");
      setSalvando(false);
    }
  };

  return (
    <dialog ref={ref} className="dlg" aria-labelledby="dlg-t" onClose={onFechar}>
      <form onSubmit={enviar} noValidate>
        <h2 id="dlg-t">Editar sala</h2>
        <label htmlFor="sala-nome"> Nome da sala</label>
        <input id="sala-nome" maxLength={80} autoComplete="off" autoFocus value={nome}  onChange={(e) => setNome(e.target.value)}/>
        <label htmlFor="sala-desc"> Descrição da sala</label>
        <input id="sala-desc" maxLength={200} autoComplete="off" value={desc} onChange={(e) => setDesc(e.target.value)}/>
        <label htmlFor="sala-status">Disponibilidade</label>

        <select id="sala-status" value={status} onChange={(e) => setStatus(e.target.value)}>
          <option value={STATUS.DISPONIVEL}>Disponível</option>
          <option value={STATUS.INDISPONIVEL}>Indisponível</option>
        </select>

        {erro && (<p className="er" role="alert">{erro}</p>)}

        <div className="dlg-bar">
          <button className="cancelarsala" onClick={() => ref.current.close()} disabled={salvando}>Cancelar</button>
          <button className="salvarSala" disabled={salvando}>{salvando ? "Salvando..." : "Salvar"}</button>
        </div>
      </form>
    </dialog>
  );
}

export default function Cronograma() {
  useTitle("Cronograma – Keys");
  const navigate = useNavigate();
  const toast = useToast();
  const [salas, setSalas] = useState([]);
  const [reservas, setReservas] = useState([]);
  const [ref, setRef] = useState(new Date());
  const [sel, setSel] = useState(new Date());
  const [filtro, setFiltro] = useState("todas");
  const [buscaSala, setBuscaSala] = useState("");
  const [buscaRes, setBuscaRes] = useState("");
  const [editando, setEditando] = useState(null);
  const [ocupada, setOcupada] = useState(null);

  useEffect(() => {
    async function carregarDados() {
      try {
        const [responseSalas, responseReservas] =
          await Promise.all([
            api.get("/salasDaEmpresa"),
            api.get("/reservasDaEmpresa"),
          ]);
        setSalas(responseSalas.data || []);
        setReservas(responseReservas.data || []);

      } catch (error) {
        console.error("Erro ao carregar cronograma:", error);
        toast("Não foi possível carregar os dados do servidor.",3200);
      }
    }

    carregarDados();
  }, []);

  const y = ref.getFullYear();
  const m = ref.getMonth();
  const vazios = (new Date(y, m, 1).getDay() + 6) % 7;
  const diasNoMes = new Date(y, m + 1, 0).getDate();
  const mudarMes = (quantidade) => {setRef(new Date(y, m + quantidade, 1));};

  const listaSalas = salas.filter((sala) => {
    const nome = (sala.nome || "").toLowerCase();
    const correspondeNome = nome.includes(buscaSala.toLowerCase());
    const correspondeStatus = filtro === "todas" ||
      (filtro === "disp" && sala.statusSala === STATUS.DISPONIVEL) ||
      (filtro === "indisp" && sala.statusSala === STATUS.INDISPONIVEL);

    return correspondeNome && correspondeStatus;
  });

  const grupos = useMemo(() => {
    const termo = buscaRes.toLowerCase();
    const reservasDoDia = reservas.filter((reserva) => {
        const dataReserva = obterData(reserva);
        const nomeSala = reserva.NomeSala || "";
        const nomeUsuario = reserva.NomeUser || "";
        const correspondeData = dataReserva === sel.toLocaleDateString("en-CA");
        const correspondeBusca = (nomeSala + " " + nomeUsuario).toLowerCase().includes(termo);
        return correspondeData && correspondeBusca;

      })
      .sort((a, b) => {
        return (obterHoraNumero(a.inicioReserva) - obterHoraNumero(b.inicioReserva));});

    const periodos = [
      { nome: "Manhã", inicio: 0, fim: 12 },
      { nome: "Tarde", inicio: 12, fim: 18 },
      { nome: "Noite", inicio: 18, fim: 24 },
    ];

    return periodos.map((periodo) => {
      const itens = reservasDoDia.filter((reserva) => {
          const horaInicio = obterHoraNumero(reserva.inicioReserva);
          return (horaInicio >= periodo.inicio && horaInicio < periodo.fim);
        }).map((reserva) => {
          const status = informacoesStatus(obterStatusReserva(reserva));
          return {
            id: reserva.cdReserva,
            t: hora(reserva.inicioReserva) + " – " + hora(reserva.fimReserva),
            f: reserva.NomeUser || "Usuário",
            s: reserva.NomeSala || "Sala",
            statusTexto: status.texto,
            statusClasse: status.classe,
          };
        });

      return {
        nome: periodo.nome,
        total: itens.length,
        itens,
      };
    });
  }, [reservas, sel, buscaRes]);

  const totalDia = grupos.reduce((total, grupo) => total + grupo.itens.length, 0);

  function Reservar(sala) {
    if (sala.statusSala === STATUS.INDISPONIVEL) {
      alert("Não é possível reservar essa sala.");
    } else if (sala.statusSala === STATUS.DISPONIVEL) {
      window.location.href = "/";
    }
  }

  const alternar = async (sala) => {
    setOcupada(sala.cd);
    const novoStatus = sala.statusSala === STATUS.DISPONIVEL ? STATUS.INDISPONIVEL : STATUS.DISPONIVEL;
    try {
      await api.patch("/sala/estado", null, {
        params: {
          statusSalaEnum: novoStatus,
          salaId: sala.cd,
        },
      });

      setSalas((lista) => lista.map((item) => item.cd === sala.cd ? { ...item, statusSala: novoStatus } : item));

      toast(`Disponibilidade de ${sala.nome} alterada com sucesso.`, 3200);

    } catch (error) {
      console.error("Erro ao alterar disponibilidade:", error);
      toast("Não foi possível alterar a disponibilidade.", 3200);

    } finally {
      setOcupada(null);
    }
  };

  const salvarSala = async (
    id, nome, descricao, status
  ) => {
    try {
      await api.patch("/salas", null, {
        params: {
          salaId: id,
          nome: nome,
          descricao: descricao,
        },
      });

      await api.patch("/sala/estado", null, {
        params: {
          statusSalaEnum: status,
          salaId: id,
        },
      });

      setSalas((lista) =>
        lista.map((sala) =>
          sala.cd === id
            ? {...sala, nome, descricao, statusSala: status,}: sala
        )
      );
      setEditando(null);
      toast("Sala alterada com sucesso.", 3200);

    } catch (error) {
      console.error("Erro ao editar sala:", error);
      throw error;

    }
  };


  return (
    <>
      <Header variant="app" ativo="cronograma" />
      <div className="p-cronograma">
        <div className="grid">
          <div>
            <div className="pan">
              <div className="hd">
                <h2> {MESES[m] + " de " + y}</h2>
                <div className="cn">
                  <button className="sq" type="button" aria-label="Mês anterior" onClick={() => mudarMes(-1)}>‹</button>
                  <button className="sq" type="button" aria-label="Próximo mês" onClick={() => mudarMes(1)}>›</button>
                </div>
              </div>

              <div className="cal">
                {[
                  "seg",
                  "ter",
                  "qua",
                  "qui",
                  "sex",
                  "sab",
                  "dom",
                ].map((dia) => (
                  <b key={dia}>{dia}</b>
                ))}

                {Array.from( { length: vazios },(_, i) => (
                    <span key={"v" + i} />
                  )
                )}

                {Array.from({ length: diasNoMes },(_, i) => {
                    const data = new Date(y, m, i + 1);
                    return (
                      <button key={i} type="button" className={mesmoDia(data, sel) ? "sel" : undefined} onClick={() => setSel(data)}>{i + 1}</button>
                    );
                  }
                )}
              </div>
              <p className="note">Clique em um dia para ver as reservas.</p>
            </div>

            <div className="pan">
              <div className="hd">
                <h2>Salas{" "}<span className="light">({salas.length})</span></h2>
              </div>

              <input className="sf" placeholder="Buscar sala" value={buscaSala} onChange={(e) => setBuscaSala(e.target.value)}/>
              <div className="fl">
                {[
                  ["todas", "Todas"],
                  ["disp", "Disponíveis"],
                  ["indisp", "Indisponíveis"],
                ].map(([chave, nome]) => (
                  <button key={chave} type="button" className={`pill${ filtro === chave ? " on" : ""}`} onClick={() => setFiltro(chave)}>{nome}</button>
                ))}
              </div>

              <div className="scr">
                {listaSalas.map((sala) => (
                  <div className="sl" key={sala.cd}>
                    <span title={sala.descricao}>{sala.nome}</span>
                    <button type="button" className={`st ${ sala.statusSala === STATUS.DISPONIVEL ? "a" : "u"}`}  title="Alterar disponibilidade" disabled={ocupada === sala.cd} onClick={() => alternar(sala)}>
                      {sala.statusSala === STATUS.DISPONIVEL ? "Disponível" : "Indisponível"}
                    </button>
                    <button type="button" aria-label={`Editar ${sala.nome}`} onClick={() => setEditando(sala)}>✎</button>
                  </div>
                ))}

                {!listaSalas.length && (
                  <p className="note vazio">Nenhuma sala encontrada.</p>
                )}
              </div>
              <p className="note mt">Mostrando {listaSalas.length} de{" "}{salas.length} salas.</p>

            </div>
          </div>

          <div className="pan">
            <div className="hd top2">
              <div>
                <h2 className="dia">{dtxt(sel)}</h2>
                <p className="note sub">{plural(totalDia, "reserva", "reservas")}</p>
              </div>
              <button type="button" className="btn-adicionar-reserva" onClick={() => navigate("/reserva")}><span>+</span>Adicionar reserva</button>
            </div>

            <div className="fl filtros">
              <input className="sf busca" placeholder="Buscar por funcionário ou sala" value={buscaRes} onChange={(e) =>setBuscaRes(e.target.value)}/>
            </div>

            <div>
              {grupos.map((grupo) => (
                <div key={grupo.nome}>
                  <div className="sec">
                    <span>{grupo.nome}</span>
                    <span>{plural(grupo.total, "reserva", "reservas")}</span>
                  </div>

                  {grupo.itens.map((item) => (
                    <div className="rv" key={item.id}>
                      <span className="t">{item.t}</span>
                      <span className="av">{iniciais(item.f)}</span>
                      <span className="n">{item.f}</span>
                      <span className={`status-reserva ${item.statusClasse}`}>{item.statusTexto}</span>
                      <span className="tag">{item.s}</span>
                    </div>
                  ))}

                  {!grupo.itens.length && (
                    <p className="note vazio"> Nenhuma reserva neste período.</p>
                  )}
                </div>
              ))}
            </div>

            <div className="pag">
              <span>Mostrando {totalDia} de {totalDia} reservas</span>
            </div>
          </div>
        </div>

        {editando && (
          <SalaDialog key={editando.cd} sala={editando} onFechar={() => setEditando(null)}onSalvar={salvarSala}/>
        )}
      </div>
    </>
  );
}