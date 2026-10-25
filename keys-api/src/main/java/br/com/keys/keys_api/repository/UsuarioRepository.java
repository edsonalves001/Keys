package br.com.keys.keys_api.repository;

import br.com.keys.keys_api.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {

   public Optional<Usuario> findByEmail(String email);
   public Optional<Usuario> findByEmailAndChefeIsNull(String email);
   public Optional<Usuario> findByEmailAndSenha(String email, String senha);

}
