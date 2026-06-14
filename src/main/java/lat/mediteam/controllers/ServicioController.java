package lat.mediteam.controllers;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import lat.mediteam.commands.CommandResponse;
import lat.mediteam.core.AppContext;
import lat.mediteam.core.Session;
import lat.mediteam.enums.ServicioEstado;
import lat.mediteam.models.Servicio;
import lat.mediteam.services.ServicioService;

public class ServicioController {

    private AppContext ctx;
    private Session session;
    private ServicioService service;

    public ServicioController(AppContext ctx, Session session, ServicioService service) {
        this.ctx = ctx;
        this.session = session;
        this.service = service;
    }

    public CommandResponse crearServicio(String titulo, String descripcion,
                                         Double precio, String duracion,
                                         ServicioEstado estado) {
        Servicio nuevo = service.crear(titulo, descripcion, precio, duracion, estado);
        return new CommandResponse(true,
            "Servicio creado: " + nuevo.getTitulo()
            + " | Precio: " + nuevo.getPrecio()
            + " | Estado: " + nuevo.getEstado());
    }

    public CommandResponse obtenerServicio(Long id) {
        Optional<Servicio> servicio = service.obtenerPorId(id);
        if (servicio.isEmpty()) {
            throw new EntityNotFoundException("Servicio no encontrado: " + id);
        }

        Servicio s = servicio.get();
        return new CommandResponse(true,
            "Servicio #" + s.getId()
            + " | Titulo: " + s.getTitulo()
            + " | Descripcion: " + s.getDescripcion()
            + " | Precio: " + s.getPrecio()
            + " | Duracion: " + s.getDuracion()
            + " | Estado: " + s.getEstado());
    }

    public CommandResponse listarServicios() {
        List<Servicio> servicios = service.listar();
        if (servicios.isEmpty()) {
            return new CommandResponse(true, "No hay servicios registrados.");
        }

        StringBuilder resultado = new StringBuilder();
        resultado.append("=== Lista de Servicios ===\n");
        for (Servicio s : servicios) {
            resultado.append(System.lineSeparator());
            resultado.append(s.getId()).append(" | ")
                .append(s.getTitulo())
                .append(" | Precio: ").append(s.getPrecio())
                .append(" | Duracion: ").append(s.getDuracion() != null ? s.getDuracion() : "-")
                .append(" | Estado: ").append(s.getEstado());
        }
        return new CommandResponse(true, resultado.toString());
    }

    public CommandResponse listarDisponibles() {
        List<Servicio> servicios = service.listarDisponibles();
        if (servicios.isEmpty()) {
            return new CommandResponse(true, "No hay servicios disponibles.");
        }

        StringBuilder resultado = new StringBuilder();
        resultado.append("=== Servicios Disponibles ===\n");
        for (Servicio s : servicios) {
            resultado.append(System.lineSeparator());
            resultado.append(s.getId()).append(" | ")
                .append(s.getTitulo())
                .append(" | Precio: ").append(s.getPrecio())
                .append(" | Duracion: ").append(s.getDuracion() != null ? s.getDuracion() : "-");
        }
        return new CommandResponse(true, resultado.toString());
    }

    public CommandResponse buscarServicio(String titulo) {
        List<Servicio> servicios = service.buscarPorTitulo(titulo);
        if (servicios.isEmpty()) {
            return new CommandResponse(true, "No se encontraron servicios con: " + titulo);
        }

        StringBuilder resultado = new StringBuilder();
        resultado.append("=== Resultados para: ").append(titulo).append(" ===\n");
        for (Servicio s : servicios) {
            resultado.append(System.lineSeparator());
            resultado.append(s.getId()).append(" | ")
                .append(s.getTitulo())
                .append(" | Precio: ").append(s.getPrecio())
                .append(" | Estado: ").append(s.getEstado());
        }
        return new CommandResponse(true, resultado.toString());
    }

    public CommandResponse editarServicio(Long id, String titulo, String descripcion,
                                          Double precio, String duracion,
                                          ServicioEstado estado) {
        Servicio actualizado = service.actualizar(id, titulo, descripcion, precio, duracion, estado);
        return new CommandResponse(true,
            "Servicio actualizado: " + actualizado.getTitulo()
            + " | Precio: " + actualizado.getPrecio()
            + " | Estado: " + actualizado.getEstado());
    }

    public CommandResponse eliminarServicio(Long id) {
        service.eliminar(id);
        return new CommandResponse(true, "Servicio eliminado correctamente.");
    }
}