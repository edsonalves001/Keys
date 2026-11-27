import { forwardRef, useEffect, useRef, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../../api/api.js";
import Header from "../../components/Header";
import useTitle from "../../hooks/useTitle";
import { emailOk } from "../../utils/helpers";
import "./registro.css";


const COR = ["#a06bd6", "#ef5b5b", "#f29b38", "#88cc4a", "#3ab5e6"];
const ETAPAS = ["#f2c14e", "#3ab5e6", "#a06bd6"];
const mascara = (v) => v.replace(/\D/g, "").slice(0, 14).replace(/^(\d{2})(\d)/, "$1.$2").replace(/^(\d{2})\.(\d{3})(\d)/, "$1.$2.$3").replace(/\.(\d{3})(\d)/, ".$1/$2").replace(/(\d{4})(\d)/, "$1-$2");

function cnpjOk(v) {
  const d = v.replace(/\D/g, "");
  if (d.length !== 14 || /^(\d)\1+$/.test(d)) return false;
  const dv = (n) => {
     let s = 0;
     let p = n - 7;
    for (let i = 0; i < n; i++) {
      s += d[i] * p--;
      if (p < 2) p = 9;
    }
    const r = s % 11;
    return r < 2 ? 0 : 11 - r;
  };
  return dv(12) == d[12] && dv(13) == d[13];
}

const senhaOk = (s) => s.length >= 8 && /[A-Z]/.test(s) && /[a-z]/.test(s) && /\d/.test(s) && /[@#$%^&+=!]/.test(s);
const mensagemErro = (erro) => erro?.response?.data?.mensagem || erro?.response?.data?.erro || erro?.response?.data?.message || erro?.message || "Não foi possível concluir a operação.";

const Olho = () => (
  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" aria-hidden="true">
    <path d="M1.5 12S5.5 5 12 5s10.5 7 10.5 7-4 7-10.5 7S1.5 12 1.5 12z" />
    <circle cx="12" cy="12" r="3" />
  </svg>
);

function CampoSenha({ id, label, valor, onChange, placeholder, erro }) {
  const [mostrar, setMostrar] = useState(false);
  return (
    <div className="rf">
      <label htmlFor={id}>{label}</label>
      <div className="w pw">
        <input id={id} type={mostrar ? "text" : "password"} autoComplete="new-password" placeholder={placeholder} className={erro ? "bad" : undefined} aria-invalid={!!erro} value={valor} onChange={onChange}/>
        <button className="eyeb" type="button" aria-label={(mostrar ? "Ocultar" : "Mostrar") + " senha"} aria-pressed={mostrar} onClick={() => setMostrar((m) => !m)}>
          <Olho />
        </button>
      </div>
      <p className="er" role="alert">{erro}</p>
    </div>
  );
}

const Campo = forwardRef(function Campo({ id, label, erro, className = "rf", ...resto }, ref) {

  return (
    <div className={className}>
      <label htmlFor={id}>{label}</label>
      <div className="w">
        <input id={id} ref={ref} className={erro ? "bad" : undefined} aria-invalid={!!erro} {...resto}/>
      </div>
      <p className="er" role="alert">{erro}</p>
    </div>
  );

});


export default function Registro() {
  useTitle("Área de Registro – Keys");
  const navigate = useNavigate();
  const [d, setD] = useState({ cnpj: "", emp: "", cod: "", adm: "", mail: "", salas: [],});
  const [pw, setPw] = useState("");
  const [pw2, setPw2] = useState("");
  const [sn, setSn] = useState("");
  const [sd, setSd] = useState("");
  const [erros, setErros] = useState({});
  const [step, setStep] = useState(0);
  const [reach, setReach] = useState(0);
  const [ok, setOk] = useState([false, false, false]);
  const [saved, setSaved] = useState(false);
  const [carregando, setCarregando] = useState(false);
  const salaRef = useRef();
  const titulo = useRef();
  const primeira = useRef(true);


  useEffect(() => {
    if (primeira.current) {
      primeira.current = false;
      return;
    }
    titulo.current?.focus();
  }, [step, saved]);

  const limpa = (k) =>setErros((e) => ({ ...e, [k]: "" }));
  const campo = (k) => (e) => {
    const v = k === "cnpj" ? mascara(e.target.value) : e.target.value;
    setD((x) => ({ ...x, [k]: v }));
    limpa(k);
  };

  const ir = (n) => {
    setStep(n);
    setReach((r) => Math.max(r, n));
  };

  const v0 = () => {
    const e = {
      cnpj: cnpjOk(d.cnpj) ? "" : "Informe um CNPJ válido.",
      emp: d.emp.trim() ? "" : "Informe o nome da empresa.",
    };
    setErros((x) => ({ ...x, ...e }));
    return !e.cnpj && !e.emp;
  };

  const v1 = () => {
    const e = {cod: d.cod.trim() ? "" : "Informe o código do admin.",
      adm: d.adm.trim() ? "" : "Informe o nome do admin.",
      mail: emailOk(d.mail.trim()) ? "" : "Informe um e-mail válido.",
      pw: senhaOk(pw) ? "" : "A senha ainda não atende aos requisitos.",
      pw2: pw2 && pw2 === pw ? "" : "As senhas não coincidem.",};

    setErros((x) => ({ ...x, ...e }));
    return Object.values(e).every((m) => !m);
  };

  const validaSala = () => {
    const n = sn.trim();
    const desc = sd.trim();
    const e = {sn: !n ? "Informe o nome da sala." : d.salas.some((s) => s.n.toLowerCase() === n.toLowerCase())
          ? "Já existe uma sala com esse nome.": "", sd: desc ? "" : "Informe a descrição da sala.",
    };
    setErros((x) => ({ ...x, ...e }));
    return !e.sn && !e.sd ? { n, d: desc } : null;
  };

  const adicionarSala = () => {
    const s = validaSala();
    if (!s) return null;
    setD((x) => ({...x, salas: [...x.salas, s],}));
    setSn("");
    setSd("");
    limpa("rooms");
    salaRef.current?.focus();
    return s;
  };

  const removerSala = (indice) => {
    setD((x) => ({...x, salas: x.salas.filter((_, i) => i !== indice),}));
  };

  const enviar0 = (e) => {
    e.preventDefault();
    if (!v0() || carregando) return;
    setOk((o) => [true, o[1], o[2]]);
    ir(1);
  };

  const enviar1 = (e) => {
    e.preventDefault();
    if (!v1() || carregando) return;
    setOk((o) => [o[0], true, o[2]]);
    ir(2);
  };


  const enviar2 = async (e) => {
    e.preventDefault();
    if (carregando) return;
    let lista = [...d.salas];
    if (sn.trim() || sd.trim()) {
      const s = validaSala();
      if (!s) return;
      lista.push(s);
    }

    if (!lista.length) {
      setErros((x) => ({
        ...x,
        rooms: "Adicione ao menos uma sala.",
      }));
      return;
    }

    setCarregando(true);

    try {
      const empresa = await api.post("/api/empresa",
        {
          cd_cnpj_empresa: d.cnpj.replace(/\D/g, ""),
          nm_empresa: d.emp.trim(),
        },
        {
          validateStatus: () => true,
        }
      );

      if (empresa.status !== 201) {
        throw new Error(
          empresa.data?.mensagem ||
          empresa.data?.erro ||
          "Não foi possível cadastrar a empresa."
        );
      }

      const admin = await api.post("/auth/criarAdmin",
        {
          cd_usuario: d.cod.trim(),
          nm_usuario: d.adm.trim(),
          nm_email_usuario: d.mail.trim(),
          nm_senha_usuario: pw,
          nm_confirmar_senha: pw2,
          cd_cnpj_empresa: d.cnpj.replace(/\D/g, ""),
        },
        {
          validateStatus: () => true,
        }
      );

      if (admin.status !== 201) {
        throw new Error(
          admin.data?.mensagem ||
          admin.data?.erro ||
          "Não foi possível cadastrar o administrador."
        );
      }

      for (const sala of lista) {
        const params = new URLSearchParams();
        params.append("nome", sala.n);
        params.append("desc", sala.d);
        params.append("cnpj", d.cnpj.replace(/\D/g, ""));
        const resp = await api.post("/api/salas", params,
          {headers: { "Content-Type": "application/x-www-form-urlencoded",},validateStatus: () => true,});

        if (![200, 201].includes(resp.status)) {
          throw new Error(
            resp.data?.mensagem ||
            resp.data?.erro ||
            `Não foi possível cadastrar a sala ${sala.n}.`
          );
        }
      }
      setD((x) => ({...x,salas: lista,}));
      setSn("");
      setSd("");
      setOk([true, true, true]);
      localStorage.setItem("emailLogin", d.mail.trim());
      setSaved(true);
    } catch (erro) {
      setErros((x) => ({...x, rooms: mensagemErro(erro),}));
    } finally {
      setCarregando(false);
    }
  };

  const cancelar = () => {
    if (carregando) return;
    if (window.confirm("Cancelar o registro? Os dados preenchidos serão perdidos.")) {
      navigate("/");
    }
  };

  const teclaSala = (e) => {
    if (e.key === "Enter") {
      e.preventDefault();
      adicionarSala();
    }
  };

  const n = d.salas.length;
  const regras = [
  ["Mínimo de 8 caracteres", pw.length >= 8],
  ["Uma letra maiúscula", /[A-Z]/.test(pw)],
  ["Uma letra minúscula", /[a-z]/.test(pw)],
  ["Um número", /\d/.test(pw)],
  ["Um caractere especial (@#$%^&+=!)", /[@#$%^&+=!]/.test(pw)],
];

  const etiqueta = (i, conteudo) => (
    <li key={i}>
      <button className={`kt${i === step && !saved ? " on" : ""}${ok[i] ? " done" : ""}`} type="button" style={{ "--c": ETAPAS[i] }} disabled={saved || carregando || i > reach} aria-current={i === step && !saved ? "step" : undefined} onClick={() => !saved && !carregando && i <= reach && ir(i)}>
        <span className="hole">{ok[i] ? "✓" : i + 1}</span>
        <span className="card3">{conteudo}</span>
      </button>
    </li>
  );

  const corpo = (rotulo, valor, sub, ph) => (
    <>
      <small>{rotulo}</small>
      <strong className={valor ? "" : "ph"}>{valor || ph}</strong>
      <span className="sub">{sub}</span>
    </>
  );


  return (
    <>
      <Header />
      <div className="p-registro">
        <main className="rg">
          <aside className="board">
            <h1>Área de Registro</h1>
            <p>Acesso liberado após a compra do Keys. Em três etapas, sua instituição fica pronta para reservar salas. Clique no tópico anterior caso precise corrigir algo.</p>
            <ol className="tags">
              {etiqueta(0, corpo( "Empresa", d.emp.trim(), d.cnpj || "00.000.000/0000-00", "Nome da empresa"))}

              {etiqueta(1,corpo("Administrador", d.adm.trim(), d.mail.trim() || "E-mail do admin", "Nome do admin"))}

              {etiqueta(2,
                <>
                  <small>Salas</small>
                  <strong className={n ? "" : "ph"}>
                    {n ? n + (n > 1 ? " salas" : " sala") : "Nenhuma sala ainda"}
                  </strong>

                  <span className="rm">
                    {d.salas.slice(0, 5).map((s, i) => (
                      <b key={i} style={{ background: COR[i % 5] }}>{s.n}</b>
                    ))}

                    {n > 5 && (
                      <b style={{ background: "var(--chip)", color: "var(--ink)",}}>
                        +{n - 5}
                      </b>
                    )}

                  </span>
                </>
              )}
            </ol>
          </aside>

          <section className="stage">
            {!saved && (
              <>
                <div className="cnt"> Etapa {step + 1} de 3 </div>
                <div className="prog" aria-hidden="true">
                  {ETAPAS.map((c, i) => (
                    <i key={c} className={i <= step ? "on" : undefined} style={{ "--c": c }}/>
                  ))}
                </div>
              </>
            )}

            {!saved && step === 0 && (
              <form onSubmit={enviar0} noValidate>
                <h2 className="big"  ref={titulo} tabIndex={-1}>Comece pela empresa</h2>
                <p className="lead"> Esses dados identificam a sua instituição no Keys.</p>
                <div className="grid2 one">
                  <Campo id="cnpj" label="CNPJ da empresa" inputMode="numeric" autoComplete="off" maxLength={18} placeholder="00.000.000/0000-00" value={d.cnpj} onChange={campo("cnpj")} erro={erros.cnpj}/>
                  <Campo id="emp" label="Nome da empresa" autoComplete="organization" placeholder="Ex.: Instituto Boqueirão" value={d.emp} onChange={campo("emp")}  erro={erros.emp}/>
                </div>


                <div className="bar">
                  <button className="rb can" type="button" onClick={cancelar} disabled={carregando}>Cancelar</button>
                  <div className="r">
                    <button className="rb pri"  disabled={carregando}>{carregando ? "Cadastrando..." : "Próximo"}</button>
                  </div>
                </div>

              </form>

            )}

            {!saved && step === 1 && (
              <form onSubmit={enviar1} noValidate>
                <h2 className="big" ref={titulo} tabIndex={-1}>Quem vai administrar?</h2>
                <p className="lead"> O administrador cria reservas, usuários e salas.</p>
                <div className="grid2">
                  <Campo id="cod" label="Código" autoComplete="off" placeholder="Matrícula ou código" value={d.cod} onChange={campo("cod")} erro={erros.cod}/>

                  <Campo id="adm" label="Nome" autoComplete="name" placeholder="Nome completo" value={d.adm} onChange={campo("adm")} erro={erros.adm}/>

                  <Campo id="mail" className="rf span2" label="E-mail" type="email" autoComplete="email"  placeholder="nome@instituicao.edu.br"  value={d.mail} onChange={campo("mail")} erro={erros.mail}/>

                  <CampoSenha id="pw" label="Senha" placeholder="Crie uma senha" valor={pw}
                    onChange={(e) => {
                      setPw(e.target.value);
                      limpa("pw");
                    }}
                    erro={erros.pw}
                  />

                  <CampoSenha id="pw2" label="Confirmar senha" placeholder="Repita a senha" valor={pw2}
                    onChange={(e) => {
                      setPw2(e.target.value);
                      limpa("pw2");
                    }}
                    erro={erros.pw2}
                  />


                  <ul className="rules span2">
                    {regras.map(([t, v]) => (
                      <li key={t}className={v ? "ok" : undefined}>
                        <i></i>
                        {t}
                      </li>
                    ))}
                  </ul>
                </div>

                <div className="bar">
                  <button className="rb can" type="button" onClick={cancelar} disabled={carregando}>Cancelar</button>
                  <div className="r">
                    <button className="rb pri" disabled={carregando}>
                      {carregando ? "Cadastrando..." : "Próximo"}
                    </button>
                  </div>
                </div>
              </form>
            )}

            {!saved && step === 2 && (
              <form onSubmit={enviar2} noValidate>
                <h2 className="big" ref={titulo} tabIndex={-1}> Cadastre as salas</h2>
                <p className="lead">Adicione quantas precisar. Você pode criar outras depois.</p>
                <div className="grid2">
                  <Campo id="sn" ref={salaRef} label="Nome da sala" autoComplete="off" placeholder="Ex.: Laboratório 9" value={sn}
                    onChange={(e) => {
                      setSn(e.target.value);
                      limpa("sn");
                    }}
                    onKeyDown={teclaSala}
                    erro={erros.sn}
                  />

                  <Campo id="sd" label="Descrição da sala" autoComplete="off" placeholder="Ex.: 30 lugares, projetor" value={sd}
                    onChange={(e) => {
                      setSd(e.target.value);
                      limpa("sd");
                    }}
                    onKeyDown={teclaSala}
                    erro={erros.sd}
                  />
                </div>

                <button className="rb ghost2 add" type="button" onClick={adicionarSala} disabled={carregando}>+ Adicionar sala</button>
                <p className="er" role="alert">{erros.rooms}</p>
                <ul className="rooms" aria-label="Salas adicionadas">
                  {d.salas.map((s, i) => (
                    <li key={i}>
                      <i className="rk" style={{ background: COR[i % 5] }}/>
                      <div className="t">{s.n}<small>{s.d}</small></div>
                      <button type="button" aria-label={`Remover ${s.n}`} onClick={() => removerSala(i)} disabled={carregando}>Remover</button>
                    </li>
                  ))}
                </ul>

                <div className="bar">
                  <button className="rb can" type="button" onClick={cancelar} disabled={carregando}>Cancelar</button>
                  <div className="r">
                    <button className="Salvar" disabled={carregando}>
                      <b>{carregando ? "Salvando salas..." : "Salvar"}</b>
                    </button>
                  </div>
                </div>
              </form>
            )}

            {saved && (
              <div className="okw">
                <div className="okc">
                  <svg width="40" height="40" viewBox="0 0 24 24" fill="none"
                    stroke="#0a1a2e" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
                    <path d="M5 12.5l4.5 4.5L19 7.5" />
                  </svg>
                </div>

                <h2 className="big" ref={titulo} tabIndex={-1}>Tudo pronto!</h2>

                <p className="lead" style={{ marginBottom: 0 }}> Sua instituição foi registrada. Agora é só entrar e criar a primeira reserva.</p>

                <div className="sumr">
                  <div>
                    <small>Empresa</small>
                    <strong>{d.emp.trim()}</strong>
                  </div>

                  <div>
                    <small>Administrador</small>
                    <strong> {d.adm.trim()} · {d.mail.trim()}</strong>
                  </div>

                  <div>
                    <small>Salas</small>
                    <strong>{n} {n > 1 ? "salas" : "sala"}</strong>
                  </div>

                </div>

                <Link className="rb pri" to="/login">Entrar no Keys</Link>

              </div>
            )}
          </section>
        </main>
      </div>
    </>
  );
}
