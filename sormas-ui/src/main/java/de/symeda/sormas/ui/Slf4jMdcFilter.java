package de.symeda.sormas.ui;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.MDC;

@WebFilter(asyncSupported = true, urlPatterns = "/*")
public class Slf4jMdcFilter implements Filter {

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		setMDC((HttpServletRequest) request);
		try {
			chain.doFilter(request, response);
		} finally {
			MDC.clear();
		}
	}

	@Override
	public void init(FilterConfig cfg) throws ServletException {

	}

	private void setMDC(HttpServletRequest request) {
		MDC.put("USER", request.getRemoteUser());
		MDC.put("HOST", request.getRemoteHost());
		MDC.put("BROWSER", request.getHeader("User-Agent"));
		MDC.put("URI", request.getRequestURI());
		MDC.put("CONTEXT", request.getContextPath());
		MDC.put("SERVLETPATH", request.getServletPath());
	}
}
