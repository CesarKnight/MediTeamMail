package lat.mediteam.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import lat.mediteam.core.DatabaseManager;
import lat.mediteam.models.Diagnostico;
import lat.mediteam.models.HistoriaClinica;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class DiagnosticoService {

    private EntityManager crearEntityManager() {
        return DatabaseManager.getEntityManager();
    }

    public Diagnostico crear(Long historiaId, String diagnostico) {
        validarDatos(historiaId, diagnostico);

        EntityManager entityManager = crearEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            HistoriaClinica historia = entityManager.find(HistoriaClinica.class, historiaId);

            if (historia == null) {
                throw new NoSuchElementException("No existe una historia clinica con id " + historiaId);
            }

            Diagnostico nuevo = new Diagnostico(diagnostico, historia);

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

            throw new IllegalStateException("No se pudo crear el diagnostico", exception);
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public Optional<Diagnostico> obtenerPorId(Long id) {
        EntityManager entityManager = crearEntityManager();

        try {
            return Optional.ofNullable(entityManager.find(Diagnostico.class, id));
        } catch (RuntimeException exception) {
            throw new IllegalStateException("No se pudo obtener el diagnostico", exception);
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public List<Diagnostico> listar() {
        EntityManager entityManager = crearEntityManager();

        try {
            return entityManager
                .createQuery("SELECT d FROM Diagnostico d", Diagnostico.class)
                .getResultList();
        } catch (RuntimeException exception) {
            throw new IllegalStateException("No se pudieron listar los diagnosticos", exception);
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public Diagnostico actualizar(Long id, String diagnostico) {
        if (diagnostico == null || diagnostico.isBlank()) {
            throw new IllegalArgumentException("El diagnostico es obligatorio");
        }

        EntityManager entityManager = crearEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            Diagnostico existente = entityManager.find(Diagnostico.class, id);

            if (existente == null) {
                throw new NoSuchElementException("No existe un diagnostico con id " + id);
            }

            transaction.begin();
            existente.setDiagnostico(diagnostico);
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

            throw new IllegalStateException("No se pudo actualizar el diagnostico", exception);
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
            Diagnostico existente = entityManager.find(Diagnostico.class, id);

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

            throw new IllegalStateException("No se pudo eliminar el diagnostico", exception);
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    private void validarDatos(Long historiaId, String diagnostico) {
        if (historiaId == null || historiaId <= 0) {
            throw new IllegalArgumentException("El id de historia clinica es obligatorio");
        }
        if (diagnostico == null || diagnostico.isBlank()) {
            throw new IllegalArgumentException("El diagnostico es obligatorio");
        }
    }
}