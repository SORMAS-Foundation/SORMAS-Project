package de.symeda.sormas.ui;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;


@Component
public class ConfigVaadinServiceInitListener implements VaadinServiceInitListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static String csp;
	static {
		String defaultSrc = "default-src 'self'";
		String styleSrc = "style-src 'unsafe-inline' 'self'";
		String fontSrc = "font-src 'self' data:";
		String scriptSrc = "script-src 'unsafe-inline' 'unsafe-eval' 'self' data:";

		csp = Arrays.asList(defaultSrc, styleSrc, fontSrc, scriptSrc)

				.stream().collect(Collectors.joining("; "));
	}

	@Override
	public void serviceInit(ServiceInitEvent event) {
		event.addBootstrapListener(bootstrap -> {
			bootstrap.getResponse().setHeader("Content-Security-Policy", csp);
		});
	}

	
}