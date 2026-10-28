package br.com.keys.keys_api.controller;

import br.com.keys.keys_api.model.dto.*;
//import br.com.keys.keys_api.security.JwtService;
//import br.com.keys.keys_api.service.AuthService;
import br.com.keys.keys_api.service.UsuarioService;
import jakarta.persistence.GeneratedValue;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/usuarios")
    public ResponseEntity<List<FindAllUSersResponse>> findAllUsers(@Valid @RequestBody FindAllUsersRequest findAllUsersRequest, @RequestHeader("Authorization") String token) {
        List<FindAllUSersResponse> listaUsuarios = this.usuarioService.buscarTodosUsuarios(findAllUsersRequest, token);
        return ResponseEntity.status(200).body(listaUsuarios);
    }

    @GetMapping("/usuarios/{id}")
    public ResponseEntity<FindUserByIdResponse> findUserById(@PathVariable("id") String id) {
        FindUserByIdResponse findUserByIdResponse = this.usuarioService.buscarUsuarioPorId(id);
        return ResponseEntity.status(200).body(findUserByIdResponse);
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable("id") String id, String token) //só adm deleta
    {
        this.usuarioService.deletarUsuario(id, token);
        return ResponseEntity.noContent().build(); //retorna status 204 = no content, mas ação feita com sucesso
    }

    @PatchMapping("/usuarios/{id}")
    public ResponseEntity<UpdateUserResponse> updateUserById(@PathVariable("id") String id, @RequestBody UpdateUserRequest updateUserRequest) {
        UpdateUserResponse updateUserResponse = this.usuarioService.atualizarInformacoesUsuario(id, updateUserRequest);
        return ResponseEntity.status(200).body(updateUserResponse);
    }

    @PatchMapping("/usuarios/password/{id}")
    public ResponseEntity<Void> updateUserPassword(@PathVariable("id") String id, @Valid @RequestBody UpdateUserPasswordRequest updateUserPasswordRequest) {
        this.usuarioService.atualizarSenhaUsuario(updateUserPasswordRequest, id);
        return ResponseEntity.noContent().build();
    }

    /*
    @PatchMapping("/usuarios/alterarSenha") String updateSenhaByEmail(@RequestBody UpdateSenhaByEmailRequest updateSenhaByEmailRequest) {
        UpdateSenhaByEmailResponse updateSenhaByEmailResponse = this.usuarioService.updateSenhaByEmail(updateSenhaByEmailRequest);
        return "";
    }
     */


}
