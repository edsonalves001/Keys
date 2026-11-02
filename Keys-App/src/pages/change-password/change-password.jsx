import { useEffect } from "react";
import './change-password.css'



const Change_Password = function Change_Password() {

    useEffect(()=>{document.title = "Alterar senha";},[])
    return (
        <>
            
            <header className="full-menu"><p>keYs</p></header><br></br>
            <form className="alterarSenha">
                <h1>Altere sua senha</h1>
                <input type="text" id="senha" placeholder="Digite sua nova senha"></input><br></br>
                <input type="text" id="senhaconf" placeholder="Confirme a senha"></input><br></br>
                <button>Confirmar</button><br></br><br></br>
            </form>                
            
        </>
    );
}

export default Change_Password