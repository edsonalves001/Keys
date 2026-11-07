import React, { useState } from "react";
import "./landing.css"
import iconeModoEscuro from '../../assets/icone_modoEscuro.png';
import iconeKeys from '../../assets/icone_Keys.png';
import produtoProvisorio from '../../assets/ProdutoProvisorio.png';
import seguranca from '../../assets/Seguranca.png';
import praticidade from '../../assets/Praticidade.png';
import suporte from '../../assets/Suporte.png'
import { useNavigate } from "react-router";
const Landing = function landing() {

    const navigate = useNavigate()
    const [isMenuOpen, setIsMenuOpen] = useState(false);

    const handleLoginClick = () => {
        navigate('/login')
    }

    const alterarModo = () => { //manipula aqui, Pedro

    }

    const ativarMenu = () => {
        setIsMenuOpen(true)
    }

    const desativarMenu = () => {
        setIsMenuOpen(false)
    }

    return (
        <>
            <header className="menuLanding">
                <img src={iconeKeys} alt="Logo do Keys" />
                <nav>
                    <ul>
                        <li><a href="#inicio">Início</a></li>
                        <li><a href="#sobre">Sobre</a></li>
                        <li><a href="#vantagens">Vantagens</a></li>
                        <li><a href="#comoFunciona">Como Funciona</a></li>
                    </ul>
                </nav>

                <div className="botao-modo" onClick={alterarModo()}> 
                    <input type="checkbox" name="checkbox" id="checkbox" className="checkbox"/>
                    <label htmlFor="checkbox" className="checkbox-label"></label>
                </div>
            </header>

            <header className="menuMobileLanding">
                 
            </header>

            <section className="landingConteudo" id="inicio">
                <div className="textoLanding">
                    <h1 id="sloganKeys"><span className="keysText">keYs</span> é a chave dos seus problemas</h1>
                    <p id="paragrafoLanding">
                        Keys é uma empresa de segurança com foco em instituições
                        acadêmicas, nosso objetivo é tornar a sua gestão de salas
                        algo mais simples e prático, sem abrir mão da segurança.
                    </p>
                    <div className="botoesLanding">
                        <button id="botaoEntre" onClick={handleLoginClick}><b>Entre no </b><span><b className="keysButtonEntre">keYs</b></span></button>
                        <button id="botaoAdquira"><b>Adquira o </b><span><b className="keysButton">keYs</b></span></button>
                    </div>
                </div>
                <div className="imagemLanding">
                    <img id="imagemdoProduto" src={produtoProvisorio} alt="Produto Keys" />
                </div>
                <a id="sobre"></a>
            </section>
            <a><h1 className="elementosMenu">Sobre o Keys</h1></a>
            <a id="vantagens"></a>
            <p className="textoSobre">
                O Keys é uma plataforma inteligente de gerenciamento de reservas e controle de acesso a ambientes. Por meio dela, os funcionários podem realizar reservas de salas de forma prática e segura. Após a validação da reserva, o acesso à chave correspondente é liberado, permitindo que o funcionário utilize o ambiente previamente reservado. Tudo isso com mais organização, segurança e rastreabilidade no gerenciamento de espaços e chaves.
            </p>
            <a><h1 className="elementosMenu">Vantagens</h1></a>
            <div class="vantagens-container">
                <div class="vantagens-card">
                    <img src={seguranca} alt="Foto para exemplificar a segurança do Keys" class="vantagens-img" />
                    <h3>Segurança</h3>
                    <p>Trabalhamos diariamente para trazer o mais alto nível de segurança para você e para os seus colaboradores.</p>
                </div>

                <div class="vantagens-card">
                    <img src={praticidade} alt="Foto de um usuário escaneando o Qr code do Keys" class="vantagens-img" />
                    <h3>Praticidade</h3>
                    <p>Buscamos trazer cada vez mais praticidade no uso do nosso sistema, permitindo a você e seus colaboradores uma agradável experiência.</p>
                </div>

                <div class="vantagens-card" >
                    <img src={suporte} alt="Foto do suporte do Keys" class="vantagens-img" />
                    <h3>Suporte</h3>
                    <p id="comoFunciona">Estamos sempre à disposição para tirar as dúvidas que você e seus colaboradores tenham em relação ao nosso sistema.</p>
                </div>
            </div>
            <a><h1 className="elementosMenu" >Como funciona?</h1></a>
            <p className="textoSobre">Por meio de uma plataforma centralizada, o KEYS permite organizar reservas em um cronograma intuitivo, com a possibilidade de cadastrar agendamentos manualmente ou importar planilhas Excel. Também oferece ferramentas para cadastrar e editar salas, gerenciar usuários e consultar a disponibilidade dos ambientes. Para tornar o controle das chaves mais eficiente, a plataforma integra validações por QR Code, permitindo acompanhar as operações de retirada e devolução.</p><footer class="footer">
                <div class="footer-content">
                    <div class="footer-col">
                        <h3>Endereço</h3>
                        <p>Praça 19 de Janeiro, 144 - Boqueirão</p>
                        <p>Praia Grande/SP - CEP: 11700-100</p>
                    </div>
                    <div class="footer-col">
                        <h3>Funcionamento</h3>
                        <p>Segunda a sexta - das 07:00 às 21:00</p>
                        <p>Sábado e Domingo - das 09:00 às 19:00</p>
                    </div>
                    <div class="footer-col">
                        <h3>Suporte</h3>
                        <p>Celular: 11 99999-9999</p>
                        <p>Telefone fixo: 11 88888-8888</p>
                    </div>
                </div>
                <hr class="footer-divider" />
                <div class="footer-bottom"><p>Todos os direitos reservados a Keys LTDA.</p></div>
            </footer>
        </>
    );
}

export default Landing