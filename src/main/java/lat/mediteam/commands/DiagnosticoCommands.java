package lat.mediteam.commands;

import java.util.List;

import lat.mediteam.controllers.DiagnosticoController;
import lat.mediteam.core.AppContext;
import lat.mediteam.core.Session;
import lat.mediteam.exceptions.InvalidArgumentException;
import lat.mediteam.services.DiagnosticoService;

public class DiagnosticoCommands implements Command {

    private AppContext ctx;
    private Session session;

    @Override
    public CommandResponse execute(
            AppContext ctx,
            Session session,
            List<String> args) {

        if (args.isEmpty()) {
            throw new InvalidArgumentException(
                    "No se proporcionó ningún subcomando para 'diagnostico'");
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
                        "Subcomando de diagnostico inválido: "
                                + subCommand);
        }
    }

    @Override
    public String getHelp() {
        return "Comandos de diagnóstico:\n"
                + "  diagnostico crear <historiaId> <diagnostico>\n"
                + "  diagnostico obtener <id>\n"
                + "  diagnostico listar\n"
                + "  diagnostico editar <id> <diagnostico>\n"
                + "  diagnostico eliminar <id>";
    }

    private CommandResponse crear(List<String> args) {

        if (args.size() != 2) {
            throw new InvalidArgumentException(
                    "Argumentos erróneos para 'crear'. "
                            + "Se requieren 2 argumentos: "
                            + "<historiaId> <diagnostico>");
        }

        Long historiaId = parseId(args.get(0));
        String diagnostico = args.get(1);

        DiagnosticoController controller = new DiagnosticoController(
                ctx,
                session,
                new DiagnosticoService());

        return controller.crearDiagnostico(
                historiaId,
                diagnostico);
    }

    private CommandResponse obtener(List<String> args) {

        if (args.size() != 1) {
            throw new InvalidArgumentException(
                    "Argumentos erróneos para 'obtener'. "
                            + "Se requiere 1 argumento: <id>");
        }

        Long id = parseId(args.get(0));

        DiagnosticoController controller = new DiagnosticoController(
                ctx,
                session,
                new DiagnosticoService());

        return controller.obtenerDiagnostico(id);
    }

    private CommandResponse listar(List<String> args) {

        if (!args.isEmpty()) {
            throw new InvalidArgumentException(
                    "Argumentos erróneos para 'listar'. "
                            + "No se requieren argumentos.");
        }

        DiagnosticoController controller = new DiagnosticoController(
                ctx,
                session,
                new DiagnosticoService());

        return controller.listarDiagnosticos();
    }

    private CommandResponse editar(List<String> args) {

        if (args.size() != 2) {
            throw new InvalidArgumentException(
                    "Argumentos erróneos para 'editar'. "
                            + "Se requieren 2 argumentos: "
                            + "<id> <diagnostico>");
        }

        Long id = parseId(args.get(0));
        String diagnostico = args.get(1);

        DiagnosticoController controller = new DiagnosticoController(
                ctx,
                session,
                new DiagnosticoService());

        return controller.editarDiagnostico(
                id,
                diagnostico);
    }

    private CommandResponse eliminar(List<String> args) {

        if (args.size() != 1) {
            throw new InvalidArgumentException(
                    "Argumentos erróneos para 'eliminar'. "
                            + "Se requiere 1 argumento: <id>");
        }

        Long id = parseId(args.get(0));

        DiagnosticoController controller = new DiagnosticoController(
                ctx,
                session,
                new DiagnosticoService());

        return controller.eliminarDiagnostico(id);
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