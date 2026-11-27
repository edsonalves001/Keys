import { Link } from "react-router-dom";
import Header from "../../components/Header";
import useTitle from "../../hooks/useTitle";
import "./landing.css";

const CHAVES = ["#a06bd6", "#a06bd6", "#a06bd6", "#ef5b5b", "#ef5b5b", "#ef5b5b", "#f29b38", "#f29b38", "#f6d36b", "#f6d36b", "#fff", "#fff", "#88cc4a", "#88cc4a", "#b5e04d", "#3ab5e6", "#3ab5e6", "#3ab5e6"];

export default function Landing() {
  useTitle("Keys");
  return (
    <div className="p-landing">
      <div className="land">
        <Header variant="landing" />
        <div className="hero">
          <div>
            <small>SEGURANÇA PARA INSTITUIÇÕES ACADÊMICAS</small>
            <h1>A chave para a gestão de salas sem abrir mão da segurança</h1>
            <p>Reserve salas, libere chaves e acompanhe cada retirada e devolução em uma única plataforma.</p>
            <div className="bt">
              <Link className="primary" to="/registro">Adquira o Keys</Link>
            </div>
          </div>
          <div className="mini">
            {CHAVES.map((c, i) => (
              <span key={i} style={{ background: c }} />
            ))}
          </div>
        </div>

        <div className="blk" id="sobre">
          <h2>Sobre o Keys</h2>
          <p>Plataforma de reservas e controle de acesso a ambientes. O funcionário reserva a sala, a reserva é validada e a chave correspondente é liberada, com organização, segurança e rastreabilidade.</p>
          <div className="cols" id="vantagens">
            <div><b className="ic">✓</b><h3>Segurança</h3>O mais alto nível de segurança para você e para seus colaboradores, todos os dias.</div>
            <div><b className="ic">▦</b><h3>Praticidade</h3>Uma experiência simples no uso do sistema, com validação por QR Code.</div>
            <div><b className="ic">☊</b><h3>Suporte</h3>Sempre à disposição para tirar as dúvidas que você e sua equipe tenham.</div>
          </div>
        </div>

        <div className="blk alt" id="como">
          <h2>Como funciona</h2>
          <div className="steps">
            <div><div className="num">1</div><h3>Cadastre salas e usuários</h3><p>Crie agendamentos manualmente ou importe planilhas Excel.</p></div>
            <div><div className="num">2</div><h3>Reserve no cronograma</h3><p>Consulte a disponibilidade e organize reservas em um cronograma intuitivo.</p></div>
            <div><div className="num">3</div><h3>Retire e devolva com QR Code</h3><p>Acompanhe cada retirada e devolução de chave com rastreabilidade.</p></div>
          </div>
        </div>

        <footer className="ft" id="contato">
          <div className="cs">
            <div><h4>Endereço</h4><p>Praça 19 de Janeiro, 144 – Boqueirão<br />Praia Grande/SP – CEP 11700-100</p></div>
            <div><h4>Funcionamento</h4><p>Segunda a sexta: 07:00 às 21:00<br />Sábado e domingo: 09:00 às 19:00</p></div>
            <div><h4>Suporte</h4><p>Celular: 11 99999-9999<br />Fixo: 11 88888-8888</p></div>
          </div>
          <div className="cp">Todos os direitos reservados a Keys LTDA.</div>
        </footer>
      </div>
    </div>
  );
}
