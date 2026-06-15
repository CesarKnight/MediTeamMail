package lat.mediteam.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import lat.mediteam.commands.CommandResponse;
import lat.mediteam.commands.BaseCommands;
import lat.mediteam.mail.Email;
import lat.mediteam.mail.SmtpCliente;

public class RespuestaWorker implements Runnable{
    int id;
    String mailserver;
    Email parsedEmail;
    
    AppContext appContext;
    BaseCommands parser;
    Session session;

    public RespuestaWorker(int threadId, String server, String rawEmail, AppContext appContext) {
        if (rawEmail == null || rawEmail.isEmpty()) {
            throw new IllegalArgumentException("El correo electrónico no puede ser nulo o vacío");
        }
        if (server == null || server.isEmpty()) {
            throw new IllegalArgumentException("El servidor de correo no puede ser nulo o vacío");
        }
        if (appContext == null) {
            throw new IllegalArgumentException("El contexto de la aplicación no puede ser nulo");
        }
        
        this.id = threadId;
        this.mailserver = server;
        this.appContext = appContext;
        
        parsedEmail = new Email(rawEmail, mailserver);
        System.out.println(parsedEmail);
    }

    private CommandResponse executeCommand(String command) {
        List<String> tokens = new ArrayList<>(List.of(command.split("\\s+")));
        return parser.execute(appContext, session, tokens);
    }

    private void sendEmail(String recipient, String subject, String body) {
        int pointIndex = Config.MAIL_SERVER.indexOf(".") + 1;
        String fullServerSender = Config.MAIL_TO_LISTEN + "@" + Config.MAIL_SERVER.substring(pointIndex);
        
        SmtpCliente smtp = new SmtpCliente(
            mailserver, 
            parsedEmail.getSender(), 
            fullServerSender
        );
        
        smtp.connect();
        smtp.sendEmail(subject,body,""); //todo cambiar implementacion de smtp

        System.out.println(
            "Enviando correo a " + recipient + "\n" + 
            "Con asunto '" + subject + "'\n" 
            // + "Cuerpo: \n" + body
        );
    }


    @Override
    public void run() {
        System.out.println("Hilo " + id + " atendiendo a cliente " + parsedEmail.getSender());
        if (parsedEmail == null) {
            throw new IllegalArgumentException("El correo electrónico no puede ser nulo");
        }
        try {
            parser = new BaseCommands();
            // ponerSessiondePrueba(); // TODO Pone una sesion ya logeada, eliminar en produccion
            
            session = appContext.getAuthManager().findByEmail(parsedEmail.getSender());
            if (session == null) {
                session = Session.nonAuthenticated(parsedEmail.getSender()); // produccion
            }
            
            System.out.println("Comando recibido: " + parsedEmail.getSubject());
            String command = parsedEmail.getSubject().trim();
            CommandResponse response = executeCommand(command);
            
            System.out.println("Comando ejecutado exitosamente para usuario " + parsedEmail.getSender());
            
            sendEmail(
                parsedEmail.getSender(),
                "Respuesta a comando: " + command, 
                response.getMessage()
            );
            
        } catch (Exception e) {
            String errorMessage = e.getClass().getSimpleName() + ": " + e.getMessage();
            sendEmail(
                parsedEmail.getSender(),
                "Error al ejecutar commando: " + parsedEmail.getSubject(), 
                errorMessage
            );
        }
    }

    private void ponerSessiondePrueba(){
        Long pruebaId = 1L;
        String pruebaEmail = parsedEmail.getSender();
        Set<String> permisos = Set.of("permiso1", "permiso2"); // todo implementar permisos
        

        AuthManager authManager = appContext.getAuthManager();
        authManager.login(pruebaId,pruebaEmail,permisos); // todo implementar permisos
        session = authManager.findByEmail(pruebaEmail);
    }
}