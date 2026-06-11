package lat.mediteam.commands;
import java.util.List;
import lat.mediteam.controllers.AuthController;
import lat.mediteam.core.AppContext;
import lat.mediteam.core.Session;
import lat.mediteam.services.UsuarioService;
import lat.mediteam.exceptions.InvalidArgumentException;

public class AuthCommands {
    AppContext ctx;
    Session session;

    public CommandResponse login(AppContext ctx, Session session, List<String> args) {
        if (args.isEmpty()) {
            throw new InvalidArgumentException("No se proporcionó credenciales para login");
        }

        if (args.size() != 1) {
            throw new InvalidArgumentException("Argumentos erróneos para 'login'. Se requiere <password>");
        }
        
        this.ctx = ctx;
        this.session = session;
        String password = args.get(0);

        AuthController controller = new AuthController(ctx, session, new UsuarioService());
        return controller.login(password);
    }

    public CommandResponse logout(AppContext ctx, Session session, List<String> args) {
        if (args.size() != 0) {
            throw new InvalidArgumentException("Argumentos erróneos para 'logout'. No se requieren argumentos");
        }

        this.ctx = ctx;

        AuthController controller = new AuthController(ctx, session, new UsuarioService());
        return controller.logout();
    }
}
