import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import api from "../../api/api.js";
import Header from "../../components/Header";
import Timeline from "../../components/Timeline";
import { useToast } from "../../context/ToastContext";
import useTitle from "../../hooks/useTitle";
import { fmt } from "../../utils/helpers";
import "./editarReserva.css";

const STEP = 15;
const DIA = [420, 1260]; // 07:00 às 21:00

function paraMinutos(data) {
  if (!data) return null;

  const horario = data.split("T")[1];
  if (!horario) return null;

  const [hora, minuto] = horario.split(":").map(Number);
  return hora * 60 + minuto;
}

// Formata minutos como "09:30".
function paraHorario(minutos) {
  const hora = String(Math.floor(minutos / 60)).padStart(2, "0");
  const minuto = String(minutos % 60).padStart(2, "0");
  return `${hora}:${minuto}`;
}

function extrairData(data) {
  return data?.split("T")[0] ?? "";
}

function formatarData(data) {
  if (!data) return "";
  const [ano, mes, dia] = data.split("-").map(Number);
  return new Intl.DateTimeFormat("pt-BR", {
    weekday: "long",
    day: "numeric",
    month: "long",
  }).format(new Date(ano, mes - 1, dia));
}

function gerarHorarios(inicio, fim) {
  const horarios = [];
  for (let minuto = inicio; minuto <= fim; minuto += STEP) {
    horarios.push(minuto);
  }
  return horarios;
}

