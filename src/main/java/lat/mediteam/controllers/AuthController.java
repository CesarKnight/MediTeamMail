package lat.mediteam.controllers;
import java.util.Set;

import lat.mediteam.commands.CommandResponse;
import lat.mediteam.core.AppContext;
import lat.mediteam.core.Session;
import lat.mediteam.models.Usuario;
import lat.mediteam.services.UsuarioService;

public class AuthController {
    private AppContext ctx;
    private Session session;
    private UsuarioService service;

    public AuthController(AppContext ctx, Session session, UsuarioService service) {
        this.ctx = ctx;
		this.session = session;
        this.service = service;
    }

    public CommandResponse login(String password) {

        Usuario usuario = service.obtenerPorEmail(session.getEmail()).orElse(null);
        if (usuario == null) {
            return new CommandResponse(false, "Usuario no encontrado: " + session.getEmail());
        }

        //todo aqui poner logic de encriptacion despues

        if (usuario.getPassword().equals(password)) {
        
            Set<String> permisos = Set.of("permiso1", "permiso2"); // todo implementar permisos    

            ctx.getAuthManager().login(usuario.getId(), session.getEmail(),permisos); // todo implementar permisos
            return new CommandResponse(
                true, 
                "Login exitoso, Bienvenido \n " 
                + usuario.getTipo() + " " + usuario.getEmail()
            );
            
        } else {
            return new CommandResponse(false, "Credenciales inválidas para " + session.getEmail());
        }
    }


    public CommandResponse logout() {
        if (!session.isAuthenticated()) {
            return new CommandResponse(false, "No estás logueado");
        }

        ctx.getAuthManager().logout(session.getEmail());
        return new CommandResponse(true, "Logout exitoso para " + session.getEmail());
    }
}
