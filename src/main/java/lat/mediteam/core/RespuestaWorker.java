package lat.mediteam.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private List<String> tokenize(String command) {
        List<String> tokens = new ArrayList<>();
        Matcher matcher = Pattern.compile("\"([^\"]*)\"|'([^')']*)'|(\\S+)").matcher(command);
        
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                tokens.add(matcher.group(1)); // Double-quoted: strip quotes
            } else if (matcher.group(2) != null) {
                tokens.add(matcher.group(2)); // Single-quoted: strip quotes
            } else {
                tokens.add(matcher.group(3)); // Unquoted token
            }
        }
        
        return tokens;
    }
    
    private void sendEmail(String recipient, String subject, String body) {
        int pointIndex = Config.MAIL_SERVER.indexOf(".") + 1;
        String fullServerSender = Config.MAIL_TO_LISTEN + "@" + Config.MAIL_SERVER.substring(pointIndex);
        
        SmtpCliente smtp = new SmtpCliente(
            mailserver, 
            parsedEmail.getSender(), 
            fullServerSender
        );
        
        Boolean enviado = false;
        
        int retries = 0;
        while (retries < Config.MAX_SEND_EMAIL_RETRIES) {
            smtp.connect(Config.SMTP_TIMEOUT_MS);
            enviado = smtp.sendEmail(subject,body);
            if (enviado){
                smtp.disconnect();
                
                System.out.println(
                    "Correo enviado a " + recipient + "\n" + 
                    "Con asunto '" + subject + "'\n" 
                    + "Cuerpo: \n" + body
                );
                break;
            }
            
            System.out.println("fallo al enviar respuesta a " + parsedEmail.getSender());
            System.out.println("reenviando...");
            retries++;
        }
        if (retries > Config.MAX_SEND_EMAIL_RETRIES){
            System.out.println("NO se puedo enviar el correo a " +  parsedEmail.getSender() + " \n Con sujeto: " + subject);
        }
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
                session = Session.nonAuthenticated(parsedEmail.getSender());
            }
            
            System.out.println("Comando recibido: " + parsedEmail.getSubject());
            
            String command = parsedEmail.getSubject().trim();
            List<String> tokens = tokenize(command);
            CommandResponse response = parser.execute(appContext, session, tokens);;
            
            System.out.println("Comando ejecutado exitosamente para usuario " + parsedEmail.getSender());

            Email respuestaEmail = new Email();
            respuestaEmail.addPlainBody(response.getMessage());
            if(response.getImageB64() != null){
                respuestaEmail.addImageB64(response.getImageB64());
            }

            sendEmail(
                parsedEmail.getSender(),
                "Respuesta a comando: " + command, 
                respuestaEmail.getBody()
            );
            
        } catch (Exception e) {
            String errorMessage = "Error " + e.getClass().getSimpleName() + ": " + e.getMessage();
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