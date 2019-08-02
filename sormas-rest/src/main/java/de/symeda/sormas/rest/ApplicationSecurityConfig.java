package de.symeda.sormas.rest;

import javax.enterprise.context.ApplicationScoped;
import javax.security.enterprise.authentication.mechanism.http.BasicAuthenticationMechanismDefinition;

@BasicAuthenticationMechanismDefinition(realmName = "sormas-rest-realm")
@ApplicationScoped
public class ApplicationSecurityConfig {

}
