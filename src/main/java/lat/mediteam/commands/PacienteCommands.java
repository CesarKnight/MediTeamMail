package lat.mediteam.commands;

import java.util.concurrent.Callable;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import lat.mediteam.controllers.PacienteController;
import lat.mediteam.services.PacienteService;

@Command(
    name = "paciente",
    description = "Gestion de pacientes",
    subcommands = {
        PacienteCommands.Crear.class,
        PacienteCommands.Obtener.class,
        PacienteCommands.Listar.class,
        PacienteCommands.Editar.class,
        PacienteCommands.Eliminar.class,
    }
)
public class PacienteCommands {

    // paciente crear <usuarioId> <nombre> <apellido> <ci> <telefono> <email> <fechaNacimiento>
    // Ejemplo: paciente crear 2 Juan Perez 87654321 75512345 juan@gmail.com 1990-03-15
    @Command(name = "crear")
    static class Crear implements Callable<CommandResponse> {

        @Parameters(index = "0") Long usuarioId;
        @Parameters(index = "1") String nombre;
        @Parameters(index = "2") String apellido;
        @Parameters(index = "3") String ci;
        @Parameters(index = "4", defaultValue = "") String telefono;
        @Parameters(index = "5", defaultValue = "") String email;
        @Parameters(index = "6", defaultValue = "") String fechaNacimiento;

        @Override
        public CommandResponse call() {
            PacienteController controller = new PacienteController(new PacienteService());
            return controller.crearPaciente(usuarioId, nombre, apellido,
                                            ci, telefono, email, fechaNacimiento);
        }
    }

    // paciente obtener <id>
    // Ejemplo: paciente obtener 2
    @Command(name = "obtener")
    static class Obtener implements Callable<CommandResponse> {

        @Parameters(index = "0") Long id;

        @Override
        public CommandResponse call() {
            PacienteController controller = new PacienteController(new PacienteService());
            return controller.obtenerPaciente(id);
        }
    }

    // paciente listar
    @Command(name = "listar")
    static class Listar implements Callable<CommandResponse> {

        @Override
        public CommandResponse call() {
            PacienteController controller = new PacienteController(new PacienteService());
            return controller.listarPacientes();
        }
    }

    // paciente editar <id> <nombre> <apellido> <telefono> <email>
    // Ejemplo: paciente editar 2 Juan Perez 75512345 juan@gmail.com
    @Command(name = "editar")
    static class Editar implements Callable<CommandResponse> {

        @Parameters(index = "0") Long id;
        @Parameters(index = "1") String nombre;
        @Parameters(index = "2") String apellido;
        @Parameters(index = "3", defaultValue = "") String telefono;
        @Parameters(index = "4", defaultValue = "") String email;

        @Override
        public CommandResponse call() {
            PacienteController controller = new PacienteController(new PacienteService());
            return controller.editarPaciente(id, nombre, apellido, telefono, email);
        }
    }

    // paciente eliminar <id>
    // Ejemplo: paciente eliminar 2
    @Command(name = "eliminar")
    static class Eliminar implements Callable<CommandResponse> {

        @Parameters(index = "0") Long id;

        @Override
        public CommandResponse call() {
            PacienteController controller = new PacienteController(new PacienteService());
            return controller.eliminarPaciente(id);
        }
    }
}