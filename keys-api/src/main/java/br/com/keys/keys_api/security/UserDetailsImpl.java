package br.com.keys.keys_api.security;

import br.com.keys.keys_api.model.entity.UsuarioEntity;
import br.com.keys.keys_api.model.entity.UsuarioEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserDetailsImpl implements UserDetails {

    private final UsuarioEntity usuario;

    public UserDetailsImpl(UsuarioEntity usuario) {
        this.usuario = usuario;
    }

    @Override
    public String getUsername() {
        return usuario.getNmEmailUsuario(); //no nosso caso, o email vai ser usado para identificar o user
    }

    @Override
    public String getPassword() {
        return usuario.getNmSenhaUsuario(); //precisa da senha tmb para verificar e tals
    }

    //Codigo que faz com que o admin seja necessário vai ficar comentado por enquanto
    @Override
    public Collection<?extends GrantedAuthority> getAuthorities() { //retorno de uma lista de um objeto que representa o nível de permissão
        if(usuario.getChefe().getCdUsuario() == null) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else { //quem tem cd_admin foi criado por admin
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }
    }
    //opcionais (funcionalidades extras):
    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
