package de.symeda.sormas.backend.util;

import javax.ejb.SessionContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.backend.audit.AuditLoggerEjb;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.user.CurrentUserService;

public class BackendFacadeProvider {

	private static final Logger logger = LoggerFactory.getLogger(BackendFacadeProvider.class);

	private final InitialContext ic;

	private static volatile BackendFacadeProvider instance;

	protected BackendFacadeProvider() {
		if (instance != null) {
			throw new IllegalStateException("BackendFacadeProvider instance already created");
		}

		try {
			ic = new InitialContext();
		} catch (NamingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static BackendFacadeProvider getInstance() {
		if (instance == null) {
			synchronized (BackendFacadeProvider.class) {
				if (instance == null) {
					instance = new BackendFacadeProvider();
				}
			}
		}
		return instance;
	}

	public static AuditLoggerEjb.AuditLoggerEjbLocal getAuditLogger() {
		try {
			// Use java:module for LOCAL lookup within same EJB module
			return (AuditLoggerEjb.AuditLoggerEjbLocal) getInstance().ic.lookup("java:module/AuditLoggerEjb");
		} catch (NamingException e) {
			throw new RuntimeException("Failed to lookup AuditLoggerEjb: " + e.getMessage(), e);
		}
	}

	public static ConfigFacadeEjb.ConfigFacadeEjbLocal getConfigFacade() {
		try {
			// Use java:module for LOCAL lookup within same EJB module
			return (ConfigFacadeEjb.ConfigFacadeEjbLocal) getInstance().ic.lookup("java:module/ConfigFacadeEjbLocal");
		} catch (NamingException e) {
			throw new RuntimeException("Failed to lookup AuditLoggerEjb: " + e.getMessage(), e);
		}
	}

	public static CurrentUserService getCurrentUserService() {
		try {
			// Use java:module for LOCAL lookup within same EJB module
			return (CurrentUserService) getInstance().ic.lookup("java:module/CurrentUserService");
		} catch (NamingException e) {
			throw new RuntimeException("Failed to lookup AuditLoggerEjb: " + e.getMessage(), e);
		}
	}

	public static SessionContext getSessionContextSafely() {
		try {
			// Use java:module for LOCAL lookup within same EJB module
			return (SessionContext) getInstance().ic.lookup("java:comp/EJBContext");
		} catch (NamingException e) {
			// This is expected in CDI interceptor context or during startup
			logger.trace("SessionContext lookup failed (expected in CDI/startup): {}", e.getMessage());
			return null;
		} catch (Exception e) {
			logger.debug("Unexpected error looking up SessionContext", e);
			return null;
		}
	}
}
