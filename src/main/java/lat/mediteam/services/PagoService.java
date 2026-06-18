package lat.mediteam.services;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import lat.mediteam.core.DatabaseManager;
import lat.mediteam.enums.PagoEstado;
import lat.mediteam.enums.PagoTipo;
import lat.mediteam.exceptions.InvalidArgumentException;
import lat.mediteam.exceptions.ServiceException;
import lat.mediteam.models.Paciente;
import lat.mediteam.models.Pago;
import lat.mediteam.models.Secretaria;
import lat.mediteam.models.Servicio;

public class PagoService {

    private EntityManager crearEntityManager() {
        return DatabaseManager.getEntityManager();
    }

    public Pago crear(Long secretariaId, Long servicioId,Long pacienteId, String fechaCreacion,
                      Float total, PagoEstado estado, PagoTipo tipo) {

        validarDatos(secretariaId, servicioId,pacienteId, fechaCreacion, total, tipo);

        PagoEstado estadoFinal = estado != null ? estado : PagoEstado.PENDIENTE;

        EntityManager entityManager = crearEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            Secretaria secretaria = entityManager.find(Secretaria.class, secretariaId);
            if (secretaria == null) {
                throw new EntityNotFoundException("No existe una secretaria con id " + secretariaId);
            }

            Servicio servicio = entityManager.find(Servicio.class, servicioId);
            if (servicio == null) {
                throw new EntityNotFoundException("No existe un servicio con id " + servicioId);
            }

            Paciente paciente = entityManager.find(Paciente.class, pacienteId);
            if (paciente == null) {
                throw new EntityNotFoundException("No existe un servicio con id " + pacienteId);
            }

            Pago pago = new Pago(fechaCreacion, total, estadoFinal, tipo, secretaria, servicio, paciente);

            transaction.begin();
            entityManager.persist(pago);
            transaction.commit();

            return pago;

        } catch (EntityNotFoundException | InvalidArgumentException e) {
            if (transaction.isActive()) transaction.rollback();
            throw e;
        } catch (RuntimeException e) {
            if (transaction.isActive()) transaction.rollback();
            throw new ServiceException("No se pudo crear el pago", e);
        } finally {
            if (entityManager.isOpen()) entityManager.close();
        }
    }

    public Optional<Pago> obtenerPorId(Long id) {
        EntityManager entityManager = crearEntityManager();
        try {
            Pago pago = entityManager.createQuery(
                    "SELECT p FROM Pago p " +
                    "JOIN FETCH p.secretaria " +
                    "JOIN FETCH p.servicio " +
                    "JOIN FETCH p.paciente " +
                    "WHERE p.id = :id", Pago.class)
                .setParameter("id", id)
                .getSingleResult();
            return Optional.of(pago);
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (RuntimeException e) {
            throw new ServiceException("No se pudo obtener el pago", e);
        } finally {
            if (entityManager.isOpen()) entityManager.close();
        }
    }

    public List<Pago> listar() {
        EntityManager entityManager = crearEntityManager();
        try {
            return entityManager
                .createQuery(
                    "SELECT p FROM Pago p " +
                    "JOIN FETCH p.secretaria " +
                    "JOIN FETCH p.servicio" +
                    "JOIN FETCH p.paciente", Pago.class)
                .getResultList();
        } catch (RuntimeException e) {
            throw new ServiceException("No se pudieron listar los pagos", e);
        } finally {
            if (entityManager.isOpen()) entityManager.close();
        }
    }

    private void validarDatos(Long secretariaId, Long servicioId,Long pacienteId, String fechaCreacion,
                               Float total, PagoTipo tipo) {
        if (secretariaId == null || secretariaId <= 0)
            throw new InvalidArgumentException("El id de secretaria es obligatorio");
        if (servicioId == null || servicioId <= 0)
            throw new InvalidArgumentException("El id de servicio es obligatorio");
        if (pacienteId == null || pacienteId <= 0)
            throw new InvalidArgumentException("El id de paciente es obligatorio");
        if (fechaCreacion == null || fechaCreacion.isBlank())
            throw new InvalidArgumentException("La fecha de creación es obligatoria");
        if (total == null || total <= 0)
            throw new InvalidArgumentException("El total debe ser mayor a 0");
        if (tipo == null)
            throw new InvalidArgumentException("El tipo de pago es obligatorio");
    }
}