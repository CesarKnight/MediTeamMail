package lat.mediteam.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Medico {

    @Id
    @Getter
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
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

    @Column
    @Getter
    @Setter
    private String especialidad;

    @Column
    @Getter
    @Setter
    private String telefono;

    @Column
    @Getter
    @Setter
    private String fechaNacimiento;

    @Column
    @Getter
    @Setter
    private String universidad;

    protected Medico() {}

    public Medico(String nombre, String apellido, String ci,
                  String especialidad, String telefono,
                  String fechaNacimiento, String universidad,
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
}