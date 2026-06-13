package lat.mediteam.services;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.EntityTransaction;
import lat.mediteam.core.DatabaseManager;
import lat.mediteam.exceptions.InvalidArgumentException;
import lat.mediteam.exceptions.ServiceException;
import lat.mediteam.models.Medico;
import lat.mediteam.models.Usuario;

public class MedicoService {

    private EntityManager crearEntityManager() {
        return DatabaseManager.getEntityManager();
    }

    public Medico crear(Long usuarioId, String nombre, String apellido, String ci,
                        String especialidad, String fechaNacimiento) {
        validarDatos(usuarioId, nombre, apellido, ci, especialidad);

        EntityManager entityManager = crearEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            Usuario usuario = entityManager.find(Usuario.class, usuarioId);

            if (usuario == null) {
                throw new EntityNotFoundException("No existe un usuario con id " + usuarioId);
            }

            Medico medico = new Medico(nombre, apellido, ci, especialidad, fechaNacimiento, usuario);

            transaction.begin();
            entityManager.persist(medico);
            transaction.commit();

            return medico;

        } catch (EntityNotFoundException e) {
            if (transaction.isActive()) transaction.rollback();
            throw e;
        } catch (RuntimeException exception) {
            if (transaction.isActive()) transaction.rollback();
            throw new ServiceException("No se pudo crear el medico", exception);
        } finally {
            if (entityManager.isOpen()) entityManager.close();
        }
    }

    public Optional<Medico> obtenerPorId(Long id) {
        EntityManager entityManager = crearEntityManager();

        try {
            return Optional.ofNullable(entityManager.find(Medico.class, id));
        } catch (RuntimeException exception) {
            throw new ServiceException("No se pudo obtener el medico", exception);
        } finally {
            if (entityManager.isOpen()) entityManager.close();
        }
    }

    public List<Medico> listar() {
        EntityManager entityManager = crearEntityManager();

        try {
            return entityManager
                .createQuery("SELECT m FROM Medico m", Medico.class)
                .getResultList();
        } catch (RuntimeException exception) {
            throw new ServiceException("No se pudieron listar los medicos", exception);
        } finally {
            if (entityManager.isOpen()) entityManager.close();
        }
    }

    public Medico actualizar(Long id, String nombre, String apellido, String ci,
                             String especialidad, String fechaNacimiento) {
        validarNombreYApellido(nombre, apellido);

        EntityManager entityManager = crearEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            Medico medico = entityManager.find(Medico.class, id);

            if (medico == null) {
                throw new EntityNotFoundException("No existe un medico con id " + id);
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

        } catch (EntityNotFoundException e) {
            if (transaction.isActive()) transaction.rollback();
            throw e;
        } catch (RuntimeException exception) {
            if (transaction.isActive()) transaction.rollback();
            throw new ServiceException("No se pudo actualizar el medico", exception);
        } finally {
            if (entityManager.isOpen()) entityManager.close();
        }
    }

    public boolean eliminar(Long id) {
        EntityManager entityManager = crearEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            Medico medico = entityManager.find(Medico.class, id);

            if (medico == null) {
                throw new EntityNotFoundException("No existe un medico con id " + id);
            }

            transaction.begin();
            entityManager.remove(entityManager.contains(medico) ? medico : entityManager.merge(medico));
            transaction.commit();

            return true;

        } catch (EntityNotFoundException e) {
            if (transaction.isActive()) transaction.rollback();
            throw e;
        } catch (RuntimeException exception) {
            if (transaction.isActive()) transaction.rollback();
            throw new ServiceException("No se pudo eliminar el medico", exception);
        } finally {
            if (entityManager.isOpen()) entityManager.close();
        }
    }

    private void validarDatos(Long usuarioId, String nombre, String apellido,
                               String ci, String especialidad) {
        if (usuarioId == null || usuarioId <= 0)
            throw new InvalidArgumentException("El id de usuario es obligatorio");
        if (ci == null || ci.isBlank())
            throw new InvalidArgumentException("El CI es obligatorio");
        if (especialidad == null || especialidad.isBlank())
            throw new InvalidArgumentException("La especialidad es obligatoria");
        validarNombreYApellido(nombre, apellido);
    }

    private void validarNombreYApellido(String nombre, String apellido) {
        if (nombre == null || nombre.isBlank())
            throw new InvalidArgumentException("El nombre es obligatorio");
        if (apellido == null || apellido.isBlank())
            throw new InvalidArgumentException("El apellido es obligatorio");
    }
}