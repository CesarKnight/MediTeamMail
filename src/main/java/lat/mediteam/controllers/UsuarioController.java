package lat.mediteam.controllers;

import java.util.List;
import java.util.Optional;

import lat.mediteam.models.Usuario;
import lat.mediteam.services.UsuarioService;

public class UsuarioController {
    
    private UsuarioService service;
    
    public UsuarioController(UsuarioService service){
        this.service = service;
    }

    public String crearUsuario(String email, String password) {
        try {
            Usuario nuevoUsuario = service.crear(email, password);
            return "Usuario creado: " + nuevoUsuario.getEmail();
        } catch (Exception e) {
            return e.getMessage();
        }
    }


    // por el momento solo devuelve string pero 
    // todo: definir un formato de respuesta estandarizado
    public String obtenerUsuario(Long id) {
        try {
            Optional<Usuario> usuario = service.obtenerPorId(id);
            if (usuario.isPresent()) {
                return "Usuario: " + usuario.get().getEmail();
            } else {
                return "Usuario no encontrado";
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    
    public String listarUsuarios() {
        try {
            List<Usuario> usuarios = service.listar();
            if (usuarios.isEmpty()) {
                return "No hay usuarios";
            }

            StringBuilder resultado = new StringBuilder();
            for (Usuario u : usuarios) {
                if (resultado.length() > 0) {
                    resultado.append(System.lineSeparator());
                }
                resultado.append(u.getId()).append(" - ").append(u.getEmail());
            }

            return resultado.toString();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String editarUsuario(Long id, String email, String password) {
        try {
            Usuario actualizado = service.actualizar(id, email, password);
            return "Usuario actualizado: " + actualizado.getEmail();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String eliminarUsuario(Long id) {
        try {
            boolean ok = service.eliminar(id);
            return ok ? "Usuario eliminado" : "Usuario no encontrado";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

}
