package br.com.keys.keys_api.service;

import br.com.keys.keys_api.enums.StatusReservaEnum;
import br.com.keys.keys_api.exception.ErrosEfetivarReservaException;
import br.com.keys.keys_api.model.dto.*;
import br.com.keys.keys_api.model.entity.ReservaEntity;
import br.com.keys.keys_api.model.entity.SalaEntity;
import br.com.keys.keys_api.model.entity.UsuarioEntity;
import br.com.keys.keys_api.repository.ReservaRepository;
import br.com.keys.keys_api.repository.SalaRepository;
import br.com.keys.keys_api.repository.UsuarioRepository;
import br.com.keys.keys_api.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReservaService {
    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final SalaRepository salaRepository;
    private final PasswordEncoder passwordEncoder;

    private final ObjectProvider<MqttService> mqttServiceProvider; //apenas entrega o objeto se existir. Se não = null


    public AddReservationResponseDTO criarReserva(
            AddReservationRequestDTO addReservationRequestDTO) {

        if (addReservationRequestDTO.proprietario() == null) {
            throw new RuntimeException("O proprietário da reserva não foi informado.");
        }

        if (addReservationRequestDTO.salaEntity() == null) {
            throw new RuntimeException("A sala da reserva não foi informada.");
        }

        ReservaEntity novaReservaEntity = new ReservaEntity();

        // Verifica se o proprietário existe
        UsuarioEntity proprietario = usuarioRepository.findById(
                addReservationRequestDTO.proprietario().getId()
        ).orElseThrow(() ->
                new RuntimeException("Usuário não encontrado.")
        );

        // Verifica se a sala existe
        SalaEntity salaEntity = salaRepository.findById(
                addReservationRequestDTO.salaEntity().getCd()
        ).orElseThrow(() ->
                new RuntimeException("Sala não encontrada.")
        );

        LocalDate dataReserva = addReservationRequestDTO.dataReserva();

        LocalTime horaInicioReserva = addReservationRequestDTO.horaInicioReserva();
        LocalTime horaFimReserva = addReservationRequestDTO.horaFimReserva();

        // Validação dos horários
        if (!horaFimReserva.isAfter(horaInicioReserva)) {
            throw new RuntimeException(
                    "O horário de término deve ser posterior ao horário de início."
            );
        }

        LocalDateTime inicio = dataReserva.atTime(horaInicioReserva);
        LocalDateTime fim = dataReserva.atTime(horaFimReserva);

        novaReservaEntity.setInicioReserva(inicio);
        novaReservaEntity.setFimReserva(fim);
        novaReservaEntity.setProprietario(proprietario);
        novaReservaEntity.setSalaEntity(salaEntity);
        novaReservaEntity.setstatusReserva(StatusReservaEnum.ESPERANDO_EFETIVACAO);

        reservaRepository.save(novaReservaEntity);

        return new AddReservationResponseDTO(
                novaReservaEntity.getInicioReserva(),
                novaReservaEntity.getFimReserva(),
                novaReservaEntity.getProprietario(),
                novaReservaEntity.getSalaEntity()
        );
    }


    List<AddReservationForListRequestDTO> reservasDaLista = new ArrayList<>();

    public void adicionarReservaaLista(AddReservationForListRequestDTO reserva){
        reservasDaLista.add(reserva);
    }
    public List<AddReservationForListRequestDTO> verListaReservas(){
        return reservasDaLista;
    }
    public void adicionarListadeReservas() {

        if (reservasDaLista.isEmpty()) {
            return;
        }

        for (AddReservationForListRequestDTO dto : reservasDaLista) {

            UsuarioEntity proprietario = usuarioRepository
                    .findBycdUsuario(dto.proprietario().cdUsuario());

            if (proprietario == null) {
                throw new RuntimeException(
                        "Funcionário não encontrado: " +
                                dto.proprietario().cdUsuario()
                );
            }

            SalaEntity sala = salaRepository.findByCd(
                    dto.salaEntity().cd()
            );

            if (sala == null) {
                throw new RuntimeException(
                        "Sala não encontrada: " + dto.salaEntity().cd()
                );
            }

            AddReservationRequestDTO reserva = new AddReservationRequestDTO(
                    dto.dataReserva(),
                    dto.horaInicioReserva(),
                    dto.horaFimReserva(),
                    proprietario,
                    sala
            );

            criarReserva(reserva);
        }

        reservasDaLista.clear();
    }
    public List<ReservaEntity> verMinhasReservasLista(String cd){
        List<ReservaEntity> minhasReservas = new ArrayList<>();
        UsuarioEntity user = usuarioRepository.findBycdUsuario(cd);
        minhasReservas = user.getReservas();

        return minhasReservas;
    }

    public ReservationDTO verReserva( Integer id){
        ReservationDTO reservationDTO;
        ReservaEntity rev = reservaRepository.findBycdreserva(id);
        if(rev == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Essa reserva não existe");
        }
        reservationDTO = new ReservationDTO(rev.getCdreserva(),rev.getFimReserva(),rev.getInicioReserva(),rev.getStatusReserva(),rev.getProprietario().getNmUsuario(),rev.getSalaEntity().getNome());
        return reservationDTO;
    }

    public List<ReservationDTO> minhasReservas(String cd){
        UsuarioEntity MyUser = usuarioRepository.findBycdUsuario(cd);
        List<ReservationDTO> minhaListadeReservas =  new ArrayList<>();

        MyUser.getReservas().forEach(reserva -> {
            ReservationDTO reservas = new ReservationDTO(
                    reserva.getCdreserva(),
                    reserva.getFimReserva(),
                    reserva.getInicioReserva(),
                    reserva.getStatusReserva(),
                    reserva.getProprietario().getNmUsuario(),
                    reserva.getSalaEntity().getNome());
            minhaListadeReservas.add(reservas);
        });
        return minhaListadeReservas;
    }
    public String apagarReservar(Integer id, String senha,Authentication authentication){
        if(!reservaRepository.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Essa reserva não existe");
        }else{
            String emailAut = authentication.getName();
            UsuarioEntity userAdmin = usuarioRepository.findBynmEmailUsuario(emailAut);
            boolean senhaCorreta = passwordEncoder.matches(senha,
                    userAdmin.getNmSenhaUsuario());
            if(!senhaCorreta){
                throw new ErrosEfetivarReservaException("Senha incorreta");
            }else{
                ReservaEntity reservaEntityApagada = reservaRepository.findBycdreserva(id);
                reservaRepository.delete(reservaEntityApagada);
            }

        }
        return "Reserva cancelada com sucesso";
    }
    public List<MyEnterpriseReservationsResponseDTO> AdminUsuariosLista(Authentication authentication){
        String emailAut = authentication.getName();
        UsuarioEntity userAdmin = usuarioRepository.findBynmEmailUsuario(emailAut);
        List<UsuarioEntity> usuarios = usuarioRepository.findAll();

        List<MyEnterpriseReservationsResponseDTO> usuariosFirma = new ArrayList<>();
        for(UsuarioEntity usuario : usuarios){
            if(usuario.getChefe() != null){
                if(usuario.getChefe().equals(userAdmin)){
                   if(!usuario.getReservas().isEmpty()){
                       List<ReservaEntity> reservas = usuario.getReservas();
                       for(ReservaEntity reserva : reservas){
                           MyEnterpriseReservationsResponseDTO reservationsEnterprise = new MyEnterpriseReservationsResponseDTO(
                                   reserva.getCdreserva(),
                                   usuario.getNmUsuario(),
                                   reserva.getSalaEntity().getNome(),
                                   reserva.getInicioReserva().toLocalTime(),
                                   reserva.getFimReserva().toLocalTime(),
                                   reserva.getInicioReserva(),
                                   reserva.getStatusReserva()
                           );
                           usuariosFirma.add(reservationsEnterprise);
                       }
                   }
                }
            }
        }
        return  usuariosFirma;
    }
    public String editarReservar(Integer id, LocalDateTime Start, LocalDateTime End){
        String responseUpdate = "";
        if(!this.reservaRepository.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Reserva inexistente");
        }else{
            ReservaEntity reservaEntityEditada = reservaRepository.findBycdreserva(id);
            Map<String, Object> filtros = new HashMap<>();
            if (Start != null) {
                filtros.put("inicioReserva", Start);
            }
            if (End != null) {
                filtros.put("fimReserva", End);
            }

            UsuarioEntity usuarioDessaReserva = usuarioRepository.findBycdUsuario(reservaEntityEditada.getProprietario().getCdUsuario());

            for(int c=0; c<usuarioDessaReserva.getReservas().size(); c++){
                if(usuarioDessaReserva.getReservas().get(0).getInicioReserva().equals(Start)){
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Esse usuário já possui uma reserva nesse horário");
                }
            }

            //Gambiarra para ver se o usuario vai atualizar o inicio e o fim da reserva ao mesmo
            //tempo
            int contador = 0;

            for(Map.Entry<String,Object> entry2 : filtros.entrySet()){
                if(entry2.getKey().equals("inicioReserva")){
                    contador +=1;
                }
                if(entry2.getKey().equals("fimReserva")){
                    contador +=1;
                }
            }

            if(contador == 2){
                if(Start.isBefore(End)){
                    if(Start.isBefore(reservaEntityEditada.getFimReserva())){
                        reservaEntityEditada.setInicioReserva(Start);
                    }else{
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"A reserva tem que começar antes do fim da reserva");
                    }
                }else{
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"A reserva tem que começar antes do fim da reserva");
                }
                if(End.isAfter(Start)){
                    if(End.isAfter(reservaEntityEditada.getInicioReserva())){
                        reservaEntityEditada.setFimReserva(End);
                    }else{
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"O fim da reserva deve ser depois do inicio dela");
                    }
                }else{
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"O fim da reserva deve ser depois do inicio dela");
                }
            }

            for (Map.Entry<String, Object> entry : filtros.entrySet()) {

                if(entry.getKey().equals("inicioReserva")){
                    if(Start.isBefore(reservaEntityEditada.getFimReserva())){
                        reservaEntityEditada.setInicioReserva(Start);
                    }else{
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"A reserva tem que começar antes do fim da reserva");
                    }
                }

                if(entry.getKey().equals("fimReserva")){
                    if(End.isAfter(reservaEntityEditada.getInicioReserva())){
                        reservaEntityEditada.setFimReserva(End);
                    }else{
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"O fim da reserva deve ser depois do inicio dela");
                    }
                }
            }
            reservaRepository.save(reservaEntityEditada);
            if(filtros.isEmpty()){
                responseUpdate = "Nenhum dado foi atualizado";
            }else{
                responseUpdate = "Reserva atualizada com sucesso";
            }
        }

        return responseUpdate;
    }
