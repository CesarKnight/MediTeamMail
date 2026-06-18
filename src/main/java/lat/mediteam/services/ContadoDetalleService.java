package lat.mediteam.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.EntityTransaction;
import lat.mediteam.core.DatabaseManager;
import lat.mediteam.enums.MetodoPago;
import lat.mediteam.enums.PagoEstado;
import lat.mediteam.exceptions.InvalidArgumentException;
import lat.mediteam.exceptions.ServiceException;
import lat.mediteam.models.ContadoDetalle;
import lat.mediteam.models.Pago;

public class ContadoDetalleService {

    private EntityManager crearEntityManager() {
        return DatabaseManager.getEntityManager();
    }

    public ContadoDetalle registrar(Long pagoId, MetodoPago metodoPago, Float montoRecibido) {

        validarDatos(pagoId, metodoPago, montoRecibido);

        EntityManager entityManager = crearEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            Pago pago = entityManager.find(Pago.class, pagoId);
            if (pago == null) {
                throw new EntityNotFoundException("No existe un pago con id " + pagoId);
            }

            if (pago.getDetalle() != null) {
                throw new ServiceException("El pago con id " + pagoId + " ya tiene un detalle registrado");
            }


            Float cambio = metodoPago == MetodoPago.EFECTIVO
                ? montoRecibido - pago.getTotal()
                : 0f;

            ContadoDetalle detalle = new ContadoDetalle(
                LocalDate.now(), metodoPago, montoRecibido, cambio, pago);

            transaction.begin();
            entityManager.persist(detalle);

            if (metodoPago == MetodoPago.EFECTIVO) {
                pago.setEstado(PagoEstado.PAGADO);
                entityManager.merge(pago);
            }

            transaction.commit();

            return detalle;

        } catch (EntityNotFoundException | InvalidArgumentException | ServiceException e) {
            if (transaction.isActive()) transaction.rollback();
            throw e;
        } catch (RuntimeException e) {
            if (transaction.isActive()) transaction.rollback();
            throw new ServiceException("No se pudo registrar el detalle de pago", e);
        } finally {
            if (entityManager.isOpen()) entityManager.close();
        }
    }

    public Optional<ContadoDetalle> obtenerPorId(Long id) {
        EntityManager entityManager = crearEntityManager();
        try {
            return Optional.ofNullable(entityManager.find(ContadoDetalle.class, id));
        } catch (RuntimeException e) {
            throw new ServiceException("No se pudo obtener el detalle de pago", e);
        } finally {
            if (entityManager.isOpen()) entityManager.close();
        }
    }

    private void validarDatos(Long pagoId, MetodoPago metodoPago, Float montoRecibido) {
        if (pagoId == null || pagoId <= 0)
            throw new InvalidArgumentException("El id de pago es obligatorio");
        if (metodoPago == null)
            throw new InvalidArgumentException("El método de pago es obligatorio");
    }
}