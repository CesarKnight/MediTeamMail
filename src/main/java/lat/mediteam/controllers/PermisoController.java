package lat.mediteam.controllers;

import java.util.List;
import java.util.Optional;

import lat.mediteam.commands.CommandResponse;
import lat.mediteam.models.Permiso;
import lat.mediteam.services.PermisoService;

public class PermisoController {

    private PermisoService service;

    public PermisoController(PermisoService service) {
        this.service = service;
    }

    public CommandResponse crearPermiso(String nombre, String descripcion) {
        try {
            Permiso nuevo = service.crear(nombre, descripcion);
            return new CommandResponse(true, "Permiso creado: " + nuevo.getNombre());
        } catch (Exception e) {
            return new CommandResponse(false, "Error al crear permiso: " + e.getMessage());
        }
    }

    public CommandResponse obtenerPermiso(Long id) {
        try {
            Optional<Permiso> permiso = service.obtenerPorId(id);
            if (permiso.isPresent()) {
                Permiso p = permiso.get();
                return new CommandResponse(true,
                    "Permiso #" + p.getId() + " | Nombre: " + p.getNombre() +
                    " | Descripción: " + (p.getDescripcion() != null ? p.getDescripcion() : "-"));
            } else {
                return new CommandResponse(false, "Permiso no encontrado con id: " + id);
            }
        } catch (Exception e) {
            return new CommandResponse(false, "Error al obtener permiso: " + e.getMessage());
        }
    }

    public CommandResponse listarPermisos() {
        try {
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
        } catch (Exception e) {
            return new CommandResponse(false, "Error al listar permisos: " + e.getMessage());
        }
    }

    public CommandResponse editarPermiso(Long id, String nombre, String descripcion) {
        try {
            Permiso actualizado = service.actualizar(id, nombre, descripcion);
            return new CommandResponse(true, "Permiso actualizado: " + actualizado.getNombre());
        } catch (Exception e) {
            return new CommandResponse(false, "Error al editar permiso: " + e.getMessage());
        }
    }

    public CommandResponse eliminarPermiso(Long id) {
        try {
            boolean ok = service.eliminar(id);
            return ok ? new CommandResponse(true, "Permiso eliminado correctamente.")
                      : new CommandResponse(false, "No se encontró permiso con id: " + id);
        } catch (Exception e) {
            return new CommandResponse(false, "Error al eliminar permiso: " + e.getMessage());
        }
    }

    public CommandResponse asignarPermiso(Long usuarioId, Long permisoId) {
        try {
            service.asignarPermiso(usuarioId, permisoId);
            return new CommandResponse(true, "Permiso asignado al usuario " + usuarioId);
        } catch (Exception e) {
            return new CommandResponse(false, "Error al asignar permiso: " + e.getMessage());
        }
    }

    public CommandResponse removerPermiso(Long usuarioId, Long permisoId) {
        try {
            service.removerPermiso(usuarioId, permisoId);
            return new CommandResponse(true, "Permiso removido del usuario " + usuarioId);
        } catch (Exception e) {
            return new CommandResponse(false, "Error al remover permiso: " + e.getMessage());
        }
    }

    public CommandResponse listarPermisosDeUsuario(Long usuarioId) {
        try {
            List<String> nombres = service.listarPermisosDeUsuario(usuarioId);
            if (nombres.isEmpty()) {
                return new CommandResponse(true, "El usuario no tiene permisos asignados.");
            }
            StringBuilder sb = new StringBuilder("Permisos del usuario " + usuarioId + ":\n");
            for (String nombre : nombres) {
                sb.append(" - ").append(nombre).append(System.lineSeparator());
            }
            return new CommandResponse(true, sb.toString().trim());
        } catch (Exception e) {
            return new CommandResponse(false, "Error al listar permisos del usuario: " + e.getMessage());
        }
    }
}