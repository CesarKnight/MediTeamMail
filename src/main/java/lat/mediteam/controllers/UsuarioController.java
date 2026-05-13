package lat.mediteam.controllers;

import java.util.List;
import java.util.Optional;

import lat.mediteam.models.Usuario;
import lat.mediteam.services.UsuarioService;
import lat.mediteam.commands.CommandResponse;

public class UsuarioController {
    
    private UsuarioService service;
    
    public UsuarioController(UsuarioService service){
        this.service = service;
    }

    public CommandResponse crearUsuario(String email, String password) {
        try {
            Usuario nuevoUsuario = service.crear(email, password);
            return new CommandResponse(true, "Usuario creado: " + nuevoUsuario.getEmail());
        } catch (Exception e) {
            return new CommandResponse(false, e.getMessage());
        }
    }


    public CommandResponse obtenerUsuario(Long id) {
        try {
            Optional<Usuario> usuario = service.obtenerPorId(id);
            if (usuario.isPresent()) {
                return new CommandResponse(true, "Usuario: " + usuario.get().getEmail());
            } else {
                return new CommandResponse(false, "Usuario no encontrado");
            }
        } catch (Exception e) {
            return new CommandResponse(false, e.getMessage());
        }
    }

    
    public CommandResponse listarUsuarios() {
        try {
            List<Usuario> usuarios = service.listar();
            if (usuarios.isEmpty()) {
                return new CommandResponse(true, "No hay usuarios");
            }

            StringBuilder resultado = new StringBuilder();
            for (Usuario u : usuarios) {
                if (resultado.length() > 0) {
                    resultado.append(System.lineSeparator());
                }
                resultado.append(u.getId()).append(" - ").append(u.getEmail());
            }

            return new CommandResponse(true, resultado.toString());
        } catch (Exception e) {
            return new CommandResponse(false, e.getMessage());
        }
    }

    public CommandResponse editarUsuario(Long id, String email, String password) {
        try {
            Usuario actualizado = service.actualizar(id, email, password);
            return new CommandResponse(true, "Usuario actualizado: " + actualizado.getEmail());
        } catch (Exception e) {
            return new CommandResponse(false, e.getMessage());
        }
    }

    public CommandResponse eliminarUsuario(Long id) {
        try {
            boolean ok = service.eliminar(id);
            return ok
                ? new CommandResponse(true, "Usuario eliminado")
                : new CommandResponse(false, "Usuario no encontrado");
        } catch (Exception e) {
            return new CommandResponse(false, e.getMessage());
        }
    }

}
