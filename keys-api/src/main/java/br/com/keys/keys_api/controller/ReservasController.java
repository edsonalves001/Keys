package br.com.keys.keys_api.controller;

import br.com.keys.keys_api.model.dto.AddReservationForListRequestDTO;
import org.springframework.security.core.Authentication;
import br.com.keys.keys_api.model.dto.AddReservationRequestDTO;
import br.com.keys.keys_api.model.dto.AddReservationResponseDTO;
import br.com.keys.keys_api.model.entity.ReservaEntity;
import br.com.keys.keys_api.model.entity.SalaEntity;
import br.com.keys.keys_api.model.entity.UsuarioEntity;
import br.com.keys.keys_api.repository.ReservaRepository;
import br.com.keys.keys_api.repository.SalaRepository;
import br.com.keys.keys_api.repository.UsuarioRepository;
import br.com.keys.keys_api.service.MqttService;
import br.com.keys.keys_api.service.ReservaService;
import br.com.keys.keys_api.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReservasController {
    private final ReservaService reservaService;
    private final MqttService mqttService;

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;



    @PostMapping("/criarReserva")
    public ResponseEntity<AddReservationResponseDTO> criarReserva( @Valid @RequestBody AddReservationRequestDTO addReservationRequestDTO) {
        AddReservationResponseDTO addReservationResponseDTO = this.reservaService.criarReserva(addReservationRequestDTO);
        return ResponseEntity.status(201).body(addReservationResponseDTO);
    }

    //Daqui começa os endpoint para adicionar na lista pré cadastro final
    @GetMapping("/TodasasReservasLista")
    public List<AddReservationForListRequestDTO> listarReservas(){
        return this.reservaService.verListaReservas();
    }

    @PostMapping("/adicionarReservaaLista")
    public void adicionarReservaaLista(@Valid @RequestBody AddReservationForListRequestDTO addReservationRequestDTO) {
        this.reservaService.adicionarReservaaLista(addReservationRequestDTO);
    }

    @PostMapping("/AdicionarListadeReservas")
    public void adicionarListadeReservas() {
        this.reservaService.adicionarListadeReservas();
    }
    //aqui acaba os endpoints das listas

    @GetMapping("/TodasasReservas")
    public List<ReservaEntity> verReservas(){
        return this.reservaRepository.findAll();
    }

    @GetMapping("/VerReserva/{id}")
    public ReservaEntity verReserva(@PathVariable Integer id){
        return this.reservaRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Reserva inexistente"));
    }

    @DeleteMapping("ApagarReserva/{id}")
    public String apagarReserva(@PathVariable Integer id){
        return reservaService.apagarReservar(id);
    }

    @PatchMapping("AtualizarReserva/{id}")
    public String EditarReserva(@PathVariable Integer id, @RequestParam(required = false) LocalDateTime dataInicioAtualizacao, @RequestParam(required = false) LocalDateTime dataFimAtualizacao, @RequestParam(required = false) UsuarioEntity usuarioEntityCd, @RequestParam(required = false) SalaEntity salaEntityCd) {
        return reservaService.editarReservar(id,dataInicioAtualizacao,dataFimAtualizacao, usuarioEntityCd, salaEntityCd);
    }

    @GetMapping("/ReservaIniciaEm/{dataInicio}")
    public List<ReservaEntity> ReservasIniciaEm(@PathVariable LocalDateTime dataInicio){
        return reservaService.ReservationStarted(dataInicio);
    }

    @GetMapping("/ReservaFinalizamEm/{dataFim}")
    public List<ReservaEntity> ReservasFinalizamEm(@PathVariable LocalDateTime dataFim){
        return reservaService.ReservationEnded(dataFim);
    }
    @PostMapping("/EfetivarReserva/{cnpjEmpresa}")
    public void EfetivarviaQrCode(@PathVariable String cnpjEmpresa) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication(); //Pra pegar o usuário logado
        reservaService.efetivarReserva(auth,cnpjEmpresa);
    }

}
