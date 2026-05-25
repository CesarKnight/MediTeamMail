package lat.mediteam.commands;

import java.util.List;

import lat.mediteam.controllers.UsuarioController;
import lat.mediteam.core.AppContext;
import lat.mediteam.core.Session;
import lat.mediteam.enums.UsuarioTipo;
import lat.mediteam.services.UsuarioService;

public class UsuarioCommands implements Command {
    AppContext ctx;
    Session session;

    public CommandResponse execute(AppContext ctx, Session session, List<String> args) {
        if (args.isEmpty()) {
            return new CommandResponse(false, "No se proporcionó ningún comando");
        }

        this.ctx = ctx;
        this.session = session;

        String subCommand = args.get(1).toLowerCase();

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
                return new CommandResponse(false, "Subcomando desconocido: " + subCommand);
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
            return new CommandResponse(false, "Argumentos erroneos \n" + getHelp());
        }

        String email = args.get(0);
        String password = args.get(1);
        String tipoStr = args.get(2).toLowerCase();

        UsuarioTipo tipo;
        try {
            tipo = UsuarioTipo.valueOf(tipoStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return new CommandResponse(false, "Tipo de usuario inválido: " + tipoStr);
        }

        UsuarioController controller = new UsuarioController(ctx, session, new UsuarioService());
        return controller.crearUsuario(email, password, tipo);
    }

    private CommandResponse editar(List<String> args){
        if (args.size() != 3) {
            return new CommandResponse(false, "Argumentos erroneos \n" + getHelp());
        }

        Long id;
        try {
            id = Long.parseLong(args.get(0));
        } catch (NumberFormatException e) {
            return new CommandResponse(false, "ID de usuario inválido: " + args.get(0));
        }

        String email = args.get(1);
        String password = args.get(2);

        UsuarioController controller = new UsuarioController(ctx, session, new UsuarioService());
        return controller.editarUsuario(id, email, password);
    }

    private CommandResponse eliminar(List<String> args){
        if (args.size() != 1) {
            return new CommandResponse(false, "Argumentos erroneos \n" + getHelp());
        }

        Long id;
        try {
            id = Long.parseLong(args.get(0));
        } catch (NumberFormatException e) {
            return new CommandResponse(false, "ID de usuario inválido: " + args.get(0));
        }

        UsuarioController controller = new UsuarioController(ctx, session, new UsuarioService());
        return controller.eliminarUsuario(id);
    }

    private CommandResponse obtener(List<String> args){
        if (args.size() != 1) {
            return new CommandResponse(false, "Argumentos erroneos \n" + getHelp());
        }

        Long id;
        try {
            id = Long.parseLong(args.get(0));
        } catch (NumberFormatException e) {
            return new CommandResponse(false, "ID de usuario inválido: " + args.get(0));
        }

        UsuarioController controller = new UsuarioController(ctx, session, new UsuarioService());
        return controller.obtenerUsuario(id);
    }

    private CommandResponse listar(List<String> args){
        // if (!args.isEmpty()) {
        //     return new CommandResponse(false, "Argumentos erroneos \n" + getHelp());
        // }

        UsuarioController controller = new UsuarioController(ctx, session, new UsuarioService());
        return controller.listarUsuarios();
    }

}
