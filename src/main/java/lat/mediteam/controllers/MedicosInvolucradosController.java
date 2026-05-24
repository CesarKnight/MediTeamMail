package lat.mediteam.controllers;

import lat.mediteam.commands.CommandResponse;
import lat.mediteam.services.MedicosInvolucradosService;

public class MedicosInvolucradosController {

    private MedicosInvolucradosService service;

    public MedicosInvolucradosController(MedicosInvolucradosService service) {
        this.service = service;
    }

    public CommandResponse asignarMedico(Long medicoId, Long historiaId) {
        try {
            service.asignar(medicoId, historiaId);
            return new CommandResponse(true, "Medico " + medicoId + " asignado a historia clinica " + historiaId);
        } catch (Exception e) {
            return new CommandResponse(false, e.getMessage());
        }
    }

    public CommandResponse removerMedico(Long medicoId, Long historiaId) {
        try {
            service.remover(medicoId, historiaId);
            return new CommandResponse(true, "Medico " + medicoId + " removido de historia clinica " + historiaId);
        } catch (Exception e) {
            return new CommandResponse(false, e.getMessage());
        }
    }
}