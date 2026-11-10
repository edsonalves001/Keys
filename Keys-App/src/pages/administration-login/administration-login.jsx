import { useEffect, useState } from "react";
import { useNavigate } from "react-router";
import './administration-login.css';
import PhotoLogin from '../../assets/fotoLogin.png';
import auth from "../../service/auth";

const Administration_Login = function Administration_Login() {

    const [email, setEmail] = useState("");
    const [senha, setSenha] = useState("");
    const navigate = useNavigate()

    const handleLandingClick = () => {
        navigate("/")
    }

    useEffect(() => {
        document.title = "Acesse o Keys";
    }, []);

    async function realizarLogin(e) {

        e.preventDefault();

        try {
            const response = await auth.post("/login", {
                nm_email_usuario: email,
                nm_senha_usuario: senha
            });

            const token = response.data.token;

            localStorage.setItem("token", token);

            alert("Login realizado com sucesso!");

            if(token){
                window.location.href = "/cronograma"
            }

            console.log("Token armazenado:", localStorage.getItem("token"));

        } catch (error) {

            console.error("Erro ao realizar login:", error);
            alert("Não foi possível realizar o login.");

        }
    }

    function RecuperacaoSenha(){
        window.location.href="recuperacaodeSenha"
    }

    function cadastrarse(){
        window.location.href="recuperacaodeSenha"
    }

    function visualizarSenha(e) {
        e.preventDefault();

        const campoSenha = document.getElementById("senha");

        if (campoSenha.type === "password") {
            campoSenha.type = "text";
        } else {
            campoSenha.type = "password";
        }
    }

    return (
        <>
            <header className="full-menu">
                <p onClick={handleLandingClick}>keYs</p>
            </header>

            <br />

            <form className="BoxLogin" onSubmit={realizarLogin}>

                <img
                    id="fotoMenuLogin"
                    src={PhotoLogin}
                    alt="Foto de uma porta com uma chave"
                />

                <div className="inputs-group">

                    <h1>Bem vindo de volta</h1>

                    <input
                        type="text"
                        id="email"
                        placeholder="Digite seu email"
                        value={email}
                        onChange={e => setEmail(e.target.value)}
                    />

                    <br />

                    <input
                        type="password"
                        id="senha"
                        placeholder="Digite sua senha"
                        value={senha}
                        onChange={e => setSenha(e.target.value)}
                    />

                    <button
                        id="botaoversenha"
                        type="button"
                        onClick={visualizarSenha}
                    >
                        Ver senha
                    </button>

                    <br />

                    <a href="/recuperacaodeSenha">
                        <h3 className="redirecionamentos" onClick={RecuperacaoSenha}>
                            Esqueceu sua senha?
                        </h3>
                    </a>

                    <a>
                        <h3 className="redirecionamentos">
                            Clique aqui para se cadastrar
                        </h3>
                    </a>

                    <button id="botaoLogin" type="submit">
                        Acessar
                    </button>

                </div>
            </form>
        </>
    );
}

export default Administration_Login;