package br.com.keys.keys_api.exception;

public class UsuarioInexistenteException  extends RuntimeException{

    public UsuarioInexistenteException(String mensagem) {
        super(mensagem);
    }
}
