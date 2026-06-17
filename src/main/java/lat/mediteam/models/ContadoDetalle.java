package lat.mediteam.models;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lat.mediteam.enums.MetodoPago;
import lombok.Getter;
import lombok.Setter;

@Entity
public class ContadoDetalle {

    @Id
    @Getter
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    @Getter
    @Setter
    private Pago pago;

    @Column(nullable = false)
    @Getter
    @Setter
    private LocalDate fechaDeposito;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Getter
    @Setter
    private MetodoPago metodoPago;

    @Column(nullable = false)
    @Getter
    @Setter
    private Float montoRecibido;

    @Column(nullable = false)
    @Getter
    @Setter
    private Float cambio;

    protected ContadoDetalle() {}

    public ContadoDetalle(LocalDate fechaDeposito, MetodoPago metodoPago,
                          Float montoRecibido, Float cambio, Pago pago) {
        this.fechaDeposito = fechaDeposito;
        this.metodoPago = metodoPago;
        this.montoRecibido = montoRecibido;
        this.cambio = cambio;
        this.pago = pago;
    }
}