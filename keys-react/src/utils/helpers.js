export const PAL = ["#a06bd6", "#ef5b5b", "#f29b38", "#88cc4a", "#3ab5e6"];
export const MESES = ["Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"];
export const REP = { none: "Não se repete", weekly: "Repete toda semana", monthly: "Repete todo mês" };

const hash = (s) => [...s].reduce((a, c) => a + c.charCodeAt(0), 0);
export const corAvatar = (nome) => PAL[hash(nome) % 5];
export const corSala = (sala) => PAL[(parseInt(sala.replace(/\D/g, "")) || 1) % 5];
export const iniciais = (nome) =>
  nome.trim().split(/\s+/).map((w, i, a) => (i === 0 || i === a.length - 1 ? w[0] : "")).join("").toUpperCase();

export const toM = (t) => {
  const [h, m] = t.split(":").map(Number);
  return h * 60 + m;
};
export const fmt = (m) => String(Math.floor(m / 60)).padStart(2, "0") + ":" + String(m % 60).padStart(2, "0");

export const br = (d) => d.split("-").reverse().join("/");

export const dtxt = (d) => {
  const x = d instanceof Date ? d : new Date(d + "T12:00");
  const t = x.toLocaleDateString("pt-BR", { weekday: "long", day: "numeric", month: "long" });
  return t[0].toUpperCase() + t.slice(1);
};

export const isoHoje = () => {
  const d = new Date();
  return d.getFullYear() + "-" + String(d.getMonth() + 1).padStart(2, "0") + "-" + String(d.getDate()).padStart(2, "0");
};

export const mesmoDia = (a, b) => a.toDateString() === b.toDateString();
export const emailOk = (v) => /^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(v);
