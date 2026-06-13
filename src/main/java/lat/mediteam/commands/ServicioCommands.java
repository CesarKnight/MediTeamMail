package lat.mediteam.commands;

import java.util.List;

import lat.mediteam.controllers.ServicioController;
import lat.mediteam.core.AppContext;
import lat.mediteam.core.Session;
import lat.mediteam.enums.ServicioEstado;
import lat.mediteam.exceptions.InvalidArgumentException;
import lat.mediteam.services.ServicioService;

public class ServicioCommands implements Command {
    AppContext ctx;
    Session session;

    public CommandResponse execute(AppContext ctx, Session session, List<String> args) {
        if (args.isEmpty()) {
            throw new InvalidArgumentException("No se proporcionó ningún subcomando para 'servicio'");
        }

        this.ctx = ctx;
        this.session = session;

        String subCommand = args.remove(0).toLowerCase();

        switch (subCommand) {
            case "crear":
                return crear(args);
            case "obtener":
                return obtener(args);
            case "listar":
                return listar(args);
            case "disponibles":
                return disponibles(args);
            case "buscar":
                return buscar(args);
            case "editar":
                return editar(args);
            case "eliminar":
                return eliminar(args);
            default:
                throw new InvalidArgumentException("Subcomando de servicio inválido: " + subCommand);
        }
    }

    public String getHelp() {
        return "Comandos de servicio:\n" +
               "  servicio crear <titulo> <descripcion> <precio> [duracion] [estado] - Crea un nuevo servicio\n" +
               "  servicio obtener <id> - Obtiene los detalles de un servicio por ID\n" +
               "  servicio listar - Lista todos los servicios\n" +
               "  servicio disponibles - Lista los servicios disponibles\n" +
               "  servicio buscar <titulo> - Busca servicios por título\n" +
               "  servicio editar <id> <titulo> <descripcion> <precio> [duracion] [estado] - Edita un servicio existente\n" +
               "  servicio eliminar <id> - Elimina un servicio por ID";
    }

    // servicio crear <titulo> <descripcion> <precio> [duracion] [estado]
    private CommandResponse crear(List<String> args) {
        if (args.size() < 3 || args.size() > 5) {
            throw new InvalidArgumentException(
                "Argumentos erróneos para 'crear'. Se requieren entre 3 y 5 argumentos: " +
                "<titulo> <descripcion> <precio> [duracion] [estado]");
        }

        String titulo = args.get(0);
        String descripcion = args.get(1);
        Double precio = parsePrecio(args.get(2));
        String duracion = args.size() > 3 ? args.get(3) : "";
        ServicioEstado estado = args.size() > 4 ? parseEstado(args.get(4)) : ServicioEstado.DISPONIBLE;

        ServicioController controller = new ServicioController(ctx, session, new ServicioService());
        return controller.crearServicio(titulo, descripcion, precio, duracion, estado);
    }

    // servicio obtener <id>
    private CommandResponse obtener(List<String> args) {
        if (args.size() != 1) {
            throw new InvalidArgumentException("Argumentos erróneos para 'obtener'. Se requiere 1 argumento: <id>");
        }

        Long id = parseId(args.get(0));

        ServicioController controller = new ServicioController(ctx, session, new ServicioService());
        return controller.obtenerServicio(id);
    }

    // servicio listar
    private CommandResponse listar(List<String> args) {
        if (!args.isEmpty()) {
            throw new InvalidArgumentException("Argumentos erróneos para 'listar'. No se requieren argumentos.");
        }

        ServicioController controller = new ServicioController(ctx, session, new ServicioService());
        return controller.listarServicios();
    }

    // servicio disponibles
    private CommandResponse disponibles(List<String> args) {
        if (!args.isEmpty()) {
            throw new InvalidArgumentException("Argumentos erróneos para 'disponibles'. No se requieren argumentos.");
        }

        ServicioController controller = new ServicioController(ctx, session, new ServicioService());
        return controller.listarDisponibles();
    }

    // servicio buscar <titulo>
    private CommandResponse buscar(List<String> args) {
        if (args.size() != 1) {
            throw new InvalidArgumentException("Argumentos erróneos para 'buscar'. Se requiere 1 argumento: <titulo>");
        }

        ServicioController controller = new ServicioController(ctx, session, new ServicioService());
        return controller.buscarServicio(args.get(0));
    }

    // servicio editar <id> <titulo> <descripcion> <precio> [duracion] [estado]
    private CommandResponse editar(List<String> args) {
        if (args.size() < 4 || args.size() > 6) {
            throw new InvalidArgumentException(
                "Argumentos erróneos para 'editar'. Se requieren entre 4 y 6 argumentos: " +
                "<id> <titulo> <descripcion> <precio> [duracion] [estado]");
        }

        Long id = parseId(args.get(0));
        String titulo = args.get(1);
        String descripcion = args.get(2);
        Double precio = parsePrecio(args.get(3));
        String duracion = args.size() > 4 ? args.get(4) : "";
        ServicioEstado estado = args.size() > 5 ? parseEstado(args.get(5)) : ServicioEstado.DISPONIBLE;

        ServicioController controller = new ServicioController(ctx, session, new ServicioService());
        return controller.editarServicio(id, titulo, descripcion, precio, duracion, estado);
    }

    // servicio eliminar <id>
    private CommandResponse eliminar(List<String> args) {
        if (args.size() != 1) {
            throw new InvalidArgumentException("Argumentos erróneos para 'eliminar'. Se requiere 1 argumento: <id>");
        }

        Long id = parseId(args.get(0));

        ServicioController controller = new ServicioController(ctx, session, new ServicioService());
        return controller.eliminarServicio(id);
    }

    // validaciones
    private Long parseId(String idStr) {
        try {
            return Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            throw new InvalidArgumentException("ID de servicio inválido: " + idStr);
        }
    }

    private Double parsePrecio(String precioStr) {
        try {
            return Double.parseDouble(precioStr);
        } catch (NumberFormatException e) {
            throw new InvalidArgumentException("Precio inválido: " + precioStr);
        }
    }

    private ServicioEstado parseEstado(String estadoStr) {
        try {
            return ServicioEstado.valueOf(estadoStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidArgumentException("Estado de servicio inválido: " + estadoStr);
        }
    }
}