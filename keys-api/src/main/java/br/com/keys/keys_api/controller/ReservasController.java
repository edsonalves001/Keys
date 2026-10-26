package br.com.keys.keys_api.controller;

import br.com.keys.keys_api.model.dto.AddReservationRequestDTO;
import br.com.keys.keys_api.model.dto.AddReservationResponseDTO;
import br.com.keys.keys_api.model.entity.ReservaEntity;
import br.com.keys.keys_api.model.entity.SalaEntity;
import br.com.keys.keys_api.model.entity.UsuarioEntity;
import br.com.keys.keys_api.repository.ReservaRepository;
import br.com.keys.keys_api.service.ReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReservasController {
    private final ReservaService reservaService;
    @Autowired
    ReservaRepository reservaRepository;

    @PostMapping("/criarReserva")
    public ResponseEntity<AddReservationResponseDTO> criarReserva( @Valid @RequestBody AddReservationRequestDTO addReservationRequestDTO) {
        AddReservationResponseDTO addReservationResponseDTO = this.reservaService.criarReserva(addReservationRequestDTO);
        return ResponseEntity.status(201).body(addReservationResponseDTO);
    }

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


}
