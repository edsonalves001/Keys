package br.com.keys.keys_api.controller;

import br.com.keys.keys_api.model.dto.CreateEnterpriseRequestDTO;
import br.com.keys.keys_api.model.dto.CreateEnterpriseResponseDTO;
import br.com.keys.keys_api.service.EmpresaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class EmpresaController {

    private final EmpresaService empresaService;

    public ResponseEntity<CreateEnterpriseResponseDTO> createEnterprise(@Valid @RequestBody CreateEnterpriseRequestDTO createEnterpriseRequestDTO) {
        CreateEnterpriseResponseDTO createEnterpriseResponseDTO = this.empresaService.criarEmpresa(createEnterpriseRequestDTO);
        return ResponseEntity.status(201).body(createEnterpriseResponseDTO);
    }
}
