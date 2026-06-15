package lat.mediteam.commands;

import java.util.List;

import lat.mediteam.controllers.SecretariaController;
import lat.mediteam.core.AppContext;
import lat.mediteam.core.Session;
import lat.mediteam.services.SecretariaService;
import lat.mediteam.exceptions.InvalidArgumentException;

public class SecretariaCommands implements Command {

    @Override
    public CommandResponse execute(AppContext ctx, Session session, List<String> args) {
        if (args.isEmpty()) {
            throw new InvalidArgumentException("No se proporcionó ningún subcomando para 'secretaria'");
        }

        String subCommand = args.remove(0).toLowerCase();

        switch (subCommand) {
            case "crear":
                return crear(ctx, session, args);
            case "obtener":
                return obtener(ctx, session, args);
            case "listar":
                return listar(ctx, session, args);
            case "editar":
                return editar(ctx, session, args);
            case "eliminar":
                return eliminar(ctx, session, args);
            default:
                throw new InvalidArgumentException("Subcomando de secretaria inválido: " + subCommand);
        }
    }

    @Override
    public String getHelp() {
        return "Comandos de secretaria:\n" +
               "  secretaria crear <usuarioId> <nombre> <apellido> <ci> <telefono> <email> <fechaNacimiento>\n" +
               "  secretaria obtener <id>\n" +
               "  secretaria listar\n" +
               "  secretaria editar <id> <nombre> <apellido> <telefono> <email>\n" +
               "  secretaria eliminar <id>";
    }

    private CommandResponse crear(AppContext ctx, Session session, List<String> args) {
        if (args.size() < 4) {
            throw new InvalidArgumentException("Argumentos insuficientes. Uso: secretaria crear <usuarioId> <nombre> <apellido> <ci> [telefono] [email] [fechaNacimiento]");
        }

        Long usuarioId = parseLong(args.get(0));
        String nombre = args.get(1);
        String apellido = args.get(2);
        String ci = args.get(3);
        String telefono = args.size() > 4 ? args.get(4) : "";
        String email = args.size() > 5 ? args.get(5) : "";
        String fechaNacimiento = args.size() > 6 ? args.get(6) : "";

        SecretariaController controller = new SecretariaController(ctx, session, new SecretariaService());
        return controller.crearSecretaria(usuarioId, nombre, apellido, ci, telefono, email, fechaNacimiento);
    }

    private CommandResponse obtener(AppContext ctx, Session session, List<String> args) {
        if (args.size() != 1) {
            throw new InvalidArgumentException("Uso: secretaria obtener <id>");
        }
        Long id = parseLong(args.get(0));
        SecretariaController controller = new SecretariaController(ctx, session, new SecretariaService());
        return controller.obtenerSecretaria(id);
    }

    private CommandResponse listar(AppContext ctx, Session session, List<String> args) {
        if (!args.isEmpty()) {
            throw new InvalidArgumentException("Uso: secretaria listar (sin argumentos)");
        }
        SecretariaController controller = new SecretariaController(ctx, session, new SecretariaService());
        return controller.listarSecretarias();
    }

    private CommandResponse editar(AppContext ctx, Session session, List<String> args) {
        if (args.size() < 3) {
            throw new InvalidArgumentException("Uso: secretaria editar <id> <nombre> <apellido> [telefono] [email]");
        }
        Long id = parseLong(args.get(0));
        String nombre = args.get(1);
        String apellido = args.get(2);
        String telefono = args.size() > 3 ? args.get(3) : "";
        String email = args.size() > 4 ? args.get(4) : "";

        SecretariaController controller = new SecretariaController(ctx, session, new SecretariaService());
        return controller.editarSecretaria(id, nombre, apellido, telefono, email);
    }

    private CommandResponse eliminar(AppContext ctx, Session session, List<String> args) {
        if (args.size() != 1) {
            throw new InvalidArgumentException("Uso: secretaria eliminar <id>");
        }
        Long id = parseLong(args.get(0));
        SecretariaController controller = new SecretariaController(ctx, session, new SecretariaService());
        return controller.eliminarSecretaria(id);
    }

    private Long parseLong(String str) {
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            throw new InvalidArgumentException("Número inválido: " + str);
        }
    }
}