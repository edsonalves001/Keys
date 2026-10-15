package br.com.keys.keys_api.repository;

import br.com.keys.keys_api.model.Sala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalaRepository extends JpaRepository<Sala, Integer> {
    //métodos mais complexos criados aqui....
}
