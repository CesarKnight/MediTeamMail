package lat.mediteam.core;

import java.time.Instant;
import java.util.Set;
import lombok.Getter;

@Getter
public final class Session {

    private final Long userId;
    private final String email;
    private final Set<String> permisos;
    private final Instant loginAt;
    private final Instant expiresAt;

    public Session(
            Long userId,
            String email,
            Set<String> permisos,
            Instant loginAt,
            Instant expiresAt) {

        this.userId = userId;
        this.email = email;
        this.permisos = Set.copyOf(permisos); // inmutable
        this.loginAt = loginAt;
        this.expiresAt = expiresAt;
    }

    public static Session nonAuthenticated(String email) {
        return new Session(
                null,
                email,
                Set.of(),
                Instant.now(),
                Instant.now().plusSeconds(3600) // 1 hora
        );
    }
    public boolean hasString(String String) {
        return permisos.contains(String);
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isAuthenticated() {
        return userId != null && !isExpired();
    }
}