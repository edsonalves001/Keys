package br.com.keys.keys_api.security;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor

//classe para aplicar o filtro de autenticação jwt
public class JwtAuthenticationFilter extends OncePerRequestFilter { //apenas uma chamada por req
    private final JwtService jwtService; //para criar token e verificar
    private final UserDetailsService userDetailsService; //para criar user e comparar com o token

    @Override
    protected boolean shouldNotFilter(HttpServletRequest req) throws ServletException { //permite rotas de não passarem pelo filtro pré autorização da classe SecurityConfig
        String path = req.getRequestURI();
        return path.startsWith("/auth/") || path.startsWith("/api/empresa");
    }


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain //objeto usado para deixar a requisição http passar pelo filtro da classe
    ) throws ServletException, java.io.IOException { //

        // A requisição guarda o token nessa parte chamada "Authorization". Pegamos de lá por isso:
        final String authHeader = request.getHeader("Authorization");

        // Se não houver header e esse "Bearer" (que significa que tem um token aqui e nao outro tipo de autorização), quer dizer que não há token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);  // passa adiante, pois não há token
            return;
        }

        //Extrai o token (remove o "Bearer ")
        final String jwt = authHeader.substring(7);

        //Extrai o username (email) do token
        final String username = jwtService.extractUsername(jwt);

        //Se tem username e o usuário ainda não está autenticado
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 6. Carrega o usuário do banco e armazena no objeto nativo de user do Spring Security que criamos
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            //Valida o token contra esse usuário
            if (jwtService.isTokenValid(jwt, userDetails)) {

                //Cria autenticação e passa nivel de user. Esse null é de senha que não precisamos
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );
                //mds... mt classe
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // precisamos disso aqui pra n ficar preso aqui e poder ir pros controller ou outra desgraça de filtro
        filterChain.doFilter(request, response);
    }
}