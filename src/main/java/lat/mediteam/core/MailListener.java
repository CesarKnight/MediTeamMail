package lat.mediteam.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lat.mediteam.mail.PopCliente;



public class MailListener {
    
	public static void runServer(AppContext appContext) {
		final long tasaRefresco = Config.MAIL_SYNC_INTERVAL_MS;
		final String mailServer = Config.MAIL_SERVER;
		final String email = Config.MAIL_TO_LISTEN;
		final String password = Config.PASSWORD;
		
		
		PopCliente popCliente = new PopCliente(mailServer, email, password);
		ExecutorService threadPool = Executors.newFixedThreadPool(Config.MAX_THREADS);

		System.out.println("Escuchando correos en " + email + "@" + mailServer.substring(5));

		while (true) {
			Boolean loggedIn = popCliente.connect(Config.POP_TIMETOUT_MS);
			loggedIn = popCliente.login();
			
			if (loggedIn){
				int emailCount = popCliente.getEmailCount();
				System.out.println("Cantidad de correos a responder: " + emailCount);
				
				for (int i = 1; i <= emailCount; i++) {
					try {
						String rawEmail = popCliente.readEmail(i);
						threadPool.submit(new RespuestaWorker(i, mailServer, rawEmail, appContext));
						popCliente.deleteEmail(i);
					} catch (Exception e) {
						System.out.println(e);
					}
				}
				popCliente.logout();
				popCliente.disconnect();

			}else{
				System.out.println("Inicio de sesion en POP fallido");
			}
			try {
				System.out.println("Durmiendo... ");
				Thread.sleep(tasaRefresco);
			} catch (InterruptedException e) {
				System.out.println("Error en el Listener: " + e.getMessage());
			}
		}
	}

	public static void testRun(AppContext appContext) {
		try {
			ExecutorService threadPool = Executors.newFixedThreadPool(Config.MAX_THREADS);
			
			String command = "usuario crear udemicscesar@gmail.com 123 admin";
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
            // String command = "historiaclinica listar";
			// String command = "ayuda";
			String correo = "choco@gmail.com";
			Boolean enviarCorreo = false;
			String testRaw = 
			"+OK 505 octets\n" +
			"Return-Path: <" +correo+ ">\n" +
			"X-Original-To: prueba@prueba.prueba\n" +
			"Delivered-To: prueba@prueba.prueba\n" +
			"Received: from mail.mediteam.lat (unknown [192.223.106.20])\n" +
			"	by mediteam.lat (Postfix) with SMTP id 300473F983\n" +
			"	for <prueba@prueba.prueba>; Mon, 15 Jun 2026 00:20:15 +0000 (UTC)\n" +
			"To: undisclosed-recipients:;\n" +
			"From: <" +correo+ ">\n" +
			"Message-Id: <20260614202055.30487@tigo.net.bo>\n" +
			"Date: Sun, 14 Jun 2026 20:20:55 -0400 (BOT)\n" +
			"SUBJECT:" + command + "\n" +
			"\n" +
			"asdfas\n" +
			"asdfas\n" +
			"lorem ipsum dolor";

			threadPool.submit(new RespuestaWorker(
				2, 
				enviarCorreo? Config.MAIL_SERVER : "pruebaQueNoEnvia", 
				testRaw, appContext)
			);
			Thread.sleep(1000);
			
			threadPool.shutdown();	

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
