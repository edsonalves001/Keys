package br.com.keys.keys_api.exception;

public class EmpresaInexistenteException extends RuntimeException {
    public EmpresaInexistenteException(String message) {
        super(message);
    }
}
