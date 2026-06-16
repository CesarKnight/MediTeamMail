package lat.mediteam.core;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AuthManager {
    private final ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();

    public void login(Long userId,String email,Set<String> permisos) {
        Session session = new Session(
            userId,
            email,
            permisos,
            Instant.now(),
            Instant.now().plus(Config.SESSION_LIFE_MINUTES, ChronoUnit.MINUTES)
        );

        sessions.put(email, session);
    }

    public Session findByEmail(String email) {
        Session s = sessions.get(email);

        if (s == null) {
            return null;
        }

        if (s.getExpiresAt().isBefore(Instant.now())) {
            sessions.remove(email);
            return null;
        }

        return s;
    }

    public void logout(String email) {
        sessions.remove(email);
    }
}