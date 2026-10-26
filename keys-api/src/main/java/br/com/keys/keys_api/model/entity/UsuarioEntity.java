package br.com.keys.keys_api.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import org.springframework.data.domain.Persistable;

import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "tb_usuario")
@Getter 
@Setter 
public class UsuarioEntity implements Persistable<String> {
    @Id
    @Column(name = "cdusuario")
    private String cdUsuario;
    @Transient
    private boolean isNew = true;

    @Column(name = "nm_usuario", nullable = false, length = 100)
    private String nome;
    @Column(name = "nm_email_usuario", nullable = false, length = 100, unique = true)
    private String email;
    @Column(name = "nm_link_imagem_usuario", length = 255)
    private String imagem;
    @Column(name = "nm_senha_usuario", length = 255, nullable = false)
    private String senha;
    @ManyToOne
    @JoinColumn(name = "cd_empresa")
    private EmpresaEntity empresaEntity;

    //relacionamento recursivo:
    @ManyToOne
    @JoinColumn(name = "cd_admin")
    @JsonBackReference //não serializa esse lado, evitando serializar infinitamente
    private UsuarioEntity chefe;

    //Codigo que faz com que o admin seja necessário vai ficar comentado por enquanto

    //@Column(name = "cd_admin", insertable = false, updatable = false)
    //private String cdAdmin;  //pra eu conseguir visualizar no getter

    @JsonManagedReference //lado serializado
    @OneToMany(mappedBy = "chefe")
    private List<UsuarioEntity> subordinados;

    //Suas reservas:
    @OneToMany(mappedBy = "proprietario")
    @JsonManagedReference("usuario-reserva")
    private List<ReservaEntity> reservaEntities;

    public UsuarioEntity(Optional<UsuarioEntity> byEmail) {}

    public UsuarioEntity() {}

    public void setEmpresaEntity(EmpresaEntity empresaEntity) {
        this.empresaEntity = empresaEntity;
    }

    public UsuarioEntity getChefe() {
        return chefe;
    }

    public void setChefe(UsuarioEntity chefe) {
        this.chefe = chefe;
    }

    public List<UsuarioEntity> getSubordinados() {
        return subordinados;
    }

    public void setSubordinados(List<UsuarioEntity> subordinados) {
        this.subordinados = subordinados;
    }

    //pro Persistable:
    @Override
    public String getId() {
        return this.cdUsuario;
    }

    //com a implementação da interface, o data jpa se baseia o valor dessa props para decidir se insere ou atualiza
    @Override public boolean isNew() {
        return this.isNew;
    }

    //chamada depois de inserir e depois de buscar do banco.
    @PostPersist @PostLoad
    void markNotNew() { this.isNew = false; }

}
