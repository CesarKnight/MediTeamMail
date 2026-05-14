package lat.mediteam.commands;

import java.util.concurrent.Callable;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import lat.mediteam.controllers.UsuarioController;
import lat.mediteam.enums.UsuarioTipo;
import lat.mediteam.services.UsuarioService;

@Command(
    name = "usuario",
    description = "Gestion de usuario",
    subcommands = {
        UsuarioCommands.Crear.class,
        UsuarioCommands.Obtener.class,
        UsuarioCommands.Listar.class,
        UsuarioCommands.Editar.class,
        UsuarioCommands.Eliminar.class,
        // UsuarioCommands.AsignarPermiso.class,
        // UsuarioCommands.RemoverPermiso.class
    }
)
public class UsuarioCommands {
    
    @Command(name = "crear")
    static class Crear implements Callable<CommandResponse> {
        @Parameters(index = "0")
        String email;

        @Parameters(index = "1")
        String password;

        @Parameters(index = "2")
        UsuarioTipo tipo;

        @Override
        public CommandResponse call() {
            UsuarioController controller = new UsuarioController(new UsuarioService());
            return controller.crearUsuario(email, password, tipo);
        }
    }

    @Command(name = "obtener")
    static class Obtener implements Callable<CommandResponse> {
        @Parameters(index = "0")
        Long id;

        @Override
        public CommandResponse call() {
            UsuarioController controller = new UsuarioController(new UsuarioService());
            return controller.obtenerUsuario(id);
        }
    }

    @Command(name = "listar")
    static class Listar implements Callable<CommandResponse> {

        @Override
        public CommandResponse call() {
            UsuarioController controller = new UsuarioController(new UsuarioService());
            return controller.listarUsuarios();
        }
    }

    @Command(name = "editar")
    static class Editar implements Callable<CommandResponse> {
        @Parameters(index = "0")
        Long id;

        @Parameters(index = "1")
        String email;

        @Parameters(index = "2")
        String password;

        @Override
        public CommandResponse call() {
            UsuarioController controller = new UsuarioController(new UsuarioService());
            return controller.editarUsuario(id, email, password);
        }
    }

    @Command(name = "eliminar")
    static class Eliminar implements Callable<CommandResponse> {
        @Parameters(index = "0")
        Long id;

        @Override
        public CommandResponse call() {
            UsuarioController controller = new UsuarioController(new UsuarioService());
            return controller.eliminarUsuario(id);
        }
    }

    
}
