package br.com.keys.keys_api.security;

import br.com.keys.keys_api.model.entity.Usuario;
import br.com.keys.keys_api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException { //cria o objeto de user usado no Spring Security
        Usuario usuario = usuarioRepository.findBynmEmailUsuario(username).orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado!"));

        return new UserDetailsImpl(usuario);
    }
}
