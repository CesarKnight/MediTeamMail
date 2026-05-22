package lat.mediteam.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "diagnostico")
public class Diagnostico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Getter
    @Setter
    private String diagnostico;

    @ManyToOne
    @JoinColumn(name = "historia_id", nullable = false)
    @Getter
    @Setter
    private HistoriaClinica historia;

    protected Diagnostico() {}

    public Diagnostico(String diagnostico, HistoriaClinica historia) {
        this.diagnostico = diagnostico;
        this.historia = historia;
    }
}