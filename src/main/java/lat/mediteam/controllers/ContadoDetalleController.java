package lat.mediteam.controllers;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import lat.mediteam.commands.CommandResponse;
import lat.mediteam.core.AppContext;
import lat.mediteam.core.Session;
import lat.mediteam.enums.MetodoPago;
import lat.mediteam.models.ContadoDetalle;
import lat.mediteam.services.ContadoDetalleService;
import lat.mediteam.services.QrPagoService;

public class ContadoDetalleController {

    private AppContext ctx;
    private Session session;
    private ContadoDetalleService service;

    public ContadoDetalleController(AppContext ctx, Session session, ContadoDetalleService service) {
        this.ctx = ctx;
        this.session = session;
        this.service = service;
    }

    public CommandResponse registrarDetalle(Long pagoId, MetodoPago metodoPago, Float montoRecibido) {
        if(metodoPago == MetodoPago.QR){

            String qrBase64 = QrPagoService.generateQR("Cesar Caballero", "123456789", "", null, null, 0, null, null)
            
            ContadoDetalle nuevo = service.registrar(pagoId, MetodoPago.QR, 0f);
            return new CommandResponse(true,
                "Detalle de pago creado para pago #" + nuevo.getPago().getId()
                + " | Método: " + nuevo.getMetodoPago()
                + " | Monto a pagar: " + montoRecibido
                + " | Fecha: " + nuevo.getFechaDeposito()
                + " | --------QR-------- |" ,
                qrBase64
            );
        }


        ContadoDetalle nuevo = service.registrar(pagoId, metodoPago, montoRecibido);
        return new CommandResponse(true,
            "Detalle de pago registrado para pago #" + nuevo.getPago().getId()
            + " | Método: " + nuevo.getMetodoPago()
            + " | Monto recibido: " + nuevo.getMontoRecibido()
            + " | Cambio: " + nuevo.getCambio()
            + " | Fecha: " + nuevo.getFechaDeposito());
    }

    public CommandResponse obtenerDetalle(Long id) {
        Optional<ContadoDetalle> detalle = service.obtenerPorId(id);
        if (detalle.isEmpty()) {
            throw new EntityNotFoundException("Detalle de pago no encontrado: " + id);
        }

        ContadoDetalle d = detalle.get();
        return new CommandResponse(true,
            "Detalle #" + d.getId()
            + " | Pago: " + d.getPago().getId()
            + " | Método: " + d.getMetodoPago()
            + " | Monto recibido: " + d.getMontoRecibido()
            + " | Cambio: " + d.getCambio()
            + " | Fecha depósito: " + d.getFechaDeposito());
    }

}