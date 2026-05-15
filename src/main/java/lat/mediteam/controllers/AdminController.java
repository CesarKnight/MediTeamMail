package lat.mediteam.controllers;

import java.util.List;
import java.util.Optional;

import lat.mediteam.commands.CommandResponse;
import lat.mediteam.models.Admin;
import lat.mediteam.services.AdminService;

public class AdminController {
    
	private AdminService service;
    
	public AdminController(AdminService service){
		this.service = service;
	}

	public CommandResponse crearAdmin(Long usuarioId, String nombre, String apellido) {
		try {
			Admin nuevo = service.crear(usuarioId, nombre, apellido);
			return new CommandResponse(true, "Admin creado: " + nuevo.getNombre() + " " + nuevo.getApellido());
		} catch (Exception e) {
			return new CommandResponse(false, e.getMessage());
		}
	}

	public CommandResponse obtenerAdmin(Long id) {
		try {
			Optional<Admin> admin = service.obtenerPorId(id);
			if (admin.isPresent()) {
				Admin a = admin.get();
				return new CommandResponse(true, "Admin: " + a.getNombre() + " " + a.getApellido() + " - " + a.getUsuario().getEmail());
			} else {
				return new CommandResponse(false, "Admin no encontrado");
			}
		} catch (Exception e) {
			return new CommandResponse(false, e.getMessage());
		}
	}

	public CommandResponse listarAdmins() {
		try {
			List<Admin> admins = service.listar();
			if (admins.isEmpty()) {
				return new CommandResponse(true, "No hay admins");
			}

			StringBuilder resultado = new StringBuilder();
			for (Admin a : admins) {
				if (resultado.length() > 0) {
					resultado.append(System.lineSeparator());
				}
				resultado.append(a.getId()).append(" - ")
					.append(a.getNombre()).append(" ").append(a.getApellido())
					.append(" - ")
					.append(a.getUsuario() != null ? a.getUsuario().getEmail() : "(sin usuario)");
			}

			return new CommandResponse(true, resultado.toString());
		} catch (Exception e) {
			return new CommandResponse(false, e.getMessage());
		}
	}

	public CommandResponse editarAdmin(Long id, String nombre, String apellido) {
		try {
			Admin actualizado = service.actualizar(id, nombre, apellido);
			return new CommandResponse(true, "Admin actualizado: " + actualizado.getNombre() + " " + actualizado.getApellido());
		} catch (Exception e) {
			return new CommandResponse(false, e.getMessage());
		}
	}

	public CommandResponse eliminarAdmin(Long id) {
		try {
			boolean ok = service.eliminar(id);
			return ok
				? new CommandResponse(true, "Admin eliminado")
				: new CommandResponse(false, "Admin no encontrado");
		} catch (Exception e) {
			return new CommandResponse(false, e.getMessage());
		}
	}

}
