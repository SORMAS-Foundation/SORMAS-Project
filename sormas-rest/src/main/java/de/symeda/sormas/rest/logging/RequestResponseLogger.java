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
package de.symeda.sormas.rest.logging;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet Filter implementation class RequestLogger
 */
@WebFilter("/*")
public class RequestResponseLogger implements Filter {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * Default constructor.
	 */
	public RequestResponseLogger() {

	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {

	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
// 		There is an issue, when both criteria are enabled
//		if (logger.isDebugEnabled()) {

		// request logging
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		logger.debug("requestUri = {}", httpRequest.getRequestURI());

		Map<String, String[]> params = httpRequest.getParameterMap();
		for (String s : params.keySet()) {
			// logger.debug(" " + s + " = " + req.getParameter(s));
			logger.debug("  {} = {}", s, httpRequest.getParameter(s));
		}
//		}

//	 	There is an issue, when both criteria are enabled
//		if (logger.isTraceEnabled()) {
		// response logging
		if (response.getCharacterEncoding() == null) {
			response.setCharacterEncoding("UTF-8");
		}

		HttpServletResponseCopier responseCopier = new HttpServletResponseCopier((HttpServletResponse) response);

		try {
			// pass the request along the filter chain
			chain.doFilter(request, responseCopier);
			responseCopier.flushBuffer();
		} finally {
			byte[] copy = responseCopier.getCopy();
			logger.trace(new String(copy, response.getCharacterEncoding()));
		}
//		}

	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {

	}
}
