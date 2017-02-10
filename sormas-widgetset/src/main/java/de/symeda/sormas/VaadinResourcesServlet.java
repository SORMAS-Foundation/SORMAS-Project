package de.symeda.sormas;

import javax.servlet.annotation.WebServlet;

import com.vaadin.server.VaadinServlet;

/**
 * Servlet needed to deliver the widgetset resources
 */
@SuppressWarnings("serial")
@WebServlet(urlPatterns="/VAADIN/*", name = "VaadinResourcesServlet")
public class VaadinResourcesServlet extends VaadinServlet {

	public VaadinResourcesServlet() {
		// TODO Auto-generated constructor stub
	}
}

