package lat.mediteam.controllers;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import lat.mediteam.commands.CommandResponse;
import lat.mediteam.core.AppContext;
import lat.mediteam.core.Session;
import lat.mediteam.models.Permiso;
import lat.mediteam.services.PermisoService;

public class PermisoController {

    private AppContext ctx;
    private Session session;
    private PermisoService service;

    public PermisoController(AppContext ctx, Session session, PermisoService service) {
        this.ctx = ctx;
        this.session = session;
        this.service = service;
    }

    public CommandResponse crearPermiso(String nombre, String descripcion) {
        Permiso nuevo = service.crear(nombre, descripcion);
        return new CommandResponse(true, "Permiso creado: " + nuevo.getNombre());
    }

    public CommandResponse obtenerPermiso(Long id) {
        Optional<Permiso> permiso = service.obtenerPorId(id);
        if (permiso.isEmpty()) {
            throw new EntityNotFoundException("Permiso no encontrado: " + id);
        }

        Permiso p = permiso.get();
        return new CommandResponse(true,
            "Permiso #" + p.getId() + " | Nombre: " + p.getNombre() +
            " | Descripción: " + (p.getDescripcion() != null ? p.getDescripcion() : "-"));
    }

    public CommandResponse listarPermisos() {
        List<Permiso> permisos = service.listar();
        if (permisos.isEmpty()) {
            return new CommandResponse(true, "No hay permisos registrados.");
        }

        StringBuilder sb = new StringBuilder("=== Lista de Permisos ===\n");
        for (Permiso p : permisos) {
            sb.append(System.lineSeparator())
              .append(p.getId()).append(" | ")
              .append(p.getNombre())
              .append(" | ").append(p.getDescripcion() != null ? p.getDescripcion() : "-");
        }
        return new CommandResponse(true, sb.toString());
    }

    public CommandResponse editarPermiso(Long id, String nombre, String descripcion) {
        Permiso actualizado = service.actualizar(id, nombre, descripcion);
        return new CommandResponse(true, "Permiso actualizado: " + actualizado.getNombre());
    }

    public CommandResponse eliminarPermiso(Long id) {
        service.eliminar(id);
        return new CommandResponse(true, "Permiso eliminado correctamente.");
    }

    public CommandResponse asignarPermiso(Long usuarioId, Long permisoId) {
        service.asignarPermiso(usuarioId, permisoId);
        return new CommandResponse(true, "Permiso asignado al usuario " + usuarioId);
    }

    public CommandResponse removerPermiso(Long usuarioId, Long permisoId) {
        service.removerPermiso(usuarioId, permisoId);
        return new CommandResponse(true, "Permiso removido del usuario " + usuarioId);
    }

    public CommandResponse listarPermisosDeUsuario(Long usuarioId) {
        List<String> nombres = service.listarPermisosDeUsuario(usuarioId);
        if (nombres.isEmpty()) {
            return new CommandResponse(true, "El usuario no tiene permisos asignados.");
        }

        StringBuilder sb = new StringBuilder("Permisos del usuario " + usuarioId + ":\n");
        for (String nombre : nombres) {
            sb.append(" - ").append(nombre).append(System.lineSeparator());
        }
        return new CommandResponse(true, sb.toString().trim());
    }
}