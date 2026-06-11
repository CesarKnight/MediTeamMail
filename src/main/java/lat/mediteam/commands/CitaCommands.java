package lat.mediteam.commands;

import java.util.concurrent.Callable;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import lat.mediteam.controllers.CitaController;
import lat.mediteam.services.CitaService;

@Command(
    name = "cita",
    description = "Gestion de citas medicas",
    subcommands = {
        CitaCommands.Crear.class,
        CitaCommands.Obtener.class,
        CitaCommands.Listar.class,
        CitaCommands.ListarPorPaciente.class,
        CitaCommands.ListarPorMedico.class,
        CitaCommands.Reprogramar.class,
        CitaCommands.Cancelar.class,
    }
)
public class CitaCommands {

    // cita crear <pacienteId> <medicoId> <servicioId> <fecha> <horaInicio> <horaFin> <motivo>
    // Ejemplo: cita crear 1 2 1 2026-06-15 09:00 10:00 Consulta
    @Command(name = "crear")
    static class Crear implements Callable<CommandResponse> {

        @Parameters(index = "0") Long pacienteId;
        @Parameters(index = "1") Long medicoId;
        @Parameters(index = "2") Long servicioId;
        @Parameters(index = "3") String fecha;
        @Parameters(index = "4") String horaInicio;
        @Parameters(index = "5") String horaFin;
        @Parameters(index = "6", defaultValue = "") String motivo;

        @Override
        public CommandResponse call() {
            CitaController controller = new CitaController(new CitaService());
            return controller.crearCita(pacienteId, medicoId, servicioId,
                                        fecha, horaInicio, horaFin, motivo);
        }
    }

    // cita obtener <id>
    // Ejemplo: cita obtener 1
    @Command(name = "obtener")
    static class Obtener implements Callable<CommandResponse> {

        @Parameters(index = "0") Long id;

        @Override
        public CommandResponse call() {
            CitaController controller = new CitaController(new CitaService());
            return controller.obtenerCita(id);
        }
    }

    // cita listar
    @Command(name = "listar")
    static class Listar implements Callable<CommandResponse> {

        @Override
        public CommandResponse call() {
            CitaController controller = new CitaController(new CitaService());
            return controller.listarCitas();
        }
    }

    // cita porpaciente <pacienteId>
    // Ejemplo: cita porpaciente 1
    @Command(name = "porpaciente")
    static class ListarPorPaciente implements Callable<CommandResponse> {

        @Parameters(index = "0") Long pacienteId;

        @Override
        public CommandResponse call() {
            CitaController controller = new CitaController(new CitaService());
            return controller.listarPorPaciente(pacienteId);
        }
    }

    // cita pormedico <medicoId>
    // Ejemplo: cita pormedico 2
    @Command(name = "pormedico")
    static class ListarPorMedico implements Callable<CommandResponse> {

        @Parameters(index = "0") Long medicoId;

        @Override
        public CommandResponse call() {
            CitaController controller = new CitaController(new CitaService());
            return controller.listarPorMedico(medicoId);
        }
    }

    // cita reprogramar <id> <nuevaFecha> <nuevaHoraInicio> <nuevaHoraFin>
    // Ejemplo: cita reprogramar 1 2026-06-20 10:00 11:00
    @Command(name = "reprogramar")
    static class Reprogramar implements Callable<CommandResponse> {

        @Parameters(index = "0") Long id;
        @Parameters(index = "1") String nuevaFecha;
        @Parameters(index = "2") String nuevaHoraInicio;
        @Parameters(index = "3") String nuevaHoraFin;

        @Override
        public CommandResponse call() {
            CitaController controller = new CitaController(new CitaService());
            return controller.reprogramarCita(id, nuevaFecha, nuevaHoraInicio, nuevaHoraFin);
        }
    }

    // cita cancelar <id> <motivo>
    // Ejemplo: cita cancelar 1 PacienteNoDisponible
    @Command(name = "cancelar")
    static class Cancelar implements Callable<CommandResponse> {

        @Parameters(index = "0") Long id;
        @Parameters(index = "1", defaultValue = "") String motivo;

        @Override
        public CommandResponse call() {
            CitaController controller = new CitaController(new CitaService());
            return controller.cancelarCita(id, motivo);
        }
    }
}