package lat.mediteam.mail;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class SmtpCliente {
    int puertoSMTP;
    String servidor;
    String user_receptor;
    String user_emisor;
    
    Socket socketSMTP;
    BufferedReader entrada;
    BufferedWriter salida;

    String comando="";
    
    public SmtpCliente(String servidor, String user_receptor, String user_emisor, int puertoSMTP) {
        this.servidor = servidor;
        this.user_receptor = user_receptor;
        this.user_emisor = user_emisor;
        this.puertoSMTP = puertoSMTP; // Puerto por defecto del protocolo SMTP
    }

    public SmtpCliente(String servidor, String user_receptor, String user_emisor) {
        this(servidor, user_receptor, user_emisor, 25);
    }

    public void connect(){
        try {
            socketSMTP = new Socket(servidor, puertoSMTP);
            entrada = new BufferedReader(new InputStreamReader(socketSMTP.getInputStream()));
            salida = new BufferedWriter(new OutputStreamWriter(socketSMTP.getOutputStream()));

            System.out.println("S : " + entrada.readLine());
        } catch (IOException e) {
            System.out.println("S : No se pudo conectar error: " + e.getMessage());
        }
    }

    public void sendEmail(String Subject, String plainBody, String htmlBody){
        if (socketSMTP == null || entrada == null || salida == null) {
            System.out.println(" S : No se pudo establecer la conexión con el servidor SMTP");
            return;
        }
        try {
            comando="HELO "+servidor+" \r\n";
            System.out.print("C : "+comando);
            salida.write( comando );   
            salida.flush();
            System.out.println("S : " + entrada.readLine());
            
            comando="MAIL FROM: <"+user_emisor+"> \r\n";
            System.out.print("C : "+comando);
            salida.write( comando );               
            salida.flush();
            System.out.println("S : "+entrada.readLine());               

            comando="RCPT TO: <"+user_receptor+"> \r\n";
            System.out.print("C : "+comando);
            salida.write( comando );               
            salida.flush();
            System.out.println("S : "+entrada.readLine());
            
            comando="DATA\r\n";
            System.out.print("C : "+comando);
            salida.write( comando );               
            salida.flush();
            System.out.println("S : "+entrada.readLine());
            
            String boundary = "boundary-simple-123456";

            comando="From: "+user_emisor+"\r\n"+"To: "+user_receptor+"\r\n";
            System.out.print("C : "+comando);
            salida.write( comando );               

                comando="Subject: "+Subject+"\r\n"
                    +"MIME-Version: 1.0\r\n"
                    +"Content-Type: multipart/alternative; boundary=\""+boundary+"\"\r\n"
                    +"\r\n"
                    +"--"+boundary+"\r\n"
                    +"Content-Type: text/plain; charset=UTF-8\r\n"
                    +"Content-Transfer-Encoding: 7bit\r\n"
                    +"\r\n"
                    +plainBody+"\r\n"
                    +"\r\n"
                    +"--"+boundary+"\r\n"
                    +"Content-Type: text/html; charset=UTF-8\r\n"
                    +"Content-Transfer-Encoding: 7bit\r\n"
                    +"\r\n"
                    +htmlBody+"\r\n"
                    +"\r\n"
                    +"--"+boundary+"--\r\n"
                    +".\r\n";

            System.out.print("C : "+comando);
            salida.write( comando );               
            salida.flush();
            System.out.println("S : "+entrada.readLine());
            
            comando="QUIT\r\n";
            System.out.print("C : "+comando);
            salida.write( comando );    
            salida.flush();           
            System.out.println("S : "+entrada.readLine());   
        } catch (IOException e) {
            System.out.println(" S : Error al enviar el correo: " + e.getMessage());
        } finally {
            disconnect();
        }
    }

    public void disconnect(){
        try {
            if (socketSMTP != null) socketSMTP.close();
            if (entrada != null) entrada.close();
            if (salida != null) salida.close();
        } catch (IOException e) {
            System.out.println(" S : Error al cerrar la conexión: " + e.getMessage());
        }
    }
}
