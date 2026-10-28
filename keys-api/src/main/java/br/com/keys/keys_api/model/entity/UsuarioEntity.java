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
    @Column(name = "cd_usuario")
    private String cdUsuario;
    @Transient
    private boolean isNew = true;

    @Column(nullable = false, length = 100, name = "nm_usuario")
    private String nmUsuario;
    @Column(nullable = false, length = 100, unique = true, name = "nm_email_usuario")
    private String nmEmailUsuario;
    @Column(length = 255, name = "nm_link_imagem_usuario")
    private String nmLinkImagemUsuario;
    @Column(length = 255, nullable = false, name = "nm_senha_usuario")
    private String nmSenhaUsuario;
    @ManyToOne
    @JoinColumn(name = "cd_empresa", nullable = false)
    private EmpresaEntity empresa;

    //relacionamento recursivo:
    @ManyToOne
    @JoinColumn(name = "cd_admin")
    @JsonBackReference //não serializa esse lado, evitando serializar infinitamente
    private UsuarioEntity chefe;

    @JsonManagedReference //lado serializado
    @OneToMany(mappedBy = "chefe")
    private List<UsuarioEntity> subordinados;

    //Suas reservas:
    @OneToMany(mappedBy = "proprietario")
    private List<ReservaEntity> reservas;

    public UsuarioEntity(Optional<UsuarioEntity> byEmail) {}

    public UsuarioEntity() {}

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
