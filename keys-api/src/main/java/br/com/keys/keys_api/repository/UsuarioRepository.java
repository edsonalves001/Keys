package br.com.keys.keys_api.repository;

import br.com.keys.keys_api.model.entity.EmpresaEntity;
import br.com.keys.keys_api.model.entity.UsuarioEntity;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, String> {

   public Optional<UsuarioEntity> findBynmEmailUsuario(String email);
   public Optional<UsuarioEntity> findByNmEmailUsuarioAndChefeIsNull(String email);
   public Optional<UsuarioEntity> findByNmEmailUsuarioAndNmSenhaUsuario(String email, String senha);
   public List<UsuarioEntity> findByEmpresa(EmpresaEntity empresaEntity); //buscar usuários do chefe
   public Optional<UsuarioEntity> findByEmpresaAndChefeIsNull(EmpresaEntity empresa);
}
