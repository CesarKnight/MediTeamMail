package lat.mediteam.commands;

import java.util.List;

import lat.mediteam.controllers.AdminController;
import lat.mediteam.core.AppContext;
import lat.mediteam.core.Session;
import lat.mediteam.services.AdminService;
import lat.mediteam.exceptions.InvalidArgumentException;

public class AdminCommands implements Command {
    AppContext ctx;
    Session session;

    public CommandResponse execute(AppContext ctx, Session session, List<String> args) {
        if (args.isEmpty()) {
            throw new InvalidArgumentException("No se proporcionó ningún subcomando para 'admin'");
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
            default:
                throw new InvalidArgumentException("Subcomando de admin inválido: " + subCommand);
        }
    }

    public String getHelp() {
        return "Comandos de admin:\n" +
               "  admin crear <usuarioId> <nombre> <apellido> - Crea un nuevo admin\n" +
               "  admin obtener <id> - Obtiene los detalles de un admin por ID\n" +
               "  admin listar - Lista todos los admins\n" +
               "  admin editar <id> <nombre> <apellido> - Edita un admin existente\n" +
               "  admin eliminar <id> - Elimina un admin por ID";
    }

    private CommandResponse crear(List<String> args) {
        if (args.size() != 3) {
            throw new InvalidArgumentException("Argumentos erróneos para 'crear'. Se requieren 3 argumentos: <usuarioId> <nombre> <apellido>");
        }

        Long usuarioId = parseId(args.get(0));
        String nombre = args.get(1);
        String apellido = args.get(2);

        AdminController controller = new AdminController(ctx, session, new AdminService());
        return controller.crearAdmin(usuarioId, nombre, apellido);
    }

    private CommandResponse editar(List<String> args) {
        if (args.size() != 3) {
            throw new InvalidArgumentException("Argumentos erróneos para 'editar'. Se requieren 3 argumentos: <id> <nombre> <apellido>");
        }

        Long id = parseId(args.get(0));
        String nombre = args.get(1);
        String apellido = args.get(2);

        AdminController controller = new AdminController(ctx, session, new AdminService());
        return controller.editarAdmin(id, nombre, apellido);
    }

    private CommandResponse eliminar(List<String> args) {
        if (args.size() != 1) {
            throw new InvalidArgumentException("Argumentos erróneos para 'eliminar'. Se requiere 1 argumento: <id>");
        }

        Long id = parseId(args.get(0));

        AdminController controller = new AdminController(ctx, session, new AdminService());
        return controller.eliminarAdmin(id);
    }

    private CommandResponse obtener(List<String> args) {
        if (args.size() != 1) {
            throw new InvalidArgumentException("Argumentos erróneos para 'obtener'. Se requiere 1 argumento: <id>");
        }

        Long id = parseId(args.get(0));

        AdminController controller = new AdminController(ctx, session, new AdminService());
        return controller.obtenerAdmin(id);
    }

    private CommandResponse listar(List<String> args) {
        if (!args.isEmpty()) {
            throw new InvalidArgumentException("Argumentos erróneos para 'listar'. No se requieren argumentos.");
        }

        AdminController controller = new AdminController(ctx, session, new AdminService());
        return controller.listarAdmins();
    }

    // Validaciones
    private Long parseId(String idStr) {
        try {
            return Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            throw new InvalidArgumentException("ID inválido: " + idStr);
        }
    }
}
