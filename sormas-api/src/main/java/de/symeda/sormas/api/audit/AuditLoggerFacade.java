package de.symeda.sormas.api.audit;

import javax.ejb.Remote;

@Remote
public interface AuditLoggerFacade {

	void logRestCall(String path, String method);
	void logFailedUiLogin(String caller, String method, String pathInfo);
	void logFailedRestLogin(String authorizationHeader, String method, String pathInfo);
}
