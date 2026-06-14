package lat.mediteam.commands;

import java.util.List;

import lat.mediteam.controllers.CitaController;
import lat.mediteam.core.AppContext;
import lat.mediteam.core.Session;
import lat.mediteam.exceptions.InvalidArgumentException;
import lat.mediteam.services.CitaService;

public class CitaCommands implements Command {
    AppContext ctx;
    Session session;

    public CommandResponse execute(AppContext ctx, Session session, List<String> args) {
        if (args.isEmpty()) {
            throw new InvalidArgumentException("No se proporcionó ningún subcomando para 'cita'");
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
            case "porpaciente":
                return porPaciente(args);
            case "pormedico":
                return porMedico(args);
            case "reprogramar":
                return reprogramar(args);
            case "cancelar":
                return cancelar(args);
            default:
                throw new InvalidArgumentException("Subcomando de cita inválido: " + subCommand);
        }
    }

    public String getHelp() {
        return "Comandos de cita:\n" +
               "  cita crear <pacienteId> <medicoId> <servicioId> <fecha> <horaInicio> <horaFin> [motivo] - Crea una nueva cita\n" +
               "  cita obtener <id> - Obtiene los detalles de una cita por ID\n" +
               "  cita listar - Lista todas las citas\n" +
               "  cita porpaciente <pacienteId> - Lista las citas de un paciente\n" +
               "  cita pormedico <medicoId> - Lista las citas de un médico\n" +
               "  cita reprogramar <id> <nuevaFecha> <nuevaHoraInicio> <nuevaHoraFin> - Reprograma una cita\n" +
               "  cita cancelar <id> [motivo] - Cancela una cita";
    }

    // cita crear <pacienteId> <medicoId> <servicioId> <fecha> <horaInicio> <horaFin> [motivo]
    private CommandResponse crear(List<String> args) {
        if (args.size() < 6 || args.size() > 7) {
            throw new InvalidArgumentException(
                "Argumentos erróneos para 'crear'. Se requieren entre 6 y 7 argumentos: " +
                "<pacienteId> <medicoId> <servicioId> <fecha> <horaInicio> <horaFin> [motivo]");
        }

        Long pacienteId = parseId(args.get(0), "paciente");
        Long medicoId = parseId(args.get(1), "médico");
        Long servicioId = parseId(args.get(2), "servicio");
        String fecha = args.get(3);
        String horaInicio = args.get(4);
        String horaFin = args.get(5);
        String motivo = args.size() > 6 ? args.get(6) : "";

        CitaController controller = new CitaController(ctx, session, new CitaService());
        return controller.crearCita(pacienteId, medicoId, servicioId, fecha, horaInicio, horaFin, motivo);
    }

    // cita obtener <id>
    private CommandResponse obtener(List<String> args) {
        if (args.size() != 1) {
            throw new InvalidArgumentException("Argumentos erróneos para 'obtener'. Se requiere 1 argumento: <id>");
        }

        Long id = parseId(args.get(0), "cita");

        CitaController controller = new CitaController(ctx, session, new CitaService());
        return controller.obtenerCita(id);
    }

    // cita listar
    private CommandResponse listar(List<String> args) {
        if (!args.isEmpty()) {
            throw new InvalidArgumentException("Argumentos erróneos para 'listar'. No se requieren argumentos.");
        }

        CitaController controller = new CitaController(ctx, session, new CitaService());
        return controller.listarCitas();
    }

    // cita porpaciente <pacienteId>
    private CommandResponse porPaciente(List<String> args) {
        if (args.size() != 1) {
            throw new InvalidArgumentException("Argumentos erróneos para 'porpaciente'. Se requiere 1 argumento: <pacienteId>");
        }

        Long pacienteId = parseId(args.get(0), "paciente");

        CitaController controller = new CitaController(ctx, session, new CitaService());
        return controller.listarPorPaciente(pacienteId);
    }

    // cita pormedico <medicoId>
    private CommandResponse porMedico(List<String> args) {
        if (args.size() != 1) {
            throw new InvalidArgumentException("Argumentos erróneos para 'pormedico'. Se requiere 1 argumento: <medicoId>");
        }

        Long medicoId = parseId(args.get(0), "médico");

        CitaController controller = new CitaController(ctx, session, new CitaService());
        return controller.listarPorMedico(medicoId);
    }

    // cita reprogramar <id> <nuevaFecha> <nuevaHoraInicio> <nuevaHoraFin>
    private CommandResponse reprogramar(List<String> args) {
        if (args.size() != 4) {
            throw new InvalidArgumentException(
                "Argumentos erróneos para 'reprogramar'. Se requieren 4 argumentos: " +
                "<id> <nuevaFecha> <nuevaHoraInicio> <nuevaHoraFin>");
        }

        Long id = parseId(args.get(0), "cita");
        String nuevaFecha = args.get(1);
        String nuevaHoraInicio = args.get(2);
        String nuevaHoraFin = args.get(3);

        CitaController controller = new CitaController(ctx, session, new CitaService());
        return controller.reprogramarCita(id, nuevaFecha, nuevaHoraInicio, nuevaHoraFin);
    }

    // cita cancelar <id> [motivo]
    private CommandResponse cancelar(List<String> args) {
        if (args.size() < 1 || args.size() > 2) {
            throw new InvalidArgumentException(
                "Argumentos erróneos para 'cancelar'. Se requieren entre 1 y 2 argumentos: <id> [motivo]");
        }

        Long id = parseId(args.get(0), "cita");
        String motivo = args.size() > 1 ? args.get(1) : "";

        CitaController controller = new CitaController(ctx, session, new CitaService());
        return controller.cancelarCita(id, motivo);
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