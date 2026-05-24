package lat.mediteam.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import lat.mediteam.core.DatabaseManager;
import lat.mediteam.enums.HistoriaClinicaEstado;
import lat.mediteam.enums.HistoriaClinicaTipo;
import lat.mediteam.models.HistoriaClinica;
import lat.mediteam.models.Medico;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class HistoriaClinicaService {

    private EntityManager crearEntityManager() {
        return DatabaseManager.getEntityManager();
    }

    public HistoriaClinica crear(Long medicoId, String estado, String tipo) {
        validarDatos(medicoId, estado, tipo);

        EntityManager entityManager = crearEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            Medico medico = entityManager.find(Medico.class, medicoId);
            if (medico == null) {
                throw new NoSuchElementException("No existe un medico con id " + medicoId);
            }

            HistoriaClinica historia = new HistoriaClinica(
                HistoriaClinicaEstado.valueOf(estado.toLowerCase()),
                HistoriaClinicaTipo.valueOf(tipo.toLowerCase()),
                medico
            );

            transaction.begin();
            entityManager.persist(historia);
            transaction.commit();

            return historia;
        } catch (RuntimeException exception) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            if (exception instanceof NoSuchElementException) {
                throw exception;
            }
            throw new IllegalStateException("No se pudo crear la historia clinica", exception);
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public Optional<HistoriaClinica> obtenerPorId(Long id) {
        EntityManager entityManager = crearEntityManager();

        try {
            return Optional.ofNullable(entityManager.find(HistoriaClinica.class, id));
        } catch (RuntimeException exception) {
            throw new IllegalStateException("No se pudo obtener la historia clinica", exception);
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public List<HistoriaClinica> listar() {
        EntityManager entityManager = crearEntityManager();

        try {
            return entityManager
                .createQuery("SELECT h FROM HistoriaClinica h", HistoriaClinica.class)
                .getResultList();
        } catch (RuntimeException exception) {
            throw new IllegalStateException("No se pudieron listar las historias clinicas", exception);
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public HistoriaClinica actualizar(Long id, String estado, String tipo) {
        validarCampos(estado, tipo);

        EntityManager entityManager = crearEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            HistoriaClinica historia = entityManager.find(HistoriaClinica.class, id);

            if (historia == null) {
                throw new NoSuchElementException("No existe una historia clinica con id " + id);
            }

            transaction.begin();
            historia.setEstado(HistoriaClinicaEstado.valueOf(estado.toLowerCase()));
            historia.setTipo(HistoriaClinicaTipo.valueOf(tipo.toLowerCase()));
            historia = entityManager.merge(historia);
            transaction.commit();

            return historia;
        } catch (RuntimeException exception) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            if (exception instanceof NoSuchElementException) {
                throw exception;
            }
            throw new IllegalStateException("No se pudo actualizar la historia clinica", exception);
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
            HistoriaClinica historia = entityManager.find(HistoriaClinica.class, id);

            if (historia == null) {
                return false;
            }

            transaction.begin();
            entityManager.remove(entityManager.contains(historia) ? historia : entityManager.merge(historia));
            transaction.commit();

            return true;
        } catch (RuntimeException exception) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new IllegalStateException("No se pudo eliminar la historia clinica", exception);
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    private void validarDatos(Long medicoId, String estado, String tipo) {
            if (medicoId == null || medicoId <= 0) {
                throw new IllegalArgumentException("El id de medico es obligatorio");
            }
            validarCampos(estado, tipo);
        }

        private void validarCampos(String estado, String tipo) {
        if (estado == null || estado.isBlank()) {
            throw new IllegalArgumentException("El estado es obligatorio");
        }
        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("El tipo es obligatorio");
        }
        try {
            HistoriaClinicaEstado.valueOf(estado.toLowerCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado invalido. Debe ser: pendiente, anulado o aprobado");
        }
        try {
            HistoriaClinicaTipo.valueOf(tipo.toLowerCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo invalido. Debe ser: diagnostico o tratamiento");
        }
    }
}