import { useEffect, useMemo, useState } from "react";
import Header from "../../components/Header";
import Abas from "../../components/Abas";
import PlanilhaPanel from "../../components/PlanilhaPanel";
import Timeline from "../../components/Timeline";
import { useToast } from "../../context/ToastContext";
import useTitle from "../../hooks/useTitle";
import { REP, corSala, dtxt, isoHoje, toM } from "../../utils/helpers";
import api from "../../service/api";
import "./reserva.css";

const STATUS = { DISPONIVEL: "DISPONIVEL",};
const STEP = 15;
const HORA_INICIO_DIA = 7 * 60;
const HORA_FIM_DIA = 21 * 60;
const gerarHorarios = (inicio, fim) => {
  const horarios = [];
  for (let minuto = inicio; minuto <= fim; minuto += STEP) {
    const hora = String(Math.floor(minuto / 60)).padStart(2, "0");
    const minutos = String(minuto % 60).padStart(2, "0");
    horarios.push(`${hora}:${minutos}`);
  }

  return horarios;
};

const hora = (valor) => {
  if (!valor) return "";
  return String(valor).includes("T") ? String(valor).split("T")[1].substring(0, 5) : String(valor).substring(0, 5);
};

const obterData = (reserva) => {
  const valor = reserva.dataReserva ?? reserva.infosReserva ?? reserva.inicioReserva ?? reserva.data;
  if (!valor) return "";
  return String(valor).split("T")[0];
};

const obterInicio = (reserva) => {
  return hora(reserva.horaInicioReserva ?? reserva.inicioReserva ?? reserva.horaInicio);
};

const obterFim = (reserva) => {
  return hora(reserva.horaFimReserva ?? reserva.fimReserva ?? reserva.horaFim);
};

const obterIdSala = (reserva) => {
  return (reserva.salaEntity?.cd ?? reserva.sala?.cd ?? reserva.cdSala ?? reserva.salaId ?? null);
};

const obterNomeSala = (reserva) => {
  return (reserva.NomeSala ?? reserva.nomeSala ?? reserva.salaEntity?.nome ?? reserva.sala?.nome ?? "");
};

const obterLista = (resposta) => {
  if (Array.isArray(resposta)) return resposta;
  if (Array.isArray(resposta?.data)) return resposta.data;
  return [];
};

const formatarISO = (data) => {
  const ano = data.getFullYear();
  const mes = String(data.getMonth() + 1).padStart(2, "0");
  const dia = String(data.getDate()).padStart(2, "0");

  return `${ano}-${mes}-${dia}`;
};

const adicionarDias = (dataISO, quantidade) => {
  const [ano, mes, dia] = dataISO.split("-").map(Number);
  const data = new Date(ano, mes - 1, dia);
  data.setDate(data.getDate() + quantidade);
  return formatarISO(data);
};

const adicionarMeses = (dataISO, quantidade) => {
  const [ano, mes, dia] = dataISO.split("-").map(Number);
  const mesDestino = mes - 1 + quantidade;
  const primeiroDia = new Date(ano, mesDestino, 1);
  const ultimoDia = new Date(primeiroDia.getFullYear(), primeiroDia.getMonth() + 1, 0).getDate();
  const diaDestino = Math.min(dia, ultimoDia);
  return formatarISO(new Date(primeiroDia.getFullYear(), primeiroDia.getMonth(), diaDestino));
};

const gerarDatasRepeticao = (data, repeticao) => {
  const datas = [data];
  if (repeticao === "weekly") {
    for (let i = 1; i <= 4; i++) {
      datas.push(adicionarDias(data, 7 * i));
    }
  }

  if (repeticao === "monthly") {
    for (let i = 1; i <= 4; i++) {
      datas.push(adicionarMeses(data, i));
    }
  }
  return datas;
};

//Reservas com o mesmo grupoId são exibidas juntas e reservas sem repetição continuam sendo individuais.

