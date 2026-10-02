package de.symeda.sormas.backend.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

import javax.ejb.SessionContext;
import javax.security.enterprise.CallerPrincipal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import de.symeda.sormas.api.user.OidcCallerPrincipal;

@ExtendWith(MockitoExtension.class)
public class CurrentUserServiceTokenTest {

	@Mock
	private SessionContext context;

	@InjectMocks
	private CurrentUserService service;

	@Test
	public void testTokenComesFromCurrentInvocation() {
		when(context.getCallerPrincipal())
			.thenReturn(new OidcCallerPrincipal("same-user", "first-session-token"), new OidcCallerPrincipal("same-user", "second-session-token"));

		assertEquals("first-session-token", service.getCurrentAccessToken().get());
		assertEquals("second-session-token", service.getCurrentAccessToken().get());
	}

	@Test
	public void testNonOidcCallerHasNoToken() {
		when(context.getCallerPrincipal()).thenReturn(new CallerPrincipal("test-user"));
		assertFalse(service.getCurrentAccessToken().isPresent());
	}

	@Test
	public void testMissingPrincipalHasNoToken() {
		assertFalse(service.getCurrentAccessToken().isPresent());
	}
}
