package br.com.keys.keys_api.service;

import br.com.keys.keys_api.model.dto.AddReservationRequestDTO;
import br.com.keys.keys_api.model.dto.AddReservationResponseDTO;
import br.com.keys.keys_api.model.entity.ReservaEntity;
import br.com.keys.keys_api.model.entity.SalaEntity;
import br.com.keys.keys_api.model.entity.UsuarioEntity;
import br.com.keys.keys_api.repository.ReservaRepository;
import br.com.keys.keys_api.repository.SalaRepository;
import br.com.keys.keys_api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReservaService {
    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final SalaRepository salaRepository;
    public AddReservationResponseDTO criarReserva(AddReservationRequestDTO addReservationRequestDTO) {
        ReservaEntity novaReservaEntity = new ReservaEntity();

        //Verifica se o usuario existe, caso não exista, ele informa que o usuario não existe, caso exista, ele é inserido no banco abaixo
        UsuarioEntity proprietario = usuarioRepository.findById(addReservationRequestDTO.proprietario().getId()).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        //Verifica se a sala existe, caso não exista, ele informa que a sala não foi encontrada, caso existe, ela é inserida no banco abaixo
        SalaEntity salaEntity = salaRepository.findById(addReservationRequestDTO.salaEntity().getCd()).orElseThrow(() -> new RuntimeException("Sala não encontrada"));
        novaReservaEntity.setInicioReserva(addReservationRequestDTO.inicioReserva());
        novaReservaEntity.setFimReserva(addReservationRequestDTO.fimReserva());
        novaReservaEntity.setProprietario(proprietario);
        novaReservaEntity.setSalaEntity(salaEntity);
        novaReservaEntity.setstatusReserva();
        reservaRepository.save(novaReservaEntity);

        return new AddReservationResponseDTO(novaReservaEntity.getInicioReserva(), novaReservaEntity.getFimReserva(), novaReservaEntity.getProprietario(), novaReservaEntity.getSalaEntity());
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
}
