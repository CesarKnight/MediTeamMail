package lat.mediteam.commands;

import java.util.List;

import lat.mediteam.core.AppContext;
import lat.mediteam.core.Session;
import lat.mediteam.exceptions.InvalidArgumentException;

public class BaseCommands  {
    List<Class<? extends Command>> subCommandsClasses = List.of(
        UsuarioCommands.class
    );

    public CommandResponse execute (AppContext ctx, Session session, List<String> args) {        
        if (args.isEmpty()) {
            throw new InvalidArgumentException( "No se proporcionó ningún comando");
        }
  
        String mainCommand = args.remove(0).toLowerCase();
    
        switch (mainCommand) {
            case "usuario":
                return new UsuarioCommands().execute(ctx, session, args);
            case "ayuda":
                return getAllHelp();
            default:
                throw new InvalidArgumentException( "Comando desconocido: " + mainCommand);
        }
    }
   
    // diavlo
    public CommandResponse getAllHelp() {
        StringBuilder allHelp = new StringBuilder();
        for (Class<? extends Command> commandClass : subCommandsClasses) {
            try {
                Command command = commandClass.getDeclaredConstructor().newInstance();
                allHelp.append(command.getHelp()).append("\n\n");
            } catch (Exception e) {
                System.err.println("Error instantiating " + commandClass.getSimpleName());
            }
        }
        return new CommandResponse(true, allHelp.toString());
    }
}
