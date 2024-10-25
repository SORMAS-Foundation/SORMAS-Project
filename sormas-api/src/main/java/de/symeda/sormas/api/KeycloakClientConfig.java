package de.symeda.sormas.api;

import java.io.Serializable;

public class KeycloakClientConfig implements Serializable {
    private final String keycloakUrl;
    private final String realm;
    private final String clientId;

    public KeycloakClientConfig(String keycloakUrl, String realm, String clientId) {
        this.keycloakUrl = keycloakUrl;
        this.realm = realm;
        this.clientId = clientId;
    }

    public String getKeycloakUrl() {
        return keycloakUrl;
    }

    public String getRealm() {
        return realm;
    }

    public String getClientId() {
        return clientId;
    }
}
