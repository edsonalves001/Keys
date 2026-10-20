package br.com.keys.keys_api.service;

import br.com.keys.keys_api.exception.EmpresaCadastradaException;
import br.com.keys.keys_api.model.dto.CreateEnterpriseRequest;
import br.com.keys.keys_api.model.dto.CreateEnterpriseResponse;
import br.com.keys.keys_api.model.entity.Empresa;
import br.com.keys.keys_api.repository.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    public CreateEnterpriseResponse criarEmpresa(CreateEnterpriseRequest createEnterpriseRequest) {
            //Validar se cnpj já existe
            empresaRepository.findById(createEnterpriseRequest.cd_cnpj_empresa()).orElseThrow(() ->
                    new EmpresaCadastradaException("Empresa já cadastrada! Tente outro Cnpj!"));

            //Criando:
            Empresa empresa = new Empresa();
            empresa.setCd_cnpj_empresa(createEnterpriseRequest.cd_cnpj_empresa());
            empresa.setNm_empresa(createEnterpriseRequest.nm_empresa());
            empresaRepository.save(empresa);

            return new CreateEnterpriseResponse(empresa.getCd_cnpj_empresa());
    }

}
