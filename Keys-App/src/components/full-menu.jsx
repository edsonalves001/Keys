import React from "react";
import logout from "../assets/logout.png"
import './full-menu.css'
import { useNavigate } from "react-router";

const Menu = function menu() {

    const navigate = useNavigate();

    const handleClick = () => {
        navigate('/');
    }

    return (
        <>
            <header className="full-menu">
                <p onClick={handleClick}>keYs</p>
                <nav>
                    <ul>
                        <li><a href="#">Início</a></li>
                        <li><a href="#">Usuários</a></li>
                        <li><a href="#">Fale Conosco</a></li>
                    </ul>
                    <button>
                        Sair
                        <img src={logout} alt="Ícone de sair do botão. Apenas enfeite do botão de sair"/>
                    </button>
                </nav>
            </header><br></br>
        </>
    );
}

export default Menu