package br.com.keys.keys_api.controller;

import br.com.keys.keys_api.model.dto.LoginRequest;
import br.com.keys.keys_api.model.dto.LoginResponse;
import br.com.keys.keys_api.model.dto.RegisterRequest;
import br.com.keys.keys_api.model.dto.RegisterResponse;
import br.com.keys.keys_api.repository.UsuarioRepository;
import br.com.keys.keys_api.security.JwtService;
import br.com.keys.keys_api.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
       LoginResponse loginResponse =  this.authService.logarUser(loginRequest);
       return  ResponseEntity.status(201).body(loginResponse);
    }

    @PostMapping("/criarUser")
    public ResponseEntity<RegisterResponse> createUser(@Valid @RequestBody RegisterRequest registerRequest, @RequestHeader String token) {
        RegisterResponse registerResponse = this.authService.criarUser(registerRequest, token);
        return ResponseEntity.status(201).body(registerResponse);
    }

}