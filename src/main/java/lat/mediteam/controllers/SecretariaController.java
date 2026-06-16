package lat.mediteam.controllers;

import java.util.List;
import java.util.Optional;

import lat.mediteam.commands.CommandResponse;
import lat.mediteam.core.AppContext;
import lat.mediteam.core.Session;
import lat.mediteam.models.Secretaria;
import lat.mediteam.services.SecretariaService;

public class SecretariaController {

    private AppContext ctx;
    private Session session;
    private SecretariaService service;

    public SecretariaController(AppContext ctx, Session session, SecretariaService service) {
        this.ctx = ctx;
        this.session = session;
        this.service = service;
    }

    public CommandResponse crearSecretaria(Long usuarioId, String nombre, String apellido,
                                           String ci, String telefono, String email,
                                           String fechaNacimiento) {
        try {
            Secretaria nueva = service.crear(usuarioId, nombre, apellido,
                                             ci, telefono, email, fechaNacimiento);
            return new CommandResponse(true,
                "Secretaria creada: " + nueva.getNombre() + " " + nueva.getApellido()
                + " | CI: " + nueva.getCi());
        } catch (Exception e) {
            return new CommandResponse(false, "Error al crear secretaria: " + e.getMessage());
        }
    }

    public CommandResponse obtenerSecretaria(Long id) {
        try {
            Optional<Secretaria> secretaria = service.obtenerPorId(id);
            if (secretaria.isPresent()) {
                Secretaria s = secretaria.get();
                return new CommandResponse(true,
                    "Secretaria #" + s.getId()
                    + " | Nombre: " + s.getNombre() + " " + s.getApellido()
                    + " | CI: " + s.getCi()
                    + " | Tel: " + (s.getTelefono() != null ? s.getTelefono() : "-")
                    + " | Email: " + (s.getEmail() != null ? s.getEmail() : "-")
                    + " | Nacimiento: " + (s.getFechaNacimiento() != null ? s.getFechaNacimiento() : "-"));
            } else {
                return new CommandResponse(false, "Secretaria no encontrada con id: " + id);
            }
        } catch (Exception e) {
            return new CommandResponse(false, "Error al obtener secretaria: " + e.getMessage());
        }
    }

    public CommandResponse listarSecretarias() {
        try {
            List<Secretaria> secretarias = service.listar();
            if (secretarias.isEmpty()) {
                return new CommandResponse(true, "No hay secretarias registradas.");
            }

            StringBuilder sb = new StringBuilder("=== Lista de Secretarias ===\n");
            for (Secretaria s : secretarias) {
                sb.append(System.lineSeparator())
                  .append(s.getId()).append(" | ")
                  .append(s.getNombre()).append(" ").append(s.getApellido())
                  .append(" | CI: ").append(s.getCi())
                  .append(" | Tel: ").append(s.getTelefono() != null ? s.getTelefono() : "-")
                  .append(" | Email: ").append(s.getEmail() != null ? s.getEmail() : "-");
            }
            return new CommandResponse(true, sb.toString());

        } catch (Exception e) {
            return new CommandResponse(false, "Error al listar secretarias: " + e.getMessage());
        }
    }

    public CommandResponse editarSecretaria(Long id, String nombre, String apellido,
                                            String telefono, String email) {
        try {
            Secretaria actualizada = service.actualizar(id, nombre, apellido, telefono, email);
            return new CommandResponse(true,
                "Secretaria actualizada: " + actualizada.getNombre() + " " + actualizada.getApellido());
        } catch (Exception e) {
            return new CommandResponse(false, "Error al editar secretaria: " + e.getMessage());
        }
    }

    public CommandResponse eliminarSecretaria(Long id) {
        try {
            boolean ok = service.eliminar(id);
            return ok
                ? new CommandResponse(true, "Secretaria eliminada correctamente.")
                : new CommandResponse(false, "No se encontró secretaria con id: " + id);
        } catch (Exception e) {
            return new CommandResponse(false, "Error al eliminar secretaria: " + e.getMessage());
        }
    }
}