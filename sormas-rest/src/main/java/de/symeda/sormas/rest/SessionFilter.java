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
package de.symeda.sormas.rest;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.RequestContextHolder;
import de.symeda.sormas.api.RequestContextTO;

@WebFilter(asyncSupported = true, urlPatterns = "/*")
public class SessionFilter implements Filter {

	@EJB
	private SessionFilterBean sessionFilterBean;

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		try {
			sessionFilterBean.doFilter((req, resp) -> {
				final String isMobileSyncHeader = ((HttpServletRequest) request).getHeader("mobile-sync");
				final RequestContextTO requestContext = new RequestContextTO(isMobileSyncHeader != null ? Boolean.valueOf(isMobileSyncHeader) : false);
				RequestContextHolder.setRequestContext(requestContext);
				FacadeProvider.getConfigFacade().setRequestContext(requestContext);
				chain.doFilter(req, response);
			}, request, response);
		} finally {
			RequestContextHolder.reset();
			FacadeProvider.getConfigFacade().resetRequestContext();
		}
	}

	@Override
	public void init(FilterConfig cfg) throws ServletException {

	}
}
