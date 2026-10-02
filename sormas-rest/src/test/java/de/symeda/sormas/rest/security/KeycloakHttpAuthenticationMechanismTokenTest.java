package de.symeda.sormas.rest.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.spi.KeycloakAccount;
import org.keycloak.representations.AccessToken;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import de.symeda.sormas.api.user.OidcCallerPrincipal;

public class KeycloakHttpAuthenticationMechanismTokenTest {

	@Test
	public void testValidatedTokenAndGroupsReachContainer() {
		Set<String> groups = Collections.singleton("SORMAS_REST");
		CredentialValidationResult result = authenticate(new CredentialValidationResult("test-user", groups));
		OidcCallerPrincipal principal = assertInstanceOf(OidcCallerPrincipal.class, result.getCallerPrincipal());
		assertEquals("test-user", principal.getName());
		assertEquals("validated-access-token", principal.getAccessToken());
		assertEquals(groups, result.getCallerGroups());
	}

	@Test
	public void testRejectedUserDoesNotReceiveTokenPrincipal() {
		assertEquals(CredentialValidationResult.INVALID_RESULT, authenticate(CredentialValidationResult.INVALID_RESULT));
	}

	@SuppressWarnings("unchecked")
	private CredentialValidationResult authenticate(CredentialValidationResult validationResult) {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		HttpMessageContext messageContext = mock(HttpMessageContext.class);
		KeycloakSecurityContext keycloak = mock(KeycloakSecurityContext.class);
		AccessToken token = new AccessToken();
		token.setPreferredUsername("test-user");
		when(request.getHeader("Authorization")).thenReturn("Bearer validated-access-token");
		when(request.getAttribute(KeycloakSecurityContext.class.getName())).thenReturn(keycloak);
		when(keycloak.getToken()).thenReturn(token);

		if (validationResult.getStatus() == CredentialValidationResult.Status.VALID) {
			when(keycloak.getTokenString()).thenReturn("validated-access-token");
			KeycloakAccount account = mock(KeycloakAccount.class);
			when(account.getRoles()).thenReturn(new HashSet<>());
			when(request.getAttribute(KeycloakAccount.class.getName())).thenReturn(account);
		}

		IdentityStoreHandler handler = mock(IdentityStoreHandler.class);
		when(handler.validate(any(Credential.class))).thenReturn(validationResult);
		CDI<Object> cdi = mock(CDI.class);
		Instance<IdentityStoreHandler> instance = mock(Instance.class);
		when(cdi.select(eq(IdentityStoreHandler.class), any(Annotation[].class))).thenReturn(instance);
		when(instance.get()).thenReturn(handler);
		when(messageContext.notifyContainerAboutLogin(any(CredentialValidationResult.class))).thenReturn(AuthenticationStatus.SUCCESS);

		try (MockedStatic<CDI> currentCdi = mockStatic(CDI.class)) {
			currentCdi.when(CDI::current).thenReturn(cdi);
			new KeycloakHttpAuthenticationMechanism().validateRequest(request, response, messageContext);
		}

		if (validationResult.getStatus() != CredentialValidationResult.Status.VALID) {
			verify(keycloak, never()).getTokenString();
			verify(messageContext, never()).setRegisterSession(any(), any());
		}
		ArgumentCaptor<CredentialValidationResult> result = ArgumentCaptor.forClass(CredentialValidationResult.class);
		verify(messageContext).notifyContainerAboutLogin(result.capture());
		return result.getValue();
	}
}
