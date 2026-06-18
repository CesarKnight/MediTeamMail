package lat.mediteam.mail;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

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

    public boolean connect(int timeout){
        try {
            socketSMTP = new Socket();
            SocketAddress socketAddress = new InetSocketAddress(servidor,puertoSMTP); 
            socketSMTP.connect(socketAddress, timeout);
            socketSMTP.setSoTimeout(timeout);

            entrada = new BufferedReader(new InputStreamReader(socketSMTP.getInputStream()));
            salida = new BufferedWriter(new OutputStreamWriter(socketSMTP.getOutputStream()));

            System.out.println(entrada.readLine());
            return true;
        } catch (Exception e) {
            return false;
            // throw new RuntimeException("S: No se pudo conectar al servidor SMTP: " + e.getMessage());
        }
    }

    private String normalizeLineEndings(String text) {
        if (text == null) return "";
        // Normalize all variants (\r\n, \r, \n) to \r\n
        return text.replaceAll("\r\n|\r|\n", "\r\n");
    }

    public boolean sendEmail(String Subject, String rawBody){
        if (socketSMTP == null || entrada == null || salida == null) {
            throw new RuntimeException(" S : No se pudo establecer la conexión con el servidor SMTP");
        }

        String body = normalizeLineEndings(rawBody);
        boolean delivered = false;
        try {
            comando="HELO "+servidor+" \r\n";
            System.out.print("C : "+comando);
            salida.write( comando );   
            salida.flush();
            System.out.println(entrada.readLine());
            
            comando="MAIL FROM: <"+user_emisor+"> \r\n";
            System.out.print("C : "+comando);
            salida.write( comando );               
            salida.flush();
            System.out.println(entrada.readLine());

            comando="RCPT TO: <"+user_receptor+"> \r\n";
            System.out.print("C : "+comando);
            salida.write( comando );               
            salida.flush();
            System.out.println(entrada.readLine());
            
            comando="DATA\r\n";
            System.out.print("C : "+comando);
            salida.write( comando );               
            salida.flush();
            System.out.println(entrada.readLine());     

            comando="Subject: "+Subject+"\r\n"+
                    body+ "\r\n"+
                    ".\r\n";

            salida.write( comando ); 
            System.out.println(comando);              
            salida.flush();
            String response = entrada.readLine();
            System.out.println(response);
            if (response.startsWith("250")) {
                delivered = true;
            }

            comando="QUIT\r\n";
            salida.write( comando );    
            salida.flush();
            System.out.println(entrada.readLine());
            return delivered;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            disconnect();
        }
    }

    public void disconnect(){
        try {
            if (socketSMTP != null) socketSMTP.close();
            if (entrada != null) entrada.close();
            if (salida != null) salida.close();
        } catch (Exception e) {
            throw new RuntimeException("S: Error al cerrar la conexión: " + e.getMessage());
        }
    }
}