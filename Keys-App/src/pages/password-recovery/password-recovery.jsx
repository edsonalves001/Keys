import { useEffect } from "react";
import './password-recovery.css'
import iconeChave from '../../assets/esqueceu_senha.png';

const Password_Recovery = function Password_Recovery() {

    useEffect(()=>{document.title = "Recuperação de senha";},[])
    return (
        <>
            
            <header className="full-menu"><p>keYs</p></header><br></br>
            <form className="recuperarSenha">
                <img alt="Icone de chave para informar que essa é a página de recuperar senha" src={iconeChave}></img>
                <h1>Esqueceu sua senha?</h1>
                <input type="text" id="email" placeholder="Digite seu email"></input><br></br>
                <button>Confirmar</button><br></br><br></br>
            </form>                
            
        </>
    );
}

export default Password_Recovery