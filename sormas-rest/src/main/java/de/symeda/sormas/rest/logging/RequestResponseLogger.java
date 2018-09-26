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
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
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
