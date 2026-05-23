package lat.mediteam.commands;

import java.util.concurrent.Callable;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import lat.mediteam.controllers.ServicioController;
import lat.mediteam.enums.ServicioEstado;
import lat.mediteam.services.ServicioService;

@Command(
    name = "servicio",
    description = "Gestion de servicios medicos",
    subcommands = {
        ServicioCommands.Crear.class,
        ServicioCommands.Obtener.class,
        ServicioCommands.Listar.class,
        ServicioCommands.ListarDisponibles.class,
        ServicioCommands.Buscar.class,
        ServicioCommands.Editar.class,
        ServicioCommands.Eliminar.class,
    }
)
public class ServicioCommands {

    // servicio crear <titulo> <descripcion> <precio> <duracion> <estado>
    // Ejemplo: servicio crear Cardiologia ConsultaCardiologica 150.0 30min DISPONIBLE
    @Command(name = "crear")
    static class Crear implements Callable<CommandResponse> {

        @Parameters(index = "0") String titulo;
        @Parameters(index = "1") String descripcion;
        @Parameters(index = "2") Double precio;
        @Parameters(index = "3", defaultValue = "") String duracion;
        @Parameters(index = "4", defaultValue = "DISPONIBLE") ServicioEstado estado;

        @Override
        public CommandResponse call() {
            ServicioController controller = new ServicioController(new ServicioService());
            return controller.crearServicio(titulo, descripcion, precio, duracion, estado);
        }
    }

    // servicio obtener <id>
    // Ejemplo: servicio obtener 1
    @Command(name = "obtener")
    static class Obtener implements Callable<CommandResponse> {

        @Parameters(index = "0") Long id;

        @Override
        public CommandResponse call() {
            ServicioController controller = new ServicioController(new ServicioService());
            return controller.obtenerServicio(id);
        }
    }

    // servicio listar
    @Command(name = "listar")
    static class Listar implements Callable<CommandResponse> {

        @Override
        public CommandResponse call() {
            ServicioController controller = new ServicioController(new ServicioService());
            return controller.listarServicios();
        }
    }

    // servicio disponibles
    @Command(name = "disponibles")
    static class ListarDisponibles implements Callable<CommandResponse> {

        @Override
        public CommandResponse call() {
            ServicioController controller = new ServicioController(new ServicioService());
            return controller.listarDisponibles();
        }
    }

    // servicio buscar <titulo>
    // Ejemplo: servicio buscar Cardio
    @Command(name = "buscar")
    static class Buscar implements Callable<CommandResponse> {

        @Parameters(index = "0") String titulo;

        @Override
        public CommandResponse call() {
            ServicioController controller = new ServicioController(new ServicioService());
            return controller.buscarServicio(titulo);
        }
    }

    // servicio editar <id> <titulo> <descripcion> <precio> <duracion> <estado>
    // Ejemplo: servicio editar 1 Cardiologia ConsultaEspecializada 200.0 45min DISPONIBLE
    @Command(name = "editar")
    static class Editar implements Callable<CommandResponse> {

        @Parameters(index = "0") Long id;
        @Parameters(index = "1") String titulo;
        @Parameters(index = "2") String descripcion;
        @Parameters(index = "3") Double precio;
        @Parameters(index = "4", defaultValue = "") String duracion;
        @Parameters(index = "5", defaultValue = "DISPONIBLE") ServicioEstado estado;

        @Override
        public CommandResponse call() {
            ServicioController controller = new ServicioController(new ServicioService());
            return controller.editarServicio(id, titulo, descripcion, precio, duracion, estado);
        }
    }

    // servicio eliminar <id>
    // Ejemplo: servicio eliminar 1
    @Command(name = "eliminar")
    static class Eliminar implements Callable<CommandResponse> {

        @Parameters(index = "0") Long id;

        @Override
        public CommandResponse call() {
            ServicioController controller = new ServicioController(new ServicioService());
            return controller.eliminarServicio(id);
        }
    }
}