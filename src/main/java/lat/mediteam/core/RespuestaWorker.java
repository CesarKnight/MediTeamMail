package lat.mediteam.core;

import lat.mediteam.commands.RootCommand;
import lat.mediteam.mail.Email;
import picocli.CommandLine;
import picocli.CommandLine.ParseResult;

public class RespuestaWorker implements Runnable{
    int id;
    String mailserver;
    String newRecipient;
    String Sender;
    Email parsedEmail;

    CommandLine cmd;

    public RespuestaWorker(int id, String server, String rawEmail) {
        if (rawEmail == null || rawEmail.isEmpty()) {
            return;
        }

        this.id = id;
        this.mailserver = server;
        parsedEmail = new Email(rawEmail);
        this.newRecipient = parsedEmail.getSender();
        this.Sender = parsedEmail.getRecipient();
    }

    @Override
    public void run() {
        System.out.println("Hilo " + id + " atendiendo a cliente " + newRecipient);
        if (parsedEmail == null) {
            System.out.println("No se pudo analizar el correo con ID " + id);
            return;
        }

      // String subject = parsedEmail.getSubject();

        cmd = new CommandLine(new RootCommand());
        // cmd.execute("usuario editar 52 cesar1@example.com 654321".split(" "));
        
        // cmd.execute("usuario crear eliezer@example.com 123456".split(" "));
        // cmd.execute("usuario obtener 52".split(" "));
        cmd.execute("usuario listar".split(" "));
        ParseResult parseResult = cmd.getParseResult();
        if (parseResult.subcommand() != null) {
            CommandLine sub = parseResult.subcommand().subcommand().commandSpec().commandLine();
            
            // todo: definir un formato de respuesta estandarizado para los comandos
            String respuesta = sub.getExecutionResult();

            System.out.println("Respuesta para - test : " + respuesta);
        }
    }
}