import "./user-dashboard.css"
import random_profile from "../../assets/random-profile.png"
import edit_name from "../../assets/edit-name.svg"
import delete_img from "../../assets/delete.png"
import reservation_edit from "../../assets/edit-reservation.png"
import Menu from "../../components/full-menu";
import { useEffect, useState } from "react"
import iconeSucesso from "../../assets/icone-sucesso.png"
import iconeErro from "../../assets/icone-erro.png"
import { api } from "../../api/api.js";
import { useNavigate } from "react-router"

const User_dashboard = function user_dashboard() {

    //Navigate:

    const navigate = useNavigate()

    const handleAddUserclick = () => {
        navigate("/addUser")
    }

    //Abrir modais:
    const [abrirModalSucesso, setAbrirModalSucesso] = useState(false)
    const [abrirModalErro, setAbrirModalErro] = useState(false)
    const [abrirModalDeletar, setAbrirModalDeletar] = useState(false)
    const [abrirModalEditar, setAbrirModalEditar] = useState(false)

    //Campos users a serem atualizados:
    const [nomeUser, setNomeUser] = useState("")
    const [emailUser, setEmailUser] = useState("")

    //Info modal:
    const [infoModal, setInfoModal] = useState(null)

    //Users:
    const [usersBanco, setUsersBanco] = useState([])
    const [usuarioExibido, setUsuarioExibido] = useState(null) // o que vai aparecer no card
    const [usuarioLista, setUsuarioLista] = useState([]) //exibe os usuários na lista baseando-se num filter do input

    //Buscando usuários ao iniciar a página:

    async function buscarUsers() {
        //Buscar token para enviar na req:
        const token = localStorage.getItem("token")
        console.log(token)
        try {
            const resp = await api.get('/api/usuarios', {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            })
            if (resp.status == 200) {
                setUsersBanco(resp.data)
                setUsuarioLista(resp.data)
                setUsuarioExibido(resp.data[0] ?? null) //pega o primeiro pra exibir
                setNomeUser(resp.data.nome ?? null)
                setEmailUser(resp.email_user ?? null)
                console.log(resp.data)
            }
        } catch (e) {
            console.log(e.message)
        }
    }

    //Buscar users banco:

    useEffect(() => {
        buscarUsers()
    }, [])

    //Users a ser exibidos na lista
    const handleExibirLista = (e) => {
        setUsuarioLista(usersBanco.filter((user) => user.nome.includes(e.target.value)))
        console.log(e.target.value)
    }

    //Capturar usuário selecionado e exibir perfil :
    const handleSelectUserClick = (e) => {
        const usuarioExibido = usersBanco.filter((user) => user.id == e.target.getAttribute('data-id'))
        setUsuarioExibido(usuarioExibido[0]) //preciso passar de lá pra cá pq se não retorna array
        setNomeUser(usuarioExibido.data.nome)
        setEmailUser(usuarioExibido.email_user)

        console.log("Atributo: " + e.target.getAttribute('data-id'))
    }
    const handleDeletarUser = async function () {
        const token = localStorage.getItem("token")
        try {
            console.log(usuarioExibido.id) //só pra confirmar
            const resp = await api.delete(`/api/usuarios/${usuarioExibido.id}`, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            })
            console.log(resp)

            if (resp.status == 204) {
                setAbrirModalDeletar(false)
                setInfoModal("Usuário deletado com sucesso!")
                setAbrirModalSucesso(true)

                //Atualizando users:
                console.log("Usuário a ser excluído: ", usuarioExibido.id)

                setUsersBanco((ant) => ant.filter((user) => user.id !== usuarioExibido.id));
                setUsuarioLista((ant) => ant.filter((u) => u.id !== usuarioExibido.id));
            }
        } catch (e) {
            console.log(e.message)
        }
    }

    console.log("Usuários banco: ", usersBanco)
    console.log("Usuário a ser exibido: ", usuarioExibido)
    console.log("Usuários da listagem: ", usuarioLista)

    const handleAtualizarUser = async function () {
        const token = localStorage.getItem("token")

        console.log(nomeUser)
        console.log(emailUser)
        if (nomeUser.trim().length == 0 || emailUser.trim().length == 0) {
            setAbrirModalEditar(false)
            setInfoModal("Envie dados válidos!")
            setAbrirModalErro(true)
            return
        }

        if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(emailUser)) {
            setAbrirModalEditar(false)
            setInfoModal("Formato de email inválido!");
            setAbrirModalErro(true);
            return;
        }

        try {
            const resp = await api.patch(`/api/usuarios/${usuarioExibido.id}`,
                {
                    "nm_usuario": nomeUser,
                    "nm_email_usuario": emailUser
                }, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            }
            )

            if (resp.status == 200) {
                setAbrirModalEditar(false)
                setInfoModal("Usuário atualizado com sucesso!")
                setAbrirModalSucesso(true)
                buscarUsers()
                return
            }
        } catch (e) {
            setAbrirModalEditar(false)
            console.log(e.message)
            return
        }
    }
    function abrirEdicao() {
        if (!usuarioExibido) return;
        setNomeUser(usuarioExibido.nome ?? "");
        setEmailUser(usuarioExibido.email_user ?? "");
        setAbrirModalEditar(true);
    }
    return (
        <>
            <Menu />
            <main className="user-dashboard-main">
                <section className="users-list-section">
                    <h1>Usuários</h1>

                    <input type="text" placeholder="Pesquisar..." onChange={(e) => handleExibirLista(e)} />
                    <button onClick={handleAddUserclick}>Adicionar usuário</button>

                    <ul className="listagem-usuarios">
                        {usersBanco.length == 0 && <p>Sem usuários adicionados ainda!</p>}
                        {usuarioLista.map((usuario) => {
                            return <li data-id={usuario.id} onClick={(e) => handleSelectUserClick(e)}>{usuario.nome}</li>
                        })}
                    </ul>
                </section>

                <section className="user-card-section">

                    <div className="user-card">
                        <div className="card-user-header">
                            <div className="user-info">
                                <div className="user-info-profile">
                                    <img src={`http://localhost:8080${usuarioExibido ? usuarioExibido.nm_link_imagem_usuario : ""}`} alt="Imagem do usuário" />
                                    <div className="user-info-details">
                                        <div className="edit-name">
                                            <p>{usuarioExibido ? usuarioExibido.nome : "Sem nome"}</p>
                                            <button onClick={abrirEdicao}><img src={edit_name} alt="Botão de editar nome" /></button>
                                        </div>
                                        <p><span>Email:</span> {usuarioExibido ? usuarioExibido.email_user : "Sem email"}</p>
                                    </div>
                                </div>

                                <button onClick={(() => {
                                    setInfoModal("Você tem certeza que quer deletar este perfil?")
                                    setAbrirModalDeletar(true)
                                })}><img src={delete_img} alt="Botão de deletar" /></button>
                            </div>
                        </div>
                        <div className="user-card-body">
                            <h2>Reservas</h2>
                            <table>
                                <thead>
                                    <tr>
                                        <th>Local</th>
                                        <th>Data</th>
                                        <th>Horário</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td>
                                            Laboratório 9
                                        </td>
                                        <td>
                                            01/10/2026
                                        </td>
                                        <td>
                                            18:00-19:30
                                            <button className="edit-reservation-button"><img src={reservation_edit}
                                                alt="Botão de editar reserva" /></button>
                                            <button className="delete-reservation-button"><img src={delete_img}
                                                alt="Botão de deletar reserva" /></button>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>
                                            Laboratório 9
                                        </td>
                                        <td>
                                            01/10/2026
                                        </td>
                                        <td>
                                            18:00-19:30
                                            <button className="edit-reservation-button"><img src={reservation_edit}
                                                alt="Botão de editar reserva" /></button>
                                            <button className="delete-reservation-button"><img src={reservation_edit}
                                                alt="Botão de deletar reserva" /></button>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>
                                            Laboratório 9
                                        </td>
                                        <td>
                                            01/10/2026
                                        </td>
                                        <td>
                                            18:00-19:30
                                            <button className="edit-reservation-button"><img src={reservation_edit}
                                                alt="Botão de editar reserva" /></button>
                                            <button className="delete-reservation-button"><img src={delete_img}
                                                alt="Botão de deletar reserva" /></button>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>
                                            Laboratório 9
                                        </td>
                                        <td>
                                            01/10/2026
                                        </td>
                                        <td>
                                            18:00-19:30
                                            <button className="edit-reservation-button"><img src={reservation_edit}
                                                alt="Botão de editar reserva" /></button>
                                            <button className="delete-reservation-button"><img src={delete_img}
                                                alt="Botão de deletar reserva" /></button>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </section>

                {/* Modais: */}

                {/*Modal erro: */}
                <div className={`background-modal ${abrirModalErro ? "ativo" : ""}`}>
                    <div className="modal">
                        <img src={iconeErro} alt="Ícone de erro" />
                        <h2>Algo deu errado!</h2>
                        <p>{infoModal}</p>
                        <button onClick={()=> setAbrirModalErro(false)}>Ok</button>
                    </div>
                </div>

                {/* Modal editar user: */}
                <div className={`background-modal ${abrirModalEditar ? "ativo" : ""}`}>
                    <div className="modal">
                        <h2>Edite o Usuário:</h2>
                        <input
                            type="text"
                            placeholder="Digite o novo nome aqui"
                            value={nomeUser}
                            onChange={(e) => setNomeUser(e.target.value)}
                        />
                        <input
                            type="email"
                            placeholder="Digite o novo email aqui"
                            value={emailUser}
                            onChange={(e) => setEmailUser(e.target.value)}
                        />
                        <div className="botoes-acoes">
                            <button onClick={handleAtualizarUser}>Atualizar</button>
                            <button className="deletar" onClick={() => setAbrirModalEditar(false)}>Cancelar</button>
                        </div>
                    </div>
                </div>

                {/* Modal deletar: */}
                <div className={`background-modal ${abrirModalDeletar ? "ativo" : ""}`}>
                    <div className="modal">
                        <img src={iconeErro} alt="Ícone de erro" />
                        <p>{infoModal}</p>
                        <div className="botoes-acoes">
                            <button className="deletar" onClick={handleDeletarUser}>Sim</button>
                            <button className="manter" onClick={() => setAbrirModalDeletar(false)}>Não</button>
                        </div>
                    </div>
                </div>

                {/* Modal sucesso: */}
                <div className={`background-modal ${abrirModalSucesso ? "ativo" : ""}`}>
                    <div className="modal">
                        <img src={iconeSucesso} alt="Ícone sucesso" />
                        <p>{infoModal}</p>
                        <button onClick={() => setAbrirModalSucesso(false)}>Ok</button>
                    </div>
                </div>
            </main>
        </>
    )
}

export default User_dashboard