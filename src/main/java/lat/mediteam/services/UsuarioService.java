package lat.mediteam.services;

import jakarta.persistence.EntityManager;
import lat.mediteam.core.DatabaseManager;

public class UsuarioService {
    
    private EntityManager emf;

    public UsuarioService() {
        this.emf = DatabaseManager.getEntityManager();
    }

    public void crear(String email, String password, String tipoUsuario) {
        // Lógica para crear un usuario
        System.out.println(emf.toString());
        System.out.println("Creando usuario con email: " + email + ", tipo: " + tipoUsuario);
    }
}
