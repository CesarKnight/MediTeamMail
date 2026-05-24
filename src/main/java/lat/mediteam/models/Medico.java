package lat.mediteam.models;

import java.util.ArrayList;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
public class Medico {
    @Id
    @Getter
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    @Getter
    @Setter
    private Usuario usuario;

    @Column(nullable = false)
    @Getter
    @Setter
    private String nombre;

    @Column(nullable = false)
    @Getter
    @Setter
    private String apellido;

    @Column(nullable = false, unique = true)
    @Getter
    @Setter
    private String ci;

    @Column(nullable = false)
    @Getter
    @Setter
    private String especialidad;

    @Column(name = "fecha_nacimiento")
    @Getter
    @Setter
    private String fechaNacimiento;

    // NUEVO: Relación de N a N para la tabla MedicosInvolucrados (Issue #8)
    @ManyToMany
    @JoinTable(
        name = "medicos_involucrados",
        joinColumns = @JoinColumn(name = "medico_id"),
        inverseJoinColumns = @JoinColumn(name = "historia_id")
    )
    @Getter
    @Setter
    private List<HistoriaClinica> historiasInvolucradas = new ArrayList<>();

    protected Medico() {
    }

    public Medico(String nombre, String apellido, String ci, String especialidad, String fechaNacimiento, Usuario usuario) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.ci = ci;
        this.especialidad = especialidad;
        this.fechaNacimiento = fechaNacimiento;
        this.usuario = usuario;
    }
}