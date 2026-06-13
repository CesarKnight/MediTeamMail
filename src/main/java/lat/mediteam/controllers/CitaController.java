package lat.mediteam.controllers;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import lat.mediteam.commands.CommandResponse;
import lat.mediteam.core.AppContext;
import lat.mediteam.core.Session;
import lat.mediteam.models.Cita;
import lat.mediteam.services.CitaService;

public class CitaController {

    private AppContext ctx;
    private Session session;
    private CitaService service;

    public CitaController(AppContext ctx, Session session, CitaService service) {
        this.ctx = ctx;
        this.session = session;
        this.service = service;
    }

    public CommandResponse crearCita(Long pacienteId, Long medicoId, Long servicioId,
                                     String fecha, String horaInicio, String horaFin,
                                     String motivo) {
        Cita nueva = service.crear(pacienteId, medicoId, servicioId,
                                   fecha, horaInicio, horaFin, motivo);
        return new CommandResponse(true,
            "Cita programada #" + nueva.getId()
            + " | Fecha: " + nueva.getFecha()
            + " | Horario: " + nueva.getHoraInicio() + " - " + nueva.getHoraFin()
            + " | Estado: " + nueva.getEstado());
    }

    public CommandResponse obtenerCita(Long id) {
        Optional<Cita> cita = service.obtenerPorId(id);
        if (cita.isEmpty()) {
            throw new EntityNotFoundException("Cita no encontrada: " + id);
        }

        Cita c = cita.get();
        return new CommandResponse(true,
            "Cita #" + c.getId()
            + " | Paciente: " + c.getPaciente().getNombre() + " " + c.getPaciente().getApellido()
            + " | Medico: " + c.getMedico().getNombre() + " " + c.getMedico().getApellido()
            + " | Servicio: " + c.getServicio().getTitulo()
            + " | Fecha: " + c.getFecha()
            + " | Horario: " + c.getHoraInicio() + " - " + c.getHoraFin()
            + " | Estado: " + c.getEstado()
            + " | Motivo: " + (c.getMotivo() != null ? c.getMotivo() : "-"));
    }

    public CommandResponse listarCitas() {
        List<Cita> citas = service.listar();
        if (citas.isEmpty()) {
            return new CommandResponse(true, "No hay citas registradas.");
        }

        StringBuilder resultado = new StringBuilder();
        resultado.append("=== Lista de Citas ===\n");
        for (Cita c : citas) {
            resultado.append(System.lineSeparator());
            resultado.append(c.getId()).append(" | ")
                .append(c.getFecha())
                .append(" ").append(c.getHoraInicio())
                .append("-").append(c.getHoraFin())
                .append(" | Paciente: ").append(c.getPaciente().getNombre())
                .append(" | Medico: ").append(c.getMedico().getNombre())
                .append(" | Estado: ").append(c.getEstado());
        }
        return new CommandResponse(true, resultado.toString());
    }

    public CommandResponse listarPorPaciente(Long pacienteId) {
        List<Cita> citas = service.listarPorPaciente(pacienteId);
        if (citas.isEmpty()) {
            return new CommandResponse(true, "No hay citas para el paciente con id: " + pacienteId);
        }

        StringBuilder resultado = new StringBuilder();
        resultado.append("=== Citas del Paciente #").append(pacienteId).append(" ===\n");
        for (Cita c : citas) {
            resultado.append(System.lineSeparator());
            resultado.append(c.getId()).append(" | ")
                .append(c.getFecha())
                .append(" ").append(c.getHoraInicio())
                .append("-").append(c.getHoraFin())
                .append(" | Medico: ").append(c.getMedico().getNombre())
                .append(" | Estado: ").append(c.getEstado());
        }
        return new CommandResponse(true, resultado.toString());
    }

    public CommandResponse listarPorMedico(Long medicoId) {
        List<Cita> citas = service.listarPorMedico(medicoId);
        if (citas.isEmpty()) {
            return new CommandResponse(true, "No hay citas para el médico con id: " + medicoId);
        }

        StringBuilder resultado = new StringBuilder();
        resultado.append("=== Citas del Médico #").append(medicoId).append(" ===\n");
        for (Cita c : citas) {
            resultado.append(System.lineSeparator());
            resultado.append(c.getId()).append(" | ")
                .append(c.getFecha())
                .append(" ").append(c.getHoraInicio())
                .append("-").append(c.getHoraFin())
                .append(" | Paciente: ").append(c.getPaciente().getNombre())
                .append(" | Estado: ").append(c.getEstado());
        }
        return new CommandResponse(true, resultado.toString());
    }

    public CommandResponse reprogramarCita(Long id, String nuevaFecha,
                                           String nuevaHoraInicio, String nuevaHoraFin) {
        Cita reprogramada = service.reprogramar(id, nuevaFecha, nuevaHoraInicio, nuevaHoraFin);
        return new CommandResponse(true,
            "Cita reprogramada #" + reprogramada.getId()
            + " | Nueva fecha: " + reprogramada.getFecha()
            + " | Nuevo horario: " + reprogramada.getHoraInicio()
            + " - " + reprogramada.getHoraFin()
            + " | Estado: " + reprogramada.getEstado());
    }

    public CommandResponse cancelarCita(Long id, String motivo) {
        Cita cancelada = service.cancelar(id, motivo);
        return new CommandResponse(true,
            "Cita cancelada #" + cancelada.getId()
            + " | Motivo: " + (cancelada.getMotivo() != null ? cancelada.getMotivo() : "-"));
    }
}