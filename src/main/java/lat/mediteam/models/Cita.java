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
import lombok.Getter;
import lombok.Setter;
import lat.mediteam.enums.CitaEstado;

@Entity
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    @Getter
    @Setter
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id", nullable = false)
    @Getter
    @Setter
    private Medico medico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servicio_id", nullable = false)
    @Getter
    @Setter
    private Servicio servicio;

    @Column(nullable = false)
    @Getter
    @Setter
    private String fecha;

    @Column(nullable = false)
    @Getter
    @Setter
    private String horaInicio;

    @Column(nullable = false)
    @Getter
    @Setter
    private String horaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Getter
    @Setter
    private CitaEstado estado;

    @Column
    @Getter
    @Setter
    private String motivo;

    protected Cita() {}

    public Cita(Paciente paciente, Medico medico, Servicio servicio,
                String fecha, String horaInicio, String horaFin,
                String motivo) {
        this.paciente = paciente;
        this.medico = medico;
        this.servicio = servicio;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.motivo = motivo;
        this.estado = CitaEstado.PROGRAMADA;
    }
}