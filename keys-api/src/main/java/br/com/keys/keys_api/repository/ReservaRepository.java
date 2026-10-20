package br.com.keys.keys_api.repository;

import br.com.keys.keys_api.model.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {
    //métodos mais complexos criados aqui....
}
