package lat.mediteam.commands;

import java.util.List;

import lat.mediteam.controllers.ConsultaController;
import lat.mediteam.core.AppContext;
import lat.mediteam.core.Session;
import lat.mediteam.exceptions.InvalidArgumentException;
import lat.mediteam.services.ConsultaService;

public class ConsultaCommands implements Command {

    private AppContext ctx;
    private Session session;

    @Override
    public CommandResponse execute(
            AppContext ctx,
            Session session,
            List<String> args) {

        if (args.isEmpty()) {
            throw new InvalidArgumentException(
                "No se proporcionó ningún subcomando para 'consulta'"
            );
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
                    "Subcomando de consulta inválido: "
                        + subCommand
                );
        }
    }

    @Override
    public String getHelp() {
        return "Comandos de consulta:\n"
            + "  consulta crear <historiaId> <descripcion>\n"
            + "  consulta obtener <id>\n"
            + "  consulta listar\n"
            + "  consulta editar <id> <descripcion>\n"
            + "  consulta eliminar <id>";
    }

    private CommandResponse crear(List<String> args) {

        if (args.size() != 2) {
            throw new InvalidArgumentException(
                "Argumentos erróneos para 'crear'. "
                    + "Se requieren 2 argumentos: "
                    + "<historiaId> <descripcion>"
            );
        }

        Long historiaId = parseId(args.get(0));
        String descripcion = args.get(1);

        ConsultaController controller =
            new ConsultaController(
                ctx,
                session,
                new ConsultaService()
            );

        return controller.crearConsulta(
            historiaId,
            descripcion
        );
    }

    private CommandResponse obtener(List<String> args) {

        if (args.size() != 1) {
            throw new InvalidArgumentException(
                "Argumentos erróneos para 'obtener'. "
                    + "Se requiere 1 argumento: <id>"
            );
        }

        Long id = parseId(args.get(0));

        ConsultaController controller =
            new ConsultaController(
                ctx,
                session,
                new ConsultaService()
            );

        return controller.obtenerConsulta(id);
    }

    private CommandResponse listar(List<String> args) {

        if (!args.isEmpty()) {
            throw new InvalidArgumentException(
                "Argumentos erróneos para 'listar'. "
                    + "No se requieren argumentos."
            );
        }

        ConsultaController controller =
            new ConsultaController(
                ctx,
                session,
                new ConsultaService()
            );

        return controller.listarConsultas();
    }

    private CommandResponse editar(List<String> args) {

        if (args.size() != 2) {
            throw new InvalidArgumentException(
                "Argumentos erróneos para 'editar'. "
                    + "Se requieren 2 argumentos: "
                    + "<id> <descripcion>"
            );
        }

        Long id = parseId(args.get(0));
        String descripcion = args.get(1);

        ConsultaController controller =
            new ConsultaController(
                ctx,
                session,
                new ConsultaService()
            );

        return controller.editarConsulta(
            id,
            descripcion
        );
    }

    private CommandResponse eliminar(List<String> args) {

        if (args.size() != 1) {
            throw new InvalidArgumentException(
                "Argumentos erróneos para 'eliminar'. "
                    + "Se requiere 1 argumento: <id>"
            );
        }

        Long id = parseId(args.get(0));

        ConsultaController controller =
            new ConsultaController(
                ctx,
                session,
                new ConsultaService()
            );

        return controller.eliminarConsulta(id);
    }

    private Long parseId(String value) {

        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new InvalidArgumentException(
                "ID inválido: " + value
            );
        }
    }
}