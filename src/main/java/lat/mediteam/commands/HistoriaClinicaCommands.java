package lat.mediteam.commands;

import java.util.List;

import lat.mediteam.controllers.HistoriaClinicaController;
import lat.mediteam.controllers.MedicosInvolucradosController;
import lat.mediteam.core.AppContext;
import lat.mediteam.core.Session;
import lat.mediteam.enums.HistoriaClinicaEstado;
import lat.mediteam.enums.HistoriaClinicaTipo;
import lat.mediteam.exceptions.InvalidArgumentException;
import lat.mediteam.services.HistoriaClinicaService;
import lat.mediteam.services.MedicosInvolucradosService;
    
public class HistoriaClinicaCommands implements Command {

    private AppContext ctx;
    private Session session;

    @Override
    public CommandResponse execute(AppContext ctx, Session session, List<String> args) {

        if (args.isEmpty()) {
            throw new InvalidArgumentException(
                "No se proporcionó ningún subcomando para 'historia'"
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
            
            case "agregar_medico":
                return asignarMedico(args);
            
            case "remover_medico":
                return removerMedico(args);

            default:
                throw new InvalidArgumentException(
                    "Subcomando de historia inválido: " + subCommand
                );
        }
    }

    @Override
    public String getHelp() {
        return "Comandos de historia clínica:\n"
            + "  historia crear <medicoId> <pacienteId> <fecha> <estado> <tipo>\n"
            + "  historia obtener <id>\n"
            + "  historia listar\n"
            + "  historia editar <id> <fecha> <estado> <tipo>\n"
            + "  historia eliminar <id>";
    }

    private CommandResponse crear(List<String> args) {

        if (args.size() != 4) {
            throw new InvalidArgumentException(
                "Argumentos erróneos para 'crear'. "
                    + "Se requieren 4 argumentos: "
                    + "<medicoId> <fecha> <estado> <tipo>"
            );
        }

        Long medicoId = parseId(args.get(0));
        Long pacienteId = parseId(args.get(1));
        String fecha = args.get(2);

        HistoriaClinicaEstado estado =
            parseEstado(args.get(3));

        HistoriaClinicaTipo tipo =
            parseTipo(args.get(4));

        HistoriaClinicaController controller =
            new HistoriaClinicaController(
                ctx,
                session,
                new HistoriaClinicaService()
            );

        return controller.crearHistoria(
            medicoId,
            pacienteId,
            fecha,
            estado,
            tipo
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

        HistoriaClinicaController controller =
            new HistoriaClinicaController(
                ctx,
                session,
                new HistoriaClinicaService()
            );

        return controller.obtenerHistoria(id);
    }

    private CommandResponse listar(List<String> args) {

        if (!args.isEmpty()) {
            throw new InvalidArgumentException(
                "Argumentos erróneos para 'listar'. "
                    + "No se requieren argumentos."
            );
        }

        HistoriaClinicaController controller =
            new HistoriaClinicaController(
                ctx,
                session,
                new HistoriaClinicaService()
            );

        return controller.listarHistorias();
    }

    private CommandResponse editar(List<String> args) {

        if (args.size() != 4) {
            throw new InvalidArgumentException(
                "Argumentos erróneos para 'editar'. "
                    + "Se requieren 4 argumentos: "
                    + "<id> <fecha> <estado> <tipo>"
            );
        }

        Long id = parseId(args.get(0));

        String fecha = args.get(1);

        HistoriaClinicaEstado estado =
            parseEstado(args.get(2));

        HistoriaClinicaTipo tipo =
            parseTipo(args.get(3));

        HistoriaClinicaController controller =
            new HistoriaClinicaController(
                ctx,
                session,
                new HistoriaClinicaService()
            );

        return controller.editarHistoria(
            id,
            fecha,
            estado,
            tipo
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

        HistoriaClinicaController controller =
            new HistoriaClinicaController(
                ctx,
                session,
                new HistoriaClinicaService()
            );

        return controller.eliminarHistoria(id);
    }

    private HistoriaClinicaEstado parseEstado(String estado) {
        try {
            return HistoriaClinicaEstado.valueOf(
                estado.toLowerCase()
            );
        } catch (Exception e) {
            throw new InvalidArgumentException(
                "Estado inválido: " + estado
            );
        }
    }

    private HistoriaClinicaTipo parseTipo(String tipo) {
        try {
            return HistoriaClinicaTipo.valueOf(
                tipo.toLowerCase()
            );
        } catch (Exception e) {
            throw new InvalidArgumentException(
                "Tipo inválido: " + tipo
            );
        }
    }

    private CommandResponse asignarMedico(List<String> args) {

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

    private CommandResponse removerMedico(List<String> args) {

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
                "ID inválido: " + value
            );
        }
    }
}