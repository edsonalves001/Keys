package br.com.keys.keys_api.model.entity;

import br.com.keys.keys_api.enums.StatusSala;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_reserva")
@Getter
@Setter

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
}
