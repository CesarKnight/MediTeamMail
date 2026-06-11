package lat.mediteam.services;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import lat.mediteam.models.Medico;
import lat.mediteam.models.Usuario;
import lat.mediteam.core.DatabaseManager;

public class MedicoService {
    private EntityManager crearEntityManager() {
        return DatabaseManager.getEntityManager();
    }

    public Medico crear(Long usuarioId, String nombre, String apellido, String ci, String especialidad, String fechaNacimiento) {
        validarDatos(usuarioId, nombre, apellido, ci, especialidad);

        EntityManager entityManager = crearEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            Usuario usuario = entityManager.find(Usuario.class, usuarioId);

            if (usuario == null) {
                throw new NoSuchElementException("No existe un usuario con id " + usuarioId);
            }

            Medico medico = new Medico(nombre, apellido, ci, especialidad, fechaNacimiento, usuario);

            transaction.begin();
            entityManager.persist(medico);
            transaction.commit();

            return medico;
        } catch (RuntimeException exception) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            if (exception instanceof NoSuchElementException) {
                throw exception;
            }

            throw new IllegalStateException("No se pudo crear el medico", exception);
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public Optional<Medico> obtenerPorId(Long id) {
        EntityManager entityManager = crearEntityManager();

        try {
            return Optional.ofNullable(entityManager.find(Medico.class, id));
        } catch (RuntimeException exception) {
            throw new IllegalStateException("No se pudo obtener el medico", exception);
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public List<Medico> listar() {
        EntityManager entityManager = crearEntityManager();

        try {
            return entityManager
                .createQuery("SELECT m FROM Medico m", Medico.class)
                .getResultList();
        } catch (RuntimeException exception) {
            throw new IllegalStateException("No se pudieron listar los medicos", exception);
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public Medico actualizar(Long id, String nombre, String apellido, String ci, String especialidad, String fechaNacimiento) {
        validarNombreYApellido(nombre, apellido);

        EntityManager entityManager = crearEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            Medico medico = entityManager.find(Medico.class, id);

            if (medico == null) {
                throw new NoSuchElementException("No existe un medico con id " + id);
            }

            transaction.begin();
            medico.setNombre(nombre);
            medico.setApellido(apellido);
            medico.setCi(ci);
            medico.setEspecialidad(especialidad);
            medico.setFechaNacimiento(fechaNacimiento);
            medico = entityManager.merge(medico);
            transaction.commit();

            return medico;
        } catch (RuntimeException exception) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            if (exception instanceof NoSuchElementException) {
                throw exception;
            }

            throw new IllegalStateException("No se pudo actualizar el medico", exception);
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
            Medico medico = entityManager.find(Medico.class, id);

            if (medico == null) {
                return false;
            }

            transaction.begin();
            entityManager.remove(entityManager.contains(medico) ? medico : entityManager.merge(medico));
            transaction.commit();

            return true;
        } catch (RuntimeException exception) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw new IllegalStateException("No se pudo eliminar el medico", exception);
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    private void validarDatos(Long usuarioId, String nombre, String apellido, String ci, String especialidad) {
        if (usuarioId == null || usuarioId <= 0) {
            throw new IllegalArgumentException("El id de usuario es obligatorio");
        }
        if (ci == null || ci.isBlank()) {
            throw new IllegalArgumentException("El CI es obligatorio");
        }
        if (especialidad == null || especialidad.isBlank()) {
            throw new IllegalArgumentException("La especialidad es obligatoria");
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