/*package br.com.keys.keys_api.controller;

import br.com.keys.keys_api.model.dto.LoginRequestDTO;
import br.com.keys.keys_api.model.dto.LoginResponseDTO;
import br.com.keys.keys_api.model.dto.RegisterRequestDTO;
import br.com.keys.keys_api.model.dto.RegisterResponseDTO;
import br.com.keys.keys_api.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> loginUser(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
       LoginResponseDTO loginResponseDTO =  this.authService.logarUser(loginRequestDTO);
       return  ResponseEntity.status(201).body(loginResponseDTO);
    }

    @PostMapping("/criarUser")
    public ResponseEntity<RegisterResponseDTO> createUser(@Valid @RequestBody RegisterRequestDTO registerRequestDTO, @RequestHeader String Authorization) {
        RegisterResponseDTO registerResponseDTO = this.authService.criarUser(registerRequestDTO, Authorization);
        return ResponseEntity.status(201).body(registerResponseDTO);
    }

}

 */