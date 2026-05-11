package lat.mediteam.controllers;

import lat.mediteam.services.UsuarioService;

public class UsuarioController {
    
    private UsuarioService service;
    
    public UsuarioController(UsuarioService service){
        this.service = service;
    }

    public void crearUsuario(String email, String password, String tipoUsuario) {
        // Lógica para crear un usuario
        service.crear(email, password, tipoUsuario);
    }
}
