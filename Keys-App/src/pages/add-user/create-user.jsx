import React from "react";
import Menu from "../../components/full-menu";
import "./create-user.css"
import baixarexcel from "../../assets/baixar-excel.png"
import randomProfile from "../../assets/random-profile.png"
import deleteIcon from "../../assets/delete.png"
import editIcon from "../../assets/edit-reservation.png"

const CreateUser = () => {

    return (
        <>
            <Menu />

            <main className="create-user-main">
                <section className="form-add-user">
                    <h1>Adicionar Usuário</h1>
                    <form action="#">
                        <input type="text" placeholder="Digite o nome do usuário" />
                        <input type="text" placeholder="Digite o código do usuário" />
                        <input type="email" placeholder="Digite o email do usuário" />
                        <input type="submit" value="Adicionar" />
                    </form>

                    <div className="botoes-acoes">
                        <div className="add-excel">
                            <button>+</button>
                            <p>Adicionar múltiplos usuários via excel</p>
                        </div>
                        <div className="baixar-excel">
                            <button> <img src={baixarexcel} alt="Ícone de download" /> </button>
                            <p>Excel para dicionar múltiplos usuários</p>
                        </div>
                    </div>
                </section>

                <section className="lista-usuarios">
                    <h1>Usuários</h1>
                    <div className="listagem-usuarios">
                        <div className="card-usuario">
                            <div className="foto-informacoes-user">
                                <img src={randomProfile} alt="Imagem de perfil do usuário" />
                                <div className="informacoes-usuario">
                                    <p className="nome-user">Nome</p>
                                    <p>email@email.com</p>
                                </div>
                            </div>

                            <div className="acoes-card-usuario">
                                <button className="deletar-user"> <img src={editIcon} alt="Ícone de edição" /> </button>
                                <button className="atualizar-user"> <img src={deleteIcon} alt="Ícone de deleção" /> </button>
                            </div>
                        </div>
                                                <div className="card-usuario">
                            <div className="foto-informacoes-user">
                                <img src={randomProfile} alt="Imagem de perfil do usuário" />
                                <div className="informacoes-usuario">
                                    <p className="nome-user">Nome</p>
                                    <p>email@email.com</p>
                                </div>
                            </div>

                            <div className="acoes-card-usuario">
                                <button className="deletar-user"> <img src={editIcon} alt="Ícone de edição" /> </button>
                                <button className="atualizar-user"> <img src={deleteIcon} alt="Ícone de deleção" /> </button>
                            </div>
                        </div>
                                                <div className="card-usuario">
                            <div className="foto-informacoes-user">
                                <img src={randomProfile} alt="Imagem de perfil do usuário" />
                                <div className="informacoes-usuario">
                                    <p className="nome-user">Nome</p>
                                    <p>email@email.com</p>
                                </div>
                            </div>

                            <div className="acoes-card-usuario">
                                <button className="deletar-user"> <img src={editIcon} alt="Ícone de edição" /> </button>
                                <button className="atualizar-user"> <img src={deleteIcon} alt="Ícone de deleção" /> </button>
                            </div>
                        </div>
                    </div>
                </section>
            </main>
        </>
    );
}

export default CreateUser;