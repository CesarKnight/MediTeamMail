package lat.mediteam.commands;

import java.util.List;

import lat.mediteam.core.AppContext;
import lat.mediteam.core.Session;

public class Parser {
    List<Class<? extends Command>> subCommandsClasses = List.of(
        UsuarioCommands.class
    );

    public CommandResponse execute(AppContext ctx, Session session, List<String> args) {        
        if (args.isEmpty()) {
            return new CommandResponse(false, "No se proporcionó ningún comando");
        }

        String mainCommand;
        try {
            mainCommand = args.get(0);
            mainCommand = mainCommand.toLowerCase();
       } catch (Exception e) {
            return new CommandResponse(false, "Error al procesar el comando: " + e.getMessage());
       }

        switch (mainCommand) {
            case "usuario":
                return new UsuarioCommands().execute(ctx, session, args);
            default:
                return new CommandResponse(false, "Comando desconocido: " + mainCommand);
        }
    }

    
    public CommandResponse getHelp() {
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
