package lat.mediteam.controllers;

import java.util.List;
import java.util.Optional;

import lat.mediteam.commands.CommandResponse;
import lat.mediteam.enums.ServicioEstado;
import lat.mediteam.models.Servicio;
import lat.mediteam.services.ServicioService;

public class ServicioController {

    private ServicioService service;

    public ServicioController(ServicioService service) {
        this.service = service;
    }

    public CommandResponse crearServicio(String titulo, String descripcion,
                                         Double precio, String duracion,
                                         ServicioEstado estado) {
        try {
            Servicio nuevo = service.crear(titulo, descripcion, precio, duracion, estado);
            return new CommandResponse(true,
                "Servicio creado: " + nuevo.getTitulo()
                + " | Precio: " + nuevo.getPrecio()
                + " | Estado: " + nuevo.getEstado());
        } catch (Exception e) {
            return new CommandResponse(false, "Error al crear servicio: " + e.getMessage());
        }
    }

    public CommandResponse obtenerServicio(Long id) {
        try {
            Optional<Servicio> servicio = service.obtenerPorId(id);
            if (servicio.isPresent()) {
                Servicio s = servicio.get();
                return new CommandResponse(true,
                    "Servicio #" + s.getId()
                    + " | Titulo: " + s.getTitulo()
                    + " | Descripcion: " + s.getDescripcion()
                    + " | Precio: " + s.getPrecio()
                    + " | Duracion: " + s.getDuracion()
                    + " | Estado: " + s.getEstado());
            } else {
                return new CommandResponse(false, "Servicio no encontrado con id: " + id);
            }
        } catch (Exception e) {
            return new CommandResponse(false, "Error al obtener servicio: " + e.getMessage());
        }
    }

    public CommandResponse listarServicios() {
        try {
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

        } catch (Exception e) {
            return new CommandResponse(false, "Error al listar servicios: " + e.getMessage());
        }
    }

    public CommandResponse listarDisponibles() {
        try {
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

        } catch (Exception e) {
            return new CommandResponse(false, "Error al listar servicios disponibles: " + e.getMessage());
        }
    }

    public CommandResponse buscarServicio(String titulo) {
        try {
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

        } catch (Exception e) {
            return new CommandResponse(false, "Error al buscar servicio: " + e.getMessage());
        }
    }

    public CommandResponse editarServicio(Long id, String titulo, String descripcion,
                                          Double precio, String duracion,
                                          ServicioEstado estado) {
        try {
            Servicio actualizado = service.actualizar(id, titulo, descripcion,
                                                      precio, duracion, estado);
            return new CommandResponse(true,
                "Servicio actualizado: " + actualizado.getTitulo()
                + " | Precio: " + actualizado.getPrecio()
                + " | Estado: " + actualizado.getEstado());
        } catch (Exception e) {
            return new CommandResponse(false, "Error al editar servicio: " + e.getMessage());
        }
    }

    public CommandResponse eliminarServicio(Long id) {
        try {
            boolean ok = service.eliminar(id);
            return ok
                ? new CommandResponse(true, "Servicio eliminado correctamente.")
                : new CommandResponse(false, "No se encontró servicio con id: " + id);
        } catch (Exception e) {
            return new CommandResponse(false, "Error al eliminar servicio: " + e.getMessage());
        }
    }
}