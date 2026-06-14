package lat.mediteam.services;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.EntityTransaction;

import lat.mediteam.core.DatabaseManager;
import lat.mediteam.exceptions.InvalidArgumentException;
import lat.mediteam.exceptions.ServiceException;
import lat.mediteam.models.Consulta;
import lat.mediteam.models.HistoriaClinica;

public class ConsultaService {

    private EntityManager crearEntityManager() {
        return DatabaseManager.getEntityManager();
    }

    public Consulta crear(
            Long historiaId,
            String descripcion) {

        validarDescripcion(descripcion);

        EntityManager em = crearEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {

            HistoriaClinica historia =
                em.find(
                    HistoriaClinica.class,
                    historiaId
                );

            if (historia == null) {
                throw new EntityNotFoundException(
                    "Historia clínica no encontrada: "
                        + historiaId
                );
            }

            Consulta consulta =
                new Consulta(
                    descripcion,
                    historia
                );

            tx.begin();
            em.persist(consulta);
            tx.commit();

            return consulta;

        } catch (RuntimeException ex) {

            if (tx.isActive()) {
                tx.rollback();
            }

            if (ex instanceof EntityNotFoundException) {
                throw ex;
            }

            throw new ServiceException(
                "No se pudo crear la consulta",
                ex
            );

        } finally {

            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public Optional<Consulta> obtenerPorId(Long id) {

        EntityManager em =
            crearEntityManager();

        try {

            Consulta consulta =
                em.find(
                    Consulta.class,
                    id
                );

            if (consulta == null) {
                throw new EntityNotFoundException(
                    "Consulta no encontrada: "
                        + id
                );
            }

            return Optional.of(consulta);

        } catch (EntityNotFoundException e) {

            throw e;

        } catch (RuntimeException ex) {

            throw new ServiceException(
                "No se pudo obtener la consulta",
                ex
            );

        } finally {

            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public List<Consulta> listar() {

        EntityManager em =
            crearEntityManager();

        try {

            return em.createQuery(
                "SELECT c FROM Consulta c",
                Consulta.class
            ).getResultList();

        } catch (RuntimeException ex) {

            throw new ServiceException(
                "No se pudieron listar las consultas",
                ex
            );

        } finally {

            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public Consulta actualizar(
            Long id,
            String descripcion) {

        validarDescripcion(descripcion);

        EntityManager em =
            crearEntityManager();

        EntityTransaction tx =
            em.getTransaction();

        try {

            Consulta consulta =
                em.find(
                    Consulta.class,
                    id
                );

            if (consulta == null) {
                throw new EntityNotFoundException(
                    "Consulta no encontrada: "
                        + id
                );
            }

            tx.begin();

            consulta.setDescripcion(
                descripcion
            );

            consulta = em.merge(consulta);

            tx.commit();

            return consulta;

        } catch (RuntimeException ex) {

            if (tx.isActive()) {
                tx.rollback();
            }

            if (ex instanceof EntityNotFoundException) {
                throw ex;
            }

            throw new ServiceException(
                "No se pudo actualizar la consulta",
                ex
            );

        } finally {

            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public boolean eliminar(Long id) {

        EntityManager em =
            crearEntityManager();

        EntityTransaction tx =
            em.getTransaction();

        try {

            Consulta consulta =
                em.find(
                    Consulta.class,
                    id
                );

            if (consulta == null) {
                throw new EntityNotFoundException(
                    "Consulta no encontrada: "
                        + id
                );
            }

            tx.begin();

            em.remove(
                em.contains(consulta)
                    ? consulta
                    : em.merge(consulta)
            );

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
                "No se pudo eliminar la consulta",
                ex
            );

        } finally {

            if (em.isOpen()) {
                em.close();
            }
        }
    }

    private void validarDescripcion(
            String descripcion) {

        if (descripcion == null
                || descripcion.isBlank()) {

            throw new InvalidArgumentException(
                "La descripcion es obligatoria"
            );
        }
    }
}