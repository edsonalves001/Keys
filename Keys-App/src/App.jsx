import {BrowserRouter, Routes, Route} from 'react-router'
import Create_schedule from './pages/create-schedule/create-schedule';
import Reservation_edit from './pages/reservation-edit/reservation-edit';
import Room_management from './pages/room-management/room-management';
import User_dashboard from './pages/user-dashboard/user-dashboard';
import Landing from './pages/landing/landing';

function App() {

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/criarCronograma" element={<Create_schedule/>}/>
        <Route path="/editarReserva" element={<Reservation_edit/>}/>
        <Route path="/gerenciarSalas" element={<Room_management/>}/>
        <Route path="/dashboardUsuario" element={<User_dashboard/>}/>
        <Route path="/" element={<Landing/>}/>
      </Routes>
    </BrowserRouter>
  );
}

export default App
