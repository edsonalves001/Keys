import React from "react";
import logout from "../assets/logout.png"
import './full-menu.css'
import { useNavigate } from "react-router";

const Menu = function menu() {

    const navigate = useNavigate();

    const handleClick = () => {
        navigate('/');
    }
    function clickCronograma(){
        window.location.href ="/cronograma"
    }
    function clickUsuários(){
        window.location.href="dashboardUsuario"
    }
    function sair() {
    localStorage.clear();  
    localStorage.removeItem("token")
    window.location.reload();     
}

    return (
        <>
            <header className="full-menu">
                <p onClick={handleClick}>keYs</p>
                <nav>
                    <ul>
                        <li><a href="#" onClick={clickCronograma}>Início</a></li>
                        <li><a href="#" onClick={clickUsuários}>Usuários</a></li>
                        <li><a href="https://wa.me/5513999999999" target="blank">Fale Conosco</a></li>
                    </ul>
                    <button  onClick={sair} >
                        Sair
                        <img src={logout}alt="Ícone de sair do botão. Apenas enfeite do botão de sair"/>
                    </button>
                </nav>
            </header><br></br>
        </>
    );
}

export default Menu