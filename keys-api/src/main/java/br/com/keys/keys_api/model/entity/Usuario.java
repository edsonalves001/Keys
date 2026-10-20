package br.com.keys.keys_api.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "tb_usuario")
public class Usuario implements Persistable<String> {
    @Id
    private String cd_usuario;
    @Transient
    private boolean isNew = true;

    @Column(nullable = false, length = 100)
    private String nm_usuario;
    @Column(nullable = false, length = 100, unique = true)
    private String nm_email_usuario;
    @Column(length = 255)
    private String nm_link_imagem_usuario;
    @Column(length = 255, nullable = false)
    private String nm_senha_usuario;
    @ManyToOne
    @JoinColumn(name = "cd_empresa")
    private Empresa empresa;

    //relacionamento recursivo:
    @ManyToOne
    @JoinColumn(name = "cd_admin")
    @JsonBackReference //não serializa esse lado, evitando serializar infinitamente
    private Usuario chefe;

    @Column(name = "cd_admin", insertable = false, updatable = false)
    private String cd_Admin;  //pra eu conseguir visualizar no getter

    @JsonManagedReference //lado serializado
    @OneToMany(mappedBy = "chefe")
    private List<Usuario> subordinados;

    //Suas reservas:
    @OneToMany(mappedBy = "proprietario")
    private List<Reserva> reservas;

    public Usuario(Optional<Usuario> byEmail) {}

    public Usuario() {}

    public String getCd_usuario() {
        return cd_usuario;
    }

    public String getNm_usuario() {
        return nm_usuario;
    }

    public String getNm_email_usuario() {
        return nm_email_usuario;
    }

    public String getNm_link_imagem_usuario() {
        return nm_link_imagem_usuario;
    }

    public String getNm_senha_usuario() {
        return nm_senha_usuario;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setNm_usuario(String nm_usuario) {
        this.nm_usuario = nm_usuario;
    }

    public void setNm_email_usuario(String nm_email_usuario) {
        this.nm_email_usuario = nm_email_usuario;
    }

    public void setNm_link_imagem_usuario(String nm_link_imagem_usuario) {
        this.nm_link_imagem_usuario = nm_link_imagem_usuario;
    }

    public void setNm_senha_usuario(String nm_senha_usuario) {
        this.nm_senha_usuario = nm_senha_usuario;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public Usuario getChefe() {
        return chefe;
    }

    public void setChefe(Usuario chefe) {
        this.chefe = chefe;
    }

    public List<Usuario> getSubordinados() {
        return subordinados;
    }

    public void setSubordinados(List<Usuario> subordinados) {
        this.subordinados = subordinados;
    }

    public String getCd_Admin() {
        return cd_Admin;
    }

    public void setCd_usuario(String cd_usuario) {
        this.cd_usuario = cd_usuario;
    }

    public void setCd_Admin(String cd_Admin) {
        this.cd_Admin = cd_Admin;
    }

    //pro Persistable:
    @Override
    public String getId() {
        return this.cd_usuario;
    }

    //com a implementação da interface, o data jpa se baseia o valor dessa props para decidir se insere ou atualiza
    @Override public boolean isNew() {
        return this.isNew;
    }

    //chamada depois de inserir e depois de buscar do banco.
    @PostPersist @PostLoad
    void markNotNew() { this.isNew = false; }

}
