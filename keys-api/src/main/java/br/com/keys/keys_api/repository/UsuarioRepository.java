package br.com.keys.keys_api.repository;

import br.com.keys.keys_api.model.entity.Usuario;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {

   public Optional<Usuario> findBynmEmailUsuario(String email);
   public Optional<Usuario> findByNmEmailUsuarioAndChefeIsNull(String email);
   public Optional<Usuario> findByNmEmailUsuarioAndNmSenhaUsuario(String email, String senha);
   public List<Usuario> findByChefe(Usuario chefe); //buscar usuários do chefe
}
