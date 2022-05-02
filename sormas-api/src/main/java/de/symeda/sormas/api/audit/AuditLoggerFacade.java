package de.symeda.sormas.api.audit;

import javax.ejb.Remote;
import java.util.Date;
import java.util.List;

@Remote
public interface AuditLoggerFacade {

	void logRestCall(String path, String method);

	void logFailedUiLogin(String caller, String method, String pathInfo);

	void logFailedRestLogin(String authorizationHeader, String method, String pathInfo);

	void logGetExternalLabMessagesSuccess(Date since, List<String> externalLabMessages);

	void logExternalLabMessagesHtmlSuccess(String uuid, int length);

	void logExternalLabMessagesPdfSuccess(String uuid, int length);

	void logGetExternalLabMessagesError(String outcome, String error);

	void logExternalLabMessagesHtmlError(String outcome, String error);

	void logExternalLabMessagesPdfError(String outcome, String error);

}
