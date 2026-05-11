package lat.mediteam.mail;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
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

    public void connect(){
        try {
            socketPOP = new Socket(servidor,puertoPop);
            entrada = new BufferedReader(new InputStreamReader(socketPOP.getInputStream()));
            salida = new BufferedWriter(new OutputStreamWriter(socketPOP.getOutputStream()));
            System.out.println("S : "+entrada.readLine()+"\r\n");

        } catch (IOException e) {

            if( socketPOP != null && entrada != null && salida != null ){
                System.out.println(" S : No se pudo establecer la conexión con el servidor POP3");
                return;
            }else{
                System.out.println(" S : Error al conectar con el servidor POP3: " + e.getMessage());
            }
        }
    }

    public void disconnect(){
        try {
            if (socketPOP != null) socketPOP.close();
            if (entrada != null) entrada.close();
            if (salida != null) salida.close();
        } catch (IOException e) {
            System.out.println(" S : Error al cerrar la conexión: " + e.getMessage());
        }
    }

    public void login(){
        try {
            comando="USER "+usuario+"\r\n";
            System.out.print("C : "+comando);
            salida.write( comando );   
            salida.flush();
            System.out.println("S : " + entrada.readLine());
            
            comando="PASS "+contrasena+"\r\n";
            System.out.print("C : "+comando);
            salida.write( comando );   
            salida.flush();
            String response = entrada.readLine();
            System.out.println("S : " + response);
            
            if (response.startsWith("+OK")) {
                isLoggedIn = true;
                System.out.println(" S : Inicio de sesión exitoso");
            } else {
                System.out.println(" S : Credenciales incorrectas, no se pudo iniciar sesión");
            }
        } catch (IOException e) {
            System.out.println(" S : Error al iniciar sesión: " + e.getMessage());
        }
    }

    public void logout(){
        if (!isLoggedIn) {
            System.out.println(" S : No hay sesión activa");
            return;
        }
        
        try {
            comando="QUIT\r\n";
            System.out.print("C : "+comando);
            salida.write( comando );   
            salida.flush();
            System.out.println("S : " + entrada.readLine());
            isLoggedIn = false;
        } catch (IOException e) {
            System.out.println(" S : Error al cerrar sesión: " + e.getMessage());
        }
    }

    public int getEmailCount(){
        if (!isLoggedIn) {
            System.out.println(" S : No hay sesión activa");
            return -1;
        }

        int emailCount = 0;
        try {
            comando="STAT\r\n";
            System.out.print("C : "+comando);
            salida.write( comando );   
            salida.flush();
            String response = entrada.readLine();
            System.out.println("S : " + response);
            
            if (response.startsWith("+OK")) {
                String[] parts = response.split(" ");
                if (parts.length >= 2) {
                    emailCount = Integer.parseInt(parts[1]);
                }
            } else {
                System.out.println(" S : Error al obtener el número de correos");
            }
        } catch (IOException e) {
            System.out.println(" S : Error al obtener el número de correos: " + e.getMessage());
        }
        return emailCount;
    }

    public String readEmail(int emailIndex){
        if (!isLoggedIn) {
            System.out.println(" S : No hay sesión activa");
            return "";
        }
        try {
            comando="RETR " + emailIndex + "\r\n";
            System.out.print("C : "+comando);
            salida.write( comando );    
            salida.flush();           
            
            return readMultilineResponse();
        } catch (IOException e) {
            System.out.println(" S : Error al leer el correo: " + e.getMessage());
            return "";
        }
    }
           
    public void deleteEmail(int emailIndex){
        if (!isLoggedIn) {
            System.out.println(" S : No hay sesión activa");
            return;
        }
        try {
            comando="DELE " + emailIndex + "\r\n";
            System.out.print("C : "+comando);
            salida.write( comando );               
            salida.flush();
            System.out.println("S : "+entrada.readLine());
        
        } catch (IOException e) {
            System.out.println(" S : Error al eliminar el correo: " + e.getMessage());
        }
    }

    private String readMultilineResponse() throws IOException {
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