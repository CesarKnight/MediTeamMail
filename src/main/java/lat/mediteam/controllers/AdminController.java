package lat.mediteam.controllers;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import lat.mediteam.commands.CommandResponse;
import lat.mediteam.core.AppContext;
import lat.mediteam.core.Session;
import lat.mediteam.models.Admin;
import lat.mediteam.services.AdminService;

public class AdminController {
    
	private AppContext ctx;
	private Session session;
	private AdminService service;
    
	public AdminController(AppContext ctx, Session session, AdminService service){
		this.ctx = ctx;
		this.session = session;
		this.service = service;
	}

	public CommandResponse crearAdmin(Long usuarioId, String nombre, String apellido) {
		Admin nuevo = service.crear(usuarioId, nombre, apellido);
		return new CommandResponse(true, "Admin creado: " + nuevo.getNombre() + " " + nuevo.getApellido());
	}

	public CommandResponse obtenerAdmin(Long id) {
		Optional<Admin> admin = service.obtenerPorId(id);
		if(admin.isEmpty()) {
			throw new EntityNotFoundException("Admin no encontrado: " + id);
		}
		
		Admin a = admin.get();
		return new CommandResponse(true, "Admin: " + a.getNombre() + " " + a.getApellido() + " - " + a.getUsuario().getEmail());
	}

	public CommandResponse listarAdmins() {
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
	}

	public CommandResponse editarAdmin(Long id, String nombre, String apellido) {
		Admin actualizado = service.actualizar(id, nombre, apellido);
		return new CommandResponse(true, "Admin actualizado: " + actualizado.getNombre() + " " + actualizado.getApellido());
	}

	public CommandResponse eliminarAdmin(Long id) {
		service.eliminar(id);
		return new CommandResponse(true, "Admin eliminado");
	}
}