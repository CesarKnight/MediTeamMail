package lat.mediteam.core;


public class App
{   
    public static void main( String[] args )
    {
        String info =
            "||== Iniciando Aplicacion con credenciales: ==||\n" +
            "| DB_URL: " + Config.DB_URL+ "\n" +
            "| DB_USER: " + Config.DB_USER+ "\n" +
            "| DB_PASSWORD: " + Config.DB_PASSWORD+ "\n" +
            "| DB_DRIVER: " + Config.DB_DRIVER+ "\n" +
            "| MAIL_SERVER: " + Config.MAIL_SERVER+ "\n" +
            "| MAIL_TO_LISTEN: " + Config.MAIL_TO_LISTEN+ "\n" +
            "| PASSWORD: " + Config.PASSWORD+ "\n" +
            "| COMANDO_EJECUCION: " + Config.COMANDO_EJECUCION+ "\n" +
            "| MAIL_SYNC_INTERVAL_MS: " + Config.MAIL_SYNC_INTERVAL_MS+ "\n" +
            "| MAX_THREADS: " + Config.MAX_THREADS+ "\n";
        
        
        System.out.println(info);
        DatabaseManager.getEntityManager().close();

        AppContext appContext = new AppContext(
            new AuthManager()
        );
        
        MailListener.runServer(appContext);
        // MailListener.testRun(appContext);   
        
        System.out.println("bye bye...");
    }

}