package lat.mediteam.commands;

import java.util.List;

import lat.mediteam.controllers.PacienteController;
import lat.mediteam.core.AppContext;
import lat.mediteam.core.Session;
import lat.mediteam.services.PacienteService;
import lat.mediteam.exceptions.InvalidArgumentException;

public class PacienteCommands implements Command {
    AppContext ctx;
    Session session;

    public CommandResponse execute(AppContext ctx, Session session, List<String> args) {
        if (args.isEmpty()) {
            throw new InvalidArgumentException("No se proporcionó ningún subcomando para 'paciente'");
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
                throw new InvalidArgumentException("Subcomando de paciente inválido: " + subCommand);
        }
    }

    public String getHelp() {
        return "Comandos de paciente:\n" +
               "  paciente crear <usuarioId> <nombre> <apellido> <ci> <telefono> <email> <fechaNacimiento> - Crea un nuevo paciente\n" +
               "  paciente obtener <id> - Obtiene los detalles de un paciente por ID\n" +
               "  paciente listar - Lista todos los pacientes\n" +
               "  paciente editar <id> <nombre> <apellido> <telefono> <email> - Edita un paciente existente\n" +
               "  paciente eliminar <id> - Elimina un paciente por ID";
    }

    // paciente crear <usuarioId> <nombre> <apellido> <ci> <telefono> <email> <fechaNacimiento>
    private CommandResponse crear(List<String> args) {
        if (args.size() < 4 || args.size() > 7) {
            throw new InvalidArgumentException(
                "Argumentos erróneos para 'crear'. Se requieren entre 4 y 7 argumentos: " +
                "<usuarioId> <nombre> <apellido> <ci> [telefono] [email] [fechaNacimiento]");
        }

        Long usuarioId = parseId(args.get(0));
        String nombre = args.get(1);
        String apellido = args.get(2);
        String ci = args.get(3);
        String telefono = args.size() > 4 ? args.get(4) : "";
        String email = args.size() > 5 ? args.get(5) : "";
        String fechaNacimiento = args.size() > 6 ? args.get(6) : "";

        PacienteController controller = new PacienteController(ctx, session, new PacienteService());
        return controller.crearPaciente(usuarioId, nombre, apellido, ci, telefono, email, fechaNacimiento);
    }

    // paciente obtener <id>
    private CommandResponse obtener(List<String> args) {
        if (args.size() != 1) {
            throw new InvalidArgumentException("Argumentos erróneos para 'obtener'. Se requiere 1 argumento: <id>");
        }

        Long id = parseId(args.get(0));

        PacienteController controller = new PacienteController(ctx, session, new PacienteService());
        return controller.obtenerPaciente(id);
    }

    // paciente listar
    private CommandResponse listar(List<String> args) {
        if (!args.isEmpty()) {
            throw new InvalidArgumentException("Argumentos erróneos para 'listar'. No se requieren argumentos.");
        }

        PacienteController controller = new PacienteController(ctx, session, new PacienteService());
        return controller.listarPacientes();
    }

    // paciente editar <id> <nombre> <apellido> <telefono> <email>
    private CommandResponse editar(List<String> args) {
        if (args.size() < 3 || args.size() > 5) {
            throw new InvalidArgumentException(
                "Argumentos erróneos para 'editar'. Se requieren entre 3 y 5 argumentos: " +
                "<id> <nombre> <apellido> [telefono] [email]");
        }

        Long id = parseId(args.get(0));
        String nombre = args.get(1);
        String apellido = args.get(2);
        String telefono = args.size() > 3 ? args.get(3) : "";
        String email = args.size() > 4 ? args.get(4) : "";

        PacienteController controller = new PacienteController(ctx, session, new PacienteService());
        return controller.editarPaciente(id, nombre, apellido, telefono, email);
    }

    // paciente eliminar <id>
    private CommandResponse eliminar(List<String> args) {
        if (args.size() != 1) {
            throw new InvalidArgumentException("Argumentos erróneos para 'eliminar'. Se requiere 1 argumento: <id>");
        }

        Long id = parseId(args.get(0));

        PacienteController controller = new PacienteController(ctx, session, new PacienteService());
        return controller.eliminarPaciente(id);
    }

    // validaciones
    private Long parseId(String idStr) {
        try {
            return Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            throw new InvalidArgumentException("ID de paciente inválido: " + idStr);
        }
    }
}