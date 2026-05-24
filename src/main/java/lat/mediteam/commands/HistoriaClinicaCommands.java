package lat.mediteam.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import lat.mediteam.controllers.HistoriaClinicaController;
import lat.mediteam.services.HistoriaClinicaService;

import java.util.concurrent.Callable;

@Command(
    name = "historia",
    description = "Gestion de historias clinicas",
    subcommands = {
        HistoriaClinicaCommands.Crear.class,
        HistoriaClinicaCommands.Obtener.class,
        HistoriaClinicaCommands.Listar.class,
        HistoriaClinicaCommands.Editar.class,
        HistoriaClinicaCommands.Eliminar.class
    }
)
public class HistoriaClinicaCommands {

    @Command(name = "crear")
    static class Crear implements Callable<CommandResponse> {
        @Parameters(index = "0")
        Long medicoId;

        @Parameters(index = "1")
        String fecha;

        @Parameters(index = "2")
        String estado;

        @Parameters(index = "3")
        String tipo;

        @Override
        public CommandResponse call() {
            HistoriaClinicaController controller = new HistoriaClinicaController(new HistoriaClinicaService());
            return controller.crearHistoria(medicoId, fecha, estado, tipo);
        }
    }

    @Command(name = "obtener")
    static class Obtener implements Callable<CommandResponse> {
        @Parameters(index = "0")
        Long id;

        @Override
        public CommandResponse call() {
            HistoriaClinicaController controller = new HistoriaClinicaController(new HistoriaClinicaService());
            return controller.obtenerHistoria(id);
        }
    }

    @Command(name = "listar")
    static class Listar implements Callable<CommandResponse> {
        @Override
        public CommandResponse call() {
            HistoriaClinicaController controller = new HistoriaClinicaController(new HistoriaClinicaService());
            return controller.listarHistorias();
        }
    }

    @Command(name = "editar")
    static class Editar implements Callable<CommandResponse> {
        @Parameters(index = "0")
        Long id;

        @Parameters(index = "1")
        String fecha;

        @Parameters(index = "2")
        String estado;

        @Parameters(index = "3")
        String tipo;

        @Override
        public CommandResponse call() {
            HistoriaClinicaController controller = new HistoriaClinicaController(new HistoriaClinicaService());
            return controller.editarHistoria(id, fecha, estado, tipo);
        }
    }

    @Command(name = "eliminar")
    static class Eliminar implements Callable<CommandResponse> {
        @Parameters(index = "0")
        Long id;

        @Override
        public CommandResponse call() {
            HistoriaClinicaController controller = new HistoriaClinicaController(new HistoriaClinicaService());
            return controller.eliminarHistoria(id);
        }
    }
}