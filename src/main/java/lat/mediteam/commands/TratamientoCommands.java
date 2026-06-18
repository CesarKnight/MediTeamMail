package lat.mediteam.commands;

import java.util.List;

import lat.mediteam.controllers.TratamientoController;
import lat.mediteam.core.AppContext;
import lat.mediteam.core.Session;
import lat.mediteam.exceptions.InvalidArgumentException;
import lat.mediteam.services.TratamientoService;

public class TratamientoCommands implements Command {

    private AppContext ctx;
    private Session session;

    @Override
    public CommandResponse execute(
            AppContext ctx,
            Session session,
            List<String> args) {

        if (args.isEmpty()) {
            throw new InvalidArgumentException(
                    "No se proporcionó ningún subcomando para 'tratamiento'");
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
                throw new InvalidArgumentException(
                        "Subcomando de tratamiento inválido: "
                                + subCommand);
        }
    }

    @Override
    public String getHelp() {
        return "Comandos de tratamiento:\n"
                + "  tratamiento crear <historiaId> <pacienteId> <tratamiento>\n"
                + "  tratamiento obtener <id>\n"
                + "  tratamiento listar\n"
                + "  tratamiento editar <id> <tratamiento>\n"
                + "  tratamiento eliminar <id>";
    }

    private CommandResponse crear(List<String> args) {

        if (args.size() != 2) {
            throw new InvalidArgumentException(
                    "Argumentos erróneos para 'crear'. "
                            + "Se requieren 2 argumentos: "
                            + "<historiaId> <tratamiento>");
        }

        Long historiaId = parseId(args.get(0));
        Long pacienteId = parseId(args.get(1));
        String tratamiento = args.get(2);

        TratamientoController controller = new TratamientoController(
                ctx,
                session,
                new TratamientoService());

        return controller.crearTratamiento(
                historiaId,
                pacienteId,
                tratamiento);
    }

    private CommandResponse obtener(List<String> args) {

        if (args.size() != 1) {
            throw new InvalidArgumentException(
                    "Argumentos erróneos para 'obtener'. "
                            + "Se requiere 1 argumento: <id>");
        }

        Long id = parseId(args.get(0));

        TratamientoController controller = new TratamientoController(
                ctx,
                session,
                new TratamientoService());

        return controller.obtenerTratamiento(id);
    }

    private CommandResponse listar(List<String> args) {

        if (!args.isEmpty()) {
            throw new InvalidArgumentException(
                    "Argumentos erróneos para 'listar'. "
                            + "No se requieren argumentos.");
        }

        TratamientoController controller = new TratamientoController(
                ctx,
                session,
                new TratamientoService());

        return controller.listarTratamientos();
    }

    private CommandResponse editar(List<String> args) {

        if (args.size() != 2) {
            throw new InvalidArgumentException(
                    "Argumentos erróneos para 'editar'. "
                            + "Se requieren 2 argumentos: "
                            + "<id> <tratamiento>");
        }

        Long id = parseId(args.get(0));
        String tratamiento = args.get(1);

        TratamientoController controller = new TratamientoController(
                ctx,
                session,
                new TratamientoService());

        return controller.editarTratamiento(
                id,
                tratamiento);
    }

    private CommandResponse eliminar(List<String> args) {

        if (args.size() != 1) {
            throw new InvalidArgumentException(
                    "Argumentos erróneos para 'eliminar'. "
                            + "Se requiere 1 argumento: <id>");
        }

        Long id = parseId(args.get(0));

        TratamientoController controller = new TratamientoController(
                ctx,
                session,
                new TratamientoService());

        return controller.eliminarTratamiento(id);
    }

    private Long parseId(String value) {

        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new InvalidArgumentException(
                    "ID inválido: " + value);
        }
    }
}