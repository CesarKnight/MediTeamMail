package lat.mediteam.commands;

import java.util.List;

import lat.mediteam.controllers.PermisoController;
import lat.mediteam.core.AppContext;
import lat.mediteam.core.Session;
import lat.mediteam.exceptions.InvalidArgumentException;
import lat.mediteam.services.PermisoService;

public class PermisoCommands implements Command {
    AppContext ctx;
    Session session;

    public CommandResponse execute(AppContext ctx, Session session, List<String> args) {
        if (args.isEmpty()) {
            throw new InvalidArgumentException("No se proporcionó ningún subcomando para 'permiso'");
        }

        this.ctx = ctx;
        this.session = session;

        String subCommand = args.remove(0).toLowerCase();

        switch (subCommand) {
            case "crear":
                return crear(args);
            case "obtener":
                return obtener(args);
            case "listar":
                return listar(args);
            case "editar":
                return editar(args);
            case "eliminar":
                return eliminar(args);
            case "asignar":
                return asignar(args);
            case "remover":
                return remover(args);
            case "listardeusuario":
                return listarDeUsuario(args);
            default:
                throw new InvalidArgumentException("Subcomando de permiso inválido: " + subCommand);
        }
    }

    public String getHelp() {
        return "Comandos de permiso:\n" +
               "  permiso crear <nombre> [descripcion] - Crea un nuevo permiso\n" +
               "  permiso obtener <id> - Obtiene los detalles de un permiso por ID\n" +
               "  permiso listar - Lista todos los permisos\n" +
               "  permiso editar <id> <nombre> [descripcion] - Edita un permiso existente\n" +
               "  permiso eliminar <id> - Elimina un permiso por ID\n" +
               "  permiso asignar <usuarioId> <permisoId> - Asigna un permiso a un usuario\n" +
               "  permiso remover <usuarioId> <permisoId> - Remueve un permiso de un usuario\n" +
               "  permiso listardeusuario <usuarioId> - Lista los permisos de un usuario";
    }

    // permiso crear <nombre> [descripcion]
    private CommandResponse crear(List<String> args) {
        if (args.size() < 1 || args.size() > 2) {
            throw new InvalidArgumentException(
                "Argumentos erróneos para 'crear'. Se requieren entre 1 y 2 argumentos: <nombre> [descripcion]");
        }

        String nombre = args.get(0);
        String descripcion = args.size() > 1 ? args.get(1) : "";

        PermisoController controller = new PermisoController(ctx, session, new PermisoService());
        return controller.crearPermiso(nombre, descripcion);
    }

    // permiso obtener <id>
    private CommandResponse obtener(List<String> args) {
        if (args.size() != 1) {
            throw new InvalidArgumentException("Argumentos erróneos para 'obtener'. Se requiere 1 argumento: <id>");
        }

        Long id = parseId(args.get(0), "permiso");

        PermisoController controller = new PermisoController(ctx, session, new PermisoService());
        return controller.obtenerPermiso(id);
    }

    // permiso listar
    private CommandResponse listar(List<String> args) {
        if (!args.isEmpty()) {
            throw new InvalidArgumentException("Argumentos erróneos para 'listar'. No se requieren argumentos.");
        }

        PermisoController controller = new PermisoController(ctx, session, new PermisoService());
        return controller.listarPermisos();
    }

    // permiso editar <id> <nombre> [descripcion]
    private CommandResponse editar(List<String> args) {
        if (args.size() < 2 || args.size() > 3) {
            throw new InvalidArgumentException(
                "Argumentos erróneos para 'editar'. Se requieren entre 2 y 3 argumentos: <id> <nombre> [descripcion]");
        }

        Long id = parseId(args.get(0), "permiso");
        String nombre = args.get(1);
        String descripcion = args.size() > 2 ? args.get(2) : "";

        PermisoController controller = new PermisoController(ctx, session, new PermisoService());
        return controller.editarPermiso(id, nombre, descripcion);
    }

    // permiso eliminar <id>
    private CommandResponse eliminar(List<String> args) {
        if (args.size() != 1) {
            throw new InvalidArgumentException("Argumentos erróneos para 'eliminar'. Se requiere 1 argumento: <id>");
        }

        Long id = parseId(args.get(0), "permiso");

        PermisoController controller = new PermisoController(ctx, session, new PermisoService());
        return controller.eliminarPermiso(id);
    }

    // permiso asignar <usuarioId> <permisoId>
    private CommandResponse asignar(List<String> args) {
        if (args.size() != 2) {
            throw new InvalidArgumentException(
                "Argumentos erróneos para 'asignar'. Se requieren 2 argumentos: <usuarioId> <permisoId>");
        }

        Long usuarioId = parseId(args.get(0), "usuario");
        Long permisoId = parseId(args.get(1), "permiso");

        PermisoController controller = new PermisoController(ctx, session, new PermisoService());
        return controller.asignarPermiso(usuarioId, permisoId);
    }

    // permiso remover <usuarioId> <permisoId>
    private CommandResponse remover(List<String> args) {
        if (args.size() != 2) {
            throw new InvalidArgumentException(
                "Argumentos erróneos para 'remover'. Se requieren 2 argumentos: <usuarioId> <permisoId>");
        }

        Long usuarioId = parseId(args.get(0), "usuario");
        Long permisoId = parseId(args.get(1), "permiso");

        PermisoController controller = new PermisoController(ctx, session, new PermisoService());
        return controller.removerPermiso(usuarioId, permisoId);
    }

    // permiso listardeusuario <usuarioId>
    private CommandResponse listarDeUsuario(List<String> args) {
        if (args.size() != 1) {
            throw new InvalidArgumentException(
                "Argumentos erróneos para 'listardeusuario'. Se requiere 1 argumento: <usuarioId>");
        }

        Long usuarioId = parseId(args.get(0), "usuario");

        PermisoController controller = new PermisoController(ctx, session, new PermisoService());
        return controller.listarPermisosDeUsuario(usuarioId);
    }

    // validaciones
    private Long parseId(String idStr, String entidad) {
        try {
            return Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            throw new InvalidArgumentException("ID de " + entidad + " inválido: " + idStr);
        }
    }
}