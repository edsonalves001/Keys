import { Link } from "react-router-dom";
import { useTheme } from "../context/ThemeContext";
import modoEscuro from "../../src/assets/modoEscuro.png";
import modoClaro from "../../src/assets/modoClaro.png";
import iconKeys from "../../src/assets/icone_keys.png";
import  removerToken  from "../service/auth";

export default function Header({ variant = "auth", ativo }) {
  const { theme, alternar } = useTheme();
  const botaoTema = (
    <button type="button"  onClick={alternar}>
      <div id= "botaomodo">
        <img  id= "imagemmodo" src={theme === "dark" ? modoClaro : modoEscuro} alt="botão para trocar 
      o modo, de escuro para claro ou de claro para escuro"/> 
      </div>
    </button>
  );
  
  function logout(){
    removerToken();
    localStorage.clear();  
    localStorage.removeItem("token")
    window.location.reload();  
  }

  function abrirzapzap() {
    const telefone = "5513999999999"; 
    const mensagem = "Olá! Gostaria de obter mais informações sobre o KEYS.";

    const url = `https://wa.me/${telefone}?text=${encodeURIComponent(mensagem)}`;
    window.open(url, "_blank");
}
  
  if (variant === "app") {
    return (
      <header className="top">
        <img  className="logo" src={iconKeys}></img>
        <span className="sp" />
        <nav className="nav">
          <Link to="/cronograma" className={ativo === "cronograma" ? "on" : undefined}>Cronograma</Link>
          <Link to="/usuarios" className={ativo === "usuarios" ? "on" : undefined}>Usuários</Link>
          <a href="#" onClick={abrirzapzap} >Fale conosco</a>
        </nav>
        <span className="sp" />
        {botaoTema}
        <Link className="out d" onClick={logout}>Sair</Link>
      </header>
    );
  }

  if (variant === "landing") {
    return (
      <header className="top">
        <Link to="/"><img  className="logo" src={iconKeys}></img></Link>
        <nav className="nav" aria-label="Seções">
          <a href="#sobre">Sobre</a>
          <a href="#vantagens">Vantagens</a>
          <a href="#como">Como funciona</a>
          <a onClick={abrirzapzap}>Contato</a>
        </nav>
        <span className="sp" />
        {botaoTema}
        <Link className="ghost entrar" to="/login">Entrar</Link>
      </header>
    );
  }

  return (
    <header className="top">
      <Link to="/"><img  className="logo" src={iconKeys}></img></Link>
      <span className="sp" />
      {botaoTema}
    </header>
  );
}
