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

    public boolean hasString(String String) {
        return permisos.contains(String);
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}