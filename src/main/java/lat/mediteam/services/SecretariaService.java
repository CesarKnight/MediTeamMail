package lat.mediteam.services;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import lat.mediteam.core.DatabaseManager;
import lat.mediteam.enums.UsuarioTipo;
import lat.mediteam.models.Secretaria;
import lat.mediteam.models.Usuario;

public class SecretariaService {

    private EntityManager crearEntityManager() {
        return DatabaseManager.getEntityManager();
    }

    public Secretaria crear(Long usuarioId, String nombre, String apellido,
                            String ci, String telefono, String email,
                            String fechaNacimiento) {
        validarDatos(usuarioId, nombre, apellido, ci);

        EntityManager em = crearEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            Usuario usuario = em.find(Usuario.class, usuarioId);
            if (usuario == null)
                throw new NoSuchElementException("No existe un usuario con id " + usuarioId);

            if (usuario.getTipo() != UsuarioTipo.SECRETARIA)
                throw new IllegalArgumentException("El usuario no es de tipo SECRETARIA");

            Secretaria secretaria = new Secretaria(nombre, apellido, ci,
                                                   telefono, email, fechaNacimiento, usuario);

            tx.begin();
            em.persist(secretaria);
            tx.commit();

            return secretaria;

        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            if (e instanceof NoSuchElementException || e instanceof IllegalArgumentException) throw e;
            throw new IllegalStateException("No se pudo crear la secretaria", e);
        } finally {
            if (em.isOpen()) em.close();
        }
    }

    public Optional<Secretaria> obtenerPorId(Long id) {
        EntityManager em = crearEntityManager();
        try {
            return Optional.ofNullable(em.find(Secretaria.class, id));
        } catch (RuntimeException e) {
            throw new IllegalStateException("No se pudo obtener la secretaria", e);
        } finally {
            if (em.isOpen()) em.close();
        }
    }

    public List<Secretaria> listar() {
        EntityManager em = crearEntityManager();
        try {
            return em.createQuery("SELECT s FROM Secretaria s", Secretaria.class)
                     .getResultList();
        } catch (RuntimeException e) {
            throw new IllegalStateException("No se pudieron listar las secretarias", e);
        } finally {
            if (em.isOpen()) em.close();
        }
    }

    public Secretaria actualizar(Long id, String nombre, String apellido,
                                 String telefono, String email) {
        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio");
        if (apellido == null || apellido.isBlank())
            throw new IllegalArgumentException("El apellido es obligatorio");

        EntityManager em = crearEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            Secretaria secretaria = em.find(Secretaria.class, id);
            if (secretaria == null)
                throw new NoSuchElementException("No existe una secretaria con id " + id);

            tx.begin();
            secretaria.setNombre(nombre);
            secretaria.setApellido(apellido);
            secretaria.setTelefono(telefono);
            secretaria.setEmail(email);
            secretaria = em.merge(secretaria);
            tx.commit();

            return secretaria;

        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            if (e instanceof NoSuchElementException) throw e;
            throw new IllegalStateException("No se pudo actualizar la secretaria", e);
        } finally {
            if (em.isOpen()) em.close();
        }
    }

    public boolean eliminar(Long id) {
        EntityManager em = crearEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            Secretaria secretaria = em.find(Secretaria.class, id);
            if (secretaria == null) return false;

            tx.begin();
            em.remove(em.contains(secretaria) ? secretaria : em.merge(secretaria));
            tx.commit();

            return true;

        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw new IllegalStateException("No se pudo eliminar la secretaria", e);
        } finally {
            if (em.isOpen()) em.close();
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