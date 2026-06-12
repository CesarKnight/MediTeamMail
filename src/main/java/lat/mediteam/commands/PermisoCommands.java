package lat.mediteam.commands;

import java.util.concurrent.Callable;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import lat.mediteam.controllers.PermisoController;
import lat.mediteam.services.PermisoService;

@Command(
    name = "permiso",
    description = "Gestión de permisos y asignaciones",
    subcommands = {
        PermisoCommands.Crear.class,
        PermisoCommands.Obtener.class,
        PermisoCommands.Listar.class,
        PermisoCommands.Editar.class,
        PermisoCommands.Eliminar.class,
        PermisoCommands.Asignar.class,
        PermisoCommands.Remover.class,
        PermisoCommands.ListarDeUsuario.class
    }
)
public class PermisoCommands {

    // permiso crear <nombre> <descripcion>
    @Command(name = "crear")
    static class Crear implements Callable<CommandResponse> {
        @Parameters(index = "0") String nombre;
        @Parameters(index = "1", defaultValue = "") String descripcion;

        @Override
        public CommandResponse call() {
            PermisoController c = new PermisoController(new PermisoService());
            return c.crearPermiso(nombre, descripcion);
        }
    }

    @Command(name = "obtener")
    static class Obtener implements Callable<CommandResponse> {
        @Parameters(index = "0") Long id;

        @Override
        public CommandResponse call() {
            PermisoController c = new PermisoController(new PermisoService());
            return c.obtenerPermiso(id);
        }
    }

    @Command(name = "listar")
    static class Listar implements Callable<CommandResponse> {
        @Override
        public CommandResponse call() {
            PermisoController c = new PermisoController(new PermisoService());
            return c.listarPermisos();
        }
    }

    @Command(name = "editar")
    static class Editar implements Callable<CommandResponse> {
        @Parameters(index = "0") Long id;
        @Parameters(index = "1") String nombre;
        @Parameters(index = "2", defaultValue = "") String descripcion;

        @Override
        public CommandResponse call() {
            PermisoController c = new PermisoController(new PermisoService());
            return c.editarPermiso(id, nombre, descripcion);
        }
    }

    @Command(name = "eliminar")
    static class Eliminar implements Callable<CommandResponse> {
        @Parameters(index = "0") Long id;

        @Override
        public CommandResponse call() {
            PermisoController c = new PermisoController(new PermisoService());
            return c.eliminarPermiso(id);
        }
    }

    // permiso asignar <usuarioId> <permisoId>
    @Command(name = "asignar")
    static class Asignar implements Callable<CommandResponse> {
        @Parameters(index = "0") Long usuarioId;
        @Parameters(index = "1") Long permisoId;

        @Override
        public CommandResponse call() {
            PermisoController c = new PermisoController(new PermisoService());
            return c.asignarPermiso(usuarioId, permisoId);
        }
    }

    // permiso remover <usuarioId> <permisoId>
    @Command(name = "remover")
    static class Remover implements Callable<CommandResponse> {
        @Parameters(index = "0") Long usuarioId;
        @Parameters(index = "1") Long permisoId;

        @Override
        public CommandResponse call() {
            PermisoController c = new PermisoController(new PermisoService());
            return c.removerPermiso(usuarioId, permisoId);
        }
    }

    // permiso listardeusuario <usuarioId>
    @Command(name = "listardeusuario")
    static class ListarDeUsuario implements Callable<CommandResponse> {
        @Parameters(index = "0") Long usuarioId;

        @Override
        public CommandResponse call() {
            PermisoController c = new PermisoController(new PermisoService());
            return c.listarPermisosDeUsuario(usuarioId);
        }
    }
}