package lat.mediteam.controllers;

import lat.mediteam.commands.CommandResponse;
import lat.mediteam.models.HistoriaClinica;
import lat.mediteam.models.Medico;
import lat.mediteam.services.HistoriaClinicaService;

import java.util.List;
import java.util.Optional;

public class HistoriaClinicaController {

    private HistoriaClinicaService service;

    public HistoriaClinicaController(HistoriaClinicaService service) {
        this.service = service;
    }

    public CommandResponse crearHistoria(Long medicoId, String estado, String tipo) {
        try {
            HistoriaClinica nueva = service.crear(medicoId, estado, tipo);
            return new CommandResponse(true, "Historia clinica creada con id: " + nueva.getId());
        } catch (Exception e) {
            return new CommandResponse(false, e.getMessage());
        }
    }

    public CommandResponse obtenerHistoria(Long id) {
        try {
            Optional<HistoriaClinica> historia = service.obtenerPorId(id);
            if (historia.isPresent()) {
                HistoriaClinica h = historia.get();
                StringBuilder sb = new StringBuilder();
                sb.append("=== Historia Clinica #").append(h.getId()).append(" ===\n");
                sb.append("Fecha: ").append(h.getFechaCreacion()).append("\n");
                sb.append("Estado: ").append(h.getEstado()).append("\n");
                sb.append("Tipo: ").append(h.getTipo()).append("\n");
                sb.append("Medico Creador ID: ").append(h.getMedicoCreador().getId()).append("\n");
                sb.append("Medicos Involucrados: ");
                if (h.getMedicosInvolucrados().isEmpty()) {
                    sb.append("ninguno");
                } else {
                    for (Medico m : h.getMedicosInvolucrados()) {
                        sb.append("\n  - ").append(m.getNombre()).append(" ").append(m.getApellido());
                    }
                }
                return new CommandResponse(true, sb.toString());
            } else {
                return new CommandResponse(false, "Historia clinica no encontrada");
            }
        } catch (Exception e) {
            return new CommandResponse(false, e.getMessage());
        }
    }

    public CommandResponse listarHistorias() {
        try {
            List<HistoriaClinica> historias = service.listar();
            if (historias.isEmpty()) {
                return new CommandResponse(true, "No hay historias clinicas");
            }

            StringBuilder resultado = new StringBuilder();
            for (HistoriaClinica h : historias) {
                if (resultado.length() > 0) {
                    resultado.append(System.lineSeparator());
                }
                resultado.append(h.getId()).append(" - ")
                    .append("Fecha: ").append(h.getFechaCreacion())
                    .append(" - Estado: ").append(h.getEstado())
                    .append(" - Tipo: ").append(h.getTipo())
                    .append(" - Medico ID: ").append(h.getMedicoCreador().getId());
            }

            return new CommandResponse(true, resultado.toString());
        } catch (Exception e) {
            return new CommandResponse(false, e.getMessage());
        }
    }

    public CommandResponse editarHistoria(Long id, String estado, String tipo) {
        try {
            HistoriaClinica actualizada = service.actualizar(id, estado, tipo);
            return new CommandResponse(true, "Historia clinica actualizada con id: " + actualizada.getId());
        } catch (Exception e) {
            return new CommandResponse(false, e.getMessage());
        }
    }

    public CommandResponse eliminarHistoria(Long id) {
        try {
            boolean ok = service.eliminar(id);
            return ok
                ? new CommandResponse(true, "Historia clinica eliminada")
                : new CommandResponse(false, "Historia clinica no encontrada");
        } catch (Exception e) {
            return new CommandResponse(false, e.getMessage());
        }
    }
}