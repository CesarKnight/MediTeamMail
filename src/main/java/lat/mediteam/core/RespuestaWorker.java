package lat.mediteam.core;

import lat.mediteam.commands.CommandResponse;
import lat.mediteam.commands.RootCommand;
import lat.mediteam.mail.Email;
import picocli.CommandLine;
import picocli.CommandLine.ParseResult;

public class RespuestaWorker implements Runnable{
    int id;
    String mailserver;
    String sender;
    Email parsedEmail;

    CommandLine parser;

    public RespuestaWorker(int id, String server, String rawEmail) {
        if (rawEmail == null || rawEmail.isEmpty()) {
            return;
        }
        this.id = id;
        this.mailserver = server;
        parsedEmail = new Email(rawEmail);
        this.sender = parsedEmail.getRecipient();
    }

    private CommandResponse executeCommand(String command) {
        String[] tokens = command.split(" ");
        parser.execute(tokens);
        ParseResult parseResult = parser.getParseResult();
        if (parseResult.subcommand() != null) {
            CommandLine sub = parseResult.subcommand().subcommand().commandSpec().commandLine();
            
            CommandResponse respuesta = sub.getExecutionResult();
            return respuesta;
        }
        return new CommandResponse(false, "No existe el comando : " + tokens[0] + " " + tokens[1]);
    }

    private void sendEmail(String recipient, String subject, String body) {
        // Aquí iría la lógica para enviar un correo electrónico
        System.out.println("Enviando correo a " + recipient + " con asunto '" + subject + "' y cuerpo: " + body);
    }

    

    @Override
    public void run() {
        System.out.println("Hilo " + id + " atendiendo a cliente " + sender);
        if (parsedEmail == null) {
            System.out.println("No se pudo analizar el correo con ID " + id);
            return;
        }
        parser = new CommandLine(new RootCommand());
        parser.setCaseInsensitiveEnumValuesAllowed(true);

        // String command = "usuario crear evans@gmail.com 123 medico";
        // String command = "usuario listar";
        // String command = "usuario eliminar 202";
        // String command = "usuario editar 4 eliezer22@gmail.com 123";
        // String command = "admin listar";
        // String command = "admin crear 1 cesar caballero";

        //String command = "usuario crear prueba@gmail.com 123456 MEDICO";
        //String command = "medico crear 1 Carlos Pinto 9876543 Cardiologia 1985-10-12";
        //String command = "usuario crear paciente@gmail.com 123456 PACIENTE";
        //String command = "paciente crear 4 Juan";
        //String command = "historia crear 4 1 2025-01-01 pendiente diagnostico";
        //String command = "diagnostico crear 2 Paciente-con-fiebre-alta";
        //String command = "tratamiento crear 2 Reposo-y-medicacion";
        //String command = "involucrados asignar 1 2";

        //String command = "historia crear 1 2025-01-01 pendiente diagnostico";
        String command = "historia crear 1 2025-01-01 pendiente diagnostico";
        //String command = "historia crear 1 2025-01-01 pendiente diagnostico";
        CommandResponse response = executeCommand(command);
        
        // sendEmail(command, command, command);
        // Para testeo imprimimos 
        if (response.isSuccess()) {
            System.out.println("Comando ejecutado exitosamente para usuario " + sender);
        } else {
            System.out.println("Error al ejecutar comando para usuario " + sender);
        }
        System.out.println(response.getMessage());    
    }

}