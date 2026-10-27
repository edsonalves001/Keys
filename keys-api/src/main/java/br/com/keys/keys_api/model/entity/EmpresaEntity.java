package br.com.keys.keys_api.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CNPJ;
import org.springframework.data.domain.Persistable;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name="tb_empresa")
@Getter
@Setter
public class EmpresaEntity implements Persistable<String> {

    @Id //inserir manualmente o cnpj no form lá, lembra?
    @CNPJ(format = CNPJ.Format.ALPHANUMERIC)
    private String cd_cnpj_empresa;
    @Column(nullable = false, length = 255)
    private String nm_empresa;

    @Transient //faz com que esse atributo n seja salvo no banco
    private boolean isNew = true; //esse atributo é usado pelo data jpa para verificar se essa entidade está sendo croada (insert), ou
    // apenas atualizada(), isso porque o JPA define se insere ou atualiza por se o id for nulo. No nosso caso, ele n vai ser e ele vai
    // achar que precisa de um update, contudo vai dar erro por não existir

    @OneToMany(mappedBy = "empresa")
    private List<UsuarioEntity> funcionarios;

    public EmpresaEntity() {}

    public EmpresaEntity(String nm_empresa, String cd_cnpj_empresa) {
        this.nm_empresa = nm_empresa;
        this.cd_cnpj_empresa = cd_cnpj_empresa;
        this.funcionarios = new ArrayList<>();
    }

    @Override //preciso sobrescrever pra funcionar
    public String getId() {
        return this.cd_cnpj_empresa;
    }

    //com a implementação da interface, o data jpa se baseia o valor dessa props para decidir se insere ou atualiza
    @Override public boolean isNew() {
        return this.isNew;
    }

    //chamada depois de inserir e depois de buscar do banco.
    @PostPersist @PostLoad
    void markNotNew() { this.isNew = false; }
}
