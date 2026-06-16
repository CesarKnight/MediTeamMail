package lat.mediteam.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.EntityTransaction;

import lat.mediteam.core.DatabaseManager;
import lat.mediteam.exceptions.ServiceException;
import lat.mediteam.models.HistoriaClinica;
import lat.mediteam.models.Medico;

public class MedicosInvolucradosService {

    private EntityManager crearEntityManager() {
        return DatabaseManager.getEntityManager();
    }

    public void asignar(
            Long medicoId,
            Long historiaId) {

        EntityManager em = crearEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {

            Medico medico = em.find(Medico.class, medicoId);

            if (medico == null) {
                throw new EntityNotFoundException(
                        "Médico no encontrado: "
                                + medicoId);
            }

            HistoriaClinica historia = em.find(
                    HistoriaClinica.class,
                    historiaId);

            if (historia == null) {
                throw new EntityNotFoundException(
                        "Historia clínica no encontrada: "
                                + historiaId);
            }

            if (medico.getHistoriasInvolucradas()
                    .contains(historia)) {

                throw new IllegalStateException(
                        "El médico ya está asignado a esta historia clínica");
            }

            tx.begin();

            medico.getHistoriasInvolucradas()
                    .add(historia);

            em.merge(medico);

            tx.commit();

        } catch (RuntimeException ex) {

            if (tx.isActive()) {
                tx.rollback();
            }

            if (ex instanceof EntityNotFoundException
                    || ex instanceof IllegalStateException) {
                throw ex;
            }

            throw new ServiceException(
                    "No se pudo asignar el médico a la historia clínica",
                    ex);

        } finally {

            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public void remover(
            Long medicoId,
            Long historiaId) {

        EntityManager em = crearEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {

            Medico medico = em.find(Medico.class, medicoId);

            if (medico == null) {
                throw new EntityNotFoundException(
                        "Médico no encontrado: "
                                + medicoId);
            }

            HistoriaClinica historia = em.find(
                    HistoriaClinica.class,
                    historiaId);

            if (historia == null) {
                throw new EntityNotFoundException(
                        "Historia clínica no encontrada: "
                                + historiaId);
            }

            if (!medico.getHistoriasInvolucradas()
                    .contains(historia)) {

                throw new IllegalStateException(
                        "El médico no está asignado a esta historia clínica");
            }

            tx.begin();

            medico.getHistoriasInvolucradas()
                    .remove(historia);

            em.merge(medico);

            tx.commit();

        } catch (RuntimeException ex) {

            if (tx.isActive()) {
                tx.rollback();
            }

            if (ex instanceof EntityNotFoundException
                    || ex instanceof IllegalStateException) {
                throw ex;
            }

            throw new ServiceException(
                    "No se pudo remover el médico de la historia clínica",
                    ex);

        } finally {

            if (em.isOpen()) {
                em.close();
            }
        }
    }
}