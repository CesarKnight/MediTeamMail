package lat.mediteam.core;

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class DatabaseManager {
    private static final EntityManagerFactory emf = buildEntityManagerFactory();

    private static EntityManagerFactory buildEntityManagerFactory() {
        Map<String, Object> props = new HashMap<>();
        /*
         * JDBC configuracion
         */
	    props.put("jakarta.persistence.jdbc.driver",Config.DB_DRIVER);
	    props.put("jakarta.persistence.jdbc.url",Config.DB_URL);
	    props.put("jakarta.persistence.jdbc.user",Config.DB_USER);
	    props.put("jakarta.persistence.jdbc.password",Config.DB_PASSWORD);
        /*
         * Hibernate
         */
	    props.put("hibernate.dialect",Config.HIBERNATE_DIALECT);
        props.put("hibernate.hbm2ddl.auto","update");
        props.put("hibernate.show_sql",true);
        props.put("hibernate.format_sql",true);

        return Persistence.createEntityManagerFactory("mediteam",props);
    }

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public static void shutdown() {
        emf.close();
    }
}
