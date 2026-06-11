package lat.mediteam.services;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import lat.mediteam.core.DatabaseManager;
import lat.mediteam.enums.ServicioEstado;
import lat.mediteam.models.Servicio;

public class ServicioService {

    private EntityManager crearEntityManager() {
        return DatabaseManager.getEntityManager();
    }

    public Servicio crear(String titulo, String descripcion, Double precio,
                          String duracion, ServicioEstado estado) {

        validarDatos(titulo, descripcion, precio);

        EntityManager entityManager = crearEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            Servicio servicio = new Servicio(titulo, descripcion, precio, duracion, estado);

            transaction.begin();
            entityManager.persist(servicio);
            transaction.commit();

            return servicio;

        } catch (RuntimeException e) {
            if (transaction.isActive()) transaction.rollback();
            throw new IllegalStateException("No se pudo crear el servicio", e);
        } finally {
            if (entityManager.isOpen()) entityManager.close();
        }
    }

    public Optional<Servicio> obtenerPorId(Long id) {
        EntityManager entityManager = crearEntityManager();
        try {
            return Optional.ofNullable(entityManager.find(Servicio.class, id));
        } catch (RuntimeException e) {
            throw new IllegalStateException("No se pudo obtener el servicio", e);
        } finally {
            if (entityManager.isOpen()) entityManager.close();
        }
    }

    public List<Servicio> listar() {
        EntityManager entityManager = crearEntityManager();
        try {
            return entityManager
                .createQuery("SELECT s FROM Servicio s", Servicio.class)
                .getResultList();
        } catch (RuntimeException e) {
            throw new IllegalStateException("No se pudieron listar los servicios", e);
        } finally {
            if (entityManager.isOpen()) entityManager.close();
        }
    }

    public List<Servicio> listarDisponibles() {
        EntityManager entityManager = crearEntityManager();
        try {
            return entityManager
                .createQuery("SELECT s FROM Servicio s WHERE s.estado = :estado", Servicio.class)
                .setParameter("estado", ServicioEstado.DISPONIBLE)
                .getResultList();
        } catch (RuntimeException e) {
            throw new IllegalStateException("No se pudieron listar los servicios", e);
        } finally {
            if (entityManager.isOpen()) entityManager.close();
        }
    }

    public List<Servicio> buscarPorTitulo(String titulo) {
        EntityManager entityManager = crearEntityManager();
        try {
            return entityManager
                .createQuery("SELECT s FROM Servicio s WHERE LOWER(s.titulo) LIKE LOWER(:titulo)", Servicio.class)
                .setParameter("titulo", "%" + titulo + "%")
                .getResultList();
        } catch (RuntimeException e) {
            throw new IllegalStateException("No se pudieron buscar los servicios", e);
        } finally {
            if (entityManager.isOpen()) entityManager.close();
        }
    }

    public Servicio actualizar(Long id, String titulo, String descripcion,
                               Double precio, String duracion, ServicioEstado estado) {

        validarDatos(titulo, descripcion, precio);

        EntityManager entityManager = crearEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            Servicio servicio = entityManager.find(Servicio.class, id);

            if (servicio == null)
                throw new NoSuchElementException("No existe un servicio con id " + id);

            transaction.begin();
            servicio.setTitulo(titulo);
            servicio.setDescripcion(descripcion);
            servicio.setPrecio(precio);
            servicio.setDuracion(duracion);
            servicio.setEstado(estado);
            servicio = entityManager.merge(servicio);
            transaction.commit();

            return servicio;

        } catch (RuntimeException e) {
            if (transaction.isActive()) transaction.rollback();
            if (e instanceof NoSuchElementException) throw e;
            throw new IllegalStateException("No se pudo actualizar el servicio", e);
        } finally {
            if (entityManager.isOpen()) entityManager.close();
        }
    }

    public boolean eliminar(Long id) {
        EntityManager entityManager = crearEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            Servicio servicio = entityManager.find(Servicio.class, id);
            if (servicio == null) return false;

            transaction.begin();
            entityManager.remove(entityManager.contains(servicio) ? servicio : entityManager.merge(servicio));
            transaction.commit();

            return true;

        } catch (RuntimeException e) {
            if (transaction.isActive()) transaction.rollback();
            throw new IllegalStateException("No se pudo eliminar el servicio", e);
        } finally {
            if (entityManager.isOpen()) entityManager.close();
        }
    }

    private void validarDatos(String titulo, String descripcion, Double precio) {
        if (titulo == null || titulo.isBlank())
            throw new IllegalArgumentException("El título es obligatorio");
        if (descripcion == null || descripcion.isBlank())
            throw new IllegalArgumentException("La descripción es obligatoria");
        if (precio == null || precio <= 0)
            throw new IllegalArgumentException("El precio debe ser mayor a 0");
    }
}