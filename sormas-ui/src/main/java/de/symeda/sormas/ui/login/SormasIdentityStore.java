package de.symeda.sormas.ui.login;

import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.user.UserRole;

/**
 * See https://www.baeldung.com/java-ee-8-security
 * IdentityStore: https://developer.ibm.com/tutorials/j-javaee8-security-api-3/
 */
@ApplicationScoped
public class SormasIdentityStore implements IdentityStore {

	public CredentialValidationResult validate(UsernamePasswordCredential usernamePasswordCredential) {

		Set<UserRole> userRoles = FacadeProvider.getUserFacade()
			.getValidLoginRoles(usernamePasswordCredential.getCaller(), usernamePasswordCredential.getPasswordAsString());

		if (userRoles != null && !userRoles.isEmpty()) {
			return new CredentialValidationResult(
				usernamePasswordCredential.getCaller(),
				userRoles.stream().map(userRole -> userRole.name()).collect(Collectors.toSet()));
		}

		return CredentialValidationResult.INVALID_RESULT;
	}
}
