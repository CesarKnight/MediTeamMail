package lat.mediteam.core;

import javax.management.RuntimeErrorException;

import io.github.cdimascio.dotenv.Dotenv;

public final class Config {

	private static final Dotenv dotenv = Dotenv.configure()
			.ignoreIfMissing()
			.load();

	// Configuración de la base de datos
	public static final String DB_URL = "jdbc:" + dotenv.get("DB_DIALECT").toLowerCase() + "://" + dotenv.get("DB_HOST") + ":" + dotenv.get("DB_PORT") + "/" + dotenv.get("DB_NAME");
	public static final String DB_USER = dotenv.get("DB_USER");
	public static final String DB_PASSWORD = dotenv.get("DB_PASSWORD");
	public static final String DB_DRIVER = getDB_DRIVER();
	public static final String HIBERNATE_DIALECT = getHIBERNATE_DIALECT();

	// configuracion de MAIL
	public static final String MAIL_SERVER = dotenv.get("MAIL_SERVER");
	public static final String MAIL_TO_LISTEN = dotenv.get("MAIL_TO_LISTEN");
	public static final String PASSWORD = dotenv.get("PASSWORD");

	// Configuración de la aplicación
	public static final String COMANDO_EJECUCION = dotenv.get("COMANDO_EJECUCION");
	public static final int MAIL_SYNC_INTERVAL_MS = Integer.parseInt(dotenv.get("MAIL_SYNC_INTERVAL_MS", "60000"));
	public static final int MAX_THREADS = Integer.parseInt(dotenv.get("MAX_THREADS", "10"));

	public static String getDB_DRIVER() {
		String db_dialect = dotenv.get("DB_DIALECT");
		switch (db_dialect) {
			case "POSTGRESQL":
				return "org.postgresql.Driver";
			case "MYSQL":
				return "com.mysql.cj.jdbc.Driver";
			default:
				throw new RuntimeErrorException(new Error("Unsupported DB_DIALECT: " + db_dialect));
		}
	}

	public static String getHIBERNATE_DIALECT() {
		String db = dotenv.get("DB_DIALECT");

		switch (db) {
			case "POSTGRESQL":
				return "org.hibernate.dialect.PostgreSQLDialect";
			case "MYSQL":
				return "org.hibernate.dialect.MySQLDialect";
			default:
				throw new RuntimeErrorException(new Error("Unsupported DB_DIALECT: " + db));
		}
	}
}
