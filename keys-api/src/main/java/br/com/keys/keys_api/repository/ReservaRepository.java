package br.com.keys.keys_api.repository;

import br.com.keys.keys_api.model.entity.ReservaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<ReservaEntity, Integer> {

    List<ReservaEntity> findByinicioReserva(LocalDateTime inicioReserva);
    ReservaEntity findBycdreserva(int id);
    List<ReservaEntity> findByfimReserva(LocalDateTime fimReserva);
}
