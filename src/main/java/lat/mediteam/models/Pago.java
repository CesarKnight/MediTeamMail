package lat.mediteam.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import lat.mediteam.enums.PagoEstado;
import lat.mediteam.enums.PagoTipo;

@Entity
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Column(nullable = false)
    @Getter
    @Setter
    private String fechaCreacion;

    @Column(nullable = false)
    @Getter
    @Setter
    private Float total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Getter
    @Setter
    private PagoEstado estado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Getter
    @Setter
    private PagoTipo tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "secretaria_id", nullable = false)
    @Getter
    @Setter
    private Secretaria secretaria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    @Getter
    @Setter
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servicio_id", nullable = false)
    @Getter
    @Setter
    private Servicio servicio;

    @OneToOne(mappedBy = "pago", cascade = jakarta.persistence.CascadeType.ALL)
    @Getter
    @Setter
    private ContadoDetalle detalle;

    protected Pago() {}

    public Pago(String fechaCreacion, Float total, PagoEstado estado, PagoTipo tipo,
                Secretaria secretaria, Servicio servicio, Paciente paciente) {
        this.fechaCreacion = fechaCreacion;
        this.total = total;
        this.estado = estado;
        this.tipo = tipo;
        this.secretaria = secretaria;
        this.servicio = servicio;
        this.paciente = paciente;
    }
}