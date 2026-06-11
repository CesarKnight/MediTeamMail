package lat.mediteam.models;

import java.util.ArrayList;
import java.util.List;

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

@Entity
@Getter
@Setter
public class Medico {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Usuario usuario;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false, unique = true)
    private String ci;

    @Column(nullable = false)
    private String especialidad;

    @Column
    private String telefono;

    @Column(name = "fecha_nacimiento")
    private String fechaNacimiento;

    @Column
    private String universidad;

    @ManyToMany
    @JoinTable(
        name = "medicos_involucrados",
        joinColumns = @JoinColumn(name = "medico_id"),
        inverseJoinColumns = @JoinColumn(name = "historia_id")
    )
    private List<HistoriaClinica> historiasInvolucradas = new ArrayList<>();

    protected Medico() {
    }

    // Constructor completo (con telefono y universidad)
    public Medico(String nombre, String apellido, String ci, String especialidad,
                  String telefono, String fechaNacimiento, String universidad,
                  Usuario usuario) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.ci = ci;
        this.especialidad = especialidad;
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
        this.universidad = universidad;
        this.usuario = usuario;
    }

    // Constructor compatible con MedicoService de Gabriel
    public Medico(String nombre, String apellido, String ci, String especialidad,
                  String fechaNacimiento, Usuario usuario) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.ci = ci;
        this.especialidad = especialidad;
        this.fechaNacimiento = fechaNacimiento;
        this.usuario = usuario;
    }
}