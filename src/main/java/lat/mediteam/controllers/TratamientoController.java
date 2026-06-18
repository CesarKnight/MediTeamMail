package lat.mediteam.controllers;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import lat.mediteam.commands.CommandResponse;
import lat.mediteam.core.AppContext;
import lat.mediteam.core.Session;
import lat.mediteam.models.Tratamiento;
import lat.mediteam.services.TratamientoService;

public class TratamientoController {

    private AppContext ctx;
    private Session session;
    private TratamientoService service;

    public TratamientoController(
            AppContext ctx,
            Session session,
            TratamientoService service) {

        this.ctx = ctx;
        this.session = session;
        this.service = service;
    }

    public CommandResponse crearTratamiento(
            Long historiaId,
            Long pacienteId,
            String tratamiento) {

        Tratamiento nuevo = service.crear(
                historiaId,
                pacienteId,
                tratamiento);

        return new CommandResponse(
                true,
                "Tratamiento creado con id: "
                        + nuevo.getId());
    }

    public CommandResponse obtenerTratamiento(Long id) {

        Optional<Tratamiento> t = service.obtenerPorId(id);

        if (t.isEmpty()) {
            throw new EntityNotFoundException(
                    "Tratamiento no encontrado: " + id);
        }

        Tratamiento tratamiento = t.get();

        return new CommandResponse(
                true,
                tratamiento.getId()
                        + " - Historia ID: "
                        + tratamiento.getHistoria().getId()
                        + " - Tratamiento: "
                        + tratamiento.getTratamiento());
    }

    public CommandResponse listarTratamientos() {

        List<Tratamiento> tratamientos = service.listar();

        if (tratamientos.isEmpty()) {
            return new CommandResponse(
                    true,
                    "No hay tratamientos");
        }

        StringBuilder resultado = new StringBuilder();

        for (Tratamiento t : tratamientos) {

            if (resultado.length() > 0) {
                resultado.append(
                        System.lineSeparator());
            }

            resultado
                    .append(t.getId())
                    .append(" - Historia ID: ")
                    .append(t.getHistoria().getId())
                    .append(" - Tratamiento: ")
                    .append(t.getTratamiento());
        }

        return new CommandResponse(
                true,
                resultado.toString());
    }

    public CommandResponse editarTratamiento(
            Long id,
            String tratamiento) {

        Tratamiento actualizado = service.actualizar(
                id,
                tratamiento);

        return new CommandResponse(
                true,
                "Tratamiento actualizado con id: "
                        + actualizado.getId());
    }

    public CommandResponse eliminarTratamiento(Long id) {

        service.eliminar(id);

        return new CommandResponse(
                true,
                "Tratamiento eliminado");
    }
}