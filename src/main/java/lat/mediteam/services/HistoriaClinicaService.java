package lat.mediteam.services;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.EntityTransaction;

import lat.mediteam.core.DatabaseManager;
import lat.mediteam.enums.HistoriaClinicaEstado;
import lat.mediteam.enums.HistoriaClinicaTipo;
import lat.mediteam.exceptions.InvalidArgumentException;
import lat.mediteam.exceptions.ServiceException;
import lat.mediteam.models.HistoriaClinica;
import lat.mediteam.models.Medico;

public class HistoriaClinicaService {

    private EntityManager crearEntityManager() {
        return DatabaseManager.getEntityManager();
    }

    public HistoriaClinica crear(
            Long medicoId,
            String fecha,
            HistoriaClinicaEstado estado,
            HistoriaClinicaTipo tipo) {

        validarDatos(fecha);

        EntityManager em = crearEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {

            Medico medico = em.find(Medico.class, medicoId);

            if (medico == null) {
                throw new EntityNotFoundException(
                        "Médico no encontrado: "
                                + medicoId);
            }

            HistoriaClinica historia = new HistoriaClinica(
                    estado,
                    tipo,
                    medico);

            tx.begin();
            em.persist(historia);
            tx.commit();

            return historia;

        } catch (RuntimeException ex) {

            if (tx.isActive()) {
                tx.rollback();
            }

            if (ex instanceof EntityNotFoundException) {
                throw ex;
            }

            throw new ServiceException(
                    "No se pudo crear la historia clínica",
                    ex);

        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public Optional<HistoriaClinica> obtenerPorId(Long id) {

        EntityManager em = crearEntityManager();

        try {

            HistoriaClinica historia = em.find(
                    HistoriaClinica.class,
                    id);

            if (historia == null) {
                throw new EntityNotFoundException(
                        "Historia clínica no encontrada: "
                                + id);
            }

            return Optional.of(historia);

        } finally {

            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public List<HistoriaClinica> listar() {

        EntityManager em = crearEntityManager();

        try {

            return em.createQuery(
                    "SELECT h FROM HistoriaClinica h",
                    HistoriaClinica.class).getResultList();

        } catch (RuntimeException ex) {

            throw new ServiceException(
                    "No se pudieron listar las historias clínicas",
                    ex);

        } finally {

            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public HistoriaClinica actualizar(
            Long id,
            String fecha,
            HistoriaClinicaEstado estado,
            HistoriaClinicaTipo tipo) {

        validarDatos(fecha);

        EntityManager em = crearEntityManager();

        EntityTransaction tx = em.getTransaction();

        try {

            HistoriaClinica historia = em.find(
                    HistoriaClinica.class,
                    id);

            if (historia == null) {
                throw new EntityNotFoundException(
                        "Historia clínica no encontrada: "
                                + id);
            }

            tx.begin();

            historia.setEstado(estado);
            historia.setTipo(tipo);

            historia = em.merge(historia);

            tx.commit();

            return historia;

        } catch (RuntimeException ex) {

            if (tx.isActive()) {
                tx.rollback();
            }

            if (ex instanceof EntityNotFoundException) {
                throw ex;
            }

            throw new ServiceException(
                    "No se pudo actualizar la historia clínica",
                    ex);

        } finally {

            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public boolean eliminar(Long id) {

        EntityManager em = crearEntityManager();

        EntityTransaction tx = em.getTransaction();

        try {

            HistoriaClinica historia = em.find(
                    HistoriaClinica.class,
                    id);

            if (historia == null) {
                throw new EntityNotFoundException(
                        "Historia clínica no encontrada: "
                                + id);
            }

            tx.begin();

            em.remove(
                    em.contains(historia)
                            ? historia
                            : em.merge(historia));

            tx.commit();

            return true;

        } catch (RuntimeException ex) {

            if (tx.isActive()) {
                tx.rollback();
            }

            if (ex instanceof EntityNotFoundException) {
                throw ex;
            }

            throw new ServiceException(
                    "No se pudo eliminar la historia clínica",
                    ex);

        } finally {

            if (em.isOpen()) {
                em.close();
            }
        }
    }

    private void validarDatos(String fecha) {

        if (fecha == null || fecha.isBlank()) {
            throw new InvalidArgumentException(
                    "La fecha es obligatoria");
        }
    }
}