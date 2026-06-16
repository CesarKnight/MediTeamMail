package lat.mediteam.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lat.mediteam.enums.ServicioEstado;

@Entity
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Column(nullable = false)
    @Getter
    @Setter
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Getter
    @Setter
    private String descripcion;

    @Column(nullable = false)
    @Getter
    @Setter
    private Double precio;

    @Column
    @Getter
    @Setter
    private String duracion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Getter
    @Setter
    private ServicioEstado estado;

    protected Servicio() {}

    public Servicio(String titulo, String descripcion, Double precio,
                    String duracion, ServicioEstado estado) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.precio = precio;
        this.duracion = duracion;
        this.estado = estado;
    }
}