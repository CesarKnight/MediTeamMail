package lat.mediteam.services;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.EntityTransaction;

import lat.mediteam.core.DatabaseManager;
import lat.mediteam.exceptions.InvalidArgumentException;
import lat.mediteam.exceptions.ServiceException;
import lat.mediteam.models.Diagnostico;
import lat.mediteam.models.HistoriaClinica;

public class DiagnosticoService {

    private EntityManager crearEntityManager() {
        return DatabaseManager.getEntityManager();
    }

    public Diagnostico crear(
            Long historiaId,
            String diagnostico) {

        validarDiagnostico(diagnostico);

        EntityManager em = crearEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {

            HistoriaClinica historia = em.find(
                    HistoriaClinica.class,
                    historiaId);

            if (historia == null) {
                throw new EntityNotFoundException(
                        "Historia clínica no encontrada: "
                                + historiaId);
            }

            Diagnostico nuevo = new Diagnostico(
                    diagnostico,
                    historia);

            tx.begin();
            em.persist(nuevo);
            tx.commit();

            return nuevo;

        } catch (RuntimeException ex) {

            if (tx.isActive()) {
                tx.rollback();
            }

            if (ex instanceof EntityNotFoundException) {
                throw ex;
            }

            throw new ServiceException(
                    "No se pudo crear el diagnóstico",
                    ex);

        } finally {

            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public Optional<Diagnostico> obtenerPorId(Long id) {

        EntityManager em = crearEntityManager();

        try {

            Diagnostico diagnostico = em.find(
                    Diagnostico.class,
                    id);

            if (diagnostico == null) {
                throw new EntityNotFoundException(
                        "Diagnóstico no encontrado: "
                                + id);
            }

            return Optional.of(diagnostico);

        } catch (EntityNotFoundException e) {

            throw e;

        } catch (RuntimeException ex) {

            throw new ServiceException(
                    "No se pudo obtener el diagnóstico",
                    ex);

        } finally {

            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public List<Diagnostico> listar() {

        EntityManager em = crearEntityManager();

        try {

            return em.createQuery(
                    "SELECT d FROM Diagnostico d",
                    Diagnostico.class).getResultList();

        } catch (RuntimeException ex) {

            throw new ServiceException(
                    "No se pudieron listar los diagnósticos",
                    ex);

        } finally {

            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public Diagnostico actualizar(
            Long id,
            String diagnostico) {

        validarDiagnostico(diagnostico);

        EntityManager em = crearEntityManager();

        EntityTransaction tx = em.getTransaction();

        try {

            Diagnostico existente = em.find(
                    Diagnostico.class,
                    id);

            if (existente == null) {
                throw new EntityNotFoundException(
                        "Diagnóstico no encontrado: "
                                + id);
            }

            tx.begin();

            existente.setDiagnostico(
                    diagnostico);

            existente = em.merge(existente);

            tx.commit();

            return existente;

        } catch (RuntimeException ex) {

            if (tx.isActive()) {
                tx.rollback();
            }

            if (ex instanceof EntityNotFoundException) {
                throw ex;
            }

            throw new ServiceException(
                    "No se pudo actualizar el diagnóstico",
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

            Diagnostico existente = em.find(
                    Diagnostico.class,
                    id);

            if (existente == null) {
                throw new EntityNotFoundException(
                        "Diagnóstico no encontrado: "
                                + id);
            }

            tx.begin();

            em.remove(
                    em.contains(existente)
                            ? existente
                            : em.merge(existente));

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
                    "No se pudo eliminar el diagnóstico",
                    ex);

        } finally {

            if (em.isOpen()) {
                em.close();
            }
        }
    }

    private void validarDiagnostico(
            String diagnostico) {

        if (diagnostico == null
                || diagnostico.isBlank()) {

            throw new InvalidArgumentException(
                    "El diagnóstico es obligatorio");
        }
    }
}