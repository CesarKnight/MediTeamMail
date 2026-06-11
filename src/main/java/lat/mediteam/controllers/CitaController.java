package lat.mediteam.controllers;

import java.util.List;
import java.util.Optional;

import lat.mediteam.commands.CommandResponse;
import lat.mediteam.models.Cita;
import lat.mediteam.services.CitaService;

public class CitaController {

    private CitaService service;

    public CitaController(CitaService service) {
        this.service = service;
    }

    public CommandResponse crearCita(Long pacienteId, Long medicoId, Long servicioId,
                                     String fecha, String horaInicio, String horaFin,
                                     String motivo) {
        try {
            Cita nueva = service.crear(pacienteId, medicoId, servicioId,
                                       fecha, horaInicio, horaFin, motivo);
            return new CommandResponse(true,
                "Cita programada #" + nueva.getId()
                + " | Fecha: " + nueva.getFecha()
                + " | Horario: " + nueva.getHoraInicio() + " - " + nueva.getHoraFin()
                + " | Estado: " + nueva.getEstado());
        } catch (Exception e) {
            return new CommandResponse(false, "Error al crear cita: " + e.getMessage());
        }
    }

    public CommandResponse obtenerCita(Long id) {
        try {
            Optional<Cita> cita = service.obtenerPorId(id);
            if (cita.isPresent()) {
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
            } else {
                return new CommandResponse(false, "Cita no encontrada con id: " + id);
            }
        } catch (Exception e) {
            return new CommandResponse(false, "Error al obtener cita: " + e.getMessage());
        }
    }

    public CommandResponse listarCitas() {
        try {
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

        } catch (Exception e) {
            return new CommandResponse(false, "Error al listar citas: " + e.getMessage());
        }
    }

    public CommandResponse listarPorPaciente(Long pacienteId) {
        try {
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

        } catch (Exception e) {
            return new CommandResponse(false, "Error al listar citas: " + e.getMessage());
        }
    }

    public CommandResponse listarPorMedico(Long medicoId) {
        try {
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

        } catch (Exception e) {
            return new CommandResponse(false, "Error al listar citas: " + e.getMessage());
        }
    }

    public CommandResponse reprogramarCita(Long id, String nuevaFecha,
                                           String nuevaHoraInicio, String nuevaHoraFin) {
        try {
            Cita reprogramada = service.reprogramar(id, nuevaFecha, nuevaHoraInicio, nuevaHoraFin);
            return new CommandResponse(true,
                "Cita reprogramada #" + reprogramada.getId()
                + " | Nueva fecha: " + reprogramada.getFecha()
                + " | Nuevo horario: " + reprogramada.getHoraInicio()
                + " - " + reprogramada.getHoraFin()
                + " | Estado: " + reprogramada.getEstado());
        } catch (Exception e) {
            return new CommandResponse(false, "Error al reprogramar cita: " + e.getMessage());
        }
    }

    public CommandResponse cancelarCita(Long id, String motivo) {
        try {
            Cita cancelada = service.cancelar(id, motivo);
            return new CommandResponse(true,
                "Cita cancelada #" + cancelada.getId()
                + " | Motivo: " + (cancelada.getMotivo() != null ? cancelada.getMotivo() : "-"));
        } catch (Exception e) {
            return new CommandResponse(false, "Error al cancelar cita: " + e.getMessage());
        }
    }
}