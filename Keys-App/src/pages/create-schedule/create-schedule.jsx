import React from "react";
import './create-schedule.css'
import Menu from "../../components/full-menu.jsx"
import downloadExcel from "../../assets/download-excel-model.png"
import randomProfile from "../../assets/random-profile.png"


const Create_schedule = function Create_schedule() {

    return (
        <>
            <Menu />
            <main className="create_schedule_main">
                <section className="add-schedule-section">
                    <form action="#" className="add-schedule-form">
                        <h1>Adicionar Reserva</h1>

                        <label htmlFor="room-name" className="sr-only">Nome da sala</label>
                        <input type="text" id="room-name" name="roomName" placeholder="Nome da sala" title="Nome da sala" />

                        <label htmlFor="reservation-owner-input" className="sr-only">Quem ficará com a sala?</label>
                        <input type="text" id="reservation-owner-input" name="owner" placeholder="Quem ficará com a sala?"
                            title="Quem ficará com a sala?" list="possible-owners" />
                        <datalist id="possible-owners">
                            <option value="Ronaldo"></option>
                            <option value="João"></option>
                            <option value="Pedro"></option>
                            <option value="Pinto Parker"></option>
                        </datalist>

                        <label htmlFor="reservation-schedule" className="sr-only">Data de reserva (dia, mês, ano)</label>
                        <input type="date" id="reservation-schedule" name="date" title="Data da reserva (dia, mês, ano)" />

                        <label htmlFor="reservation-initial-time" className="sr-only">Hora e minuto do início da reserva</label>
                        <input type="time" id="reservation-initial-time" name="startTime"
                            title="Hora e minuto do início da reserva" />

                        <label htmlFor="reservation-end-time" className="sr-only">Hora e minuto do fim da reserva</label>
                        <input type="time" id="reservation-end-time" name="endTime" title="Hora e minuto do fim da reserva" />

                        <input type="submit" value="Adicionar" />
                    </form>

                    <div className="extra-shares">
                        <div className="add-more-reservations">
                            <button aria-label="Botão de adicionar múltiplas reservas">+</button>
                            <p>Adicionar múltiplas reservas</p>
                        </div>
                        <div className="download-reservation-model">
                            <button>
                                <img src={downloadExcel}
                                    alt="Imagem dentro de um botão que representa a ação de baixar o documento, em específico, um documento base do excel para adicionar múltiplos cronogramas mais facilmente" />
                            </button>
                            <p>Baixar modelo para adicionar multiplas reservas</p>
                        </div>
                    </div>
                </section>

                <section className="reservation-list-section">
                    <div className="reservation-list">
                        <div className="reservation-item">
                            <div className="owner-local">
                                <img src={randomProfile} alt="Foto do usuário"/>
                                    <div className="name-local">
                                        <p className="owner-name">Carlinhos</p>
                                        <p>Laboratório ??? </p>
                                    </div>
                            </div>
                            <div className="date-time">
                                <p>06/06/2026</p>
                                <p>06:00 - 19:00</p>
                            </div>
                        </div>

                        <div className="reservation-item">
                            <div className="owner-local">
                                <img src={randomProfile} alt="Foto do usuário"/>
                                    <div className="name-local">
                                        <p className="owner-name">Carlinhos</p>
                                        <p>Laboratório ??? </p>
                                    </div>
                            </div>
                            <div className="date-time">
                                <p>06/06/2026</p>
                                <p>06:00 - 19:00</p>
                            </div>
                        </div>

                        <div className="reservation-item">
                            <div className="owner-local">
                                <img src={randomProfile} alt="Foto do usuário"/>
                                    <div className="name-local">
                                        <p className="owner-name">Carlinhos</p>
                                        <p>Laboratório ??? </p>
                                    </div>
                            </div>
                            <div className="date-time">
                                <p>06/06/2026</p>
                                <p>06:00 - 19:00</p>
                            </div>
                        </div>
                    </div>
                    <button>Salvar</button>
                </section>
            </main>

        </>
    );
}

export default Create_schedule