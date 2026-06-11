package lat.mediteam.controllers;

import java.util.List;
import java.util.Optional;

import lat.mediteam.commands.CommandResponse;
import lat.mediteam.models.Medico;
import lat.mediteam.services.MedicoService;

public class MedicoController {
    
    private MedicoService service;
    
    public MedicoController(MedicoService service){
        this.service = service;
    }

    public CommandResponse crearMedico(Long usuarioId, String nombre, String apellido, String ci, String especialidad, String fechaNacimiento) {
        try {
            Medico nuevo = service.crear(usuarioId, nombre, apellido, ci, especialidad, fechaNacimiento);
            return new CommandResponse(true, "Médico creado: " + nuevo.getNombre() + " " + nuevo.getApellido());
        } catch (Exception e) {
            return new CommandResponse(false, e.getMessage());
        }
    }

    public CommandResponse obtenerMedico(Long id) {
        try {
            Optional<Medico> medico = service.obtenerPorId(id);
            if (medico.isPresent()) {
                Medico m = medico.get();
                return new CommandResponse(true, "Médico: " + m.getNombre() + " " + m.getApellido() + " - Especialidad: " + m.getEspecialidad() + " - " + (m.getUsuario() != null ? m.getUsuario().getEmail() : "(sin usuario)"));
            } else {
                return new CommandResponse(false, "Médico no encontrado");
            }
        } catch (Exception e) {
            return new CommandResponse(false, e.getMessage());
        }
    }

    public CommandResponse listarMedicos() {
        try {
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
        } catch (Exception e) {
            return new CommandResponse(false, e.getMessage());
        }
    }

    public CommandResponse editarMedico(Long id, String nombre, String apellido, String ci, String especialidad, String fechaNacimiento) {
        try {
            Medico actualizado = service.actualizar(id, nombre, apellido, ci, especialidad, fechaNacimiento);
            return new CommandResponse(true, "Médico actualizado: " + actualizado.getNombre() + " " + actualizado.getApellido());
        } catch (Exception e) {
            return new CommandResponse(false, e.getMessage());
        }
    }

    public CommandResponse eliminarMedico(Long id) {
        try {
            boolean ok = service.eliminar(id);
            return ok
                ? new CommandResponse(true, "Médico eliminado")
                : new CommandResponse(false, "Médico no encontrado");
        } catch (Exception e) {
            return new CommandResponse(false, e.getMessage());
        }
    }
}