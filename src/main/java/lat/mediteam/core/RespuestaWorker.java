package lat.mediteam.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import lat.mediteam.commands.CommandResponse;
import lat.mediteam.commands.BaseCommands;
import lat.mediteam.mail.Email;

public class RespuestaWorker implements Runnable{
    int id;
    String mailserver;
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
        System.out.println("Hilo " + id + " atendiendo a cliente " + parsedEmail.getSender());
        if (parsedEmail == null) {
            System.out.println("No se pudo analizar el correo con ID " + id);
            return;
        }

        try {
            parser = new BaseCommands();
            session = appContext.getAuthManager().findByEmail(parsedEmail.getSender());
            
            ponerSessiondePrueba(); // todo eliminar en produccion, Pone una sesion ya logeada

            if (session == null) {
                // session = Session.nonAuthenticated("cesar@gmail.com"); // para probar logeo
                session = Session.nonAuthenticated(parsedEmail.getSender()); // produccion
            }
            

            // String command = "usuario crear choco@gmail.com 123 paciente";
            // String command = "usuario    listar";
            // String command = "usuario eliminar 202";
            // String command = "usuario editar 4 eliezer22@gmail.com 123";
            // String command = "admin listar";
            // String command = "admin obtener 1";
            // String command = "admin crear 1 cesar caballero";
            // String command = "login 123";
            // String command = "logout";
            // String command = "paciente crear 2 choquito jimenes 754623 choco@example.com";
            // String command= "paciente listar";
            // String command = "paciente eliminar 2";
            // String command = "tratamiento crear ";
            // String command = "medico crear 1 cesar caballero 12345678 cardiologia 1990-01-01";
            // String command = "medico listar";
            // String command = "historiaclinica crear 1 2024-01-01 pendiente diagnostico";
            String command = "historiaclinica listar";

            // String command = "ayuda";
            

            CommandResponse response = executeCommand(command);
            
            System.out.println("Comando ejecutado exitosamente para usuario " + parsedEmail.getSender());
            System.out.println(response.getMessage());
            
        } catch (Exception e) {
            String errorMessage = e.getClass().getSimpleName() + ": " + e.getMessage();
            System.out.println("Error en hilo " + id + ": " + errorMessage);

            // sendEmail(sender, "Error al procesar comando", errorMessage);
        }
    }

    private void ponerSessiondePrueba(){
        Long pruebaId = 1L;
        String pruebaEmail = "cesar@gmail.com";
        Set<String> permisos = Set.of("permiso1", "permiso2"); // todo implementar permisos
        

        AuthManager authManager = appContext.getAuthManager();
        authManager.login(pruebaId,pruebaEmail,permisos); // todo implementar permisos
        session = authManager.findByEmail(pruebaEmail);
    }
}