package de.symeda.sormas.rest;

import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.user.UserRole;

@ApplicationScoped
public class SormasIdentityStore implements IdentityStore {

	// TODO build salted credential cache for the credentials of the last 2 minutes

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
