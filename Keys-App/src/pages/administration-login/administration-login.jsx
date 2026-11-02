import { useEffect } from "react";
import './administration-login.css'
import PhotoLogin from '../../assets/fotoLogin.png';


const Administration_Login = function Administration_Login() {

    useEffect(()=>{document.title = "Acesse o Keys";},[])
    return (
        <>
            
            <header className="full-menu"><p>keYs</p></header><br></br>
           <form className="BoxLogin">
    <img id="fotoMenuLogin" src={PhotoLogin} alt="Foto de uma porta com uma chave" />
    
    <div className="inputs-group">
        <h1>Bem vindo de volta</h1>
        <input type="text" id="email" placeholder="Digite seu email" /><br></br>
        <input type="password" id="senha" placeholder="Digite sua senha" /><button id="botaoversenha" onClick={
            function a(){
               const a = document.getElementById("senha");
                if(a.type === "password"){
                    a.type = "text";
                }else{
                    a.type = "password";
                }
        }}>Ver senha</button><br></br>
        <a href="/recuperacaodeSenha"><h3 className="redirecionamentos">Esqueceu sua senha?</h3></a>
        <a><h3 className="redirecionamentos">Clique aqui para se cadastrar</h3></a>
        <button id="botaoLogin" type="button">Acessar</button>
    </div>
</form>               
            
        </>
    );
}

export default Administration_Login