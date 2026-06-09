package lat.mediteam.core;

import java.util.ArrayList;
import java.util.List;

import lat.mediteam.commands.CommandResponse;
import lat.mediteam.commands.BaseCommands;
import lat.mediteam.mail.Email;

public class RespuestaWorker implements Runnable{
    int id;
    String mailserver;
    String sender;
    Email parsedEmail;
    
    AppContext appContext;
    BaseCommands parser;
    Session session;

    public RespuestaWorker(int id, String server, String rawEmail, AppContext appContext) {
        if (rawEmail == null || rawEmail.isEmpty()) {
            return;
        }
        this.id = id;
        this.mailserver = server;
        parsedEmail = new Email(rawEmail);
        this.sender = parsedEmail.getRecipient();
        this.appContext = appContext;
    }

    private CommandResponse executeCommand(String command) {
        List<String> tokens = new ArrayList<>(List.of(command.split("\\s+")));
        return parser.execute(appContext, session, tokens);
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

        try {
            parser = new BaseCommands();
            session = appContext.getAuthManager().findByEmail(sender);

            // String command = "usuario crear choco@gmail.com 123 medico";
            String command = "usuario    listar";
            // String command = "usuario eliminar 202";
            // String command = "usuario editar 4 eliezer22@gmail.com 123";
            // String command = "admin listar";
            // String command = "admin obtener 1";
            // String command = "admin crear 1 cesar caballero";
            // String command = "login evans@gmail.com 123";
            // String command = "help";

            CommandResponse response = executeCommand(command);
            
            System.out.println("Comando ejecutado exitosamente para usuario " + sender);
            System.out.println(response.getMessage());
            
        } catch (Exception e) {
            String errorMessage = e.getClass().getSimpleName() + ": " + e.getMessage();
            System.out.println("Error en hilo " + id + ": " + errorMessage);

            // sendEmail(sender, "Error al procesar comando", errorMessage);
        }
    }

}