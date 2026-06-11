package lat.mediteam.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import lat.mediteam.core.DatabaseManager;
import lat.mediteam.models.HistoriaClinica;
import lat.mediteam.models.Tratamiento;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class TratamientoService {

    private EntityManager crearEntityManager() {
        return DatabaseManager.getEntityManager();
    }

    public Tratamiento crear(Long historiaId, String tratamiento) {
        validarDatos(historiaId, tratamiento);

        EntityManager entityManager = crearEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            HistoriaClinica historia = entityManager.find(HistoriaClinica.class, historiaId);

            if (historia == null) {
                throw new NoSuchElementException("No existe una historia clinica con id " + historiaId);
            }

            Tratamiento nuevo = new Tratamiento(tratamiento, historia);

            transaction.begin();
            entityManager.persist(nuevo);
            transaction.commit();

            return nuevo;
        } catch (RuntimeException exception) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            if (exception instanceof NoSuchElementException) {
                throw exception;
            }

            throw new IllegalStateException("No se pudo crear el tratamiento", exception);
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public Optional<Tratamiento> obtenerPorId(Long id) {
        EntityManager entityManager = crearEntityManager();

        try {
            return Optional.ofNullable(entityManager.find(Tratamiento.class, id));
        } catch (RuntimeException exception) {
            throw new IllegalStateException("No se pudo obtener el tratamiento", exception);
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public List<Tratamiento> listar() {
        EntityManager entityManager = crearEntityManager();

        try {
            return entityManager
                .createQuery("SELECT t FROM Tratamiento t", Tratamiento.class)
                .getResultList();
        } catch (RuntimeException exception) {
            throw new IllegalStateException("No se pudieron listar los tratamientos", exception);
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public Tratamiento actualizar(Long id, String tratamiento) {
        if (tratamiento == null || tratamiento.isBlank()) {
            throw new IllegalArgumentException("El tratamiento es obligatorio");
        }

        EntityManager entityManager = crearEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            Tratamiento existente = entityManager.find(Tratamiento.class, id);

            if (existente == null) {
                throw new NoSuchElementException("No existe un tratamiento con id " + id);
            }

            transaction.begin();
            existente.setTratamiento(tratamiento);
            existente = entityManager.merge(existente);
            transaction.commit();

            return existente;
        } catch (RuntimeException exception) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            if (exception instanceof NoSuchElementException) {
                throw exception;
            }

            throw new IllegalStateException("No se pudo actualizar el tratamiento", exception);
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
            Tratamiento existente = entityManager.find(Tratamiento.class, id);

            if (existente == null) {
                return false;
            }

            transaction.begin();
            entityManager.remove(entityManager.contains(existente) ? existente : entityManager.merge(existente));
            transaction.commit();

            return true;
        } catch (RuntimeException exception) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw new IllegalStateException("No se pudo eliminar el tratamiento", exception);
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    private void validarDatos(Long historiaId, String tratamiento) {
        if (historiaId == null || historiaId <= 0) {
            throw new IllegalArgumentException("El id de historia clinica es obligatorio");
        }
        if (tratamiento == null || tratamiento.isBlank()) {
            throw new IllegalArgumentException("El tratamiento es obligatorio");
        }
    }
}