package lat.mediteam.controllers;

import lat.mediteam.commands.CommandResponse;
import lat.mediteam.models.Diagnostico;
import lat.mediteam.services.DiagnosticoService;

import java.util.List;
import java.util.Optional;

public class DiagnosticoController {

    private DiagnosticoService service;

    public DiagnosticoController(DiagnosticoService service) {
        this.service = service;
    }

    public CommandResponse crearDiagnostico(Long historiaId, String diagnostico) {
        try {
            Diagnostico nuevo = service.crear(historiaId, diagnostico);
            return new CommandResponse(true, "Diagnostico creado con id: " + nuevo.getId());
        } catch (Exception e) {
            return new CommandResponse(false, e.getMessage());
        }
    }

    public CommandResponse obtenerDiagnostico(Long id) {
        try {
            Optional<Diagnostico> diagnostico = service.obtenerPorId(id);
            if (diagnostico.isPresent()) {
                Diagnostico d = diagnostico.get();
                return new CommandResponse(true,
                    "Diagnostico #" + d.getId() +
                    " - Historia ID: " + d.getHistoria().getId() +
                    " - Diagnostico: " + d.getDiagnostico());
            } else {
                return new CommandResponse(false, "Diagnostico no encontrado");
            }
        } catch (Exception e) {
            return new CommandResponse(false, e.getMessage());
        }
    }

    public CommandResponse listarDiagnosticos() {
        try {
            List<Diagnostico> diagnosticos = service.listar();
            if (diagnosticos.isEmpty()) {
                return new CommandResponse(true, "No hay diagnosticos");
            }

            StringBuilder resultado = new StringBuilder();
            for (Diagnostico d : diagnosticos) {
                if (resultado.length() > 0) {
                    resultado.append(System.lineSeparator());
                }
                resultado.append(d.getId()).append(" - ")
                    .append("Historia ID: ").append(d.getHistoria().getId())
                    .append(" - Diagnostico: ").append(d.getDiagnostico());
            }

            return new CommandResponse(true, resultado.toString());
        } catch (Exception e) {
            return new CommandResponse(false, e.getMessage());
        }
    }

    public CommandResponse editarDiagnostico(Long id, String diagnostico) {
        try {
            Diagnostico actualizado = service.actualizar(id, diagnostico);
            return new CommandResponse(true, "Diagnostico actualizado con id: " + actualizado.getId());
        } catch (Exception e) {
            return new CommandResponse(false, e.getMessage());
        }
    }

    public CommandResponse eliminarDiagnostico(Long id) {
        try {
            boolean ok = service.eliminar(id);
            return ok
                ? new CommandResponse(true, "Diagnostico eliminado")
                : new CommandResponse(false, "Diagnostico no encontrado");
        } catch (Exception e) {
            return new CommandResponse(false, e.getMessage());
        }
    }
}