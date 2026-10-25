package br.com.keys.keys_api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.keys.keys_api.enums.StatusSala;
import br.com.keys.keys_api.model.entity.Sala;
import br.com.keys.keys_api.repository.SalaRepository;

@RestController
@RequestMapping("/api")
public class SalaController {
  @Autowired 
  SalaRepository salaRepository;

  //Adiciona uma nova sala
  @PostMapping("/salas")
  public String adicionarSala(@RequestParam String nome, @RequestParam String desc) {
    Sala novaSala = new Sala(nome, desc);
    salaRepository.save(novaSala);

    return novaSala.getNome()+" adicionado com sucesso!";
  }

  //Retorna todas as salas
  @GetMapping("/salas")
  public List<Sala> getAllSalas() {
    List<Sala> salas = salaRepository.findAll();

    return salas;
  }

  //Retorna uma sala a partir do codigo ou nome dela
  @GetMapping("/sala")
  public Sala getSala(@RequestParam(required = false) Integer cd, @RequestParam(required = false) String nome) {
    Sala sala = null;

    if (cd != null && cd != 0) {
      sala = salaRepository.findByCd(cd);
    } else if (nome != null && !nome.isEmpty() && !nome.isBlank()) {
      sala = salaRepository.findByNome(nome);
    } else {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Requisição deve fornecer parametro \"cd\" ou \"nome\" para buscar sala");
    }

    if (sala == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sala não encontrada");
    }

    return sala;
  }

  //Altera parte de ou todos os dados de uma sala
  @PatchMapping("/salas") 
  public Sala editarSala(@RequestParam Integer salaId, @RequestParam(required = false) String nome, @RequestParam(required = false) String descricao) {
    Sala sala = salaRepository.findByCd(salaId);

    if (nome != null && !nome.isBlank() && !nome.isEmpty()) {
      sala.setNome(nome);
    }

    if (descricao != null && !descricao.isBlank() && !descricao.isEmpty()) {
      sala.setDescricao(descricao);
    }

    salaRepository.save(sala);

    return sala;
  }

  @DeleteMapping("/sala")
  public String removerSala(@RequestParam Integer cd) {
    Sala sala = salaRepository.findByCd(cd);

    salaRepository.delete(sala);

    return sala.getNome();
  }

  @PatchMapping("/sala/estado")
  public String atualizarEstado(@RequestParam StatusSala statusSala, @RequestParam Integer salaId) {
    Sala sala = salaRepository.findByCd(salaId);

    sala.setStatusSala(statusSala);
    salaRepository.save(sala);

    return "Estado da sala alterado para "+sala.getStatusSala();
  }
}
