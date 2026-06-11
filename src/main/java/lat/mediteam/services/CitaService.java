package lat.mediteam.services;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import lat.mediteam.core.DatabaseManager;
import lat.mediteam.enums.CitaEstado;
import lat.mediteam.models.Cita;
import lat.mediteam.models.Medico;
import lat.mediteam.models.Paciente;
import lat.mediteam.models.Servicio;

public class CitaService {

    private EntityManager crearEntityManager() {
        return DatabaseManager.getEntityManager();
    }

    public Cita crear(Long pacienteId, Long medicoId, Long servicioId,
                      String fecha, String horaInicio, String horaFin,
                      String motivo) {

        validarDatos(pacienteId, medicoId, servicioId, fecha, horaInicio, horaFin);

        EntityManager entityManager = crearEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            Paciente paciente = entityManager.find(Paciente.class, pacienteId);
            if (paciente == null)
                throw new NoSuchElementException("No existe un paciente con id " + pacienteId);

            Medico medico = entityManager.find(Medico.class, medicoId);
            if (medico == null)
                throw new NoSuchElementException("No existe un médico con id " + medicoId);

            Servicio servicio = entityManager.find(Servicio.class, servicioId);
            if (servicio == null)
                throw new NoSuchElementException("No existe un servicio con id " + servicioId);

            // Verificar cruce de horario para el médico
            List<Cita> citasExistentes = entityManager
                .createQuery(
                    "SELECT c FROM Cita c WHERE c.medico.id = :medicoId " +
                    "AND c.fecha = :fecha " +
                    "AND c.estado != :cancelada " +
                    "AND (c.horaInicio < :horaFin AND c.horaFin > :horaInicio)",
                    Cita.class)
                .setParameter("medicoId", medicoId)
                .setParameter("fecha", fecha)
                .setParameter("cancelada", CitaEstado.CANCELADA)
                .setParameter("horaInicio", horaInicio)
                .setParameter("horaFin", horaFin)
                .getResultList();

            if (!citasExistentes.isEmpty()) {
                throw new IllegalStateException(
                    "El médico ya tiene una cita programada en ese horario: "
                    + fecha + " " + horaInicio + " - " + horaFin);
            }

            Cita cita = new Cita(paciente, medico, servicio,
                                  fecha, horaInicio, horaFin, motivo);

            transaction.begin();
            entityManager.persist(cita);
            transaction.commit();

            return cita;

        } catch (RuntimeException e) {
            if (transaction.isActive()) transaction.rollback();
            if (e instanceof NoSuchElementException ||
                e instanceof IllegalStateException ||
                e instanceof IllegalArgumentException) throw e;
            throw new IllegalStateException("No se pudo crear la cita", e);
        } finally {
            if (entityManager.isOpen()) entityManager.close();
        }
    }

    public Optional<Cita> obtenerPorId(Long id) {
        EntityManager entityManager = crearEntityManager();
        try {
            return Optional.ofNullable(entityManager.find(Cita.class, id));
        } catch (RuntimeException e) {
            throw new IllegalStateException("No se pudo obtener la cita", e);
        } finally {
            if (entityManager.isOpen()) entityManager.close();
        }
    }

    public List<Cita> listar() {
        EntityManager entityManager = crearEntityManager();
        try {
            return entityManager
                .createQuery("SELECT c FROM Cita c", Cita.class)
                .getResultList();
        } catch (RuntimeException e) {
            throw new IllegalStateException("No se pudieron listar las citas", e);
        } finally {
            if (entityManager.isOpen()) entityManager.close();
        }
    }

    public List<Cita> listarPorPaciente(Long pacienteId) {
        EntityManager entityManager = crearEntityManager();
        try {
            return entityManager
                .createQuery("SELECT c FROM Cita c WHERE c.paciente.id = :id", Cita.class)
                .setParameter("id", pacienteId)
                .getResultList();
        } catch (RuntimeException e) {
            throw new IllegalStateException("No se pudieron listar las citas", e);
        } finally {
            if (entityManager.isOpen()) entityManager.close();
        }
    }

    public List<Cita> listarPorMedico(Long medicoId) {
        EntityManager entityManager = crearEntityManager();
        try {
            return entityManager
                .createQuery("SELECT c FROM Cita c WHERE c.medico.id = :id", Cita.class)
                .setParameter("id", medicoId)
                .getResultList();
        } catch (RuntimeException e) {
            throw new IllegalStateException("No se pudieron listar las citas", e);
        } finally {
            if (entityManager.isOpen()) entityManager.close();
        }
    }

    public Cita reprogramar(Long id, String nuevaFecha,
                             String nuevaHoraInicio, String nuevaHoraFin) {

        if (nuevaFecha == null || nuevaFecha.isBlank())
            throw new IllegalArgumentException("La nueva fecha es obligatoria");
        if (nuevaHoraInicio == null || nuevaHoraInicio.isBlank())
            throw new IllegalArgumentException("La nueva hora de inicio es obligatoria");
        if (nuevaHoraFin == null || nuevaHoraFin.isBlank())
            throw new IllegalArgumentException("La nueva hora de fin es obligatoria");

        EntityManager entityManager = crearEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            Cita cita = entityManager.find(Cita.class, id);
            if (cita == null)
                throw new NoSuchElementException("No existe una cita con id " + id);

            if (cita.getEstado() == CitaEstado.CANCELADA)
                throw new IllegalStateException("No se puede reprogramar una cita cancelada");

            // Verificar cruce en el nuevo horario
            List<Cita> citasExistentes = entityManager
                .createQuery(
                    "SELECT c FROM Cita c WHERE c.medico.id = :medicoId " +
                    "AND c.fecha = :fecha AND c.id != :citaId " +
                    "AND c.estado != :cancelada " +
                    "AND (c.horaInicio < :horaFin AND c.horaFin > :horaInicio)",
                    Cita.class)
                .setParameter("medicoId", cita.getMedico().getId())
                .setParameter("fecha", nuevaFecha)
                .setParameter("citaId", id)
                .setParameter("cancelada", CitaEstado.CANCELADA)
                .setParameter("horaInicio", nuevaHoraInicio)
                .setParameter("horaFin", nuevaHoraFin)
                .getResultList();

            if (!citasExistentes.isEmpty())
                throw new IllegalStateException(
                    "El médico ya tiene una cita en ese horario: "
                    + nuevaFecha + " " + nuevaHoraInicio + " - " + nuevaHoraFin);

            transaction.begin();
            cita.setFecha(nuevaFecha);
            cita.setHoraInicio(nuevaHoraInicio);
            cita.setHoraFin(nuevaHoraFin);
            cita.setEstado(CitaEstado.POSPUESTA);
            cita = entityManager.merge(cita);
            transaction.commit();

            return cita;

        } catch (RuntimeException e) {
            if (transaction.isActive()) transaction.rollback();
            if (e instanceof NoSuchElementException ||
                e instanceof IllegalStateException ||
                e instanceof IllegalArgumentException) throw e;
            throw new IllegalStateException("No se pudo reprogramar la cita", e);
        } finally {
            if (entityManager.isOpen()) entityManager.close();
        }
    }

    public Cita cancelar(Long id, String motivo) {
        EntityManager entityManager = crearEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            Cita cita = entityManager.find(Cita.class, id);
            if (cita == null)
                throw new NoSuchElementException("No existe una cita con id " + id);

            if (cita.getEstado() == CitaEstado.CANCELADA)
                throw new IllegalStateException("La cita ya está cancelada");

            transaction.begin();
            cita.setEstado(CitaEstado.CANCELADA);
            cita.setMotivo(motivo);
            cita = entityManager.merge(cita);
            transaction.commit();

            return cita;

        } catch (RuntimeException e) {
            if (transaction.isActive()) transaction.rollback();
            if (e instanceof NoSuchElementException ||
                e instanceof IllegalStateException) throw e;
            throw new IllegalStateException("No se pudo cancelar la cita", e);
        } finally {
            if (entityManager.isOpen()) entityManager.close();
        }
    }

    private void validarDatos(Long pacienteId, Long medicoId, Long servicioId,
                               String fecha, String horaInicio, String horaFin) {
        if (pacienteId == null || pacienteId <= 0)
            throw new IllegalArgumentException("El id de paciente es obligatorio");
        if (medicoId == null || medicoId <= 0)
            throw new IllegalArgumentException("El id de médico es obligatorio");
        if (servicioId == null || servicioId <= 0)
            throw new IllegalArgumentException("El id de servicio es obligatorio");
        if (fecha == null || fecha.isBlank())
            throw new IllegalArgumentException("La fecha es obligatoria");
        if (horaInicio == null || horaInicio.isBlank())
            throw new IllegalArgumentException("La hora de inicio es obligatoria");
        if (horaFin == null || horaFin.isBlank())
            throw new IllegalArgumentException("La hora de fin es obligatoria");
    }
}