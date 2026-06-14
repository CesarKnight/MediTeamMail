package lat.mediteam.controllers;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import lat.mediteam.commands.CommandResponse;
import lat.mediteam.core.AppContext;
import lat.mediteam.core.Session;
import lat.mediteam.models.Consulta;
import lat.mediteam.services.ConsultaService;

public class ConsultaController {

    private AppContext ctx;
    private Session session;
    private ConsultaService service;

    public ConsultaController(
            AppContext ctx,
            Session session,
            ConsultaService service) {

        this.ctx = ctx;
        this.session = session;
        this.service = service;
    }

    public CommandResponse crearConsulta(
            Long historiaId,
            String descripcion) {

        Consulta nueva =
            service.crear(
                historiaId,
                descripcion
            );

        return new CommandResponse(
            true,
            "Consulta creada con id: "
                + nueva.getId()
        );
    }

    public CommandResponse obtenerConsulta(Long id) {

        Optional<Consulta> consulta =
            service.obtenerPorId(id);

        if (consulta.isEmpty()) {
            throw new EntityNotFoundException(
                "Consulta no encontrada: " + id
            );
        }

        Consulta c = consulta.get();

        return new CommandResponse(
            true,
            "Consulta #" + c.getId()
                + " - Fecha: " + c.getFechaCreacion()
                + " - Historia ID: " + c.getHistoria().getId()
                + " - Descripcion: " + c.getDescripcion()
        );
    }

    public CommandResponse listarConsultas() {

        List<Consulta> consultas =
            service.listar();

        if (consultas.isEmpty()) {
            return new CommandResponse(
                true,
                "No hay consultas"
            );
        }

        StringBuilder resultado =
            new StringBuilder();

        for (Consulta c : consultas) {

            if (resultado.length() > 0) {
                resultado.append(
                    System.lineSeparator()
                );
            }

            resultado
                .append(c.getId())
                .append(" - Fecha: ")
                .append(c.getFechaCreacion())
                .append(" - Historia ID: ")
                .append(c.getHistoria().getId())
                .append(" - Descripcion: ")
                .append(c.getDescripcion());
        }

        return new CommandResponse(
            true,
            resultado.toString()
        );
    }

    public CommandResponse editarConsulta(
            Long id,
            String descripcion) {

        Consulta actualizada =
            service.actualizar(
                id,
                descripcion
            );

        return new CommandResponse(
            true,
            "Consulta actualizada con id: "
                + actualizada.getId()
        );
    }

    public CommandResponse eliminarConsulta(Long id) {

        service.eliminar(id);

        return new CommandResponse(
            true,
            "Consulta eliminada"
        );
    }
}