import React, { useRef, useState } from "react";
import { Navigate, useNavigate } from "react-router";
import { api } from "../../api/api.js"
import "./basic-records.css"
import admBotao from "../../assets/logo-adm-botao.png"
import salaBotao from "../../assets/logo-sala-botao.svg"
import empresaBotao from "../../assets/logo-empresa-botao.png"
import iconeSucesso from "../../assets/icone-sucesso.png"
import iconeErro from "../../assets/icone-erro.png"
import deletarBotao from "../../assets/delete.png"
import editBotao from "../../assets/edit-reservation.png"

const BasicRecords = () => {


    //Para quando precisar alterar visibilidade form:
    const [fecharFormEmpresa, setFecharFormEmpresa] = useState(false)
    const [fecharFormAdm, setFecharFormAdm] = useState(true)
    const [fecharFormSala, setFecharFormSala] = useState(true)

    //Modal sucesso ou erro:
    const [infoModal, setInfoModal] = useState("")
    const [ativarModalSucesso, setAtivarModalSucesso] = useState(false)
    const [ativarModalErro, setAtivarModalErro] = useState(false)

    //Criar empresa
    const [cnpjEmpresa, setCnpjEmpresa] = useState(null) //usar no add adm tmb
    const [nomeEmpresa, setNomeEmpresa] = useState(null)

    //Criar admin
    const [formAdmin, setFormAdmin] = useState({
        cdAdmin: "",
        nmAdmin: "",
        emailAdmin: "",
        senhaAdmin: "",
        confirmarSenha: "",
    });

    //Criar Salas:

    const [formSala, setFormSala] = useState([]) //array de objetos sala!
    const [contadorId, setContadorId] = useState(1) //contar id temporário das salas aqui
    //pra atualizar dinamicamente no input e dps ir pro array de objetos
    const [nmSala, setNmSala] = useState("")
    const [dsSala, setDsSala] = useState("")
    const [editarSala, setEditarSala] = useState(null) //usado pra ser usado no modal de atualizar sala

    //Função deletar sala do array state:
    const handleRemoverSala = (id) => {
        console.log(id)
        setFormSala((ant) => ant.filter((s) => s.id !== id))
    }

    //Função editar sala:
    function handleAlterarSala(id) {
        if (editarSala.nm_sala.trim().length === 0) {
            setAtivarModalErro(true)
            setInfoModal("Nome da sala é obrigatório!")
            setEditarSala(false)
            return; // para aqui, pra n precisar rodar o resto
        }

        setFormSala((ant) =>
            ant.map((s) => (s.id === id ? { ...editarSala } : s))
        );
        setEditarSala(null);
    }

    //Função criar sala no array:
    const handleCriarSala = () => {

        if (nmSala.length === 0) {
            setInfoModal("Insira pelo menos o nome da tarefa!")
            setAtivarModalErro(true)
            return;
        }

        setFormSala((sala) => ([
            ...sala, { id: contadorId, nm_sala: nmSala, ds_sala: dsSala }
        ]))
        console.log(formSala)
        setContadorId((c) => c + 1)
    }


    //Função inserir no banco as salas:
    const handleInserirSalasBanco = async () => {
        console.log(cnpjEmpresa)

        if (formSala.length === 0) {
            setAtivarModalErro(true)
            setInfoModal("Não há sala a ser inserida!")
            return;
        }

        for (const sala of formSala) { //foreach
            try {
                const params = new URLSearchParams();
                params.append("nome", sala.nm_sala);
                params.append("desc", sala.ds_sala); //igor colocou query param.....
                params.append("cnpj", cnpjEmpresa);
                const resp = await api.post("/api/salas", params, {
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded"
                    }
                });
                console.log(resp)
                if (resp.status == 200) {
                    setInfoModal("Tarefas adicionadas com sucesso!")
                    setAtivarModalSucesso(true)
                }
            } catch (e) {
                setAtivarModalErro(true)
                setInfoModal(e.message)
            }
        }
    };

    //Inserindo empresa e passando pro form de adm:
    const handleContinuarEmpresa = async function () {


        //Fazendo req
        const resp = await api.post('/api/empresa', { "cd_cnpj_empresa": cnpjEmpresa, "nm_empresa": nomeEmpresa }, {
            validateStatus: () => true,   // qualquer status vira "sucesso" = nao quero gerar exceção e sim abrir modal
        })

        console.log(resp)

        //Verificando status:
        if (resp.status == 201) {
            setInfoModal("Empresa adicionada com sucesso!")
            setAtivarModalSucesso(true)

            //Fechando e abrindo modais necessários pra avançar:
            setFecharFormEmpresa(true)
            setFecharFormAdm(false)
        } else {
            setInfoModal("Cnpj ou nome da empresa inválidos!")
            setAtivarModalErro(true)
        }
    };


    //Inserindo adm e passand pro form de salas:
    const handleContinuarSala = async function () {


        console.log(formAdmin)

        //Fazendo req
        const resp = await api.post('/auth/criarAdmin', {
            "cd_usuario": formAdmin.cdAdmin, "nm_usuario": formAdmin.nmAdmin, "nm_email_usuario": formAdmin.emailAdmin,
            "nm_senha_usuario": formAdmin.senhaAdmin, "nm_confirmar_senha": formAdmin.confirmarSenha, "cd_cnpj_empresa": cnpjEmpresa
        }, {
            validateStatus: () => true,   // qualquer status vira "sucesso" = nao quero gerar exceção e sim abrir modal
        })

        console.log(resp)

        //Verificando status:
        if (resp.status == 201) {
            setInfoModal("Admin adicionado com sucesso!")
            setAtivarModalSucesso(true)

            setFecharFormAdm(true)
            setFecharFormSala(false)
        } else {
            setInfoModal("Dados inseridos tem inconsistências!")
            setAtivarModalErro(true)
        }
    }

    //Redirecionar landing:
    const navigate = useNavigate()

    const handleLandingClick = () => {
        navigate('/')
    }

    return (
        <>
            <header className="basic-records-menu">
                <p onClick={handleLandingClick}>keYs</p>
            </header>
            <main className="basic-records-main">

                <div className={`form-empresa ${fecharFormEmpresa ? "form-invisivel" : ""}`}>
                    <h1>Área de Registro</h1>
                    <nav className="menu-form">
                        <ul>
                            <li className="ativo"><button><img src={empresaBotao} alt="Ícone que representa a empresa a ser adicionado" /> Criar Empresa</button></li>
                            <li><button><img src={admBotao} alt="Ícone que representa o adm a ser adicionado" /> Criar Admin</button></li>
                            <li><button><img src={salaBotao} alt="Ícone que representa a sala a ser adicionada" /> Criar Salas</button></li>
                        </ul>
                    </nav>

                    <div className="corpo-form">
                        <h2>Informações Empresa</h2>
                        <div className="input-empresa">
                            <label htmlFor="input-cnpj-empresa">Cnpj da empresa</label>
                            <input type="text" className="input-cnpj-empresa" placeholder="XX.XXX.XXX/XXXX-XX" onChange={(e) => { setCnpjEmpresa(e.target.value) }} />
                        </div>

                        <div className="input-empresa">
                            <label htmlFor="input-nm-empresa">Nome da empresa</label>
                            <input type="text" className="input-nm-empresa" onChange={(e) => { setNomeEmpresa(e.target.value) }} />
                        </div>
                    </div>

                    <div className="footer-form">
                        <button className="seguir" onClick={(e) => handleContinuarEmpresa(e)}>Criar e Seguir</button>
                    </div>
                </div>

                <div className={`form-admin ${fecharFormAdm ? "form-invisivel" : ""}`}>
                    <h1>Área de Registro</h1>
                    <nav className="menu-form">
                        <ul>
                            <li className="ativo"><button><img src={empresaBotao} alt="Ícone que representa a empresa a ser adicionado" /> Criar Empresa</button></li>
                            <li className="ativo"><button><img src={admBotao} alt="Ícone que representa o adm a ser adicionado" /> Criar Admin</button></li>
                            <li><button><img src={salaBotao} alt="Ícone que representa a sala a ser adicionada" /> Criar Salas</button></li>
                        </ul>
                    </nav>
                    <div className="corpo-form">
                        <h2>Informações Admin</h2>
                        <div className="listagem-inputs-corpo">
                            <div className="input-admin">
                                <label htmlFor="input-cd-admin">Código do admin</label>
                                <input type="text" className="input-cd-admin" onChange={(e) => { setFormAdmin({ ...formAdmin, cdAdmin: e.target.value }); console.log(formAdmin) }} />
                            </div>
                            <div className="input-admin">
                                <label htmlFor="input-nm-admin">Nome do admin</label>
                                <input type="text" className="input-nm-admin" onChange={(e) => { setFormAdmin({ ...formAdmin, nmAdmin: e.target.value }); console.log(formAdmin) }} />
                            </div>
                            <div className="input-admin">
                                <label htmlFor="input-email-admin">Email do admin</label>
                                <input type="text" className="input-email-admin" onChange={(e) => { setFormAdmin({ ...formAdmin, emailAdmin: e.target.value }); console.log(formAdmin) }} />
                            </div>
                            <div className="input-admin">
                                <label htmlFor="input-senha-admin">Senha do admin</label>
                                <input type="password" className="input-senha-admin" onChange={(e) => { setFormAdmin({ ...formAdmin, senhaAdmin: e.target.value }); console.log(formAdmin) }} />
                            </div>
                            <div className="input-admin">
                                <label htmlFor="input-confirmar-senha-admin">Confirmar senha do admin</label>
                                <input type="password" className="input-confirmar-senha-admin" onChange={(e) => { setFormAdmin({ ...formAdmin, confirmarSenha: e.target.value }); console.log(formAdmin) }} />
                            </div>
                        </div>
                    </div>

                    <div className="footer-form">
                        <button className="seguir" onClick={handleContinuarSala}>Criar e Seguir</button>
                    </div>
                </div>

                <div className={`form-salas ${fecharFormSala ? "form-invisivel" : ""}`}> {/*${fecharFormSala ? "form-invisivel" : ""}*/}
                    <h1>Área de Registro</h1>
                    <nav className="menu-form">
                        <ul>
                            <li className="ativo"><button><img src={empresaBotao} alt="Ícone que representa a empresa a ser adicionado" /> Criar Empresa</button></li>
                            <li className="ativo"><button><img src={admBotao} alt="Ícone que representa o adm a ser adicionado" /> Criar Admin</button></li>
                            <li className="ativo"><button><img src={salaBotao} alt="Ícone que representa a sala a ser adicionada" /> Criar Salas</button></li>
                        </ul>
                    </nav>
                    <div className="corpo-form">
                        <h2>Informações Sala</h2>
                        <div className="input-sala">
                            <label htmlFor="input-nm-sala">Nome da sala</label>
                            <input type="text" className="input-nm-sala" onChange={(e) => { setNmSala(e.target.value); console.log(nmSala) }} />
                        </div>

                        <div className="input-sala">
                            <label htmlFor="input-confirmar-senha-admin">Descrição da sala</label>
                            <input type="text" className="input-ds-sala" onChange={(e) => { setDsSala(e.target.value); console.log(dsSala) }} />
                        </div>
                    </div>
                    <div className="footer-form">
                        <button className="seguir" onClick={handleCriarSala}>Adicionar</button>
                    </div>
                </div>


                <div className={`listagem-salas ${fecharFormSala ? "form-invisivel" : ""}`}> {/*${fecharFormSala ? "form-invisivel" : ""}*/}
                    <h1>Salas adicionadas: </h1>
                    <div className="listagem-cards-salas">
                        {formSala.map((sala) => (
                            <div className="card-sala">
                                <div className="corpo-card-sala">
                                    <h2>{sala.nm_sala}</h2>
                                    <p>{sala.ds_sala}</p>
                                </div>
                                <div className="opcoes-sala">
                                    <button className="btn-edit" onClick={() => { setEditarSala({ ...sala }) }}><img src={editBotao} alt="Ícone editar" /></button>
                                    <button className="btn-delete" onClick={() => { handleRemoverSala(sala.id) }}><img src={deletarBotao} alt="Ícone deletar" /></button>
                                </div>
                            </div>
                        ))
                        }
                    </div>
                    <button onClick={() => handleInserirSalasBanco()}>Finalizar</button>
                </div>

                {/* Modais de erro e sucesso invisíveis aqui: */}

                <div className={`background-modal ${ativarModalSucesso ? "ativo" : ""}`}>
                    <div className="modal">
                        <img src={iconeSucesso} alt="Ícone de sucesso" />
                        <p>{infoModal}</p>
                        <button onClick={() => setAtivarModalSucesso(false)}>Ok</button>
                    </div>
                </div>


                <div className={`background-modal ${ativarModalErro ? "ativo" : ""}`}>
                    <div className="modal">
                        <img src={iconeErro} alt="Ícone de erro" />
                        <p>{infoModal}</p>
                        <button onClick={() => setAtivarModalErro(false)}>Ok</button>
                    </div>
                </div>

                {/* Modal editar sala (lógica de tudo no mesmo form tava quebrando minha cabeça */}

                {editarSala && (
                    <div className="background-modal ativo">
                        <div className="modal">
                            <h2>Edite sua sala aqui</h2>
                            <input
                                type="text"
                                placeholder="Digite o novo nome aqui"
                                value={editarSala.nm_sala}
                                onChange={(e) => {
                                    setEditarSala((ant) => ({ ...ant, nm_sala: e.target.value }));
                                    console.log(editarSala.nm_sala)
                                }
                                }
                            />
                            <input
                                type="text"
                                placeholder="Digite a nova descrição aqui"
                                value={editarSala.ds_sala}
                                onChange={(e) => {
                                    setEditarSala((ant) => ({ ...ant, ds_sala: e.target.value }))
                                    console.log(editarSala.ds_sala)
                                }
                                }
                            />
                            <button onClick={() => handleAlterarSala(editarSala.id)}>Confirmar</button>
                        </div>
                    </div>
                )}



            </main>
        </>
    );
}

export default BasicRecords;