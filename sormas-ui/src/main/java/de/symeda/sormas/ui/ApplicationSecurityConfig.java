package de.symeda.sormas.ui;

import fish.payara.security.annotations.ClaimsDefinition;
import fish.payara.security.annotations.OpenIdAuthenticationDefinition;
import fish.payara.security.openid.api.OpenIdConstant;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@OpenIdAuthenticationDefinition(
        providerURI = "keycloak-url/auth",
        clientId = "sormas-ui",
        clientSecret = "secret",
        redirectURI = "{baseUrl}/callback",
        scope = { OpenIdConstant.OPENID_SCOPE },
        claimsDefinition = @ClaimsDefinition())
public class ApplicationSecurityConfig {

}
