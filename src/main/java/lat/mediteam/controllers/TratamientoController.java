package lat.mediteam.controllers;

import lat.mediteam.commands.CommandResponse;
import lat.mediteam.models.Tratamiento;
import lat.mediteam.services.TratamientoService;

import java.util.List;
import java.util.Optional;

public class TratamientoController {

    private TratamientoService service;

    public TratamientoController(TratamientoService service) {
        this.service = service;
    }

    public CommandResponse crearTratamiento(Long historiaId, String tratamiento) {
        try {
            Tratamiento nuevo = service.crear(historiaId, tratamiento);
            return new CommandResponse(true, "Tratamiento creado con id: " + nuevo.getId());
        } catch (Exception e) {
            return new CommandResponse(false, e.getMessage());
        }
    }

    public CommandResponse obtenerTratamiento(Long id) {
        try {
            Optional<Tratamiento> tratamiento = service.obtenerPorId(id);
            if (tratamiento.isPresent()) {
                Tratamiento t = tratamiento.get();
                return new CommandResponse(true,
                    "Tratamiento #" + t.getId() +
                    " - Historia ID: " + t.getHistoria().getId() +
                    " - Tratamiento: " + t.getTratamiento());
            } else {
                return new CommandResponse(false, "Tratamiento no encontrado");
            }
        } catch (Exception e) {
            return new CommandResponse(false, e.getMessage());
        }
    }

    public CommandResponse listarTratamientos() {
        try {
            List<Tratamiento> tratamientos = service.listar();
            if (tratamientos.isEmpty()) {
                return new CommandResponse(true, "No hay tratamientos");
            }

            StringBuilder resultado = new StringBuilder();
            for (Tratamiento t : tratamientos) {
                if (resultado.length() > 0) {
                    resultado.append(System.lineSeparator());
                }
                resultado.append(t.getId()).append(" - ")
                    .append("Historia ID: ").append(t.getHistoria().getId())
                    .append(" - Tratamiento: ").append(t.getTratamiento());
            }

            return new CommandResponse(true, resultado.toString());
        } catch (Exception e) {
            return new CommandResponse(false, e.getMessage());
        }
    }

    public CommandResponse editarTratamiento(Long id, String tratamiento) {
        try {
            Tratamiento actualizado = service.actualizar(id, tratamiento);
            return new CommandResponse(true, "Tratamiento actualizado con id: " + actualizado.getId());
        } catch (Exception e) {
            return new CommandResponse(false, e.getMessage());
        }
    }

    public CommandResponse eliminarTratamiento(Long id) {
        try {
            boolean ok = service.eliminar(id);
            return ok
                ? new CommandResponse(true, "Tratamiento eliminado")
                : new CommandResponse(false, "Tratamiento no encontrado");
        } catch (Exception e) {
            return new CommandResponse(false, e.getMessage());
        }
    }
}