package lat.mediteam.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import lat.mediteam.controllers.DiagnosticoController;
import lat.mediteam.services.DiagnosticoService;

import java.util.concurrent.Callable;

@Command(
    name = "diagnostico",
    description = "Gestion de diagnosticos",
    subcommands = {
        DiagnosticoCommands.Crear.class,
        DiagnosticoCommands.Obtener.class,
        DiagnosticoCommands.Listar.class,
        DiagnosticoCommands.Editar.class,
        DiagnosticoCommands.Eliminar.class
    }
)
public class DiagnosticoCommands {

    @Command(name = "crear")
    static class Crear implements Callable<CommandResponse> {
        @Parameters(index = "0")
        Long historiaId;

        @Parameters(index = "1")
        String diagnostico;

        @Override
        public CommandResponse call() {
            DiagnosticoController controller = new DiagnosticoController(new DiagnosticoService());
            return controller.crearDiagnostico(historiaId, diagnostico);
        }
    }

    @Command(name = "obtener")
    static class Obtener implements Callable<CommandResponse> {
        @Parameters(index = "0")
        Long id;

        @Override
        public CommandResponse call() {
            DiagnosticoController controller = new DiagnosticoController(new DiagnosticoService());
            return controller.obtenerDiagnostico(id);
        }
    }

    @Command(name = "listar")
    static class Listar implements Callable<CommandResponse> {
        @Override
        public CommandResponse call() {
            DiagnosticoController controller = new DiagnosticoController(new DiagnosticoService());
            return controller.listarDiagnosticos();
        }
    }

    @Command(name = "editar")
    static class Editar implements Callable<CommandResponse> {
        @Parameters(index = "0")
        Long id;

        @Parameters(index = "1")
        String diagnostico;

        @Override
        public CommandResponse call() {
            DiagnosticoController controller = new DiagnosticoController(new DiagnosticoService());
            return controller.editarDiagnostico(id, diagnostico);
        }
    }

    @Command(name = "eliminar")
    static class Eliminar implements Callable<CommandResponse> {
        @Parameters(index = "0")
        Long id;

        @Override
        public CommandResponse call() {
            DiagnosticoController controller = new DiagnosticoController(new DiagnosticoService());
            return controller.eliminarDiagnostico(id);
        }
    }
}