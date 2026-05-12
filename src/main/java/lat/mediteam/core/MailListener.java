package lat.mediteam.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lat.mediteam.mail.PopCliente;


public class MailListener {
    
	public static void runServer() {
		final int syncTime = Config.MAIL_SYNC_INTERVAL_MS;
		final String mailServer = Config.MAIL_SERVER;
		final String email = Config.MAIL_TO_LISTEN;
		final String password = Config.PASSWORD;
		
		
		PopCliente popCliente = new PopCliente(mailServer, email, password);
		ExecutorService threadPool = Executors.newFixedThreadPool(Config.MAX_THREADS);

		System.out.println("Escuchando correos en " + email + "@" + mailServer.substring(5));

		while (true) {
			try {
				popCliente.connect();
				popCliente.login();

				int emailCount = popCliente.getEmailCount();
				for (int i = 1; i <= emailCount; i++) {
					threadPool.submit(new RespuestaWorker(i, mailServer, email));
					popCliente.deleteEmail(i);
				}
				popCliente.logout();
				popCliente.disconnect();

				System.out.println("durmiendo... ");
				Thread.sleep(syncTime);

			} catch (InterruptedException e) {
				System.out.println("Error en el hilo de sincronización: " + e.getMessage());
			}
		}
	}

	public static void testRun(){
		try {
			ExecutorService threadPool = Executors.newFixedThreadPool(Config.MAX_THREADS);
			for (int i = 1; i <= 1; i++) {
				threadPool.submit(new RespuestaWorker(i, "mockServer", "whatedever"));
				Thread.sleep(1000);
			}
			threadPool.shutdown();	

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
