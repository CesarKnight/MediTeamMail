package lat.mediteam.services;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.EntityTransaction;

import lat.mediteam.core.DatabaseManager;
import lat.mediteam.exceptions.InvalidArgumentException;
import lat.mediteam.exceptions.ServiceException;
import lat.mediteam.models.HistoriaClinica;
import lat.mediteam.models.Tratamiento;

public class TratamientoService {

    private EntityManager crearEntityManager() {
        return DatabaseManager.getEntityManager();
    }

    public Tratamiento crear(
            Long historiaId,
            String tratamiento) {

        validarTratamiento(tratamiento);

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

            Tratamiento nuevo = new Tratamiento(
                    tratamiento,
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
                    "No se pudo crear el tratamiento",
                    ex);

        } finally {

            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public Optional<Tratamiento> obtenerPorId(Long id) {

        EntityManager em = crearEntityManager();

        try {

            Tratamiento tratamiento = em.find(
                    Tratamiento.class,
                    id);

            if (tratamiento == null) {
                throw new EntityNotFoundException(
                        "Tratamiento no encontrado: "
                                + id);
            }

            return Optional.of(tratamiento);

        } catch (EntityNotFoundException e) {

            throw e;

        } catch (RuntimeException ex) {

            throw new ServiceException(
                    "No se pudo obtener el tratamiento",
                    ex);

        } finally {

            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public List<Tratamiento> listar() {

        EntityManager em = crearEntityManager();

        try {

            return em.createQuery(
                    "SELECT t FROM Tratamiento t",
                    Tratamiento.class).getResultList();

        } catch (RuntimeException ex) {

            throw new ServiceException(
                    "No se pudieron listar los tratamientos",
                    ex);

        } finally {

            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public Tratamiento actualizar(
            Long id,
            String tratamiento) {

        validarTratamiento(tratamiento);

        EntityManager em = crearEntityManager();

        EntityTransaction tx = em.getTransaction();

        try {

            Tratamiento existente = em.find(
                    Tratamiento.class,
                    id);

            if (existente == null) {
                throw new EntityNotFoundException(
                        "Tratamiento no encontrado: "
                                + id);
            }

            tx.begin();

            existente.setTratamiento(
                    tratamiento);

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
                    "No se pudo actualizar el tratamiento",
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

            Tratamiento existente = em.find(
                    Tratamiento.class,
                    id);

            if (existente == null) {
                throw new EntityNotFoundException(
                        "Tratamiento no encontrado: "
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
                    "No se pudo eliminar el tratamiento",
                    ex);

        } finally {

            if (em.isOpen()) {
                em.close();
            }
        }
    }

    private void validarTratamiento(
            String tratamiento) {

        if (tratamiento == null
                || tratamiento.isBlank()) {

            throw new InvalidArgumentException(
                    "El tratamiento es obligatorio");
        }
    }
}