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

import br.com.keys.keys_api.enums.StatusSalaEnum;
import br.com.keys.keys_api.model.entity.SalaEntity;
import br.com.keys.keys_api.repository.SalaRepository;

@RestController
@RequestMapping("/api")
public class SalaController {
  @Autowired 
  SalaRepository salaRepository;

  //Adiciona uma nova sala
  @PostMapping("/salas")
  public String adicionarSala(@RequestParam String nome, @RequestParam String desc) {
    SalaEntity novaSalaEntity = new SalaEntity(nome, desc);
    salaRepository.save(novaSalaEntity);

    return novaSalaEntity.getNome()+" adicionado com sucesso!";
  }

  //Retorna todas as salas
  @GetMapping("/salas")
  public List<SalaEntity> getAllSalas() {
    List<SalaEntity> salaEntities = salaRepository.findAll();

    return salaEntities;
  }

  //Retorna uma sala a partir do codigo ou nome dela
  @GetMapping("/sala")
  public SalaEntity getSala(@RequestParam(required = false) Integer cd, @RequestParam(required = false) String nome) {
    SalaEntity salaEntity = null;

    if (cd != null && cd != 0) {
      salaEntity = salaRepository.findByCd(cd);
    } else if (nome != null && !nome.isEmpty() && !nome.isBlank()) {
      salaEntity = salaRepository.findByNome(nome);
    } else {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Requisição deve fornecer parametro \"cd\" ou \"nome\" para buscar sala");
    }

    if (salaEntity == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sala não encontrada");
    }

    return salaEntity;
  }

  //Altera parte de ou todos os dados de uma sala
  @PatchMapping("/salas") 
  public SalaEntity editarSala(@RequestParam Integer salaId, @RequestParam(required = false) String nome, @RequestParam(required = false) String descricao) {
    SalaEntity salaEntity = salaRepository.findByCd(salaId);

    if (nome != null && !nome.isBlank() && !nome.isEmpty()) {
      salaEntity.setNome(nome);
    }

    if (descricao != null && !descricao.isBlank() && !descricao.isEmpty()) {
      salaEntity.setDescricao(descricao);
    }

    salaRepository.save(salaEntity);

    return salaEntity;
  }

  @DeleteMapping("/sala")
  public String removerSala(@RequestParam Integer cd) {
    SalaEntity salaEntity = salaRepository.findByCd(cd);

    salaRepository.delete(salaEntity);

    return salaEntity.getNome();
  }

  @PatchMapping("/sala/estado")
  public String atualizarEstado(@RequestParam StatusSalaEnum statusSalaEnum, @RequestParam Integer salaId) {
    SalaEntity salaEntity = salaRepository.findByCd(salaId);

    salaEntity.setStatusSala(statusSalaEnum);
    salaRepository.save(salaEntity);

    return "Estado da sala alterado para "+ salaEntity.getStatusSala();
  }
}
