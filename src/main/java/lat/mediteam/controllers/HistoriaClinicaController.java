package lat.mediteam.controllers;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import lat.mediteam.commands.CommandResponse;
import lat.mediteam.core.AppContext;
import lat.mediteam.core.Session;
import lat.mediteam.enums.HistoriaClinicaEstado;
import lat.mediteam.enums.HistoriaClinicaTipo;
import lat.mediteam.models.HistoriaClinica;
import lat.mediteam.models.Medico;
import lat.mediteam.services.HistoriaClinicaService;

public class HistoriaClinicaController {

    private AppContext ctx;
    private Session session;
    private HistoriaClinicaService service;

    public HistoriaClinicaController(
            AppContext ctx,
            Session session,
            HistoriaClinicaService service) {
        this.ctx = ctx;
        this.session = session;
        this.service = service;
    }

    public CommandResponse crearHistoria(
            Long medicoId,
            String fecha,
            HistoriaClinicaEstado estado,
            HistoriaClinicaTipo tipo) {

        HistoriaClinica historia = service.crear(
                medicoId,
                fecha,
                estado,
                tipo);

        return new CommandResponse(
                true,
                "Historia clínica creada con id: "
                        + historia.getId());
    }

    public CommandResponse obtenerHistoria(Long id) {

        Optional<HistoriaClinica> h = service.obtenerPorId(id);

        if (h.isEmpty()) {
            throw new EntityNotFoundException(
                    "Historia clínica no encontrada: " + id);
        }

        HistoriaClinica historia = h.get();

        return new CommandResponse(
                true,
                historia.getId()
                        + " - "
                        + historia.getFecha()
                        + " - "
                        + historia.getEstado()
                        + " - "
                        + historia.getTipo());
    }

    public CommandResponse listarHistorias() {

        List<HistoriaClinica> historias = service.listar();

        if (historias.isEmpty()) {
            return new CommandResponse(
                    true,
                    "No hay historias clínicas");
        }

        StringBuilder resultado = new StringBuilder();

        for (HistoriaClinica h : historias) {

            if (resultado.length() > 0) {
                resultado.append(
                        System.lineSeparator());
            }

            resultado
                    .append(h.getId())
                    .append(" - ")
                    .append(h.getFecha())
                    .append(" - ")
                    .append(h.getEstado())
                    .append(" - ")
                    .append(h.getTipo());
        }

        return new CommandResponse(
                true,
                resultado.toString());
    }

    public CommandResponse editarHistoria(
            Long id,
            String fecha,
            HistoriaClinicaEstado estado,
            HistoriaClinicaTipo tipo
        ) 
    {

        HistoriaClinica historia = service.actualizar(
            id,
            fecha,
            estado,
            tipo
        );

        return new CommandResponse(
            true,
            "Historia clínica actualizada con id: " + historia.getId()
        );
    }

    public CommandResponse eliminarHistoria(Long id) {

        service.eliminar(id);

        return new CommandResponse(
                true,
                "Historia clínica eliminada");
    }
}