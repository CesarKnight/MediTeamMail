package lat.mediteam.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "consulta")
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Getter
    @Setter
    private String descripcion;

    @Column(nullable = false)
    @Getter
    @Setter
    private String fechaCreacion;

    @ManyToOne
    @JoinColumn(name = "historia_id", nullable = false)
    @Getter
    @Setter
    private HistoriaClinica historia;

    protected Consulta() {}

    public Consulta(String descripcion, HistoriaClinica historia) {
        this.descripcion = descripcion;
        this.fechaCreacion = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.historia = historia;
    }
}