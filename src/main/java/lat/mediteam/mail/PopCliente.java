package lat.mediteam.mail;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class PopCliente {
    int puertoPop;
    String servidor;
    String usuario;
    String contrasena;
    
    Socket socketPOP;
    BufferedReader entrada;
    BufferedWriter salida;

    Boolean isLoggedIn = false;
    String comando="";

    public PopCliente(String servidor, String usuario, String contrasena, int port){
        this.servidor=servidor;
        this.usuario=usuario;
        this.contrasena=contrasena;
        this.puertoPop=port;
    }

    public PopCliente(String servidor, String usuario, String contrasena){
        this(servidor, usuario, contrasena, 110);// Puerto por defecto del protocolo POP3
    }

    public boolean connect(int timeout){
        try {
            socketPOP = new Socket(servidor,puertoPop);
            socketPOP.setSoTimeout(timeout);

            entrada = new BufferedReader(new InputStreamReader(socketPOP.getInputStream()));
            salida = new BufferedWriter(new OutputStreamWriter(socketPOP.getOutputStream()));
            entrada.readLine();
            return true;
            
        } catch (Exception e) {
            return false;
            // throw new RuntimeException("No se pudo conectar al servidor POP3: " + e.getMessage());
        }
    }

    public boolean disconnect(){
        try {
            if (socketPOP != null) socketPOP.close();
            if (entrada != null) entrada.close();
            if (salida != null) salida.close();
            return true;
        } catch (Exception e) {
            return false;
            // throw new RuntimeException("Error al cerrar la conexión: " + e.getMessage());
        }
    }

    public boolean login(){
        try {
            comando="USER "+usuario+"\r\n";
            // System.out.print("C : "+comando);
            salida.write( comando );   
            salida.flush();
            String response = entrada.readLine();
            
            comando="PASS "+contrasena+"\r\n";
            salida.write( comando );   
            salida.flush();
            response = entrada.readLine();
            
            if (response.startsWith("+OK")) {
                isLoggedIn = true;
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
            // throw new RuntimeException("Error al iniciar sesión: " + e.getMessage());
        }
    }

    public boolean logout(){
        if (!isLoggedIn) {
            return false;
        }
        
        try {
            comando="QUIT\r\n";
            salida.write( comando );   
            salida.flush();
            entrada.readLine();
            isLoggedIn = false;
            return true;
        } catch (Exception e) {
            return false;
            // throw new RuntimeException(" S : Error al cerrar sesión: " + e.getMessage());
        }
    }

    public int getEmailCount(){
        if (!isLoggedIn) {
            throw new RuntimeException("S: No hay sesión activa");
        }

        int emailCount = 0;
        try {
            comando="STAT\r\n";
            salida.write( comando );   
            salida.flush();
            String response = entrada.readLine();
            // System.out.println("S: " + response);
            
            if (response.startsWith("+OK")) {
                String[] parts = response.split(" ");
                if (parts.length >= 2) {
                    emailCount = Integer.parseInt(parts[1]);
                }
            } else {
                // Si la respuesta no es +OK, se considera que no se pudo obtener el número de correos
                return -1;
            }
        } catch (Exception e) {
            return -1;
        }
        return emailCount;
    }

    public String readEmail(int emailIndex){
        if (!isLoggedIn) {
            throw new RuntimeException("S: No hay sesión activa");
        }

        try {
            comando="RETR " + emailIndex + "\r\n";
            salida.write( comando );    
            salida.flush();           
            return readMultilineResponse();
        } catch (Exception e) {
            throw new RuntimeException("S: Error al leer el correo: " + e.getMessage());
        }
    }
           
    public boolean deleteEmail(int emailIndex){
        if (!isLoggedIn) {
            throw new RuntimeException("S: No hay sesión activa");
        }
        try {
            comando="DELE " + emailIndex + "\r\n";
            salida.write( comando );               
            salida.flush();
            String response = entrada.readLine();
            // System.out.println("S : "+response);

            if (response.startsWith("+OK")) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
            // throw new RuntimeException("Error al eliminar el correo #" +emailIndex+ ": " + e.getMessage());
        }
    }

    private String readMultilineResponse() throws Exception {
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = entrada.readLine()) != null) {
            if (line.equals(".")) {
                break; // Fin de la respuesta multilinea
            }
            response.append(line).append("\r\n");
        }
        return response.toString();
    }
}