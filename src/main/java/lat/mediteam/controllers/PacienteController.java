package lat.mediteam.controllers;

import java.util.List;
import java.util.Optional;

import lat.mediteam.commands.CommandResponse;
import lat.mediteam.models.Paciente;
import lat.mediteam.services.PacienteService;

public class PacienteController {

    private PacienteService service;

    public PacienteController(PacienteService service) {
        this.service = service;
    }

    public CommandResponse crearPaciente(Long usuarioId, String nombre, String apellido,
                                         String ci, String telefono, String email,
                                         String fechaNacimiento) {
        try {
            Paciente nuevo = service.crear(usuarioId, nombre, apellido,
                                           ci, telefono, email, fechaNacimiento);
            return new CommandResponse(true,
                "Paciente creado: " + nuevo.getNombre() + " " + nuevo.getApellido()
                + " | CI: " + nuevo.getCi());
        } catch (Exception e) {
            return new CommandResponse(false, "Error al crear paciente: " + e.getMessage());
        }
    }

    public CommandResponse obtenerPaciente(Long id) {
        try {
            Optional<Paciente> paciente = service.obtenerPorId(id);
            if (paciente.isPresent()) {
                Paciente p = paciente.get();
                return new CommandResponse(true,
                    "Paciente #" + p.getId()
                    + " | Nombre: " + p.getNombre() + " " + p.getApellido()
                    + " | CI: " + p.getCi()
                    + " | Tel: " + p.getTelefono()
                    + " | Email: " + p.getEmail()
                    + " | Nacimiento: " + p.getFechaNacimiento());
            } else {
                return new CommandResponse(false, "Paciente no encontrado con id: " + id);
            }
        } catch (Exception e) {
            return new CommandResponse(false, "Error al obtener paciente: " + e.getMessage());
        }
    }

    public CommandResponse listarPacientes() {
        try {
            List<Paciente> pacientes = service.listar();
            if (pacientes.isEmpty()) {
                return new CommandResponse(true, "No hay pacientes registrados.");
            }

            StringBuilder resultado = new StringBuilder();
            resultado.append("=== Lista de Pacientes ===\n");
            for (Paciente p : pacientes) {
                if (resultado.length() > 0) resultado.append(System.lineSeparator());
                resultado.append(p.getId()).append(" | ")
                    .append(p.getNombre()).append(" ").append(p.getApellido())
                    .append(" | CI: ").append(p.getCi())
                    .append(" | Tel: ").append(p.getTelefono() != null ? p.getTelefono() : "-")
                    .append(" | Email: ").append(p.getEmail() != null ? p.getEmail() : "-");
            }
            return new CommandResponse(true, resultado.toString());

        } catch (Exception e) {
            return new CommandResponse(false, "Error al listar pacientes: " + e.getMessage());
        }
    }

    public CommandResponse editarPaciente(Long id, String nombre, String apellido,
                                          String telefono, String email) {
        try {
            Paciente actualizado = service.actualizar(id, nombre, apellido, telefono, email);
            return new CommandResponse(true,
                "Paciente actualizado: " + actualizado.getNombre() + " " + actualizado.getApellido());
        } catch (Exception e) {
            return new CommandResponse(false, "Error al editar paciente: " + e.getMessage());
        }
    }

    public CommandResponse eliminarPaciente(Long id) {
        try {
            boolean ok = service.eliminar(id);
            return ok
                ? new CommandResponse(true, "Paciente eliminado correctamente.")
                : new CommandResponse(false, "No se encontró paciente con id: " + id);
        } catch (Exception e) {
            return new CommandResponse(false, "Error al eliminar paciente: " + e.getMessage());
        }
    }
}