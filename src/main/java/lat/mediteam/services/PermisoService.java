package lat.mediteam.services;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import lat.mediteam.core.DatabaseManager;
import lat.mediteam.models.Permiso;
import lat.mediteam.models.Usuario;

public class PermisoService {

    private EntityManager crearEntityManager() {
        return DatabaseManager.getEntityManager();
    }

    // ========== CRUD de Permisos ==========

    public Permiso crear(String nombre, String descripcion) {
        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio");

        EntityManager em = crearEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            Permiso permiso = new Permiso(nombre, descripcion);
            tx.begin();
            em.persist(permiso);
            tx.commit();
            return permiso;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw new IllegalStateException("No se pudo crear el permiso", e);
        } finally {
            if (em.isOpen()) em.close();
        }
    }

    public Optional<Permiso> obtenerPorId(Long id) {
        EntityManager em = crearEntityManager();
        try {
            return Optional.ofNullable(em.find(Permiso.class, id));
        } catch (RuntimeException e) {
            throw new IllegalStateException("No se pudo obtener el permiso", e);
        } finally {
            if (em.isOpen()) em.close();
        }
    }

    public List<Permiso> listar() {
        EntityManager em = crearEntityManager();
        try {
            return em.createQuery("SELECT p FROM Permiso p", Permiso.class)
                     .getResultList();
        } catch (RuntimeException e) {
            throw new IllegalStateException("No se pudieron listar los permisos", e);
        } finally {
            if (em.isOpen()) em.close();
        }
    }

    public Permiso actualizar(Long id, String nombre, String descripcion) {
        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio");

        EntityManager em = crearEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            Permiso permiso = em.find(Permiso.class, id);
            if (permiso == null)
                throw new NoSuchElementException("No existe un permiso con id " + id);
            tx.begin();
            permiso.setNombre(nombre);
            permiso.setDescripcion(descripcion);
            permiso = em.merge(permiso);
            tx.commit();
            return permiso;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw new IllegalStateException("No se pudo actualizar el permiso", e);
        } finally {
            if (em.isOpen()) em.close();
        }
    }

    public boolean eliminar(Long id) {
        EntityManager em = crearEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            Permiso permiso = em.find(Permiso.class, id);
            if (permiso == null) return false;
            tx.begin();
            em.remove(permiso);
            tx.commit();
            return true;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw new IllegalStateException("No se pudo eliminar el permiso", e);
        } finally {
            if (em.isOpen()) em.close();
        }
    }

    // ========== Asignación de permisos (JPA puro) ==========

    public void asignarPermiso(Long usuarioId, Long permisoId) {
        EntityManager em = crearEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            Usuario usuario = em.find(Usuario.class, usuarioId);
            if (usuario == null)
                throw new NoSuchElementException("No existe un usuario con id " + usuarioId);

            Permiso permiso = em.find(Permiso.class, permisoId);
            if (permiso == null)
                throw new NoSuchElementException("No existe un permiso con id " + permisoId);

            tx.begin();
            usuario.getPermisos().add(permiso);
            em.merge(usuario);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw new IllegalStateException("No se pudo asignar el permiso", e);
        } finally {
            if (em.isOpen()) em.close();
        }
    }

    public void removerPermiso(Long usuarioId, Long permisoId) {
        EntityManager em = crearEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            Usuario usuario = em.find(Usuario.class, usuarioId);
            if (usuario == null)
                throw new NoSuchElementException("No existe un usuario con id " + usuarioId);

            Permiso permiso = em.find(Permiso.class, permisoId);
            if (permiso == null)
                throw new NoSuchElementException("No existe un permiso con id " + permisoId);

            tx.begin();
            usuario.getPermisos().remove(permiso);
            em.merge(usuario);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw new IllegalStateException("No se pudo remover el permiso", e);
        } finally {
            if (em.isOpen()) em.close();
        }
    }

    public List<String> listarPermisosDeUsuario(Long usuarioId) {
        EntityManager em = crearEntityManager();
        try {
            Usuario usuario = em.find(Usuario.class, usuarioId);
            if (usuario == null)
                throw new NoSuchElementException("No existe un usuario con id " + usuarioId);

            // forzar carga de la colección lazy
            usuario.getPermisos().size();
            return usuario.getPermisos().stream()
                    .map(Permiso::getNombre)
                    .collect(Collectors.toList());
        } catch (RuntimeException e) {
            throw new IllegalStateException("No se pudieron listar los permisos del usuario", e);
        } finally {
            if (em.isOpen()) em.close();
        }
    }
}