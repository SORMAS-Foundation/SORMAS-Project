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
package de.symeda.sormas.ui;

import java.io.Closeable;
import java.io.IOException;
import java.util.Optional;

import javax.ejb.EJB;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserFacade;
import de.symeda.sormas.ui.utils.BaseControllerProvider;

@WebFilter(asyncSupported = true, urlPatterns = "/*")
public class SessionFilter implements Filter {

	@EJB
	private SessionFilterBean sessionFilterBean;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		HttpSession session = ((HttpServletRequest) request).getSession();

		final HttpServletResponse res = (HttpServletResponse) response;
		res.addHeader("X-Content-Type-Options", "nosniff");
		res.addHeader("X-Frame-Options", "SAMEORIGIN");
		res.addHeader("Referrer-Policy", "same-origin");

		ControllerProvider controllerProvider =
			Optional.of(session).map(s -> (ControllerProvider) s.getAttribute("controllerProvider")).orElseGet(() -> {
				ControllerProvider cp = new ControllerProvider();
				session.setAttribute("controllerProvider", cp);
				return cp;
			});

		sessionFilterBean.doFilter((req, resp) -> {
			Language userLanguage =
				Optional.of(FacadeProvider.getUserFacade()).map(UserFacade::getCurrentUser).map(UserDto::getLanguage).orElse(null);
			I18nProperties.setUserLanguage(userLanguage);
			try {
				try (Closeable bc = BaseControllerProvider.requestStart(controllerProvider)) {
					chain.doFilter(req, response);
				}
			} finally {
				I18nProperties.removeUserLanguage();
			}

		}, request, response);
	}
}
