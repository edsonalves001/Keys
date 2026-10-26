package br.com.keys.keys_api.service;

import br.com.keys.keys_api.exception.EmpresaCadastradaException;
import br.com.keys.keys_api.model.dto.CreateEnterpriseRequestDTO;
import br.com.keys.keys_api.model.dto.CreateEnterpriseResponseDTO;
import br.com.keys.keys_api.model.entity.EmpresaEntity;
import br.com.keys.keys_api.repository.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    public CreateEnterpriseResponseDTO criarEmpresa(CreateEnterpriseRequestDTO createEnterpriseRequestDTO) {
            //Validar se cnpj já existe
            empresaRepository.findById(createEnterpriseRequestDTO.cd_cnpj_empresa()).orElseThrow(() ->
                    new EmpresaCadastradaException("Empresa já cadastrada! Tente outro Cnpj!"));

            //Criando:
            EmpresaEntity empresaEntity = new EmpresaEntity();
            empresaEntity.setCd_cnpj_empresa(createEnterpriseRequestDTO.cd_cnpj_empresa());
            empresaEntity.setNm_empresa(createEnterpriseRequestDTO.nm_empresa());
            empresaRepository.save(empresaEntity);

            return new CreateEnterpriseResponseDTO(empresaEntity.getCd_cnpj_empresa());
    }

}
