package lat.mediteam.controllers;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import lat.mediteam.commands.CommandResponse;
import lat.mediteam.core.AppContext;
import lat.mediteam.core.Session;
import lat.mediteam.models.Medico;
import lat.mediteam.services.MedicoService;

public class MedicoController {

    private AppContext ctx;
    private Session session;
    private MedicoService service;

    public MedicoController(AppContext ctx, Session session, MedicoService service) {
        this.ctx = ctx;
        this.session = session;
        this.service = service;
    }

    public CommandResponse crearMedico(Long usuarioId, String nombre, String apellido,
                                       String ci, String especialidad, String fechaNacimiento) {
        Medico nuevo = service.crear(usuarioId, nombre, apellido, ci, especialidad, fechaNacimiento);
        return new CommandResponse(true, "Médico creado: " + nuevo.getNombre() + " " + nuevo.getApellido());
    }

    public CommandResponse obtenerMedico(Long id) {
        Optional<Medico> medico = service.obtenerPorId(id);
        if (medico.isEmpty()) {
            throw new EntityNotFoundException("Médico no encontrado: " + id);
        }

        Medico m = medico.get();
        return new CommandResponse(true,
            "Médico: " + m.getNombre() + " " + m.getApellido()
            + " - Especialidad: " + m.getEspecialidad()
            + " - " + (m.getUsuario() != null ? m.getUsuario().getEmail() : "(sin usuario)"));
    }

    public CommandResponse listarMedicos() {
        List<Medico> medicos = service.listar();
        if (medicos.isEmpty()) {
            return new CommandResponse(true, "No hay médicos");
        }

        StringBuilder resultado = new StringBuilder();
        for (Medico m : medicos) {
            if (resultado.length() > 0) {
                resultado.append(System.lineSeparator());
            }
            resultado.append(m.getId()).append(" - ")
                .append(m.getNombre()).append(" ").append(m.getApellido())
                .append(" - CI: ").append(m.getCi())
                .append(" - Esp: ").append(m.getEspecialidad());
        }

        return new CommandResponse(true, resultado.toString());
    }

    public CommandResponse editarMedico(Long id, String nombre, String apellido,
                                        String ci, String especialidad, String fechaNacimiento) {
        Medico actualizado = service.actualizar(id, nombre, apellido, ci, especialidad, fechaNacimiento);
        return new CommandResponse(true, "Médico actualizado: " + actualizado.getNombre() + " " + actualizado.getApellido());
    }

    public CommandResponse eliminarMedico(Long id) {
        service.eliminar(id);
        return new CommandResponse(true, "Médico eliminado");
    }
}