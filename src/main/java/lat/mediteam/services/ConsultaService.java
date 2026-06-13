package lat.mediteam.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import lat.mediteam.core.DatabaseManager;
import lat.mediteam.models.Consulta;
import lat.mediteam.models.HistoriaClinica;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class ConsultaService {

    private EntityManager crearEntityManager() {
        return DatabaseManager.getEntityManager();
    }

    public Consulta crear(Long historiaId, String descripcion) {
        validarDatos(historiaId, descripcion);

        EntityManager entityManager = crearEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            HistoriaClinica historia = entityManager.find(HistoriaClinica.class, historiaId);

            if (historia == null) {
                throw new NoSuchElementException("No existe una historia clinica con id " + historiaId);
            }

            Consulta consulta = new Consulta(descripcion, historia);

            transaction.begin();
            entityManager.persist(consulta);
            transaction.commit();

            return consulta;
        } catch (RuntimeException exception) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            if (exception instanceof NoSuchElementException) {
                throw exception;
            }
            throw new IllegalStateException("No se pudo crear la consulta", exception);
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public Optional<Consulta> obtenerPorId(Long id) {
        EntityManager entityManager = crearEntityManager();

        try {
            return Optional.ofNullable(entityManager.find(Consulta.class, id));
        } catch (RuntimeException exception) {
            throw new IllegalStateException("No se pudo obtener la consulta", exception);
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public List<Consulta> listar() {
        EntityManager entityManager = crearEntityManager();

        try {
            return entityManager
                .createQuery("SELECT c FROM Consulta c", Consulta.class)
                .getResultList();
        } catch (RuntimeException exception) {
            throw new IllegalStateException("No se pudieron listar las consultas", exception);
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public Consulta actualizar(Long id, String descripcion) {
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("La descripcion es obligatoria");
        }

        EntityManager entityManager = crearEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            Consulta existente = entityManager.find(Consulta.class, id);

            if (existente == null) {
                throw new NoSuchElementException("No existe una consulta con id " + id);
            }

            transaction.begin();
            existente.setDescripcion(descripcion);
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
            throw new IllegalStateException("No se pudo actualizar la consulta", exception);
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
            Consulta existente = entityManager.find(Consulta.class, id);

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
            throw new IllegalStateException("No se pudo eliminar la consulta", exception);
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    private void validarDatos(Long historiaId, String descripcion) {
        if (historiaId == null || historiaId <= 0) {
            throw new IllegalArgumentException("El id de historia clinica es obligatorio");
        }
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("La descripcion es obligatoria");
        }
    }
}