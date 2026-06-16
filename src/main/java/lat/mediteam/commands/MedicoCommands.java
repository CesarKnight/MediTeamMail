package lat.mediteam.commands;

import java.util.List;

import lat.mediteam.controllers.MedicoController;
import lat.mediteam.core.AppContext;
import lat.mediteam.core.Session;
import lat.mediteam.exceptions.InvalidArgumentException;
import lat.mediteam.services.MedicoService;

public class MedicoCommands implements Command {
    AppContext ctx;
    Session session;

    public CommandResponse execute(AppContext ctx, Session session, List<String> args) {
        if (args.isEmpty()) {
            throw new InvalidArgumentException("No se proporcionó ningún subcomando para 'medico'");
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
                throw new InvalidArgumentException("Subcomando de medico inválido: " + subCommand);
        }
    }

    public String getHelp() {
        return "Comandos de medico:\n" +
               "  medico crear <usuarioId> <nombre> <apellido> <ci> <especialidad> <fechaNacimiento> - Crea un nuevo médico\n" +
               "  medico obtener <id> - Obtiene los detalles de un médico por ID\n" +
               "  medico listar - Lista todos los médicos\n" +
               "  medico editar <id> <nombre> <apellido> <ci> <especialidad> <fechaNacimiento> - Edita un médico existente\n" +
               "  medico eliminar <id> - Elimina un médico por ID";
    }

    // medico crear <usuarioId> <nombre> <apellido> <ci> <especialidad> <fechaNacimiento>
    private CommandResponse crear(List<String> args) {
        if (args.size() != 6) {
            throw new InvalidArgumentException(
                "Argumentos erróneos para 'crear'. Se requieren 6 argumentos: " +
                "<usuarioId> <nombre> <apellido> <ci> <especialidad> <fechaNacimiento>");
        }

        Long usuarioId = parseId(args.get(0), "usuario");
        String nombre = args.get(1);
        String apellido = args.get(2);
        String ci = args.get(3);
        String especialidad = args.get(4);
        String fechaNacimiento = args.get(5);

        MedicoController controller = new MedicoController(ctx, session, new MedicoService());
        return controller.crearMedico(usuarioId, nombre, apellido, ci, especialidad, fechaNacimiento);
    }

    // medico obtener <id>
    private CommandResponse obtener(List<String> args) {
        if (args.size() != 1) {
            throw new InvalidArgumentException("Argumentos erróneos para 'obtener'. Se requiere 1 argumento: <id>");
        }

        Long id = parseId(args.get(0), "medico");

        MedicoController controller = new MedicoController(ctx, session, new MedicoService());
        return controller.obtenerMedico(id);
    }

    // medico listar
    private CommandResponse listar(List<String> args) {
        if (!args.isEmpty()) {
            throw new InvalidArgumentException("Argumentos erróneos para 'listar'. No se requieren argumentos.");
        }

        MedicoController controller = new MedicoController(ctx, session, new MedicoService());
        return controller.listarMedicos();
    }

    // medico editar <id> <nombre> <apellido> <ci> <especialidad> <fechaNacimiento>
    private CommandResponse editar(List<String> args) {
        if (args.size() != 6) {
            throw new InvalidArgumentException(
                "Argumentos erróneos para 'editar'. Se requieren 6 argumentos: " +
                "<id> <nombre> <apellido> <ci> <especialidad> <fechaNacimiento>");
        }

        Long id = parseId(args.get(0), "medico");
        String nombre = args.get(1);
        String apellido = args.get(2);
        String ci = args.get(3);
        String especialidad = args.get(4);
        String fechaNacimiento = args.get(5);

        MedicoController controller = new MedicoController(ctx, session, new MedicoService());
        return controller.editarMedico(id, nombre, apellido, ci, especialidad, fechaNacimiento);
    }

    // medico eliminar <id>
    private CommandResponse eliminar(List<String> args) {
        if (args.size() != 1) {
            throw new InvalidArgumentException("Argumentos erróneos para 'eliminar'. Se requiere 1 argumento: <id>");
        }

        Long id = parseId(args.get(0), "medico");

        MedicoController controller = new MedicoController(ctx, session, new MedicoService());
        return controller.eliminarMedico(id);
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