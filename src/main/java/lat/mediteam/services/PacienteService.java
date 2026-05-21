package lat.mediteam.services;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import lat.mediteam.core.DatabaseManager;
import lat.mediteam.enums.UsuarioTipo;
import lat.mediteam.models.Paciente;
import lat.mediteam.models.Usuario;

public class PacienteService {

    private EntityManager crearEntityManager() {
        return DatabaseManager.getEntityManager();
    }

    public Paciente crear(Long usuarioId, String nombre, String apellido,
                          String ci, String telefono, String email,
                          String fechaNacimiento) {

        validarDatos(usuarioId, nombre, apellido, ci);

        EntityManager entityManager = crearEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            Usuario usuario = entityManager.find(Usuario.class, usuarioId);

            if (usuario == null) {
                throw new NoSuchElementException("No existe un usuario con id " + usuarioId);
            }

            if (usuario.getTipo() != UsuarioTipo.PACIENTE) {
                throw new IllegalArgumentException("El usuario con id " + usuarioId + " no es de tipo PACIENTE");
            }

            Paciente paciente = new Paciente(nombre, apellido, ci,
                                             telefono, email, fechaNacimiento, usuario);

            transaction.begin();
            entityManager.persist(paciente);
            transaction.commit();

            return paciente;

        } catch (RuntimeException e) {
            if (transaction.isActive()) transaction.rollback();
            if (e instanceof NoSuchElementException || e instanceof IllegalArgumentException) throw e;
            throw new IllegalStateException("No se pudo crear el paciente", e);
        } finally {
            if (entityManager.isOpen()) entityManager.close();
        }
    }

    public Optional<Paciente> obtenerPorId(Long id) {
        EntityManager entityManager = crearEntityManager();
        try {
            return Optional.ofNullable(entityManager.find(Paciente.class, id));
        } catch (RuntimeException e) {
            throw new IllegalStateException("No se pudo obtener el paciente", e);
        } finally {
            if (entityManager.isOpen()) entityManager.close();
        }
    }

    public List<Paciente> listar() {
        EntityManager entityManager = crearEntityManager();
        try {
            return entityManager
                .createQuery("SELECT p FROM Paciente p", Paciente.class)
                .getResultList();
        } catch (RuntimeException e) {
            throw new IllegalStateException("No se pudieron listar los pacientes", e);
        } finally {
            if (entityManager.isOpen()) entityManager.close();
        }
    }

    public Paciente actualizar(Long id, String nombre, String apellido,
                               String telefono, String email) {

        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("El nombre es obligatorio");
        if (apellido == null || apellido.isBlank()) throw new IllegalArgumentException("El apellido es obligatorio");

        EntityManager entityManager = crearEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            Paciente paciente = entityManager.find(Paciente.class, id);

            if (paciente == null) throw new NoSuchElementException("No existe un paciente con id " + id);

            transaction.begin();
            paciente.setNombre(nombre);
            paciente.setApellido(apellido);
            paciente.setTelefono(telefono);
            paciente.setEmail(email);
            paciente = entityManager.merge(paciente);
            transaction.commit();

            return paciente;

        } catch (RuntimeException e) {
            if (transaction.isActive()) transaction.rollback();
            if (e instanceof NoSuchElementException || e instanceof IllegalArgumentException) throw e;
            throw new IllegalStateException("No se pudo actualizar el paciente", e);
        } finally {
            if (entityManager.isOpen()) entityManager.close();
        }
    }

    public boolean eliminar(Long id) {
        EntityManager entityManager = crearEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            Paciente paciente = entityManager.find(Paciente.class, id);
            if (paciente == null) return false;

            transaction.begin();
            entityManager.remove(entityManager.contains(paciente) ? paciente : entityManager.merge(paciente));
            transaction.commit();

            return true;

        } catch (RuntimeException e) {
            if (transaction.isActive()) transaction.rollback();
            throw new IllegalStateException("No se pudo eliminar el paciente", e);
        } finally {
            if (entityManager.isOpen()) entityManager.close();
        }
    }

    private void validarDatos(Long usuarioId, String nombre, String apellido, String ci) {
        if (usuarioId == null || usuarioId <= 0)
            throw new IllegalArgumentException("El id de usuario es obligatorio");
        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio");
        if (apellido == null || apellido.isBlank())
            throw new IllegalArgumentException("El apellido es obligatorio");
        if (ci == null || ci.isBlank())
            throw new IllegalArgumentException("El CI es obligatorio");
    }
}