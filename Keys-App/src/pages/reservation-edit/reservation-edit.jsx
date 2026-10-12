import React from "react";
import "./reservation-edit.css"
import randomProfile from "../../assets/random-profile.png"
import Menu from "../../components/full-menu";

const Reservation_edit = function reservation_edit() {

    return (
        <>
            <Menu />
            <main className="reservartion_edit_main">
                <div className="reservation-info-header">
                    <h1>Sala 01</h1>
                    <div className="reservation-owner-profile">
                        <img src={randomProfile} alt="Imagem de perfil do usuário" />
                        <p>Reservado por Ronaldo</p>
                    </div>
                </div>

                <form className="edit-shedule-card" method="#" action="#">
                    <p>Editar horário</p>
                    <input type="time" title="Altere seu horário aqui"/>
                    <div className="action-buttons">
                        <button id="salvar">salvar</button>
                        <button id="cancelar">cancelar</button>
                    </div>
                </form>
            </main>
        </>
    )
}

export default Reservation_edit