package lat.mediteam.models;

import jakarta.persistence.*;
import lat.mediteam.enums.HistoriaClinicaEstado;
import lat.mediteam.enums.HistoriaClinicaTipo;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "historia_clinica")
public class HistoriaClinica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Column(nullable = false)
    @Getter
    @Setter
    private String fecha;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private HistoriaClinicaEstado estado;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private HistoriaClinicaTipo tipo;

    @ManyToMany(mappedBy = "historiasInvolucradas")
    @Getter
    @Setter
    private List<Medico> medicosInvolucrados = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "medico_creador_id", nullable = false)
    @Getter
    @Setter
    private Medico medicoCreador;

    protected HistoriaClinica() {}

    public HistoriaClinica(String fecha, HistoriaClinicaEstado estado, HistoriaClinicaTipo tipo, Medico medicoCreador) {
        this.fecha = fecha;
        this.estado = estado;
        this.tipo = tipo;
        this.medicoCreador = medicoCreador;
    }
}