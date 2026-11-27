package br.com.keys.keys_api.exception;

public class PermissaoNegadaException extends RuntimeException  { //usada para quando o user não tem permissão para fazer aquela ação, não necessariamente acessar tal endpoint
    public PermissaoNegadaException(String message) {
        super(message);
    }
}