//    public List<ReservaEntity> ReservationStarted(Authentication auth){
//        String userEmail = auth.getName();
//        UsuarioEntity user = usuarioRepository.findBycdUsuario(userEmail);
//        List<ReservaEntity> reservaEntityList = user.getReservas();
//        List<ReservaEntity> iniciadasEm = this.reservaRepository.findByinicioReserva(inicio);
//        if(iniciadasEm.isEmpty()){
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Nenhuma reserva se inicia nesse dia");
//        }
//        return iniciadasEm;
//    }
//
//    public List<ReservaEntity> ReservationEnded(LocalDateTime fim){
//        List<ReservaEntity> finalizadasEm = this.reservaRepository.findByfimReserva(fim);
//        if(finalizadasEm.isEmpty()){
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Nenhuma reserva é finalizada nesse dia");
//        }
//        return finalizadasEm;
//    }

    public void efetivarReserva(Authentication authentication, String Cnpj) {
        String NmEmailUserLogado = authentication.getName();
        System.out.println(NmEmailUserLogado);
        List<Integer> contadorerros = new ArrayList<>();
        contadorerros.add(0);
        contadorerros.add(0);
        MqttService mqttService = mqttServiceProvider.getIfAvailable(); //retorna null se o objeto tiver desativado como desativamos no properties e nas annotations
        Optional<UsuarioEntity> user = usuarioRepository.findOptionalBynmEmailUsuario(NmEmailUserLogado);
        UsuarioEntity usuario = user.get();
        if(usuario.getEmpresa().getCd_cnpj_empresa().equals(Cnpj)){
            LocalDateTime agora = LocalDateTime.now();
            ZoneId Fuso = ZoneId.of("America/Sao_Paulo");
            ZonedDateTime agoraNoFuso = agora.atZone(Fuso);

            //Precisei mudar essa parte, pois ele dava problema caso o usuário tivesse mais de uma reserva
            if(usuario.getReservas().isEmpty()){
                throw new ErrosEfetivarReservaException("Você não possui nenhuma reserva, consulte seu cronograma ou a administração da sua empresa");
            }else if(usuario.getReservas().size() == 1){
                ZonedDateTime inicioNoFuso = usuario.getReservas().get(0).getInicioReserva().atZone(Fuso);
                ZonedDateTime fimNoFuso =  usuario.getReservas().get(0).getFimReserva().atZone(Fuso);
                if(inicioNoFuso.equals(agoraNoFuso) || agoraNoFuso.isAfter(inicioNoFuso) && agoraNoFuso.isBefore(fimNoFuso)){
                    if(usuario.getReservas().get(0).getStatusReserva().equals(StatusReservaEnum.ESPERANDO_EFETIVACAO)){
                        if (mqttService != null) mqttService.iniciarReserva(usuario.getReservas().get(0).getCdreserva()); //só roda se o mqtt tiver ativado
                        System.out.println("Comando enviado: " + usuario.getReservas().get(0).getSalaEntity().getNome());
                    }else if(usuario.getReservas().get(0).getStatusReserva().equals(StatusReservaEnum.EFETIVADA)){
                        if (mqttService != null) mqttService.finalizarReserva(usuario.getReservas().get(0).getCdreserva()); //mesma coisa
                    }else{contadorerros.set(0, contadorerros.get(0)+1);}
                }else{contadorerros.set(1, contadorerros.get(1)+1);}
            }else{
                //ForEach para pegar a reserva mais proxima de agora
                ReservaEntity MaisProxima = null;
                for (ReservaEntity reserva : usuario.getReservas()) {
                   //Pegar a reserva, ver se ela está perto do horário atual, a mais perto deve ser armazenada
                    if (MaisProxima == null || reserva.getInicioReserva().atZone(Fuso).equals(agoraNoFuso) ||
                            reserva.getInicioReserva().atZone(Fuso).isBefore(agoraNoFuso) &&
                            reserva.getInicioReserva().atZone(Fuso).isAfter(MaisProxima.getInicioReserva().atZone(Fuso)) ) {
                        MaisProxima = reserva;
                    }
                }

                ZonedDateTime inicioNoFuso = MaisProxima.getInicioReserva().atZone(Fuso);
                ZonedDateTime fimNoFuso = MaisProxima.getFimReserva().atZone(Fuso);
                if(inicioNoFuso.equals(agoraNoFuso) || agoraNoFuso.isAfter(inicioNoFuso) && agoraNoFuso.isBefore(fimNoFuso)){
                    if(MaisProxima.getStatusReserva().equals(StatusReservaEnum.ESPERANDO_EFETIVACAO)){
                        if (mqttService != null) mqttService.iniciarReserva(MaisProxima.getCdreserva()); //só roda se o mqtt tiver ativado
                        System.out.println("Comando enviado: " + MaisProxima.getSalaEntity().getNome());
                    }else if(MaisProxima.getStatusReserva().equals(StatusReservaEnum.EFETIVADA)){

                        if (mqttService != null) mqttService.finalizarReserva(MaisProxima.getCdreserva()); //mesma coisa

                    }else{
                        contadorerros.set(0, contadorerros.get(0)+1);
                    }
                }else{
                    contadorerros.set(1, contadorerros.get(1)+1);
                }
            }

            if(contadorerros.get(0)>0){
                if (mqttService != null) mqttService.informarErro(); //mesma ideia aqui
                throw new ErrosEfetivarReservaException("Nenhuma reserva feita hoje");
            }
            if(contadorerros.get(1)>0){

                if (mqttService != null) mqttService.informarErro(); //....

                throw new ErrosEfetivarReservaException("Nenhuma reserva nesse horário, consulte o seu cronograma ou a sua administração.");
            }
        }else{
            throw new ErrosEfetivarReservaException("Este usuário não faz parte dessa empresa");
        }
    }

    public void retornoEfetivacao(String resposta){
        if(resposta.equals("INICIADO: 3")){
            ReservaEntity reservation = reservaRepository.findBycdreserva(3);
            reservation.setstatusReserva(StatusReservaEnum.EFETIVADA);
            reservaRepository.save(reservation);
            System.out.println("Comando enviado: " + reservation.getStatusReserva());
        }else if(resposta.equals("ENCERRAR: 3")){
            ReservaEntity reservation = reservaRepository.findBycdreserva(3);
            reservation.setstatusReserva(StatusReservaEnum.FINALIZADA);
            reservaRepository.save(reservation);
            System.out.println("Comando enviado: " + reservation.getStatusReserva());
        }
    }
}
