package br.com.keys.keys_api.repository;

import br.com.keys.keys_api.model.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, String> {
        //métodos mais complexos criados aqui....
}
