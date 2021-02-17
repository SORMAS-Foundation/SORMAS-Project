/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.login;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Event;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.SecurityContext;
import javax.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.servlet.ServletException;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinServletService;
import com.vaadin.server.VaadinSession;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.utils.UserRightsException;

public final class LoginHelper {

	private LoginHelper() {
		// Hide Utility Class Constructor
	}

	public static boolean login(String username, String password) throws UserRightsException {

		if (username == null || username.isEmpty()) {
			return false;
		}

		BeanManager bm = CDI.current().getBeanManager();
		@SuppressWarnings("unchecked")
		Bean<SecurityContext> securityContextBean = (Bean<SecurityContext>) bm.getBeans(SecurityContext.class).iterator().next();
		CreationalContext<SecurityContext> ctx = bm.createCreationalContext(securityContextBean);
		SecurityContext securityContext = (SecurityContext) bm.getReference(securityContextBean, SecurityContext.class, ctx);

		AuthenticationParameters authentication = new AuthenticationParameters();
		authentication.credential(new UsernamePasswordCredential(username, password));
		authentication.newAuthentication(true);
		authentication.setRememberMe(true);
		AuthenticationStatus status = securityContext.authenticate(
			VaadinServletService.getCurrentServletRequest(),
			VaadinServletService.getCurrentResponse().getHttpServletResponse(),
			authentication);

		if (status == AuthenticationStatus.SUCCESS) {
			Language userLanguage = FacadeProvider.getUserFacade().getByUserName(username).getLanguage();
			I18nProperties.setUserLanguage(userLanguage);
			FacadeProvider.getI18nFacade().setUserLanguage(userLanguage);
			return true;
		}

		return false;
	}

	/**
	 * Trigger logout event for Authentication Mechanism or other components to react.
	 */
	@SuppressWarnings("unchecked")
	public static boolean logout() {

		BeanManager bm = CDI.current().getBeanManager();
		Bean<Event> eventBean = (Bean<Event>) bm.getBeans(Event.class).iterator().next();
		CreationalContext<Event> ctx = bm.createCreationalContext(eventBean);
		Event<LogoutEvent> event = (Event<LogoutEvent>) bm.getReference(eventBean, Event.class, ctx);
		event.fire(new LogoutEvent());

		try {
			VaadinServletService.getCurrentServletRequest().logout();
		} catch (ServletException e) {
			return false;
		}

		VaadinSession.getCurrent().getSession().invalidate();
		Page.getCurrent().reload();

		return true;
	}

	public static class LogoutEvent {

	}

}
