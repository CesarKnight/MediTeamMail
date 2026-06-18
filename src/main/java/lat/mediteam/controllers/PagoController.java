package lat.mediteam.controllers;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import lat.mediteam.commands.CommandResponse;
import lat.mediteam.core.AppContext;
import lat.mediteam.core.Session;
import lat.mediteam.enums.PagoEstado;
import lat.mediteam.enums.PagoTipo;
import lat.mediteam.models.Pago;
import lat.mediteam.services.PagoService;

public class PagoController {

    private AppContext ctx;
    private Session session;
    private PagoService service;

    public PagoController(AppContext ctx, Session session, PagoService service) {
        this.ctx = ctx;
        this.session = session;
        this.service = service;
    }

    public CommandResponse crearPago(Long secretariaId, Long servicioId, Long pacienteID, String fechaCreacion,
                                     Float total, PagoEstado estado) {
        Pago nuevo = service.crear(secretariaId, servicioId,pacienteID, fechaCreacion, total, estado, PagoTipo.CONTADO);
        return new CommandResponse(true,
            "Pago creado #" + nuevo.getId()
            + " | Paciente: " + nuevo.getPaciente().getNombre() + " " + nuevo.getPaciente().getApellido()
            + " | Total: " + nuevo.getTotal()
            + " | Tipo: " + nuevo.getTipo()
            + " | Estado: " + nuevo.getEstado());
    }

    public CommandResponse obtenerPago(Long id) {
        Optional<Pago> pago = service.obtenerPorId(id);
        if (pago.isEmpty()) {
            throw new EntityNotFoundException("Pago no encontrado: " + id);
        }

        Pago p = pago.get();
        return new CommandResponse(true,
            "Pago #" + p.getId()
            + " | Fecha: " + p.getFechaCreacion()
            + " | Secretaria: " + p.getSecretaria().getNombre() + " " + p.getSecretaria().getApellido()
            + " | Servicio: " + p.getServicio().getTitulo()
            + " | Total: " + p.getTotal()
            + " | Tipo: " + p.getTipo()
            + " | Estado: " + p.getEstado());
    }

    public CommandResponse listarPagos() {
        List<Pago> pagos = service.listar();
        if (pagos.isEmpty()) {
            return new CommandResponse(true, "No hay pagos registrados.");
        }

        StringBuilder resultado = new StringBuilder();
        resultado.append("=== Lista de Pagos ===\n");
        for (Pago p : pagos) {
            resultado.append(System.lineSeparator());
            resultado.append(p.getId()).append(" | ")
                .append(p.getFechaCreacion())
                .append(" | Secretaria: ").append(p.getSecretaria().getNombre())
                .append(" | Servicio: ").append(p.getServicio().getTitulo())
                .append(" | Total: ").append(p.getTotal())
                .append(" | Estado: ").append(p.getEstado());
        }
        return new CommandResponse(true, resultado.toString());
    }
}