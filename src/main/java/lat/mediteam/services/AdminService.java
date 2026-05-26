package lat.mediteam.services;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.EntityTransaction;
import lat.mediteam.models.Admin;
import lat.mediteam.models.Usuario;
import lat.mediteam.core.DatabaseManager;
import lat.mediteam.exceptions.InvalidArgumentException;
import lat.mediteam.exceptions.ServiceException;

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
                throw new EntityNotFoundException("No existe un usuario con id " + usuarioId);
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

            if (exception instanceof EntityNotFoundException) {
                throw exception;
            }

            throw new ServiceException("No se pudo crear el admin", exception);
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public Optional<Admin> obtenerPorId(Long id) {
        EntityManager entityManager = crearEntityManager();

        try {
            Admin admin = entityManager.find(Admin.class, id);
            if (admin == null) {
                throw new EntityNotFoundException("Admin no encontrado: " + id);
            }
            return Optional.of(admin);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (RuntimeException exception) {
            throw new ServiceException("No se pudo obtener el admin", exception);
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
            throw new ServiceException("No se pudieron listar los admins", exception);
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
                throw new EntityNotFoundException("Admin no encontrado: " + id);
            }

            transaction.begin();
            admin.setNombre(nombre);
            admin.setApellido(apellido);
            admin = entityManager.merge(admin);
            transaction.commit();

            return admin;
        } catch (EntityNotFoundException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } catch (RuntimeException exception) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new ServiceException("No se pudo actualizar el admin", exception);
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
                throw new EntityNotFoundException("Admin no encontrado: " + id);
            }

            transaction.begin();
            entityManager.remove(entityManager.contains(admin) ? admin : entityManager.merge(admin));
            transaction.commit();

            return true;
        } catch (EntityNotFoundException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } catch (RuntimeException exception) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw new ServiceException("No se pudo eliminar el admin", exception);
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    private void validarDatos(Long usuarioId, String nombre, String apellido) {
        if (usuarioId == null || usuarioId <= 0) {
            throw new InvalidArgumentException("El id de usuario es obligatorio");
        }

        validarNombreYApellido(nombre, apellido);
    }

    private void validarNombreYApellido(String nombre, String apellido) {
        if (nombre == null || nombre.isBlank()) {
            throw new InvalidArgumentException("El nombre es obligatorio");
        }

        if (apellido == null || apellido.isBlank()) {
            throw new InvalidArgumentException("El apellido es obligatorio");
        }
    }
}
