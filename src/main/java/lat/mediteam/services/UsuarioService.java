package lat.mediteam.services;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.EntityTransaction;
import lat.mediteam.models.Usuario;
import lat.mediteam.core.DatabaseManager;
import lat.mediteam.enums.UsuarioTipo;
import lat.mediteam.exceptions.InvalidArgumentException;
import lat.mediteam.exceptions.ServiceException;

public class UsuarioService {
    private EntityManager crearEntityManager() {
        return DatabaseManager.getEntityManager();
    }

    public Usuario crear(String email, String password, UsuarioTipo tipo) {
        validarCredenciales(email, password);

        EntityManager entityManager = crearEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            Usuario usuario = new Usuario(email, password,tipo);

            transaction.begin();
            entityManager.persist(usuario);
            transaction.commit();

            return usuario;

        } catch (RuntimeException exception) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new ServiceException("No se pudo crear el usuario", exception);
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public Optional<Usuario> obtenerPorId(Long id) {
        EntityManager entityManager = crearEntityManager();

        try {
            Usuario usuario = entityManager.find(Usuario.class, id);
            if (usuario == null) {
                throw new EntityNotFoundException("Usuario no encontrado: " + id);
            }
            return Optional.of(usuario);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (RuntimeException exception) {
            throw new ServiceException("No se pudo obtener el usuario", exception);
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public Optional<Usuario> obtenerPorEmail(String email) {
        EntityManager entityManager = crearEntityManager();

        try {
            List<Usuario> usuarios = entityManager
                .createQuery("SELECT u FROM Usuario u WHERE u.email = :email", Usuario.class)
                .setParameter("email", email)
                .getResultList();

            if (usuarios.isEmpty()) {
                return Optional.empty();
            }

            return Optional.of(usuarios.get(0));
        } catch (RuntimeException exception) {
            throw new ServiceException("No se pudo obtener el usuario por email", exception);
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public List<Usuario> listar() {
        EntityManager entityManager = crearEntityManager();

        try {
            return entityManager
                .createQuery("SELECT u FROM Usuario u", Usuario.class)
                .getResultList();
        } catch (RuntimeException exception) {
            throw new ServiceException("No se pudieron listar los usuarios", exception);
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public Usuario actualizar(Long id, String email, String password) {
        validarCredenciales(email, password);
 
        EntityManager entityManager = crearEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            Usuario usuario = entityManager.find(Usuario.class, id);

            if (usuario == null) {
                throw new EntityNotFoundException("Usuario no encontrado: " + id);
            }

            transaction.begin();
            usuario.setEmail(email);
            usuario.setPassword(password);
            usuario = entityManager.merge(usuario);
            transaction.commit();

            return usuario;
        } catch (EntityNotFoundException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } catch (RuntimeException exception) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new ServiceException("No se pudo actualizar el usuario", exception);
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
            Usuario usuario = entityManager.find(Usuario.class, id);

            if (usuario == null) {
                throw new EntityNotFoundException("Usuario no encontrado: " + id);
            }

            transaction.begin();
            entityManager.remove(entityManager.contains(usuario) ? usuario : entityManager.merge(usuario));
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
            throw new ServiceException("No se pudo eliminar el usuario", exception);
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    private void validarCredenciales(String email, String password) {
        if (email == null || email.isBlank()) {
            throw new InvalidArgumentException("El email es obligatorio");
        }

        if (password == null || password.isBlank()) {
            throw new InvalidArgumentException("La contraseña es obligatoria");
        }
    }
}
