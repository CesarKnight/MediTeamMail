package lat.mediteam.commands;

import picocli.CommandLine.Command;

@Command(
    name = "mediteam",
    description = "MediTeam Mail - Sistema de informacion clinica por correo",
    subcommands = {
        UsuarioCommands.class,
        AdminCommands.class,
        MedicoCommands.class,
        HistoriaClinicaCommands.class,
        MedicosInvolucradosCommands.class,
        DiagnosticoCommands.class,
        TratamientoCommands.class
    }
)
public class RootCommand {
}
