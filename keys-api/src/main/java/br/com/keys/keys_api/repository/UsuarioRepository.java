package br.com.keys.keys_api.repository;

import br.com.keys.keys_api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    //métodos mais complexos criados aqui....
}
