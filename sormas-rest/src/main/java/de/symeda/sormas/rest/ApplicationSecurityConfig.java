package de.symeda.sormas.rest;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

import javax.enterprise.context.ApplicationScoped;

@OpenAPIDefinition(security = {
	@SecurityRequirement(name = "http-basic")
})
@SecurityScheme(
	name = "http-basic",
	type = SecuritySchemeType.HTTP,
	scheme = "Basic"
)
@ApplicationScoped
public class ApplicationSecurityConfig {

}
