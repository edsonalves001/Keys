package br.com.keys.keys_api.controller;

import br.com.keys.keys_api.model.dto.CreateEnterpriseRequest;
import br.com.keys.keys_api.model.dto.CreateEnterpriseResponse;
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
    public ResponseEntity<CreateEnterpriseResponse> createEnterprise(@Valid @RequestBody CreateEnterpriseRequest createEnterpriseRequest) {
        CreateEnterpriseResponse createEnterpriseResponse = this.empresaService.criarEmpresa(createEnterpriseRequest);
        return ResponseEntity.status(201).body(createEnterpriseResponse);
    }
}
