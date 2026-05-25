package lat.mediteam.core;

public class AppContext {
    private final AuthManager authManager;
    
    public AppContext(
        AuthManager authManager
    ) {
        this.authManager = authManager;
    }

    public AuthManager getAuthManager() {
        return authManager;
    }
}
