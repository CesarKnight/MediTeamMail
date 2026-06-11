package lat.mediteam.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import lat.mediteam.controllers.TratamientoController;
import lat.mediteam.services.TratamientoService;

import java.util.concurrent.Callable;

@Command(
    name = "tratamiento",
    description = "Gestion de tratamientos",
    subcommands = {
        TratamientoCommands.Crear.class,
        TratamientoCommands.Obtener.class,
        TratamientoCommands.Listar.class,
        TratamientoCommands.Editar.class,
        TratamientoCommands.Eliminar.class
    }
)
public class TratamientoCommands {

    @Command(name = "crear")
    static class Crear implements Callable<CommandResponse> {
        @Parameters(index = "0")
        Long historiaId;

        @Parameters(index = "1")
        String tratamiento;

        @Override
        public CommandResponse call() {
            TratamientoController controller = new TratamientoController(new TratamientoService());
            return controller.crearTratamiento(historiaId, tratamiento);
        }
    }

    @Command(name = "obtener")
    static class Obtener implements Callable<CommandResponse> {
        @Parameters(index = "0")
        Long id;

        @Override
        public CommandResponse call() {
            TratamientoController controller = new TratamientoController(new TratamientoService());
            return controller.obtenerTratamiento(id);
        }
    }

    @Command(name = "listar")
    static class Listar implements Callable<CommandResponse> {
        @Override
        public CommandResponse call() {
            TratamientoController controller = new TratamientoController(new TratamientoService());
            return controller.listarTratamientos();
        }
    }

    @Command(name = "editar")
    static class Editar implements Callable<CommandResponse> {
        @Parameters(index = "0")
        Long id;

        @Parameters(index = "1")
        String tratamiento;

        @Override
        public CommandResponse call() {
            TratamientoController controller = new TratamientoController(new TratamientoService());
            return controller.editarTratamiento(id, tratamiento);
        }
    }

    @Command(name = "eliminar")
    static class Eliminar implements Callable<CommandResponse> {
        @Parameters(index = "0")
        Long id;

        @Override
        public CommandResponse call() {
            TratamientoController controller = new TratamientoController(new TratamientoService());
            return controller.eliminarTratamiento(id);
        }
    }
}