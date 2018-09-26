package de.symeda.sormas.ui;

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
import javax.servlet.http.HttpSession;

import de.symeda.sormas.ui.utils.BaseControllerProvider;

@WebFilter(asyncSupported = true, urlPatterns = "/*")
public class SessionFilter implements Filter {

	//protected static final Logger LOGGER = LoggerFactory.getLogger(SessionFilter.class);
	
	@EJB
	private SessionFilterBean sessionFilterBean;

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		HttpSession session = ((HttpServletRequest) request).getSession();

		ControllerProvider controllerProvider = (ControllerProvider) session.getAttribute("controllerProvider");
		if (controllerProvider == null) {
			controllerProvider = new ControllerProvider();
			session.setAttribute("controllerProvider", controllerProvider);
		}
		BaseControllerProvider.requestStart(controllerProvider);

		try {
			sessionFilterBean.doFilter(chain, request, response);
		} finally {
			ControllerProvider.requestEnd();
		}
	}

	@Override
	public void init(FilterConfig cfg) throws ServletException {
	}

}
