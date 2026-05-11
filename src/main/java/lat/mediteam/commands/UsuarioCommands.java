package lat.mediteam.commands;

import lat.mediteam.controllers.UsuarioController;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(
    name = "usuario",
    description = "Gestion de usuario",
    subcommands = {
        UsuarioCommands.Crear.class,
        // UsuarioCommands.Obtener.class,
        // UsuarioCommands.Editar.class,
        // UsuarioCommands.Eliminar.class,
        // UsuarioCommands.Listar.class,
        // UsuarioCommands.AsignarPermiso.class,
        // UsuarioCommands.RemoverPermiso.class
    }
)
public class UsuarioCommands {
    
    @Command(name = "crear")
    static class Crear implements Runnable {
        @Parameters(index = "0")
        String email;

        @Parameters(index = "1")
        String password;

        @Parameters(index = "2")
        String tipousuario;

        @Override
        public void run() {
            UsuarioController controller = new UsuarioController(new lat.mediteam.services.UsuarioService());
            controller.crearUsuario(email, password, tipousuario);
        }
    }
}
