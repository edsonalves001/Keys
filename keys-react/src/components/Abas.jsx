export default function Abas({ aba, onChange }) {
  return (
    <div className="tabs" role="tablist">
      <button className={`pill${aba === "m" ? " on" : ""}`} type="button" onClick={() => onChange("m")}>Manual</button>
      <button className={`pill${aba === "x" ? " on" : ""}`} type="button" onClick={() => onChange("x")}>Planilha Excel</button>
    </div>
  );
}
