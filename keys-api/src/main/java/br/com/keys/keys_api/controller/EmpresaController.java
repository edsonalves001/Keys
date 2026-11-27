package br.com.keys.keys_api.controller;

import br.com.keys.keys_api.model.dto.CreateEnterpriseRequestDTO;
import br.com.keys.keys_api.model.dto.CreateEnterpriseResponseDTO;
import br.com.keys.keys_api.service.EmpresaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/")
public class EmpresaController {

    private final EmpresaService empresaService;

    @PostMapping("/empresa")
    public ResponseEntity<CreateEnterpriseResponseDTO> createEnterprise(@Valid @RequestBody CreateEnterpriseRequestDTO createEnterpriseRequest) {
        CreateEnterpriseResponseDTO createEnterpriseResponse = this.empresaService.criarEmpresa(createEnterpriseRequest);
        return ResponseEntity.status(201).body(createEnterpriseResponse);
    }

    @DeleteMapping("/empresa")

    public ResponseEntity<Void> deleteEnterprise(@RequestParam(name = "cnpj", required = true) String cnpj) {
        this.empresaService.deletarEmpresa(cnpj);
        return ResponseEntity.noContent().build();
    }
}
