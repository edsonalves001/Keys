import {BrowserRouter, Routes, Route} from 'react-router'
import Create_schedule from './pages/create-schedule/create-schedule';
import Reservation_edit from './pages/reservation-edit/reservation-edit';
import User_dashboard from './pages/user-dashboard/user-dashboard';
import Landing from './pages/landing/landing';
import Change_Password from './pages/change-password/change-password';
import Password_Recovery from './pages/password-recovery/password-recovery';
import Administration_Login from './pages/administration-login/administration-login';
import CreateUser from './pages/add-user/create-user';
import BasicRecords from './pages/basic-records/basic-records';
import Schedule from './pages/schedule/Schedule/schedule';

function App() {

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/criarCronograma" element={<Create_schedule/>}/>
        <Route path="/editarReserva" element={<Reservation_edit/>}/>
        <Route path="/dashboardUsuario" element={<User_dashboard/>}/>
        <Route path="/alterarSenha" element={<Change_Password/>}/>
        <Route path= "/recuperacaodeSenha" element={<Password_Recovery/>}/>
        <Route path='/login' element={<Administration_Login/>}/>
        <Route path="/" element={<Landing/>}/>
        <Route path="/addUser" element={<CreateUser/>} />
        <Route path="/addDadosIniciais" element={<BasicRecords />}/>
        <Route path="/cronograma" element={<Schedule/>}/>
      </Routes>
    </BrowserRouter>
  );
}

export default App
