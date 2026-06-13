package lat.mediteam.commands;

import java.util.List;

import lat.mediteam.controllers.MedicosInvolucradosController;
import lat.mediteam.core.AppContext;
import lat.mediteam.core.Session;
import lat.mediteam.exceptions.InvalidArgumentException;
import lat.mediteam.services.MedicosInvolucradosService;

public class MedicosInvolucradosCommands implements Command {

    private AppContext ctx;
    private Session session;

    @Override
    public CommandResponse execute(
            AppContext ctx,
            Session session,
            List<String> args) {

        if (args.isEmpty()) {
            throw new InvalidArgumentException(
                    "No se proporcionó ningún subcomando para 'involucrados'");
        }

        this.ctx = ctx;
        this.session = session;

        String subCommand = args.remove(0).toLowerCase();

        switch (subCommand) {

            case "asignar":
                return asignar(args);

            case "remover":
                return remover(args);

            default:
                throw new InvalidArgumentException(
                        "Subcomando inválido: " + subCommand);
        }
    }

    @Override
    public String getHelp() {
        return "Comandos de médicos involucrados:\n"
                + "  involucrados asignar <medicoId> <historiaId>\n"
                + "  involucrados remover <medicoId> <historiaId>";
    }

    private CommandResponse asignar(List<String> args) {

        if (args.size() != 2) {
            throw new InvalidArgumentException(
                    "Argumentos erróneos para 'asignar'. "
                            + "Se requieren 2 argumentos: "
                            + "<medicoId> <historiaId>");
        }

        Long medicoId = parseId(args.get(0));
        Long historiaId = parseId(args.get(1));

        MedicosInvolucradosController controller = new MedicosInvolucradosController(
                ctx,
                session,
                new MedicosInvolucradosService());

        return controller.asignarMedico(
                medicoId,
                historiaId);
    }

    private CommandResponse remover(List<String> args) {

        if (args.size() != 2) {
            throw new InvalidArgumentException(
                    "Argumentos erróneos para 'remover'. "
                            + "Se requieren 2 argumentos: "
                            + "<medicoId> <historiaId>");
        }

        Long medicoId = parseId(args.get(0));
        Long historiaId = parseId(args.get(1));

        MedicosInvolucradosController controller = new MedicosInvolucradosController(
                ctx,
                session,
                new MedicosInvolucradosService());

        return controller.removerMedico(
                medicoId,
                historiaId);
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