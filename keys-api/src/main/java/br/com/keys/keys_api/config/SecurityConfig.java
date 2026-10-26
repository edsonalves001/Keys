/*package br.com.keys.keys_api.config;

import br.com.keys.keys_api.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration //define que vou retornar beans aqui, que sao classes gerenciadas pelo Spring  (instanciadas sozinhas etc) que serão usadas em outras partes
@EnableWebSecurity //ativa o filtro spring dessa classe, que via interceptar qualquer req antes do controller
@RequiredArgsConstructor //injeção dependencias

//classe responsável por configurar a autorização.
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter; //classe de autenticação

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) //desativa verificação csrf
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //desativa criação sessão no navegador, pois usamnos token
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll() //permite apenas rotas com esse começo sem precisar de autenticação
                        .anyRequest().authenticated() //o resto precisa de autenticação
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); //permite nosso filtro rodar primeiro, pois o segundo n funciona no nosso caso

        return http.build(); //cria o objeto maldito da classe do filtro 🖕🖕🖕
    }

    @Bean
    //retorna objeto para comparar e gerar hash bcrypt. Usado em outros lugares
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    //Disponibilizo essa classe para usar no meu controller de autenticação. Ela já vem com o meu UserDetails, o método de PasswordEncoder acima,
    // lança uma exceção específica para credenciais e cria e retorna aquela interface de autenticação que representa
    // nosso user, a Authentication
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

 */
