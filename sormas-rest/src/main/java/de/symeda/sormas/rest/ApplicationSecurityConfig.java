package de.symeda.sormas.rest;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

import javax.enterprise.context.ApplicationScoped;
import javax.security.enterprise.authentication.mechanism.http.BasicAuthenticationMechanismDefinition;

@OpenAPIDefinition(security = {
	@SecurityRequirement(name = "http-basic")
})
@SecurityScheme(
	name = "http-basic",
	type = SecuritySchemeType.HTTP,
	scheme = "Basic"
)
@BasicAuthenticationMechanismDefinition(realmName = "sormas-rest-realm")
@ApplicationScoped
public class ApplicationSecurityConfig {

}
