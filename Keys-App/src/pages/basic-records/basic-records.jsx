import React from "react";
import "./basic-records.css"
import admBotao from "../../assets/logo-adm-botao.png"
import salaBotao from "../../assets/logo-sala-botao.svg"
import empresaBotao from "../../assets/logo-empresa-botao.png"


const BasicRecords = () => {

    return (
        <>
            <header className="basic-records-menu">
                <p>keYs</p>
            </header>
            <main className="basic-records-main">

                <div className="form-empresa" style={{ display: "none" }}>
                    <h1>Área de Registro</h1>
                    <nav className="menu-form">
                        <ul>
                            <li><button><img src={empresaBotao} alt="Ícone que representa a empresa a ser adicionado" /> Criar Empresa</button></li>
                            <li><button><img src={admBotao} alt="Ícone que representa o adm a ser adicionado" /> Criar Admin</button></li>
                            <li><button><img src={salaBotao} alt="Ícone que representa a sala a ser adicionada" /> Criar Salas</button></li>
                        </ul>
                    </nav>

                    <div className="corpo-form">
                        <h2>Informações Empresa</h2>
                        <div className="input-empresa">
                            <label htmlFor="input-cnpj-empresa">Cnpj da empresa</label>
                            <input type="text" className="input-cnpj-empresa" />
                        </div>

                        <div className="input-empresa">
                            <label htmlFor="input-nm-empresa">Nome da empresa</label>
                            <input type="text" className="input-nm-empresa" />
                        </div>
                    </div>

                    <div className="footer-form">
                        <button className="cancelar">Cancelar</button>
                        <button className="seguir">Próximo</button>
                    </div>
                </div>

                <div className="form-admin" style={{ display: "none" }}>
                    <h1>Área de Registro</h1>
                    <nav className="menu-form">
                        <ul>
                            <li><button><img src={empresaBotao} alt="Ícone que representa a empresa a ser adicionado" /> Criar Empresa</button></li>
                            <li><button><img src={admBotao} alt="Ícone que representa o adm a ser adicionado" /> Criar Admin</button></li>
                            <li><button><img src={salaBotao} alt="Ícone que representa a sala a ser adicionada" /> Criar Salas</button></li>
                        </ul>
                    </nav>
                    <div className="corpo-form">
                        <h2>Informações Admin</h2>
                        <div className="listagem-inputs-corpo">
                            <div className="input-admin">
                                <label htmlFor="input-cd-admin">Código do admin</label>
                                <input type="text" className="input-cd-admin" />
                            </div>
                            <div className="input-admin">
                                <label htmlFor="input-nm-admin">Nome do admin</label>
                                <input type="text" className="input-nm-admin" />
                            </div>
                            <div className="input-admin">
                                <label htmlFor="input-email-admin">Email do admin</label>
                                <input type="text" className="input-email-admin" />
                            </div>
                            <div className="input-admin">
                                <label htmlFor="input-senha-admin">Senha do admin</label>
                                <input type="text" className="input-senha-admin" />
                            </div>
                            <div className="input-admin">
                                <label htmlFor="input-confirmar-senha-admin">Confirmar senha do admin</label>
                                <input type="text" className="input-confirmar-senha-admin" />
                            </div>
                        </div>
                    </div>

                    <div className="footer-form">
                        <button className="cancelar">Cancelar</button>
                        <button className="seguir">Próximo</button>
                    </div>
                </div>

                <div className="form-salas">
                    <h1>Área de Registro</h1>
                    <nav className="menu-form">
                        <ul>
                            <li><button><img src={empresaBotao} alt="Ícone que representa a empresa a ser adicionado" /> Criar Empresa</button></li>
                            <li><button><img src={admBotao} alt="Ícone que representa o adm a ser adicionado" /> Criar Admin</button></li>
                            <li><button><img src={salaBotao} alt="Ícone que representa a sala a ser adicionada" /> Criar Salas</button></li>
                        </ul>
                    </nav>
                    <div className="corpo-form">
                        <h2>Informações Sala</h2>
                        <div className="input-sala">
                            <label htmlFor="input-nm-sala">Nome da sala</label>
                            <input type="text" className="input-nm-sala" />
                        </div>

                        <div className="input-sala">
                            <label htmlFor="input-confirmar-senha-admin">Descrição da sala</label>
                            <input type="text" className="input-ds-sala" />
                        </div>
                    </div>
                    <div className="footer-form">
                        <button className="cancelar">Cancelar</button>
                        <button className="seguir">Adicionar</button>
                    </div>
                </div>

                <div className="listagem-salas">
                    <h1>Salas adicionadas: </h1>
                    <div className="listagem-cards-salas">
                        <div className="card-sala">
                            <h2>Sala 35</h2>
                            <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit.
                                Fusce id lorem neque.</p>
                        </div>

                        <div className="card-sala">
                            <h2>Sala 35</h2>
                            <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit.
                                Fusce id lorem neque.</p>
                        </div>

                        <div className="card-sala">
                            <h2>Sala 35</h2>
                            <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit.
                                Fusce id lorem neque.</p>
                        </div>


                        <div className="card-sala">
                            <h2>Sala 35</h2>
                            <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit.
                                Fusce id lorem neque.</p>
                        </div>


                        <div className="card-sala">
                            <h2>Sala 35</h2>
                            <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit.
                                Fusce id lorem neque.</p>
                        </div>
                    </div>

                    <button>Finalizar</button>
                </div>
            </main>
        </>
    );
}

export default BasicRecords;