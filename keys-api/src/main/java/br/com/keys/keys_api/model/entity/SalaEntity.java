package br.com.keys.keys_api.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import br.com.keys.keys_api.enums.StatusSalaEnum;

@Entity
@Table(name = "tb_sala")
@Getter
@Setter
@NoArgsConstructor 
@AllArgsConstructor 
@Builder 
public class SalaEntity {
    @Id
    @Column(name = "cd_sala")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cd;
    @Column(name = "nm_sala",nullable = false, length = 80)
    private String nome;
    @Column(name = "ds_sala",length = 255)
    private String descricao;
    @Enumerated(EnumType.STRING) //Você esqueceu disso, Igor.
    @Column(name = "ic_disponibilidade_sala",nullable = false)
    private StatusSalaEnum statusSala;

    //reservas dessa sala:
    @OneToMany(mappedBy = "salaEntity")
    @JsonManagedReference("sala-reserva")
    private List<ReservaEntity> reservaEntities;

    //Construtor que recebe somente os dados inseridos pelo administrador ao criar a sala
    public SalaEntity(String nm_sala, String ds_sala) {
        this.nome = nm_sala;
        this.descricao = ds_sala;
        this.statusSala = StatusSalaEnum.DISPONIVEL;
    }
}
