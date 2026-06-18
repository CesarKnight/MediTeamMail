package lat.mediteam.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tratamiento")
public class Tratamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Getter
    @Setter     
    private String tratamiento;

    @ManyToOne
    @JoinColumn(name = "historia_id", nullable = false)
    @Getter
    @Setter
    private HistoriaClinica historia;

    @ManyToOne
    @JoinColumn(name = "paciente_id", nullable = false)
    @Getter
    @Setter
    private Paciente paciente;

    protected Tratamiento() {}

    public Tratamiento(String tratamiento, HistoriaClinica historia, Paciente paciente) {
        this.tratamiento = tratamiento;
        this.historia = historia;
        this.paciente = paciente;
    }
}