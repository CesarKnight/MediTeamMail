package lat.mediteam.commands;

import java.util.concurrent.Callable;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import lat.mediteam.controllers.AdminController;
import lat.mediteam.services.AdminService;

@Command(
    name = "admin",
    description = "Gestion de admin",
    subcommands = {
        AdminCommands.Crear.class,
        AdminCommands.Obtener.class,
        AdminCommands.Listar.class,
        AdminCommands.Editar.class,
        AdminCommands.Eliminar.class,
    }
)
public class AdminCommands {
    
    @Command(name = "crear")
    static class Crear implements Callable<CommandResponse> {
        @Parameters(index = "0")
        Long usuarioId;

        @Parameters(index = "1")
        String nombre;

        @Parameters(index = "2")
        String apellido;

        @Override
        public CommandResponse call() {
            AdminController controller = new AdminController(new AdminService());
            return controller.crearAdmin(usuarioId, nombre, apellido);
        }
    }

    @Command(name = "obtener")
    static class Obtener implements Callable<CommandResponse> {
        @Parameters(index = "0")
        Long id;

        @Override
        public CommandResponse call() {
            AdminController controller = new AdminController(new AdminService());
            return controller.obtenerAdmin(id);
        }
    }

    @Command(name = "listar")
    static class Listar implements Callable<CommandResponse> {

        @Override
        public CommandResponse call() {
            AdminController controller = new AdminController(new AdminService());
            return controller.listarAdmins();
        }
    }

    @Command(name = "editar")
    static class Editar implements Callable<CommandResponse> {
        @Parameters(index = "0")
        Long usuarioId;

        @Parameters(index = "1")
        String nombre;

        @Parameters(index = "2")
        String apellido;

        @Override
        public CommandResponse call() {
            AdminController controller = new AdminController(new AdminService());
            return controller.editarAdmin(usuarioId, nombre, apellido);
        }
    }

    @Command(name = "eliminar")
    static class Eliminar implements Callable<CommandResponse> {
        @Parameters(index = "0")
        Long id;

        @Override
        public CommandResponse call() {
            AdminController controller = new AdminController(new AdminService());
            return controller.eliminarAdmin(id);
        }
    }
}
