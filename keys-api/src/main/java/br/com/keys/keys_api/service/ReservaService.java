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
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
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

    private final MqttService mqttService;
    public AddReservationResponseDTO criarReserva(AddReservationRequestDTO addReservationRequestDTO) {
        ReservaEntity novaReservaEntity = new ReservaEntity();

        //Verifica se o usuario existe, caso não exista, ele informa que o usuario não existe, caso exista, ele é inserido no banco abaixo
        assert addReservationRequestDTO.proprietario().getId() != null;
        UsuarioEntity proprietario = usuarioRepository.findById(addReservationRequestDTO.proprietario().getId()).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        //Verifica se a sala existe, caso não exista, ele informa que a sala não foi encontrada, caso existe, ela é inserida no banco abaixo
        SalaEntity salaEntity = salaRepository.findById(addReservationRequestDTO.salaEntity().getCd()).orElseThrow(() -> new RuntimeException("Sala não encontrada"));

        LocalDate dataReserva = LocalDate.of(addReservationRequestDTO.dataReserva().getYear(), addReservationRequestDTO.dataReserva().getMonth(), addReservationRequestDTO.dataReserva().getDayOfMonth());
        LocalTime horaInicioReserva = LocalTime.of(addReservationRequestDTO.horaInicioReserva().getHour(), addReservationRequestDTO.horaInicioReserva().getMinute());
        LocalTime horaFimReserva = LocalTime.of(addReservationRequestDTO.horaFimReserva().getHour(), addReservationRequestDTO.horaFimReserva().getMinute());

        if(!horaInicioReserva.isAfter(horaFimReserva) || !horaFimReserva.isBefore(horaInicioReserva)) {
          LocalDateTime inicio = dataReserva.atTime(horaInicioReserva);
          LocalDateTime fim = dataReserva.atTime(horaFimReserva);
          novaReservaEntity.setInicioReserva(inicio);
          novaReservaEntity.setFimReserva(fim);
        }
        novaReservaEntity.setProprietario(proprietario);
        novaReservaEntity.setSalaEntity(salaEntity);
        novaReservaEntity.setstatusReserva(StatusReservaEnum.ESPERANDO_EFETIVACAO);
        reservaRepository.save(novaReservaEntity);

        return new AddReservationResponseDTO(novaReservaEntity.getInicioReserva(), novaReservaEntity.getFimReserva(), novaReservaEntity.getProprietario(), novaReservaEntity.getSalaEntity());
    }

    List<AddReservationForListRequestDTO> reservasDaLista = new ArrayList<>();

    public void adicionarReservaaLista(AddReservationForListRequestDTO reserva){
        reservasDaLista.add(reserva);
    }
    public List<AddReservationForListRequestDTO> verListaReservas(){
        return reservasDaLista;
    }
    public void adicionarListadeReservas() {
        if(!reservasDaLista.isEmpty()) {

            for (AddReservationForListRequestDTO addReservationRequestDTO : reservasDaLista) {
                AddReservationRequestDTO reserva = new AddReservationRequestDTO(
                        addReservationRequestDTO.dataReserva(),addReservationRequestDTO.horaInicioReserva(),
                        addReservationRequestDTO.horaFimReserva(), usuarioRepository.findBycdUsuario(addReservationRequestDTO.proprietario().cdUsuario()),
                        salaRepository.findByCd(addReservationRequestDTO.salaEntity().cd()));
                criarReserva(reserva);
            }
            reservasDaLista.clear();
        }
    }

    public List<ReservaEntity> verMinhasReservasLista(String cd){
        List<ReservaEntity> minhasReservas = new ArrayList<>();
        UsuarioEntity user = usuarioRepository.findBycdUsuario(cd);
        minhasReservas = user.getReservas();

        return minhasReservas;
    }
    public String apagarReservar(Integer id){
        if(!reservaRepository.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Essa reserva não existe");
        }else{
            ReservaEntity reservaEntityApagada = reservaRepository.findBycdreserva(id);
            reservaRepository.delete(reservaEntityApagada);
        }
        return "Reserva apagada com sucesso";
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
                                   reserva.getInicioReserva()
                           );
                           usuariosFirma.add(reservationsEnterprise);
                       }
                   }
                }
            }
        }
        return  usuariosFirma;
    }
    public String editarReservar(Integer id, LocalDateTime Start, LocalDateTime End, UsuarioEntity user, SalaEntity room){
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
            if(user != null) {
                filtros.put("usuarioCd", user);
            }
            if(room != null) {
                filtros.put("salaCd", room);
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
                if(entry.getKey().equals("usuarioCd")){
                    if(usuarioRepository.existsById(user.getCdUsuario())){
                        reservaEntityEditada.setProprietario(user);
                    }else{
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Usuario não encontrado");
                    }
                }

                if(entry.getKey().equals("sala")){
                    if(salaRepository.existsById(room.getCd())){
                        reservaEntityEditada.setSalaEntity(room);
                    }else{
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Sala não encontrado");
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
    public List<ReservaEntity> ReservationStarted(LocalDateTime inicio){
        List<ReservaEntity> iniciadasEm = this.reservaRepository.findByinicioReserva(inicio);
        if(iniciadasEm.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Nenhuma reserva se inicia nesse dia");
        }
        return iniciadasEm;
    }

    public List<ReservaEntity> ReservationEnded(LocalDateTime fim){
        List<ReservaEntity> finalizadasEm = this.reservaRepository.findByfimReserva(fim);
        if(finalizadasEm.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Nenhuma reserva é finalizada nesse dia");
        }
        return finalizadasEm;
    }

    public void efetivarReserva(Authentication authentication, String Cnpj) {
       String NmEmailUserLogado = authentication.getName();
       System.out.println(NmEmailUserLogado);
       List<Integer> contadorerros = new ArrayList<>();
       contadorerros.add(0);
       contadorerros.add(0);
       Optional<UsuarioEntity> user = usuarioRepository.findOptionalBynmEmailUsuario(NmEmailUserLogado);
       UsuarioEntity usuario = user.get();
        if(usuario.getEmpresa().getCd_cnpj_empresa().equals(Cnpj)){
               LocalDateTime agora = LocalDateTime.now(); //Serve para pegar o tempo atual no fuso da classe
               ZoneId Fuso = ZoneId.of("America/Sao_Paulo"); //Define qual fuso horário vamos utilizar
               ZonedDateTime agoraNoFuso = agora.atZone(Fuso); // Unindo os dois anteriores temos o horário atual no nosso fuso horário
                usuario.getReservas().forEach(reserva -> {
                   ZonedDateTime inicioNoFuso = reserva.getInicioReserva().atZone(Fuso); //Converte o inicio da reserva para ficar de acordo com o nosso fuso horário
                   ZonedDateTime fimNoFuso = reserva.getFimReserva().atZone(Fuso); //Converte o fim da reserva para ficar de acordo com o nosso fuso horário
                    if(inicioNoFuso.equals(agoraNoFuso) || agoraNoFuso.isAfter(inicioNoFuso) && agoraNoFuso.isBefore(fimNoFuso)){
                    //Se o inicio da reserva for no mesmo horário de agora ou se agora for depois do inicio, porém antes do fim da reserva
                        if(reserva.getStatusReserva().equals(StatusReservaEnum.ESPERANDO_EFETIVACAO)){
                            //Se o status da reserva estiver como esperando efetivação, ele envia o comando ao MQTT
                            mqttService.iniciarReserva(reserva.getCdreserva());
                            System.out.println("Comando enviado: " + reserva.getSalaEntity().getNome());
                        }else if(reserva.getStatusReserva().equals(StatusReservaEnum.EFETIVADA)){
                            mqttService.finalizarReserva(reserva.getCdreserva());
                        }else{
                            contadorerros.set(0, contadorerros.get(0)+1);
                        }
                    }else{
                        contadorerros.set(1, contadorerros.get(1)+1);
                   }
               });
                if(contadorerros.get(0)>0){
                    mqttService.InformarErro();
                    throw new ErrosEfetivarReservaException("Nenhuma reserva feita hoje");
                }
                if(contadorerros.get(1)>0){
                    mqttService.InformarErro();
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
