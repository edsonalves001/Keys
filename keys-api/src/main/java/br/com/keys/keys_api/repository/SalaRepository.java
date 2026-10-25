package br.com.keys.keys_api.repository;

import br.com.keys.keys_api.model.entity.Sala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalaRepository extends JpaRepository<Sala, Integer> {
    Sala findByCd(int cd);
    Sala findByNome(String nome);
}
