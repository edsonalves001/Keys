const T0 = 420;
const T1 = 1260;

export default function Timeline({ linhas }) {
  return (
    <div className="tl">
      <div className="tl-r">
        <span />
        <div className="tl-ax">
          {[7, 9, 11, 13, 15, 17, 19, 21].map((h) => (
            <span key={h} style={{ left: `${((h * 60 - T0) / (T1 - T0)) * 100}%` }}>
              {String(h).padStart(2, "0")}h
            </span>
          ))}
        </div>
      </div>
      {linhas.map((l) => (
        <div key={l.nome} className={`tl-r${l.me ? " me" : ""}`}>
          <span>{l.nome}</span>
          <div className="tl-t">
            {l.blocos.map((bl, i) => {
              const a = Math.max(T0, bl.a);
              const b = Math.min(T1, bl.b);
              if (b <= a) return null;
              return (
                <i key={i} className={`bk ${bl.tipo}`} style={{ left: `${((a - T0) / (T1 - T0)) * 100}%`, width: `${((b - a) / (T1 - T0)) * 100}%` }}/>
              );
            })}
          </div>
        </div>
      ))}
    </div>
  );
}
