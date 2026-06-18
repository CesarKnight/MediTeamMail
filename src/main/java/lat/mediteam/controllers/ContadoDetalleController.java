package lat.mediteam.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import lat.mediteam.commands.CommandResponse;
import lat.mediteam.core.AppContext;
import lat.mediteam.core.Session;
import lat.mediteam.enums.MetodoPago;
import lat.mediteam.exceptions.ServiceException;
import lat.mediteam.models.ContadoDetalle;
import lat.mediteam.models.Paciente;
import lat.mediteam.models.Pago;
import lat.mediteam.models.Servicio;
import lat.mediteam.models.Usuario;
import lat.mediteam.services.ContadoDetalleService;
import lat.mediteam.services.PagoService;
import lat.mediteam.services.QrPagoService;
import lat.mediteam.services.UsuarioService;

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
        if (metodoPago == MetodoPago.QR) {

            PagoService pagoService = new PagoService();
            Optional<Pago> pagoExist = pagoService.obtenerPorId(pagoId);
            if (!pagoExist.isPresent()) {
                throw new IllegalArgumentException("Pago con id " + pagoId + " no encontrado...");
            }
            Pago pago = pagoExist.get();
            Paciente paciente = pago.getPaciente();

            String nombreCompleto = paciente.getNombre() + " " + paciente.getApellido();
            String ci = paciente.getCi();
            String telefono = paciente.getTelefono();
            
            UsuarioService usuarioService = new UsuarioService();
            
            Optional<Usuario> usuarioExist = usuarioService.obtenerPorId(paciente.getId());
            if (!usuarioExist.isPresent()) {
                throw new IllegalArgumentException("usuario con id " + paciente.getId() + " no encontrado...");
            }
            Usuario usuario = usuarioExist.get();
            
            String email = usuario.getEmail();
            Servicio servicio = pago.getServicio();
            Double total = (double) pago.getTotal();
            Map<String, Object> responseJson = QrPagoService.generateQR(
                nombreCompleto,
                ci,
                telefono,
                email,
                pago.getId().toString(),
                total,
                paciente.getId().toString(),
                servicio.getTitulo()
            );

            String qrBase64 = extraerQrBase64(responseJson);
            
            ContadoDetalle nuevo = service.registrar(pagoId, metodoPago, 0f);
            return new CommandResponse(true,
                "Detalle de pago creado para pago #" + nuevo.getPago().getId()
                + " | Método: " + nuevo.getMetodoPago()
                + " | Monto a pagar: " + pago.getTotal()
                + " | Fecha: " + nuevo.getFechaDeposito() + "\n"
                + " | --------QR-------- |",
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

    // Extrae el campo "qrBase64" anidado dentro de "values" en la respuesta del procesador QR.
    // Estructura esperada: { "values": { "qrBase64": "...", ... }, ... }
    @SuppressWarnings("unchecked")
    private String extraerQrBase64(Map<String, Object> responseJson) {
        
        // Object valuesObj = responseJson.get("values");
        // if (!(valuesObj instanceof Map)) {
        //     throw new ServiceException("Respuesta inválida del procesador QR: falta el campo 'values'");
        // }

        // Map<String, Object> values = (Map<String, Object>) valuesObj;

        Object qrBase64Obj = responseJson.get("qrImage");
        if (qrBase64Obj == null) {
            throw new ServiceException("El procesador QR no devolvió un código QR (qrBase64 vacío)");
        }

        String base64Str = qrBase64Obj.toString();
        System.out.println(base64Str);
        // sanitizar saltos de linea 
        base64Str = base64Str.replace("\\/", "/");
        base64Str = base64Str.replaceAll("[^a-zA-Z0-9+/=]", "");

        return base64Str;
    }
}