package br.com.keys.keys_api.repository;

import br.com.keys.keys_api.model.entity.EmpresaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpresaRepository extends JpaRepository<EmpresaEntity, String> {
        //métodos mais complexos criados aqui....
}
