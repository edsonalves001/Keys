package br.com.keys.keys_api.repository;

import br.com.keys.keys_api.model.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, String> {

   public Optional<UsuarioEntity> findByEmail(String email);
   public Optional<UsuarioEntity> findByEmailAndChefeIsNull(String email);
   public Optional<UsuarioEntity> findByEmailAndSenha(String email, String senha);


}
