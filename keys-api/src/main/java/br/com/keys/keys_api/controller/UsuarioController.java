package br.com.keys.keys_api.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/")
public class UsuarioController {

    @PostMapping("/usuarios")
    public String createUser() {
        return "";
    }

    @GetMapping("/usuarios")
    public String findAllUsers() {
        return "";
    }

    @GetMapping("/usuarios/{id}")
    public String findUserById() {
        return "";
    }

    @DeleteMapping("/usuarios/{id}")
    public String deleteUserById() {
        return "";
    }
    @PatchMapping("/usuarios/{id}")
    public String updateUserById() {
        return "";
    }

}
