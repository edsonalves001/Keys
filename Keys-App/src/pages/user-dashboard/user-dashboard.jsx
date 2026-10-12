import "./user-dashboard.css"
import random_profile from "../../assets/random-profile.png"
import edit_name from "../../assets/edit-name.svg"
import delete_img from "../../assets/delete.png"
import reservation_edit from "../../assets/edit-reservation.png"
import Menu from "../../components/full-menu";

const User_dashboard = function user_dashboard() {
    return (
        <>
            <Menu />
            <main className="user-dashboard-main">
                <section className="users-list-section">
                    <h1>Usuários</h1>
                    <div className="add-find-users">
                        <button>Adicionar usuário</button>
                        <input type="text" placeholder="Pesquisar..." />
                    </div>

                    <table>
                        <thead>
                            <tr>
                                <th>NOME</th>
                                <th>EMAIL</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>Ronaldo</td>
                                <td>Ronaldo@gmail.com</td>
                            </tr>
                            <tr>
                                <td>Ronaldo</td>
                                <td>Ronaldo@gmail.com</td>
                            </tr>
                            <tr>
                                <td>Ronaldo</td>
                                <td>Ronaldo@gmail.com</td>
                            </tr>
                            <tr>
                                <td>Ronaldo</td>
                                <td>Ronaldo@gmail.com</td>
                            </tr>
                        </tbody>
                    </table>
                </section>

                <section className="user-card-section">
                    <div className="user-card">
                        <div className="card-user-header">
                            <div className="user-info">
                                <div className="user-info-profile">
                                    <img src={random_profile} alt="Imagem do usuário" />
                                    <div className="user-info-details">
                                        <div className="edit-name">
                                            <p>Ronaldo Pereiras</p>
                                            <button><img src={edit_name} alt="Botão de editar nome" /></button>
                                        </div>
                                        <p><span>Email:</span> ronaldopereiras@gmail.com</p>
                                    </div>
                                </div>

                                <button><img src={delete_img} alt="Botão de deletar" /></button>
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
            </main>
        </>
    )
}

export default User_dashboard