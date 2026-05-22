package lat.mediteam.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import lat.mediteam.core.DatabaseManager;
import lat.mediteam.models.HistoriaClinica;
import lat.mediteam.models.Medico;

import java.util.NoSuchElementException;

public class MedicosInvolucradosService {

    private EntityManager crearEntityManager() {
        return DatabaseManager.getEntityManager();
    }

    public void asignar(Long medicoId, Long historiaId) {
        EntityManager entityManager = crearEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            Medico medico = entityManager.find(Medico.class, medicoId);
            if (medico == null) {
                throw new NoSuchElementException("No existe un medico con id " + medicoId);
            }

            HistoriaClinica historia = entityManager.find(HistoriaClinica.class, historiaId);
            if (historia == null) {
                throw new NoSuchElementException("No existe una historia clinica con id " + historiaId);
            }

            if (medico.getHistoriasInvolucradas().contains(historia)) {
                throw new IllegalStateException("El medico ya esta asignado a esta historia clinica");
            }

            transaction.begin();
            medico.getHistoriasInvolucradas().add(historia);
            entityManager.merge(medico);
            transaction.commit();

        } catch (RuntimeException exception) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            if (exception instanceof NoSuchElementException || exception instanceof IllegalStateException) {
                throw exception;
            }
            throw new IllegalStateException("No se pudo asignar el medico a la historia clinica", exception);
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public void remover(Long medicoId, Long historiaId) {
        EntityManager entityManager = crearEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            Medico medico = entityManager.find(Medico.class, medicoId);
            if (medico == null) {
                throw new NoSuchElementException("No existe un medico con id " + medicoId);
            }

            HistoriaClinica historia = entityManager.find(HistoriaClinica.class, historiaId);
            if (historia == null) {
                throw new NoSuchElementException("No existe una historia clinica con id " + historiaId);
            }

            if (!medico.getHistoriasInvolucradas().contains(historia)) {
                throw new IllegalStateException("El medico no esta asignado a esta historia clinica");
            }

            transaction.begin();
            medico.getHistoriasInvolucradas().remove(historia);
            entityManager.merge(medico);
            transaction.commit();

        } catch (RuntimeException exception) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            if (exception instanceof NoSuchElementException || exception instanceof IllegalStateException) {
                throw exception;
            }
            throw new IllegalStateException("No se pudo remover el medico de la historia clinica", exception);
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
}