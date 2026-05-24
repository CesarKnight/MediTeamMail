package lat.mediteam.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import lat.mediteam.controllers.HistoriaClinicaController;
import lat.mediteam.controllers.MedicosInvolucradosController;
import lat.mediteam.services.HistoriaClinicaService;
import lat.mediteam.services.MedicosInvolucradosService;

import java.util.concurrent.Callable;

@Command(
    name = "historia",
    description = "Gestion de historias clinicas",
    subcommands = {
        HistoriaClinicaCommands.Crear.class,
        HistoriaClinicaCommands.Obtener.class,
        HistoriaClinicaCommands.Listar.class,
        HistoriaClinicaCommands.Editar.class,
        HistoriaClinicaCommands.Eliminar.class,
        HistoriaClinicaCommands.AgregarMedico.class,
        HistoriaClinicaCommands.RemoverMedico.class
    }
)
public class HistoriaClinicaCommands {

    @Command(name = "crear")
    static class Crear implements Callable<CommandResponse> {
        @Parameters(index = "0")
        Long medicoId;

        @Parameters(index = "1")
        String estado;

        @Parameters(index = "2")
        String tipo;

        @Override
        public CommandResponse call() {
            HistoriaClinicaController controller = new HistoriaClinicaController(new HistoriaClinicaService());
            return controller.crearHistoria(medicoId, estado, tipo);
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
        String estado;

        @Parameters(index = "2")
        String tipo;

        @Override
        public CommandResponse call() {
            HistoriaClinicaController controller = new HistoriaClinicaController(new HistoriaClinicaService());
            return controller.editarHistoria(id, estado, tipo);
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

    @Command(name = "agregar_medico")
    static class AgregarMedico implements Callable<CommandResponse> {
        @Parameters(index = "0")
        Long historiaId;

        @Parameters(index = "1")
        Long medicoId;

        @Override
        public CommandResponse call() {
            MedicosInvolucradosController controller = new MedicosInvolucradosController(new MedicosInvolucradosService());
            return controller.asignarMedico(medicoId, historiaId);
        }
    }

    @Command(name = "remover_medico")
    static class RemoverMedico implements Callable<CommandResponse> {
        @Parameters(index = "0")
        Long historiaId;

        @Parameters(index = "1")
        Long medicoId;

        @Override
        public CommandResponse call() {
            MedicosInvolucradosController controller = new MedicosInvolucradosController(new MedicosInvolucradosService());
            return controller.removerMedico(medicoId, historiaId);
        }
    }
}