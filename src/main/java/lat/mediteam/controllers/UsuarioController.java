package lat.mediteam.controllers;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import lat.mediteam.models.Usuario;
import lat.mediteam.services.UsuarioService;
import lat.mediteam.commands.CommandResponse;
import lat.mediteam.core.AppContext;
import lat.mediteam.core.Session;
import lat.mediteam.enums.UsuarioTipo;

public class UsuarioController {
    
    private UsuarioService service;
    
    public UsuarioController(AppContext ctx, Session session, UsuarioService service){
        this.service = service;
    }

    public CommandResponse crearUsuario(String email, String password, UsuarioTipo tipo) {
        Usuario nuevoUsuario = service.crear(email, password, tipo);
        return new CommandResponse(true, "Usuario creado: " + nuevoUsuario.getEmail());
    }


    public CommandResponse obtenerUsuario(Long id) {
        Optional<Usuario> u = service.obtenerPorId(id);
        if(u.isEmpty()) {
            throw new EntityNotFoundException("Usuario no encontrado: " + id);
        }
        
        Usuario usuario = u.get();
        return new CommandResponse(true, "Usuario: " + usuario.getEmail());
    }

    
    public CommandResponse listarUsuarios() {
        List<Usuario> usuarios = service.listar();
        if (usuarios.isEmpty()) {
            return new CommandResponse(true, "No hay usuarios");
        }

        StringBuilder resultado = new StringBuilder();
        for (Usuario u : usuarios) {
            if (resultado.length() > 0) {
                resultado.append(System.lineSeparator());
            }
            resultado.append(u.getId()).append(" - ").append(u.getEmail()).append(" - ").append(u.getTipo());
        }

        return new CommandResponse(true, resultado.toString());
    }

    public CommandResponse editarUsuario(Long id, String email, String password) {
        Usuario actualizado = service.actualizar(id, email, password);
        return new CommandResponse(true, "Usuario actualizado: " + actualizado.getEmail());
    }

    public CommandResponse eliminarUsuario(Long id) {
        service.eliminar(id);
        return new CommandResponse(true, "Usuario eliminado");
    }
}
