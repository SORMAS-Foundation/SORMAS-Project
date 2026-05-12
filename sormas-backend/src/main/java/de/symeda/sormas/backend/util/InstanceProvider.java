package de.symeda.sormas.backend.util;

import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Equivalent of {@link de.symeda.sormas.api.FacadeProvider} for backend code.
 */
public class InstanceProvider {

	private final InitialContext ic;

	private static volatile InstanceProvider instance;

	protected InstanceProvider() {
		if (instance != null) {
			throw new IllegalStateException("BackendFacadeProvider instance already created");
		}

		try {
			ic = new InitialContext();
		} catch (NamingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static InstanceProvider getInstance() {
		if (instance == null) {
			synchronized (InstanceProvider.class) {
				if (instance == null) {
					instance = new InstanceProvider();
				}
			}
		}
		return instance;
	}

	public static <T> T getInstanceFor(Class<T> clazz) {
		String classSimpleName = clazz.getSimpleName();

		try {
			// Use java:module for LOCAL lookup within same EJB module
			return (T) getInstance().ic.lookup("java:module/" + classSimpleName);
		} catch (NamingException e) {
			throw new RuntimeException("Failed to lookup class: " + clazz, e);
		}
	}
}
