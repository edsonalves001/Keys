package br.com.keys.keys_api.repository;

import br.com.keys.keys_api.model.entity.ReservaEntity;
import br.com.keys.keys_api.model.entity.UsuarioEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaRepository extends JpaRepository<ReservaEntity, Integer> {

    List<ReservaEntity> findByinicioReserva(LocalDateTime inicioReserva);
    ReservaEntity findBycdreserva(int id);
    List<ReservaEntity> findByfimReserva(LocalDateTime fimReserva);
    List<ReservaEntity> findByProprietario(UsuarioEntity user);
    @Transactional
    void deleteByProprietario(UsuarioEntity user);
}
