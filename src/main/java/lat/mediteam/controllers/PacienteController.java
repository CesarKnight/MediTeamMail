package lat.mediteam.controllers;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import lat.mediteam.commands.CommandResponse;
import lat.mediteam.core.AppContext;
import lat.mediteam.core.Session;
import lat.mediteam.models.Paciente;
import lat.mediteam.services.PacienteService;

public class PacienteController {

    private AppContext ctx;
    private Session session;
    private PacienteService service;

    public PacienteController(AppContext ctx, Session session, PacienteService service) {
        this.ctx = ctx;
        this.session = session;
        this.service = service;
    }

    public CommandResponse crearPaciente(Long usuarioId, String nombre, String apellido,
                                         String ci, String telefono, String email,
                                         String fechaNacimiento) {
        Paciente nuevo = service.crear(usuarioId, nombre, apellido, ci, telefono, email, fechaNacimiento);
        return new CommandResponse(true,
            "Paciente creado: " + nuevo.getNombre() + " " + nuevo.getApellido()
            + " | CI: " + nuevo.getCi());
    }

    public CommandResponse obtenerPaciente(Long id) {
        Optional<Paciente> paciente = service.obtenerPorId(id);
        if (paciente.isEmpty()) {
            throw new EntityNotFoundException("Paciente no encontrado: " + id);
        }

        Paciente p = paciente.get();
        return new CommandResponse(true,
            "Paciente #" + p.getId()
            + " | Nombre: " + p.getNombre() + " " + p.getApellido()
            + " | CI: " + p.getCi()
            + " | Tel: " + p.getTelefono()
            + " | Email: " + p.getEmail()
            + " | Nacimiento: " + p.getFechaNacimiento());
    }

    public CommandResponse listarPacientes() {
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
    }

    public CommandResponse editarPaciente(Long id, String nombre, String apellido,
                                          String telefono, String email) {
        Paciente actualizado = service.actualizar(id, nombre, apellido, telefono, email);
        return new CommandResponse(true,
            "Paciente actualizado: " + actualizado.getNombre() + " " + actualizado.getApellido());
    }

    public CommandResponse eliminarPaciente(Long id) {
        service.eliminar(id);
        return new CommandResponse(true, "Paciente eliminado correctamente.");
    }
}