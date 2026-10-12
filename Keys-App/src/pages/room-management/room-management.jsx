import React from "react";
import "./room-management.css"
import Menu from "../../components/full-menu";

const Room_management = function room_management() {

    return (
        <>
            <Menu />
                      <main className="room_management_main">
                <div className="room-header">
                    <h1>Suas Salas</h1>
                    <label htmlFor="find-room" className="sr-only">Buscar sala</label>
                    <input type="text" placeholder="Buscar sala" id="find-room" />
                </div>
                <div className="room-list">
                    <div className="room-item">
                        <p className="room-name">Laboratório 9</p>
                        <p className="room-desc">MUCHO TEXTO SE TRATA
                            DA DESCRIÇÃO DA SALA
                            FEITA PELA INSTITUIÇÃO
                            OMG MUCHO TEXTO</p>
                        <div className="room-status">
                            <div className="open-status">
                                <div className="open-indicator"></div>
                                <p>Liberado</p>
                            </div>

                            <div className="closed-status">
                                <div className="closed-indicator"></div>
                                <p>Bloqueado</p>
                            </div>
                        </div>
                    </div>

                    <div className="room-item">
                        <p className="room-name">Laboratório 9</p>
                        <p className="room-desc">MUCHO TEXTO SE TRATA
                            DA DESCRIÇÃO DA SALA
                            FEITA PELA INSTITUIÇÃO
                            OMG MUCHO TEXTO</p>
                        <div className="room-status">
                            <div className="open-status">
                                <div className="open-indicator"></div>
                                <p>Liberado</p>
                            </div>

                            <div className="closed-status">
                                <div className="closed-indicator"></div>
                                <p>Bloqueado</p>
                            </div>
                        </div>
                    </div>

                    <div className="room-item">
                        <p className="room-name">Laboratório 9</p>
                        <p className="room-desc">MUCHO TEXTO SE TRATA
                            DA DESCRIÇÃO DA SALA
                            FEITA PELA INSTITUIÇÃO
                            OMG MUCHO TEXTO</p>
                        <div className="room-status">
                            <div className="open-status">
                                <div className="open-indicator"></div>
                                <p>Liberado</p>
                            </div>

                            <div className="closed-status">
                                <div className="closed-indicator"></div>
                                <p>Bloqueado</p>
                            </div>
                        </div>
                    </div>
                </div>
            </main>
        </>
    );
}

export default Room_management