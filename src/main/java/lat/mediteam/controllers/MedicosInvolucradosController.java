package lat.mediteam.controllers;

import lat.mediteam.commands.CommandResponse;
import lat.mediteam.core.AppContext;
import lat.mediteam.core.Session;
import lat.mediteam.services.MedicosInvolucradosService;

public class MedicosInvolucradosController {

    private AppContext ctx;
    private Session session;
    private MedicosInvolucradosService service;

    public MedicosInvolucradosController(AppContext ctx, Session session, MedicosInvolucradosService service) {
        this.ctx = ctx;
        this.session = session;
        this.service = service;
    }

    public CommandResponse asignarMedico(
            Long medicoId,
            Long historiaId) {

        service.asignar(
                medicoId,
                historiaId);

        return new CommandResponse(
                true,
                "Médico "
                        + medicoId
                        + " asignado a historia clínica "
                        + historiaId);
    }

    public CommandResponse removerMedico(
            Long medicoId,
            Long historiaId) {

        service.remover(
                medicoId,
                historiaId);

        return new CommandResponse(
                true,
                "Médico "+ medicoId + " removido de historia clínica " + historiaId
            );
    }
}