package lat.mediteam.commands;

import picocli.CommandLine.Command;

@Command(
    name = "mediteam",
    description = "MediTeam Mail - Sistema de informacion clinica por correo",
    subcommands = {
        UsuarioCommands.class,
        AdminCommands.class,
        PermisoCommands.class   // <--- esta línea es clave
    }
)
public class RootCommand {
}