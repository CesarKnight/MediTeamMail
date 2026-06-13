package lat.mediteam.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import lat.mediteam.controllers.ConsultaController;
import lat.mediteam.services.ConsultaService;

import java.util.concurrent.Callable;

@Command(
    name = "consulta",
    description = "Gestion de consultas",
    subcommands = {
        ConsultaCommands.Crear.class,
        ConsultaCommands.Obtener.class,
        ConsultaCommands.Listar.class,
        ConsultaCommands.Editar.class,
        ConsultaCommands.Eliminar.class
    }
)
public class ConsultaCommands {

    @Command(name = "crear")
    static class Crear implements Callable<CommandResponse> {
        @Parameters(index = "0")
        Long historiaId;

        @Parameters(index = "1")
        String descripcion;

        @Override
        public CommandResponse call() {
            ConsultaController controller = new ConsultaController(new ConsultaService());
            return controller.crearConsulta(historiaId, descripcion);
        }
    }

    @Command(name = "obtener")
    static class Obtener implements Callable<CommandResponse> {
        @Parameters(index = "0")
        Long id;

        @Override
        public CommandResponse call() {
            ConsultaController controller = new ConsultaController(new ConsultaService());
            return controller.obtenerConsulta(id);
        }
    }

    @Command(name = "listar")
    static class Listar implements Callable<CommandResponse> {
        @Override
        public CommandResponse call() {
            ConsultaController controller = new ConsultaController(new ConsultaService());
            return controller.listarConsultas();
        }
    }

    @Command(name = "editar")
    static class Editar implements Callable<CommandResponse> {
        @Parameters(index = "0")
        Long id;

        @Parameters(index = "1")
        String descripcion;

        @Override
        public CommandResponse call() {
            ConsultaController controller = new ConsultaController(new ConsultaService());
            return controller.editarConsulta(id, descripcion);
        }
    }

    @Command(name = "eliminar")
    static class Eliminar implements Callable<CommandResponse> {
        @Parameters(index = "0")
        Long id;

        @Override
        public CommandResponse call() {
            ConsultaController controller = new ConsultaController(new ConsultaService());
            return controller.eliminarConsulta(id);
        }
    }
}