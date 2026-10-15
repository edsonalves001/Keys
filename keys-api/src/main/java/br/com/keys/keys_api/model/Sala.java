package br.com.keys.keys_api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "tb_sala")
@Getter
@Setter
public class Sala {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cd_sala;
    @Column(nullable = false, length = 80)
    private String nm_sala;
    @Column(length = 255)
    private String ds_sala;
    @Column(nullable = false)
    private boolean ic_disponibilidade_sala;

    //reservas dessa sala:
    @OneToMany(mappedBy = "sala")
    private List<Reserva> reservas;


    public Sala() {
    }

    public Sala(String ds_sala, String nm_sala, int cd_sala, Boolean ic_disponibilidade_sala) {
        this.ds_sala = ds_sala;
        this.nm_sala = nm_sala;
        this.cd_sala = cd_sala;
        this.ic_disponibilidade_sala = ic_disponibilidade_sala;
    }
}
