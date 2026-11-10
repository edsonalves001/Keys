import "./index.css";
import "./cal.css";
import Menu from "../../../components/full-menu";
import filtro from "../../../assets/icons/Filter.png"
import calendarioImg from "../../../assets/icons/Calendar.png"
import editarImg from "../../../assets/icons/Edit.png"
import editReserva from "../../../assets/icons/Edit-white.png"
import removeReserva from "../../../assets/icons/delete.png"
import imagemDefault from "../../../assets/icons/image 9.png"
import { useEffect, useState } from "react";
import api from "../../../service/api";


const Schedule = function Schedule() {
    const [modalAberto, setModalAberto] = useState(false);
    const [salaSelecionada, setSalaSelecionada] = useState(null);
    const [salas, setSalas] = useState([]);
    const [reservas, setReservas] = useState([]);
    const [exibirFiltro, setExibirFiltro] = useState(false);
    const dataHoje = new Date().toLocaleDateString("en-CA");
    useEffect(() => {

        api.get("/salas")
            .then(response => {
                setSalas(response.data);
            })
            .catch(error => {
                console.error("Erro ao buscar reservas:", error);
            });

        api.get("/reservasDaEmpresa").then(response => {
            setReservas(response.data);
        }).catch(error => {
            console.error("Erro ao buscar reservas:", error);
        });

    }, []);

    function atualizarSala(status, idSala, nome, descricao) {

        api.patch("/salas", null, {
            params: {
                salaId: idSala,
                nome: nome,
                descricao: descricao
            }
        })
            .then(response => {
                console.log("Sala atualizada!", response.data);
                alert("Sala alterada com sucesso")
                setModalAberto(false)
                window.location.reload()
            })
            .catch(error => {
                console.error("Erro ao editar sala:", error);
            });

        api.patch("/sala/estado", null, {
            params: {
                statusSalaEnum: status,
                salaId: idSala,
            }
        })
            .then(response => {
                console.log("Disponibilidade atualizada!", response.data);
                alert("Disponibilidade alterada com sucesso")
                setModalAberto(false)
                window.location.reload()
            })
            .catch(error => {
                console.error("Erro ao editar sala:", error);
            });

    }
   
    function Reservar(cd) {
        if (cd.statusSala === "INDISPONIVEL") {
            alert("Não é possível reservar essa sala")
        } else if (cd.statusSala === "DISPONIVEL") {
            window.location.href = "/"
        }
    }


    function abrirModal(sala) {
        setSalaSelecionada(sala);
        setModalAberto(true);
    }
    function alternarFiltro() {
        setExibirFiltro(!exibirFiltro);

    }
    const [filtrada, setFiltrada] = useState("Todas");
    function filtroSala(status) {
        setFiltrada(status);
    }



    return (
        <>
            <Menu /><br></br><br></br><br></br><br></br><br></br>
            {modalAberto && salaSelecionada && (
                <div className="modal-fundo">
                    <div className="modal-conteudo">

                        <h3>Editar sala</h3>


                        <br></br> <h4>Nome atual: {salaSelecionada.nome}</h4><br></br>
                        <h4>Novo nome: </h4>
                        <input id="novoNome" type="text" placeholder="Insira aqui o novo nome da sala" /><br></br><br></br>
                        <h4>Descrição atual: {salaSelecionada.descricao} </h4>

                        <br></br><h4>Nova descrição: </h4>
                        <input id="novaDescricao" type="text" placeholder="Insira aqui a nova descrição da sala" /><br></br>

                        <br></br><h4>Status atual: {salaSelecionada.statusSala}</h4>
                        <select id="novoFiltro">
                            <option value="DISPONIVEL">Disponivel</option>
                            <option value="INDISPONIVEL">Indisponivel</option>
                        </select><br></br>

                        <br></br><button onClick={() => setModalAberto(false)} id="fechar">Fechar</button>
                        <button id="salvar" onClick={() => atualizarSala(document.getElementById("novoFiltro").value, salaSelecionada.cd, document.getElementById("novoNome").value,
                            document.getElementById("novaDescricao").value)}>Salvar</button>
                    </div>
                </div>
            )}
            <main>
                <section className="side-a">
                    <div className="cal06">
                        <div className="cal06__main">

                            <div className="cal06__top">
                                <div className="cal06__month-block">
                                    <div className="cal06__month-label">{dataHoje}</div>
                                    <div className="cal06__year-label">2026</div>
                                </div>

                                <div className="cal06__top-right">
                                    <div className="cal06__nav-row">
                                        <div className="cal06__nav-btn">‹</div>
                                        <div className="cal06__nav-btn">›</div>
                                    </div>
                                </div>
                            </div>

                            <div className="cal06__dow">
                                <div className="cal06__dow-lbl">D</div>
                                <div className="cal06__dow-lbl">S</div>
                                <div className="cal06__dow-lbl">T</div>
                                <div className="cal06__dow-lbl">Q</div>
                                <div className="cal06__dow-lbl">Q</div>
                                <div className="cal06__dow-lbl">S</div>
                                <div className="cal06__dow-lbl">S</div>
                            </div>

                            <div className="cal06__grid" id="cal06Grid">

                                {/* May overflow */}
                                <div className="cal06__cell other weekend"><div className="cal06__cell-n">25</div></div>
                                <div className="cal06__cell other"><div className="cal06__cell-n">26</div></div>
                                <div className="cal06__cell other"><div className="cal06__cell-n">27</div></div>
                                <div className="cal06__cell other"><div className="cal06__cell-n">28</div></div>
                                <div className="cal06__cell other"><div className="cal06__cell-n">29</div></div>
                                <div className="cal06__cell other"><div className="cal06__cell-n">30</div></div>
                                <div className="cal06__cell other weekend"><div className="cal06__cell-n">31</div></div>

                                {/* June 1-7 */}
                                <div className="cal06__cell weekend"><div className="cal06__cell-n">1</div></div>
                                <div className="cal06__cell"><div className="cal06__cell-n">2</div></div>
                                <div className="cal06__cell"><div className="cal06__cell-n">3</div></div>
                                <div className="cal06__cell"><div className="cal06__cell-n">4</div><div className="cal06__pips"><span className="cal06__pip cal06__pip--y"></span></div></div>
                                <div className="cal06__cell"><div className="cal06__cell-n">5</div></div>
                                <div className="cal06__cell"><div className="cal06__cell-n">6</div><div className="cal06__pips"><span className="cal06__pip cal06__pip--p"></span></div></div>
                                <div className="cal06__cell weekend"><div className="cal06__cell-n">7</div></div>

                                {/* June 8-14 */}
                                <div className="cal06__cell today weekend"><div className="cal06__cell-n">8</div><div className="cal06__pips"><span className="cal06__pip cal06__pip--g"></span></div></div>
                                <div className="cal06__cell"><div className="cal06__cell-n">9</div><div className="cal06__pips"><span className="cal06__pip cal06__pip--g"></span><span className="cal06__pip cal06__pip--l"></span></div></div>
                                <div className="cal06__cell"><div className="cal06__cell-n">10</div></div>
                                <div className="cal06__cell"><div className="cal06__cell-n">11</div><div className="cal06__pips"><span className="cal06__pip cal06__pip--p"></span></div></div>
                                <div className="cal06__cell"><div className="cal06__cell-n">12</div></div>
                                <div className="cal06__cell"><div className="cal06__cell-n">13</div><div className="cal06__pips"><span className="cal06__pip cal06__pip--y"></span></div></div>
                                <div className="cal06__cell weekend"><div className="cal06__cell-n">14</div></div>

                                {/* June 15-21 */}
                                <div className="cal06__cell weekend"><div className="cal06__cell-n">15</div></div>
                                <div className="cal06__cell"><div className="cal06__cell-n">16</div></div>
                                <div className="cal06__cell"><div className="cal06__cell-n">17</div><div className="cal06__pips"><span className="cal06__pip cal06__pip--g"></span><span className="cal06__pip cal06__pip--g"></span></div></div>
                                <div className="cal06__cell"><div className="cal06__cell-n">18</div></div>
                                <div className="cal06__cell"><div className="cal06__cell-n">19</div></div>
                                <div className="cal06__cell"><div className="cal06__cell-n">20</div><div className="cal06__pips"><span className="cal06__pip cal06__pip--l"></span></div></div>
                                <div className="cal06__cell weekend"><div className="cal06__cell-n">21</div><div className="cal06__pips"><span className="cal06__pip cal06__pip--y"></span></div></div>

                                {/* June 22-28 */}
                                <div className="cal06__cell weekend"><div className="cal06__cell-n">22</div></div>
                                <div className="cal06__cell"><div className="cal06__cell-n">23</div></div>
                                <div className="cal06__cell"><div className="cal06__cell-n">24</div><div className="cal06__pips"><span className="cal06__pip cal06__pip--p"></span></div></div>
                                <div className="cal06__cell"><div className="cal06__cell-n">25</div></div>
                                <div className="cal06__cell"><div className="cal06__cell-n">26</div></div>
                                <div className="cal06__cell"><div className="cal06__cell-n">27</div><div className="cal06__pips"><span className="cal06__pip cal06__pip--g"></span></div></div>
                                <div className="cal06__cell weekend"><div className="cal06__cell-n">28</div></div>

                                {/* June 29-30 + July */}
                                <div className="cal06__cell weekend"><div className="cal06__cell-n">29</div></div>
                                <div className="cal06__cell"><div className="cal06__cell-n">30</div></div>
                                <div className="cal06__cell other"><div className="cal06__cell-n">1</div></div>
                                <div className="cal06__cell other"><div className="cal06__cell-n">2</div></div>
                                <div className="cal06__cell other"><div className="cal06__cell-n">3</div></div>
                                <div className="cal06__cell other"><div className="cal06__cell-n">4</div></div>
                                <div className="cal06__cell other weekend"><div className="cal06__cell-n">5</div></div>

                            </div>
                        </div>
                    </div>

                    <div className="salas-block">
                        <div className="salas-top">
                            <h1>Salas</h1>

                            <div className="filtro-container">
                                <button onClick={alternarFiltro}>
                                    <img src={filtro} alt="Filtro de seleção de salas" />
                                </button>

                                {exibirFiltro && (
                                    <select
                                        id="filtro"
                                        onChange={(e) => filtroSala(e.target.value)}
                                    >
                                        <option value="Todas">Todas as salas</option>
                                        <option value="DISPONIVEL">Disponivel</option>
                                        <option value="INDISPONIVEL">Indisponivel</option>
                                    </select>
                                )}
                            </div>
                        </div>

                        <div className="scroll-box">

                            {salas.filter(sala =>
                                filtrada === "Todas" ||
                                (filtrada === "DISPONIVEL" && sala.statusSala === "DISPONIVEL") ||
                                (filtrada === "INDISPONIVEL" && sala.statusSala !== "DISPONIVEL")
                            ).map(sala => (
                                <div className="sala-item">
                                    <h1 key={sala.cd}>{sala.nome}</h1>
                                    <b><h2 className={sala.statusSala === "DISPONIVEL" ? "disponivel" : "indisponivel"}>
                                        {sala.statusSala}
                                    </h2></b>

                                    <button onClick={() => Reservar(sala)}><img src={calendarioImg} alt="Botão de reservar sala disponivel" className="button" /></button>
                                    <button onClick={() => abrirModal(sala)}>
                                        <img src={editarImg} alt="Botão de editar reserva" />
                                    </button>
                                </div>
                            ))}
                        </div>
                    </div>
                </section>

                <section className="side-b">
                    <h1 id="title">Reservas</h1>

                    <div className="list">
                        {reservas.filter(reserva =>
                            dataHoje === reserva.infosReserva.split("T")[0]
                        ).map((reserva) => (
                            <div key={reserva.cdReserva}>
                                <div className="reserva">

                                    <div className="right_reserva">
                                        <img src={imagemDefault} alt="Imagem do usuario" />

                                        <div className="user">
                                            <h1>{reserva.NomeUser}</h1>
                                            <h2>{reserva.NomeSala}</h2>
                                        </div>
                                    </div>

                                    <div className="left_reserva">
                                        <h2>
                                            {reserva.inicioReserva} - {reserva.fimReserva}
                                        </h2>
                                        <button id="edit" className="button">
                                            Editar
                                            <img src={editReserva} alt="Botão para excluir reserva" />
                                        </button>

                                        <button id="delete" className="button">
                                            Excluir
                                            <img src={removeReserva} alt="Botão para excluir reserva" />
                                        </button>
                                    </div>

                                </div>
                                <hr />
                            </div>
                        ))}

                    </div>

                    <br />

                    <button className="add button">+ Adicionar reserva</button>
                </section>
            </main>
        </>
    );
}

export default Schedule;