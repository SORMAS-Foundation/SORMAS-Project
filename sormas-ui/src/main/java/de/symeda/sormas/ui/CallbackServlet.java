package de.symeda.sormas.ui;

import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * IDP Public callback url used for finalizing the authentication.
 *
 * @see ApplicationSecurityConfig
 * @author Alex Vidrean
 */
@WebServlet(urlPatterns = { "/callback" })
@ServletSecurity(@HttpConstraint())
public class CallbackServlet extends HttpServlet {

	/**
	 * Handles the HTTP <code>GET</code> method.
	 *
	 * @param request  servlet request
	 * @param response servlet response
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.sendRedirect("");
	}
}
