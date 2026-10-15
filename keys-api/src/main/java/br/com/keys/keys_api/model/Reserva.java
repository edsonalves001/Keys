package br.com.keys.keys_api.model;

import br.com.keys.keys_api.enums.StatusSala;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_reserva")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cd_reserva;
    @Column(nullable = false)
    private LocalDateTime dh_inicio_reserva;
    @Column(nullable = false)
    private LocalDateTime dh_fim_reserva;
    @Column(length = 255)
    private String ds_reserva;
    @ManyToOne
    @JoinColumn(name = "cd_usuario")
    private Usuario proprietario;
    @ManyToOne
    @JoinColumn(name = "cd_sala")
    private Sala sala;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusSala nm_status_sala;

    public Reserva() {}

    public Integer getCd_reserva() {
        return cd_reserva;
    }

    public LocalDateTime getDh_inicio_reserva() {
        return dh_inicio_reserva;
    }

    public LocalDateTime getDh_fim_reserva() {
        return dh_fim_reserva;
    }

    public String getDs_reserva() {
        return ds_reserva;
    }

    public Usuario getProprietario() {
        return proprietario;
    }

    public Sala getSala() {
        return sala;
    }

    public StatusSala getNm_status_sala() {
        return nm_status_sala;
    }

    public void setDh_inicio_reserva(LocalDateTime dh_inicio_reserva) {
        this.dh_inicio_reserva = dh_inicio_reserva;
    }

    public void setDh_fim_reserva(LocalDateTime dh_fim_reserva) {
        this.dh_fim_reserva = dh_fim_reserva;
    }

    public void setDs_reserva(String ds_reserva) {
        this.ds_reserva = ds_reserva;
    }

    public void setProprietario(Usuario proprietario) {
        this.proprietario = proprietario;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    public void setNm_status_sala(StatusSala nm_status_sala) {
        this.nm_status_sala = nm_status_sala;
    }
}
