package lat.mediteam.services;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import lat.mediteam.models.Admin;
import lat.mediteam.models.Usuario;
import lat.mediteam.core.DatabaseManager;

public class AdminService  {
    private EntityManager crearEntityManager() {
        return DatabaseManager.getEntityManager();
    }

    public Admin crear(Long usuarioId, String nombre, String apellido) {
        validarDatos(usuarioId, nombre, apellido);

        EntityManager entityManager = crearEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            Usuario usuario = entityManager.find(Usuario.class, usuarioId);

            if (usuario == null) {
                throw new NoSuchElementException("No existe un usuario con id " + usuarioId);
            }

            Admin admin = new Admin(nombre, apellido, usuario);

            transaction.begin();
            entityManager.persist(admin);
            transaction.commit();

            return admin;
        } catch (RuntimeException exception) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            if (exception instanceof NoSuchElementException) {
                throw exception;
            }

            throw new IllegalStateException("No se pudo crear el admin", exception);
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public Optional<Admin> obtenerPorId(Long id) {
        EntityManager entityManager = crearEntityManager();

        try {
            return Optional.ofNullable(entityManager.find(Admin.class, id));
        } catch (RuntimeException exception) {
            throw new IllegalStateException("No se pudo obtener el admin", exception);
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public List<Admin> listar() {
        EntityManager entityManager = crearEntityManager();

        try {
            return entityManager
                .createQuery("SELECT a FROM Admin a", Admin.class)
                .getResultList();
        } catch (RuntimeException exception) {
            throw new IllegalStateException("No se pudieron listar los admins", exception);
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public Admin actualizar(Long id, String nombre, String apellido) {
        validarNombreYApellido(nombre, apellido);

        EntityManager entityManager = crearEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            Admin admin = entityManager.find(Admin.class, id);

            if (admin == null) {
                throw new NoSuchElementException("No existe un admin con id " + id);
            }

            transaction.begin();
            admin.setNombre(nombre);
            admin.setApellido(apellido);
            admin = entityManager.merge(admin);
            transaction.commit();

            return admin;
        } catch (RuntimeException exception) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            if (exception instanceof NoSuchElementException) {
                throw exception;
            }

            throw new IllegalStateException("No se pudo actualizar el admin", exception);
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public boolean eliminar(Long id) {
        EntityManager entityManager = crearEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            Admin admin = entityManager.find(Admin.class, id);

            if (admin == null) {
                return false;
            }

            transaction.begin();
            entityManager.remove(entityManager.contains(admin) ? admin : entityManager.merge(admin));
            transaction.commit();

            return true;
        } catch (RuntimeException exception) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw new IllegalStateException("No se pudo eliminar el admin", exception);
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    private void validarDatos(Long usuarioId, String nombre, String apellido) {
        if (usuarioId == null || usuarioId <= 0) {
            throw new IllegalArgumentException("El id de usuario es obligatorio");
        }

        validarNombreYApellido(nombre, apellido);
    }

    private void validarNombreYApellido(String nombre, String apellido) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        if (apellido == null || apellido.isBlank()) {
            throw new IllegalArgumentException("El apellido es obligatorio");
        }
    }
}
