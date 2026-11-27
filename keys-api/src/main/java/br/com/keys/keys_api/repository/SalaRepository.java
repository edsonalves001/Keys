package br.com.keys.keys_api.repository;

import br.com.keys.keys_api.model.entity.SalaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalaRepository extends JpaRepository<SalaEntity, Integer> {
    SalaEntity findByCd(int cd);
    SalaEntity findByNome(String nome);
}
