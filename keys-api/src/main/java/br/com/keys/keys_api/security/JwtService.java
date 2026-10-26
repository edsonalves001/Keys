/*package br.com.keys.keys_api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    private final SecretKey signingKey; //armazena a secret e prepara ela para algoritmos de criptografia etc
    private final long jwtExpirationMs; //armazena o tempo de expiração

    public JwtService(
            @Value("${app.jwt.secret}") String secret, //os dois buscam valores do application.properties com essa anotação e armazenam na var
            @Value("${app.jwt.expiration-ms}") long expirationMs) {

        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8); //preciso converter essa secret em bytes, pois os algoritmos de criptografia usam bytes
        this.signingKey = Keys.hmacShaKeyFor(keyBytes); //implemento a interface usando essa classe responsável por chaves do tipo e
        // seu método específico para um algoritmo
        this.jwtExpirationMs = expirationMs;
    }

    public String generateToken(UserDetails userDetails) { //entidade que representa o user pro Security
        return Jwts.builder().subject(userDetails.getUsername()) //add email como identificador
                .issuedAt(new Date()) //define data inicio token
                .expiration(new Date(System.currentTimeMillis() + this.jwtExpirationMs)) //define data de expiração
                .signWith(this.signingKey) //define a chave que valida token
                .compact(); //cria uma String na formatação jwt com nossos dados e retorna
    }

    public String extractUsername(String token) { //usado para chamar um método private que retorna o identificador do usuario no token
        return this.extractClaim(token, Claims::getSubject); //passo a instrução que quero que execute, pois em outros métodos posso fazer outro get da claim
    }

    //generico pq pode retornar outros tipos como data de expiração e criação
    private <T> T extractClaim(String token, Function<Claims, T> ClaimsResolver) { //claims são as propriedades do corpo do token
        final Claims claims = this.extractAllClaims(token);
        return ClaimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) { //builder que extrair claim
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) { //verifica se o token é valido
        final String username = extractUsername(token); //pego identificador do token
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token); //compara se identificador é o mesmo e se data passou
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date()); //verifica se a data de expiração do token é anterior a data atual.
        // Se for, token é válido
    }
}

 */