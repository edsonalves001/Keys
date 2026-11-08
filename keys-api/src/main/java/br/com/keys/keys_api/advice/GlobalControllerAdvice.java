package br.com.keys.keys_api.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

//Essa classe melhora a forma como o sistema mostra os erros, facilitando o desenvolvimento
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> tratarErroGenerico(Exception ex) {

        Map<String, Object> resposta = Map.of(
                "mensagem", "Ocorreu um erro interno no servidor.",
                "erro", ex.getMessage(),
                "data", LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resposta);
    }
}