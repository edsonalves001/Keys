import { useState } from "react";

const Olho = () => (
  <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
    <path d="M1.5 12S5.5 5 12 5s10.5 7 10.5 7-4 7-10.5 7S1.5 12 1.5 12z" />
    <circle cx="12" cy="12" r="3" />
  </svg>
);


export default function PasswordField({ id, value, onChange, placeholder, autoComplete, required }) {
  const [mostrar, setMostrar] = useState(false);
  return (
    <div className="f">
      <input
        id={id}
        type={mostrar ? "text" : "password"}
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        autoComplete={autoComplete}
        required={required}
      />
      <button type="button" className="eye" aria-label={mostrar ? "Ocultar senha" : "Mostrar senha"} onClick={() => setMostrar((m) => !m)}>
        <Olho />
      </button>
    </div>
  );
}
