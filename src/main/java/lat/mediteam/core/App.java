package lat.mediteam.core;

public class App
{
	public static DatabaseManager databaseManager;
    
    public static void main( String[] args )
    {
        databaseManager = new DatabaseManager();
     
        // MailListener.runServer();
        MailListener.testRun();
    }

}