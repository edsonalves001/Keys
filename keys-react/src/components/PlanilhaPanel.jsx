import { useRef, useState } from "react";
import { useToast } from "../context/ToastContext";

export default function PlanilhaPanel({ tipo, titulo, nota }) {
  const toast = useToast();
  const input = useRef();
  const [arquivo, setArquivo] = useState(null);
  const [arrastando, setArrastando] = useState(false);

  const escolher = (f) => {
    if (f) {
      setArquivo(f);
      toast("Planilha selecionada: " + f.name);
    }
  };

  const baixarModelo = () => {
    const csv = tipo === "res" ? "data;inicio;termino;sala;repeticao\n13/10/2026;06:00;19:00;Sala 01;Não se repete" : "nome;identificador;email\nCarlinhos;001;carlinhos@instituicao.edu.br";
    const a = document.createElement("a");
    a.href = URL.createObjectURL(new Blob(["\ufeff" + csv], { type: "text/csv" }));
    a.download = "modelo-" + (tipo === "res" ? "reservas" : "usuarios") + ".csv";
    a.click();
  };

  const arrasto = (ativo) => (e) => {
    e.preventDefault();
    setArrastando(ativo);
  };

  return (
    <section className="pan xw">
      <h2>{titulo}</h2>
      <p className="note" style={{ fontSize: 17, marginTop: 6 }}>{nota}</p>
      <input type="file" ref={input} accept=".xlsx,.xls,.csv" hidden onChange={(e) => escolher(e.target.files[0])} />
      <button className={`drop${arrastando ? " on" : ""}`} type="button" onClick={() => input.current.click()} onDragEnter={arrasto(true)} onDragOver={arrasto(true)} onDragLeave={arrasto(false)} onDrop={(e) => {arrasto(false)(e); escolher(e.dataTransfer.files[0]);}}>
        Arraste a planilha aqui ou escolha um arquivo
        <small>{arquivo ? arquivo.name : "Formatos aceitos: .xlsx, .xls e .csv"}</small>
      </button>

      <div className="acts">
        <button className="out" type="button" onClick={baixarModelo}> Baixar modelo Excel para {tipo === "res" ? "múltiplas reservas" : "múltiplos usuários"}</button>
        <button className="btn2" type="button" onClick={() => toast(arquivo ? "Importação iniciada." : "Escolha uma planilha antes de importar.")}>Importar planilha</button>
      </div>
    </section>
  );
}
