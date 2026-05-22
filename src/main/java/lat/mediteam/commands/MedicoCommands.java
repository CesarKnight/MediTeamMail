package lat.mediteam.commands;

import java.util.concurrent.Callable;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import lat.mediteam.controllers.MedicoController;
import lat.mediteam.services.MedicoService;

@Command(
    name = "medico",
    description = "Gestion de medicos",
    subcommands = {
        MedicoCommands.Crear.class,
        MedicoCommands.Obtener.class,
        MedicoCommands.Listar.class,
        MedicoCommands.Editar.class,
        MedicoCommands.Eliminar.class
    }
)
public class MedicoCommands {
    
    @Command(name = "crear")
    static class Crear implements Callable<CommandResponse> {
        @Parameters(index = "0")
        Long usuarioId;

        @Parameters(index = "1")
        String nombre;

        @Parameters(index = "2")
        String apellido;

        @Parameters(index = "3")
        String ci;

        @Parameters(index = "4")
        String especialidad;

        @Parameters(index = "5")
        String fechaNacimiento;

        @Override
        public CommandResponse call() {
            MedicoController controller = new MedicoController(new MedicoService());
            return controller.crearMedico(usuarioId, nombre, apellido, ci, especialidad, fechaNacimiento);
        }
    }

    @Command(name = "obtener")
    static class Obtener implements Callable<CommandResponse> {
        @Parameters(index = "0")
        Long id;

        @Override
        public CommandResponse call() {
            MedicoController controller = new MedicoController(new MedicoService());
            return controller.obtenerMedico(id);
        }
    }

    @Command(name = "listar")
    static class Listar implements Callable<CommandResponse> {
        @Override
        public CommandResponse call() {
            MedicoController controller = new MedicoController(new MedicoService());
            return controller.listarMedicos();
        }
    }

    @Command(name = "editar")
    static class Editar implements Callable<CommandResponse> {
        @Parameters(index = "0")
        Long id;

        @Parameters(index = "1")
        String nombre;

        @Parameters(index = "2")
        String apellido;

        @Parameters(index = "3")
        String ci;

        @Parameters(index = "4")
        String especialidad;

        @Parameters(index = "5")
        String fechaNacimiento;

        @Override
        public CommandResponse call() {
            MedicoController controller = new MedicoController(new MedicoService());
            return controller.editarMedico(id, nombre, apellido, ci, especialidad, fechaNacimiento);
        }
    }

    @Command(name = "eliminar")
    static class Eliminar implements Callable<CommandResponse> {
        @Parameters(index = "0")
        Long id;

        @Override
        public CommandResponse call() {
            MedicoController controller = new MedicoController(new MedicoService());
            return controller.eliminarMedico(id);
        }
    }
}