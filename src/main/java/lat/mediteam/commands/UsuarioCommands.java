package lat.mediteam.commands;

import java.util.List;

import lat.mediteam.controllers.UsuarioController;
import lat.mediteam.core.AppContext;
import lat.mediteam.core.Session;
import lat.mediteam.enums.UsuarioTipo;
import lat.mediteam.services.UsuarioService;
import lat.mediteam.exceptions.InvalidArgumentException;

public class UsuarioCommands implements Command {
    AppContext ctx;
    Session session;

    public CommandResponse execute(AppContext ctx, Session session, List<String> args) {
        if (args.isEmpty()) {
            throw new InvalidArgumentException( "No se proporcionó ningún subcomando para 'usuario'");
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
            case "editar":
                return editar(args);
            case "eliminar":
                return eliminar(args);
            default:
                throw new InvalidArgumentException("Subcomando de usuario inválido: " + subCommand);
        }
    
    }

    public String getHelp() {
        return "Comandos de usuario:\n" +
               "  usuario crear <email> <password> <tipo> - Crea un nuevo usuario\n" +
               "  usuario obtener <id> - Obtiene los detalles de un usuario por ID\n" +
               "  usuario listar - Lista todos los usuarios\n" +
               "  usuario editar <id> <email> <password> - Edita un usuario existente\n" +
               "  usuario eliminar <id> - Elimina un usuario por ID";
    }
        
    private CommandResponse crear(List<String> args){
        if (args.size() != 3) {
            throw new InvalidArgumentException("Argumentos erróneos para 'crear'. Se requieren 3 argumentos: <email> <password> <tipo>");
        }

        String email = args.get(0);
        String password = args.get(1);
        String tipoStr = args.get(2).toLowerCase();

        UsuarioTipo tipo = parseTipo(tipoStr);

        UsuarioController controller = new UsuarioController(ctx, session, new UsuarioService());
        return controller.crearUsuario(email, password, tipo);
    }

    private CommandResponse editar(List<String> args){
        if (args.size() != 3) {
            throw new InvalidArgumentException("Argumentos erróneos para 'editar'. Se requieren 3 argumentos: <id> <email> <password>");
        }

        Long id = parseId(args.get(0));
        String email = args.get(1);
        String password = args.get(2);

        UsuarioController controller = new UsuarioController(ctx, session, new UsuarioService());
        return controller.editarUsuario(id, email, password);
    }

    private CommandResponse eliminar(List<String> args){
        if (args.size() != 1) {
            throw new InvalidArgumentException("Argumentos erróneos para 'eliminar'. Se requiere 1 argumento: <id>");
        }

        Long id = parseId(args.get(0));

        UsuarioController controller = new UsuarioController(ctx, session, new UsuarioService());
        return controller.eliminarUsuario(id);
    }

    private CommandResponse obtener(List<String> args){
        if (args.size() != 1) {
            throw new InvalidArgumentException("Argumentos erróneos para 'obtener'. Se requiere 1 argumento: <id>");
        }

        Long id = parseId(args.get(0));

        UsuarioController controller = new UsuarioController(ctx, session, new UsuarioService());
        return controller.obtenerUsuario(id);
    }

    private CommandResponse listar(List<String> args){
        if (!args.isEmpty()) {
            throw new InvalidArgumentException("Argumentos erróneos para 'listar'. No se requieren argumentos.");
        }

        UsuarioController controller = new UsuarioController(ctx, session, new UsuarioService());
        return controller.listarUsuarios();
    }

    // validaciones 
    private UsuarioTipo parseTipo(String tipoStr) {
        try {
            return UsuarioTipo.valueOf(tipoStr.toUpperCase());
        } catch (Exception e) {
            throw new InvalidArgumentException("Tipo de usuario inválido: " + tipoStr);
        }
    }

    private Long parseId(String idStr) {
        try {
            return Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            throw new InvalidArgumentException("ID de usuario inválido: " + idStr);
        }
    }
}
