package lat.mediteam.commands;

import java.util.List;

import lat.mediteam.core.AppContext;
import lat.mediteam.core.Session;
import lat.mediteam.exceptions.InvalidArgumentException;

public class BaseCommands  {
    List<Class<? extends Command>> subCommandsClasses = List.of(
        UsuarioCommands.class,
        AdminCommands.class,
        PacienteCommands.class,
        PermisoCommands.class
    );

    public CommandResponse execute (AppContext ctx, Session session, List<String> args) {        
        if (args.isEmpty()) {
            throw new InvalidArgumentException( "No se proporcionó ningún comando");
        }
  
        String mainCommand = args.remove(0).toLowerCase();
    
        // comandos que funcionen sin o con sesión
        switch (mainCommand) {
            case "ayuda":
                return getAllHelp();
        }
        
        // comandos que no requieren estar autenticado
        if (!session.isAuthenticated()){
            switch (mainCommand) {
                case "login":
                    return new AuthCommands().login(ctx, session, args);
                default:
                    throw new InvalidArgumentException( "Comando desconocido o requiere sesión: " + mainCommand);
            }
        }
    
        // comandos que requieren autenticación
        if(session.isAuthenticated()){
            switch (mainCommand) {
                case "login"    :
                    throw new InvalidArgumentException( "Ya estás logueado. Usa 'logout' para cerrar sesión.");
                case "logout":
                    return new AuthCommands().logout(ctx, session, args);
                case "usuario":
                    return new UsuarioCommands().execute(ctx, session, args);
                case "admin":
                    return new AdminCommands().execute(ctx, session, args);
                case "paciente":
                    return new PacienteCommands().execute(ctx, session, args);
                case "permiso":
                    return new PermisoCommands().execute(ctx, session, args);
                default:
                    throw new InvalidArgumentException( "Comando desconocido: " + mainCommand);
            }
        }

        return new CommandResponse(false, "No se pudo ejecutar el comando: " + mainCommand);
    }
   
    // diavlo
    public CommandResponse getAllHelp() {
        String extra = 
            "Comandos generales:\n" +
            "  ayuda - Muestra esta ayuda\n" +
            "  <comando> ayuda - Muestra ayuda específica para un comando\n" +
            "  login <email> <password> - Inicia sesión\n" +
            "  logout - Cierra sesión\n\n";

        StringBuilder allHelp = new StringBuilder();
        for (Class<? extends Command> commandClass : subCommandsClasses) {
            try {
                Command command = commandClass.getDeclaredConstructor().newInstance();
                allHelp.append(command.getHelp()).append("\n\n");
            } catch (Exception e) {
                System.err.println("Error instantiating " + commandClass.getSimpleName());
            }
        }
        return new CommandResponse(true, extra + allHelp.toString());
    }
}
