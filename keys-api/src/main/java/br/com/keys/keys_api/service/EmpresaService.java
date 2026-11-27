package br.com.keys.keys_api.service;

import br.com.keys.keys_api.exception.EmpresaCadastradaException;
import br.com.keys.keys_api.exception.EmpresaInexistenteException;
import br.com.keys.keys_api.exception.UsuarioInexistenteException;
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
            //Validar se empresa já existe
            EmpresaEntity empresa = empresaRepository.findById(createEnterpriseRequestDTO.cd_cnpj_empresa()).orElse(null);
            if(empresa != null) {
                throw new EmpresaCadastradaException("Empresa já cadastrada!");
            }
            //Criando:
            EmpresaEntity empresaEntity = new EmpresaEntity();
            empresaEntity.setCd_cnpj_empresa(createEnterpriseRequestDTO.cd_cnpj_empresa());
            empresaEntity.setNm_empresa(createEnterpriseRequestDTO.nm_empresa());
            empresaRepository.save(empresaEntity);

            return new CreateEnterpriseResponseDTO(empresaEntity.getNm_empresa(), "Empresa cadastrada com sucesso!!!");
    }

    public void deletarEmpresa(String cnpj) {
        //Verificar se empresa existe:
        if(cnpj.trim().isEmpty()) {
            throw new EmpresaInexistenteException("Essa empresa não existe! Envie um dado de cnpj concreto!");
        }
        this.empresaRepository.findById(cnpj).orElseThrow(()-> new EmpresaInexistenteException("Essa empresa não existe!"));

        //Deletando:

        this.empresaRepository.deleteById(cnpj);
    }
}
