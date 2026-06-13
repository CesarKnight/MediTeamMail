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
@Getter
@Setter
public class Paciente {
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
    private String telefono;

    @Column
    @Getter
    @Setter
    private String email;

    @Column
    @Getter
    @Setter
    private String fechaNacimiento;

    protected Paciente() {}

    public Paciente(String nombre, String apellido, String ci,
                    String telefono, String email,
                    String fechaNacimiento, Usuario usuario) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.ci = ci;
        this.telefono = telefono;
        this.email = email;
        this.fechaNacimiento = fechaNacimiento;
        this.usuario = usuario;
    }
}