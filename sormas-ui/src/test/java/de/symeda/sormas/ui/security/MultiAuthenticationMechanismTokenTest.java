package de.symeda.sormas.ui.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.security.Principal;
import java.util.Collections;
import java.util.Set;

import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.glassfish.soteria.mechanisms.CustomFormAuthenticationMechanism;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import de.symeda.sormas.api.AuthProvider;
import de.symeda.sormas.api.ConfigFacade;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.user.OidcCallerPrincipal;
import fish.payara.security.openid.OpenIdAuthenticationMechanism;
import fish.payara.security.openid.api.AccessToken;
import fish.payara.security.openid.domain.OpenIdContextImpl;

public class MultiAuthenticationMechanismTokenTest {

	@Test
	public void testRestoredSessionUsesCurrentTokenWithoutRegisteringSession() throws Exception {
		OpenIdAuthenticationMechanism delegate = mock(OpenIdAuthenticationMechanism.class);
		OpenIdContextImpl context = mock(OpenIdContextImpl.class);
		MultiAuthenticationMechanism mechanism = createMechanism(delegate, context);
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		HttpMessageContext messageContext = mock(HttpMessageContext.class);
		AccessToken accessToken = mock(AccessToken.class);
		Set<String> groups = Collections.singleton("CASE_VIEW");
		when(delegate.validateRequest(request, response, messageContext)).thenReturn(AuthenticationStatus.SUCCESS);
		when(context.getCallerName()).thenReturn("test-user");
		when(context.getCallerGroups()).thenReturn(groups);
		when(context.getAccessToken()).thenReturn(accessToken);
		when(accessToken.getToken()).thenReturn("initial-token", "refreshed-token");
		when(messageContext.notifyContainerAboutLogin(any(Principal.class), eq(groups))).thenReturn(AuthenticationStatus.SUCCESS);

		assertEquals(AuthenticationStatus.SUCCESS, mechanism.validateRequest(request, response, messageContext));
		assertEquals(AuthenticationStatus.SUCCESS, mechanism.validateRequest(request, response, messageContext));

		ArgumentCaptor<Principal> principals = ArgumentCaptor.forClass(Principal.class);
		verify(messageContext, times(2)).notifyContainerAboutLogin(principals.capture(), eq(groups));
		assertEquals("initial-token", ((OidcCallerPrincipal) principals.getAllValues().get(0)).getAccessToken());
		assertEquals("refreshed-token", ((OidcCallerPrincipal) principals.getAllValues().get(1)).getAccessToken());
		assertEquals("test-user", principals.getValue().getName());
		verify(messageContext, never()).setRegisterSession(any(), any());
	}

	@Test
	public void testRedirectDoesNotPublishToken() throws Exception {
		OpenIdAuthenticationMechanism delegate = mock(OpenIdAuthenticationMechanism.class);
		MultiAuthenticationMechanism mechanism = createMechanism(delegate, mock(OpenIdContextImpl.class));
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		HttpMessageContext messageContext = mock(HttpMessageContext.class);
		when(delegate.validateRequest(request, response, messageContext)).thenReturn(AuthenticationStatus.SEND_CONTINUE);

		assertEquals(AuthenticationStatus.SEND_CONTINUE, mechanism.validateRequest(request, response, messageContext));
		verify(messageContext, never()).notifyContainerAboutLogin(any(Principal.class), any());
	}

	private MultiAuthenticationMechanism createMechanism(OpenIdAuthenticationMechanism delegate, OpenIdContextImpl context) throws Exception {
		ConfigFacade config = mock(ConfigFacade.class);
		when(config.getAuthenticationProvider()).thenReturn(AuthProvider.KEYCLOAK);
		try (MockedStatic<FacadeProvider> facades = mockStatic(FacadeProvider.class)) {
			facades.when(FacadeProvider::getConfigFacade).thenReturn(config);
			MultiAuthenticationMechanism mechanism = new MultiAuthenticationMechanism(delegate, mock(CustomFormAuthenticationMechanism.class));
			Field field = MultiAuthenticationMechanism.class.getDeclaredField("openIdContext");
			field.setAccessible(true);
			field.set(mechanism, context);
			return mechanism;
		}
	}
}
