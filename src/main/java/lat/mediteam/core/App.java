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
            "| MAIL_SYNC_INTERVAL_MS: " + Config.MAIL_SYNC_INTERVAL_MS+ "\n" +
            "| MAX_THREADS: " + Config.MAX_THREADS+ "\n"+
            "| SESSION_LIFE_MINUTES: " + Config.SESSION_LIFE_MINUTES+ "\n" +
            "| MAX_SEND_EMAIL_RETRIES: " + Config.MAX_SEND_EMAIL_RETRIES+ "\n"+
            "| POP_TIMETOUT_MS: " + Config.POP_TIMETOUT_MS+ "\n" +
            "| SMTP_TIMETOUT_MS: " + Config.SMTP_TIMEOUT_MS+ "\n" ;
        
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