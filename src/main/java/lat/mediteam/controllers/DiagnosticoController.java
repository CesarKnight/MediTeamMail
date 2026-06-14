package lat.mediteam.controllers;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import lat.mediteam.commands.CommandResponse;
import lat.mediteam.core.AppContext;
import lat.mediteam.core.Session;
import lat.mediteam.models.Diagnostico;
import lat.mediteam.services.DiagnosticoService;

public class DiagnosticoController {

    private AppContext ctx;
    private Session session;
    private DiagnosticoService service;

    public DiagnosticoController(
            AppContext ctx,
            Session session,
            DiagnosticoService service) {

        this.ctx = ctx;
        this.session = session;
        this.service = service;
    }

    public CommandResponse crearDiagnostico(
            Long historiaId,
            String diagnostico) {

        Diagnostico nuevo = service.crear(
                historiaId,
                diagnostico);

        return new CommandResponse(
                true,
                "Diagnóstico creado con id: "
                        + nuevo.getId());
    }

    public CommandResponse obtenerDiagnostico(Long id) {

        Optional<Diagnostico> d = service.obtenerPorId(id);

        if (d.isEmpty()) {
            throw new EntityNotFoundException(
                    "Diagnóstico no encontrado: " + id);
        }

        Diagnostico diagnostico = d.get();

        return new CommandResponse(
                true,
                diagnostico.getId()
                        + " - Historia ID: "
                        + diagnostico.getHistoria().getId()
                        + " - Diagnóstico: "
                        + diagnostico.getDiagnostico());
    }

    public CommandResponse listarDiagnosticos() {

        List<Diagnostico> diagnosticos = service.listar();

        if (diagnosticos.isEmpty()) {
            return new CommandResponse(
                    true,
                    "No hay diagnósticos");
        }

        StringBuilder resultado = new StringBuilder();

        for (Diagnostico d : diagnosticos) {

            if (resultado.length() > 0) {
                resultado.append(
                        System.lineSeparator());
            }

            resultado
                    .append(d.getId())
                    .append(" - Historia ID: ")
                    .append(d.getHistoria().getId())
                    .append(" - Diagnóstico: ")
                    .append(d.getDiagnostico());
        }

        return new CommandResponse(
                true,
                resultado.toString());
    }

    public CommandResponse editarDiagnostico(
            Long id,
            String diagnostico) {

        Diagnostico actualizado = service.actualizar(
                id,
                diagnostico);

        return new CommandResponse(
                true,
                "Diagnóstico actualizado con id: "
                        + actualizado.getId());
    }

    public CommandResponse eliminarDiagnostico(Long id) {

        service.eliminar(id);

        return new CommandResponse(
                true,
                "Diagnóstico eliminado");
    }
}