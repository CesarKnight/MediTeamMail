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
        parser.execute(command.split(" "));
        ParseResult parseResult = parser.getParseResult();
        if (parseResult.subcommand() != null) {
            CommandLine sub = parseResult.subcommand().subcommand().commandSpec().commandLine();
            
            CommandResponse respuesta = sub.getExecutionResult();
            return respuesta;
        }
        return new CommandResponse(false, "No se pudo ejecutar el comando");
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

        String command = "usuario listar";
        CommandResponse response = executeCommand(command);
        
        // sendEmail(command, command, command);
        // Para testeo imprimimos 
        System.out.println("Respuesta para usuario " + sender + ":" + response.getMessage());    
    }

}