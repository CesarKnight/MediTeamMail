package lat.mediteam.controllers;

import lat.mediteam.commands.CommandResponse;
import lat.mediteam.models.Consulta;
import lat.mediteam.services.ConsultaService;

import java.util.List;
import java.util.Optional;

public class ConsultaController {

    private ConsultaService service;

    public ConsultaController(ConsultaService service) {
        this.service = service;
    }

    public CommandResponse crearConsulta(Long historiaId, String descripcion) {
        try {
            Consulta nueva = service.crear(historiaId, descripcion);
            return new CommandResponse(true, "Consulta creada con id: " + nueva.getId());
        } catch (Exception e) {
            return new CommandResponse(false, e.getMessage());
        }
    }

    public CommandResponse obtenerConsulta(Long id) {
        try {
            Optional<Consulta> consulta = service.obtenerPorId(id);
            if (consulta.isPresent()) {
                Consulta c = consulta.get();
                return new CommandResponse(true,
                    "Consulta #" + c.getId() +
                    " - Fecha: " + c.getFechaCreacion() +
                    " - Historia ID: " + c.getHistoria().getId() +
                    " - Descripcion: " + c.getDescripcion());
            } else {
                return new CommandResponse(false, "Consulta no encontrada");
            }
        } catch (Exception e) {
            return new CommandResponse(false, e.getMessage());
        }
    }

    public CommandResponse listarConsultas() {
        try {
            List<Consulta> consultas = service.listar();
            if (consultas.isEmpty()) {
                return new CommandResponse(true, "No hay consultas");
            }

            StringBuilder resultado = new StringBuilder();
            for (Consulta c : consultas) {
                if (resultado.length() > 0) {
                    resultado.append(System.lineSeparator());
                }
                resultado.append(c.getId()).append(" - ")
                    .append("Fecha: ").append(c.getFechaCreacion())
                    .append(" - Historia ID: ").append(c.getHistoria().getId())
                    .append(" - Descripcion: ").append(c.getDescripcion());
            }

            return new CommandResponse(true, resultado.toString());
        } catch (Exception e) {
            return new CommandResponse(false, e.getMessage());
        }
    }

    public CommandResponse editarConsulta(Long id, String descripcion) {
        try {
            Consulta actualizada = service.actualizar(id, descripcion);
            return new CommandResponse(true, "Consulta actualizada con id: " + actualizada.getId());
        } catch (Exception e) {
            return new CommandResponse(false, e.getMessage());
        }
    }

    public CommandResponse eliminarConsulta(Long id) {
        try {
            boolean ok = service.eliminar(id);
            return ok
                ? new CommandResponse(true, "Consulta eliminada")
                : new CommandResponse(false, "Consulta no encontrada");
        } catch (Exception e) {
            return new CommandResponse(false, e.getMessage());
        }
    }
}