const organizarFila = (fila) => {
  const exibicao = [];
  const grupos = new Map();

  fila.forEach((reserva) => {
    if (!reserva.grupoId) {
      exibicao.push({tipo: "individual", id: reserva.id,reservas: [reserva],});
      return;
    }

    if (!grupos.has(reserva.grupoId)) {
      const grupo = {tipo: "grupo", id: reserva.grupoId, repeticao: reserva.r, reservas: [],};
      grupos.set(reserva.grupoId, grupo);
      exibicao.push(grupo);
    }
    grupos.get(reserva.grupoId).reservas.push(reserva);
  });
  return exibicao;
};

export default function Reserva() {
  useTitle("Nova reserva – Keys");
  const toast = useToast();
  const [aba, setAba] = useState("m");
  const [f, setF] = useState({
    d: isoHoje(),
    a: "",
    b: "",
    s: "",
    r: "none",
    proprietario: "",
  });

  const [fila, setFila] = useState([]);
  const [salas, setSalas] = useState([]);
  const [funcionarios, setFuncionarios] = useState([]);
  const [reservasExistentes, setReservasExistentes] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [salvando, setSalvando] = useState(false);
  const campo = (k) => (e) => setF((x) => ({...x,[k]: e.target.value,}));
  const { d, a, b, s, r, proprietario } = f;

  useEffect(() => {
    async function carregarDados() {
      try {
        setCarregando(true);
        const [respostaSalas, respostaFuncionarios, respostaReservas,] = await Promise.all([
          api.get("/salasDaEmpresa"),
          api.get("/usuarios"),
          api.get("/TodasasReservasLista"),
        ]);
        setSalas(obterLista(respostaSalas.data));
        setFuncionarios(obterLista(respostaFuncionarios.data));
        setReservasExistentes(obterLista(respostaReservas.data));
      } catch (error) {
        console.error("Erro ao carregar dados da reserva:", error);
        toast("Não foi possível carregar as informações para reserva.",3200);
      } finally {
        setCarregando(false);
      }
    }
    carregarDados();
  }, []);

  const salasDisponiveis = useMemo(() => {
    return salas.filter(
      (sala) => sala.statusSala === STATUS.DISPONIVEL
    );
  }, [salas]);

  const ocupados = (sala, data) => {
    const salaSelecionada = salas.find(
      (item) => String(item.cd) === String(sala)
    );

    const reservasDoServidor = reservasExistentes
      .filter((reserva) => {
        const mesmaData = obterData(reserva) === data;
        const idReserva = obterIdSala(reserva);
        const nomeReserva = obterNomeSala(reserva);
        const mesmaSala = (idReserva != null && String(idReserva) === String(sala)) || (nomeReserva && nomeReserva === salaSelecionada?.nome);
        return mesmaData && mesmaSala;
      }).map((reserva) => [toM(obterInicio(reserva)),toM(obterFim(reserva)),]);

    const reservasDaFila = fila
      .filter((q) => String(q.s) === String(sala) && q.d === data)
      .map((q) => [toM(q.a), toM(q.b),]);
    return [...reservasDoServidor, ...reservasDaFila,];
  };

  const horariosValidos = Boolean(a && b) && toM(b) > toM(a);
  let aviso = "";

  if (a && b && !horariosValidos) {
    aviso = "O término precisa ser depois do início.";
  } else if (
    horariosValidos && s &&
    ocupados(s, d).some((x) => toM(a) < x[1] && toM(b) > x[0])
  ) {
    aviso = "Essa sala já está reservada nesse horário.";
  }

  const linhas = useMemo(() => {
    return salas.map((sala) => {
      const reservasServidor = reservasExistentes.filter((reserva) => {
          const mesmaData = obterData(reserva) === d;
          const idReserva = obterIdSala(reserva);
          const nomeReserva = obterNomeSala(reserva);
          const mesmaSala = (idReserva != null && String(idReserva) === String(sala.cd)) || (nomeReserva && nomeReserva === sala.nome);
          return mesmaData && mesmaSala;
        })
        .map((reserva) => ({
          a: toM(obterInicio(reserva)),
          b: toM(obterFim(reserva)),
          tipo: "ex",
        }));

      const reservasFila = fila.filter((q) => String(q.s) === String(sala.cd) && q.d === d).map((q) => ({a: toM(q.a), b: toM(q.b), tipo: "q",}));
      const novaReserva = String(sala.cd) === String(s) && horariosValidos ? [{a: toM(a),b: toM(b),tipo: "nw",},] : [];

      return {nome: sala.nome, me: String(sala.cd) === String(s),
        blocos: [...reservasServidor, ...reservasFila, ...novaReserva,],};
      });

  }, [salas, reservasExistentes, fila, d, s, a, b, horariosValidos,]);

  const resumo = [
    d ? dtxt(d) : "Sem data",
    a && b ? `${a} – ${b}` : "Sem horário",
    salas.find((sala) => String(sala.cd) === String(s))?.nome || "Sem sala",
    funcionarios.find((user) => String(user.id) === String(proprietario))?.nome || "Sem responsável",
    REP[r],
  ].join(" · ");

  const filaExibicao = useMemo(() => {
    return organizarFila(fila);
  }, [fila]);

  const adicionar = (e) => {
    e.preventDefault();
    if (!d || !a || !b || !s || !proprietario) {
      toast("Preencha todos os campos da reserva.", 3200);
      return;
    }

    if (!horariosValidos) {
      toast("O término precisa ser depois do início.", 3200);
      return;
    }

    if (aviso) {
      toast(aviso, 3200);
      return;
    }

    const datas = gerarDatasRepeticao(d, r);
    const conflito = datas.find((data) => ocupados(s, data).some((horario) => toM(a) < horario[1] && toM(b) > horario[0]));

    if (conflito) {
      toast(`Já existe uma reserva nessa sala em ${dtxt(conflito)}, no horário escolhido.`, 4500);
      return;
    }

    const grupoId = r !== "none" ? `grupo-${Date.now()}-${Math.random().toString(36).slice(2, 8)}` : null;

    const novasReservas = datas.map((data, index) => ({
      id: `${Date.now()}-${index}-${Math.random().toString(36).slice(2, 7)}`,
      grupoId,
      d: data,
      a,
      b,
      s,
      r,
      proprietario,
    }));

    setFila((lista) => [...lista, ...novasReservas,]);

    setF((x) => ({...x, a: "", b: "", s: "",}));

    toast(datas.length === 1 ? "Reserva adicionada à lista." : `${datas.length} reservas adicionadas à lista.`, 3500);
  };

  const excluirDaFila = (id) => {setFila((lista) => lista.filter((reserva) => reserva.id !== id));
  };

  const renderizarReserva = (q, mostrarRepeticao = false) => {
    const sala = salas.find((item) => String(item.cd) === String(q.s));
    const funcionario = funcionarios.find((item) => String(item.id) === String(q.proprietario));

    return (
      <div className="qi" key={q.id}>
        <span className="tag" style={{background: corSala(sala?.nome || ""), color: "#0a1a2e",}}>
          {sala?.nome || "Sala"}
        </span>
        <div className="n">
          {dtxt(q.d)}
          <small>{q.a} – {q.b}</small>
          <small>Responsável:{" "}{funcionario?.nome || "Não informado"}</small>
        </div>
        {mostrarRepeticao && (
          <span className="tag rep">{REP[q.r]}</span>
          
        )}
        <button className="out d" type="button"  disabled={salvando} onClick={() => excluirDaFila(q.id)}>Excluir</button>
      </div>
    );
  };

  const salvar = async () => {
    if (!fila.length) {
      toast("Adicione ao menos uma reserva.", 3200);
      return;
    }

    try {
      setSalvando(true);
      for (const reserva of fila) {
        const payload = {
          dataReserva: reserva.d,
          horaInicioReserva: reserva.a,
          horaFimReserva: reserva.b,
          proprietario: {cdUsuario: reserva.proprietario,},
          salaEntity: { cd: Number(reserva.s),},
        };
        await api.post("/adicionarReservaaLista", payload);
      }

      await api.post("/AdicionarListadeReservas");
      toast(fila.length + (fila.length > 1 ? " reservas salvas com sucesso." : " reserva salva com sucesso."), 3500);
      setFila([]);
      const resposta = await api.get("/TodasasReservasLista");
      setReservasExistentes(obterLista(resposta.data));
    } catch (error) {
      console.error("Erro ao salvar reservas:", error);
      const mensagem =
        error.response?.data?.mensagem ||
        error.response?.data?.erro ||
        error.response?.data?.message ||
        "Não foi possível salvar as reservas.";
      toast(typeof mensagem === "string" ? mensagem : "Não foi possível salvar as reservas.", 4000);
    } finally {
      setSalvando(false);
    }
  };

  return (
    <>
      <Header variant="app" ativo="cronograma" />
      <main className="pg p-reserva">
        <div className="title">
          <div>
            <h1>Nova reserva</h1>
            <p>Escolha o dia, o horário e a sala. A disponibilidade aparece ao lado.</p>
          </div>
          <Abas aba={aba} onChange={setAba} />
        </div>
        <div hidden={aba !== "m"}>
          <div className="cols2">
            <section className="pan">
              <form onSubmit={adicionar} noValidate>
                <div className="sh">
                  <span>Quando</span>
                </div>
                <div className="fld">
                  <label htmlFor="d">Data</label>
                  <input id="d" type="date" required value={d} onChange={campo("d")}/>
                </div>
                <div className="two">
                  <div className="fld">
                    <label htmlFor="a">Início da reserva</label>
                    <select id="a" required value={a} onChange={(e) => {
                        const novoInicio = e.target.value;
                        setF((atual) => ({
                          ...atual,
                          a: novoInicio,
                          b:
                            atual.b && toM(atual.b) <= toM(novoInicio)
                              ? ""
                              : atual.b,
                        }));
                      }}
                    >
                      <option value="">Selecione o início</option>
                      {gerarHorarios(
                        HORA_INICIO_DIA,
                        HORA_FIM_DIA - STEP
                      ).map((horario) => (
                        <option key={horario} value={horario}>
                          {horario}
                        </option>
                      ))}
                    </select>
                  </div>
                  <div className="fld">
                    <label htmlFor="b">Término da reserva</label>
                    <select id="b" required value={b} onChange={campo("b")}disabled={!a}>
                      <option value="">Selecione o término</option>
                      {gerarHorarios(a ? toM(a) + STEP : HORA_INICIO_DIA + STEP,HORA_FIM_DIA).filter((horario) => !a || toM(horario) > toM(a)).map((horario) => (
                          <option key={horario} value={horario}>
                            {horario}
                          </option>))}
                    </select>
                  </div>
                </div>
                <div className="sh"><span>Onde</span></div>
                <div className="fld">
                  <label className="sr" htmlFor="s">Sala</label>
                  <select id="s" required value={s} onChange={campo("s")} disabled={carregando}>
                    <option value="">Selecione a sala dessa reserva</option>
                    {salasDisponiveis.map((sala) => (
                      <option key={sala.cd} value={sala.cd}>
                        {sala.nome}
                      </option>))}
                  </select>
                  {!carregando && !salasDisponiveis.length && (
                    <small>Não existem salas disponíveis.</small>)}
                </div>
                <div className="sh"><span>Responsável</span></div>
                <div className="fld">
                  <label className="sr" htmlFor="proprietario">Funcionário responsável</label>
                  <select id="proprietario" required value={proprietario} onChange={campo("proprietario")} disabled={carregando}>
                    <option value="">Selecione o funcionário responsável</option>
                    {funcionarios.map((funcionario) => (
                      <option key={funcionario.id} value={funcionario.id}>{funcionario.nome}</option>
                    ))}
                  </select>
                </div>
                <div className="sh"><span>Repetição</span></div>
                <div className="fld">
                  <label className="sr" htmlFor="r">Repetição</label>
                  <select id="r" value={r} onChange={campo("r")}>
                    <option value="none">Não vai se repetir</option>
                    <option value="weekly">Repetir semanalmente</option>
                    <option value="monthly">Repetir mensalmente</option>
                  </select>
                </div>
                <p className="sum">{resumo}</p>
                <p className="warn" role="alert">{aviso}</p>
                <button id="btnadicionar" type="submit" disabled={carregando || !d || !a || !b || !s || !proprietario || !!aviso}>Adicionar à lista</button>
              </form>
            </section>

            <div className="stack">
              <section className="pan">
                <div className="hd"><h2>Disponibilidade ·{" "}{d ? dtxt(d) : "escolha uma data"}</h2></div>
                {carregando ? (<p className="empty">Carregando disponibilidade...</p>) : (<Timeline linhas={linhas} />)}
                <div className="lg">
                  <span><i style={{ background: "#4f6f9f" }} />Já reservado</span>
                  <span><i style={{ background: "#f2c14e" }} />Nova reserva</span>
                  <span><i style={{background:"repeating-linear-gradient(45deg,#f2c14e 0 4px,#d9a92f 4px 8px)",}}/>Na lista</span>
                </div>
              </section>
              <section className="pan">
                <div className="hd">
                  <h2>Reservas para salvar{" "}<span className="count">{fila.length ? `(${fila.length})` : ""}</span></h2>
                  <button className="btn2" type="button" onClick={salvar} disabled={!fila.length || salvando}>{salvando ? "Salvando..." : "Salvar"}</button>
                </div>
                <div className="lista-reservas">
                  {fila.length ? (
                    filaExibicao.map((item) => {
                      if (item.tipo === "individual") {return renderizarReserva(item.reservas[0]);}
                      const primeira = item.reservas[0];
                      const sala = salas.find((s) => String(s.cd) === String(primeira.s));
                      const funcionario = funcionarios.find((u) =>String(u.id) === String(primeira.proprietario));
                      return (
                        <details className="grupo-repeticao" key={item.id}>
                          <summary className="grupo-repeticao-resumo">
                            <div className="grupo-repeticao-info">
                              <span className="tag" style={{background: corSala(sala?.nome || ""),color: "#0a1a2e",}}
                              >{sala?.nome || "Sala"}</span>
                              <div className="grupo-repeticao-texto">
                                <strong> {REP[item.repeticao]}</strong>
                                <small>{primeira.a} – {primeira.b} {" · "}  {funcionario?.nome || "Não informado"}
                                </small>
                                <small>{item.reservas.length} ocorrências</small>
                              </div>
                            </div>
                            <span className="grupo-repeticao-seta">
                              <svg
                                viewBox="0 0 24 24"
                                width="20"
                                height="20"
                                fill="none"
                                stroke="currentColor"
                                strokeWidth="2"
                                strokeLinecap="round"
                                strokeLinejoin="round"
                                aria-hidden="true"
                              ><path d="m6 9 6 6 6-6" /></svg>
                            </span>
                          </summary>
                          <div className="grupo-repeticao-conteudo">{item.reservas.map((reserva) =>renderizarReserva(reserva))}</div>
                        </details>);})) : (<p className="empty">Nenhuma reserva na lista. Preencha o formulário e adicione.</p>)}
                </div>
              </section>
            </div>
          </div>
        </div>

        <div hidden={aba !== "x"}>
          <PlanilhaPanel
            tipo="res"
            titulo="Importar reservas via Excel"
            nota="Use o modelo para preencher data, horários, sala e repetição de cada reserva."/>
        </div>
      </main>
    </>
  );
}
