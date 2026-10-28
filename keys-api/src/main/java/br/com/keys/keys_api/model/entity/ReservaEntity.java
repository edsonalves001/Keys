package br.com.keys.keys_api.model.entity;

import br.com.keys.keys_api.enums.StatusReservaEnum;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "tb_reserva")
public class ReservaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cd_reserva")
    private Integer cdreserva;
    @Column(name = "dh_inicio_reserva", nullable = false)
    private LocalDateTime inicioReserva;
    @Column(name = "dh_fim_reserva", nullable = false)
    private LocalDateTime fimReserva;
    @ManyToOne
    @JoinColumn(name = "cd_usuario")
    @JsonBackReference("usuario-reserva")
    private UsuarioEntity proprietario;
    @ManyToOne
    @JoinColumn(name = "cd_sala")
    @JsonBackReference("sala-reserva")
    private SalaEntity salaEntity;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusReservaEnum statusReserva;

    public ReservaEntity() {}

    public void setstatusReserva() {
        this.statusReserva = StatusReservaEnum.EM_USO;
    }
}
