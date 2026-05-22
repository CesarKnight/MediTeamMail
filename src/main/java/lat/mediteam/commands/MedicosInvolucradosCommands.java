package lat.mediteam.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import lat.mediteam.controllers.MedicosInvolucradosController;
import lat.mediteam.services.MedicosInvolucradosService;

import java.util.concurrent.Callable;

@Command(
    name = "involucrados",
    description = "Asignar o remover medicos a historias clinicas",
    subcommands = {
        MedicosInvolucradosCommands.Asignar.class,
        MedicosInvolucradosCommands.Remover.class
    }
)
public class MedicosInvolucradosCommands {

    @Command(name = "asignar")
    static class Asignar implements Callable<CommandResponse> {
        @Parameters(index = "0")
        Long medicoId;

        @Parameters(index = "1")
        Long historiaId;

        @Override
        public CommandResponse call() {
            MedicosInvolucradosController controller = new MedicosInvolucradosController(new MedicosInvolucradosService());
            return controller.asignarMedico(medicoId, historiaId);
        }
    }

    @Command(name = "remover")
    static class Remover implements Callable<CommandResponse> {
        @Parameters(index = "0")
        Long medicoId;

        @Parameters(index = "1")
        Long historiaId;

        @Override
        public CommandResponse call() {
            MedicosInvolucradosController controller = new MedicosInvolucradosController(new MedicosInvolucradosService());
            return controller.removerMedico(medicoId, historiaId);
        }
    }
}