export default function EditarReserva() {
  useTitle("Editar reserva – Keys");
  const toast = useToast();
  const navigate = useNavigate();
  const { id } = useParams();
  const [reserva, setReserva] = useState(null);
  const [inicio, setInicio] = useState("");
  const [fim, setFim] = useState("");
  const [carregando, setCarregando] = useState(true);
  const [salvando, setSalvando] = useState(false);
  const [erro, setErro] = useState("");
  useEffect(() => {
    async function buscarReserva() {
      try {
        setCarregando(true);
        setErro("");
        const token = localStorage.getItem("token");
          const response = await api.get(`/VerReserva/${id}`, {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          });
        const dados = response.data;
        setReserva(dados);
        setInicio(String(paraMinutos(dados.inicioReserva)));
        setFim(String(paraMinutos(dados.fimReserva)));

      } catch (error) {
        console.error("Erro ao buscar reserva:", error);
        setErro(error.response?.data?.mensagem || "Não foi possível carregar os dados dessa reserva.");

      } finally {
        setCarregando(false);
      }
    }

    if (id) {
      buscarReserva();
    }
  }, [id]);

  async function salvar(e) {
    e.preventDefault();
    if (!reserva || !inicio || !fim) {
      toast("Preencha os horários da reserva.");
      return;
    }
    const novoInicioMinutos = Number(inicio);
    const novoFimMinutos = Number(fim);

    if (novoInicioMinutos >= novoFimMinutos) {
      toast("O horário de término deve ser posterior ao início.");
      return;
    }

    const dataOriginal = extrairData(reserva.inicioReserva);
    const novoInicio = `${dataOriginal}T${paraHorario(novoInicioMinutos)}:00`;
    const novoFim = `${dataOriginal}T${paraHorario(novoFimMinutos)}:00`;

    try {
      setSalvando(true);
        const token = localStorage.getItem("token");
        await api.patch(`/AtualizarReserva/${id}`,
          null,
          {
            params: {
              dataInicioAtualizacao: novoInicio,
              dataFimAtualizacao: novoFim,
            },
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
      toast("Horário da reserva atualizado com sucesso!");
      navigate("/usuarios");

    } catch (error) {
      console.error("Erro ao atualizar reserva:", error);
      toast(error.response?.data?.mensagem || error.response?.data?.erro || "Não foi possível atualizar a reserva.");

    } finally {
      setSalvando(false);
    }
  }

  if (carregando) {
    return (
      <>
        <Header variant="app" ativo="usuarios" />
        <main className="pg p-editar-reserva">
          <p>Carregando reserva...</p>
        </main>
      </>
    );
  }

  if (erro || !reserva) {
    return (
      <>
        <Header variant="app" ativo="usuarios" />
        <main className="pg p-editar-reserva">
          <div className="title">
            <div>
              <h1>Editar horário</h1>
              <p>{erro || "Reserva não encontrada."}</p>
            </div>
          </div>
          <Link className="out" to="/usuarios">Voltar para usuários</Link>
        </main>
      </>
    );
  }

  const inicioMinutos = Number(inicio);
  const fimMinutos = Number(fim);

  const horariosInicio = [
    ...new Set([
      ...gerarHorarios(DIA[0], DIA[1] - STEP),
      paraMinutos(reserva.inicioReserva),
    ]),
  ]
    .filter((m) => m != null)
    .sort((a, b) => a - b);

  const horariosFim = [
    ...new Set([
      ...gerarHorarios(DIA[0] + STEP, DIA[1]),
      paraMinutos(reserva.fimReserva),
    ]),
  ]
    .filter((m) => m != null && m > inicioMinutos)
    .sort((a, b) => a - b);

  const duracao = fimMinutos - inicioMinutos;
  const horas = Math.floor(duracao / 60);
  const minutos = duracao % 60;

  return (
    <>
      <Header variant="app" ativo="usuarios" />
      <main className="pg p-editar-reserva">
        <div className="title">
          <div>
            <h1>Editar horário</h1>
            <p>Altere o horário da reserva selecionada.</p>
          </div>
        </div>

        <form className="pan edc" onSubmit={salvar}>
          <div className="ev">
            <span className="ini" style={{ background: "#3ab5e6" }} aria-hidden="true">{reserva.nomeUser?.substring(0, 2).toUpperCase() || "US"}</span>
            <div className="n">Reservado por {reserva.nomeUser || "Usuário"}
              <small style={{display: "block", color: "var(--mut)", fontWeight: 500, fontSize: 16,}}>{formatarData(extrairData(reserva.inicioReserva))}</small>
            </div>
            <span className="tag">{reserva.nomeSala || "Sala"}</span>
          </div>

          <div className="tp">
            <div className="tbx">
              <label htmlFor="ia">Início</label>
              <select id="ia" value={inicio} onChange={(e) => {
                  const novoInicio = Number(e.target.value);
                  setInicio(e.target.value);
                  if (Number(fim) <= novoInicio) {
                    const proximoFim = horariosFim.find(
                      (horario) => horario > novoInicio
                    );
                    setFim(
                      String(proximoFim ?? novoInicio + STEP)
                    );
                  }
                }}
              >
                {horariosInicio.map((m) => (
                  <option key={m} value={m}>{fmt(m)}</option>
                ))}
              </select>
            </div>
            <div className="tbx">
              <label htmlFor="ib">Término</label>
              <select id="ib" value={fim} onChange={(e) => setFim(e.target.value)}>
                {horariosFim.map((m) => (
                  <option key={m} value={m}>{fmt(m)}</option>
                ))}
              </select>
            </div>
          </div>

          <p className="dur" aria-live="polite">Duração:{" "}{horas > 0 ? `${horas} h ` : ""}{minutos > 0 ? `${minutos} min` : ""}</p>
          <Timeline linhas={[
              {
                nome: reserva.nomeSala || "Sala",
                me: true,
                blocos: [
                  {
                    a: inicioMinutos,
                    b: fimMinutos,
                    tipo: "nw",
                  },
                ],
              },
            ]}
          />

          <p className="busy">
            Horário atual:{" "}
            {fmt(paraMinutos(reserva.inicioReserva))} –{" "}
            {fmt(paraMinutos(reserva.fimReserva))}
          </p>

          <div className="acts">
            <Link className="cancelarAlteracao" to="/usuarios" style={{ padding: 14 }}>Cancelar</Link>
            <button className="salvarAlteracao" type="submit" disabled={
                salvando || !inicio || !fim || inicioMinutos >= fimMinutos
              }
            >{salvando ? "Salvando..." : "Salvar horário"}
            </button>
          </div>
        </form>
      </main>
    </>
  );
}
