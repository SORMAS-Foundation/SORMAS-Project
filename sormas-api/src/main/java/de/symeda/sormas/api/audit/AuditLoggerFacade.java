package de.symeda.sormas.api.audit;

import javax.ejb.Remote;
import java.util.Date;
import java.util.List;

@Remote
public interface AuditLoggerFacade {

	void logRestCall(String path, String method);

	void logFailedUiLogin(String caller, String method, String pathInfo);

	void logFailedRestLogin(String authorizationHeader, String method, String pathInfo);

	void logGetExternalLabMessagesSuccess(Date since, List<String> externalLabMessages, Date start, Date end, String authAlias);

	void logExternalLabMessagesHtmlSuccess(String uuid, int length, Date start, Date end);

	void logExternalLabMessagesPdfSuccess(String uuid, int length, Date start, Date end);

	void logGetExternalLabMessagesError(String outcome, String error, Date start, Date end);

	void logExternalLabMessagesHtmlError(String messageUuid, String outcome, String error, Date start, Date end);

	void logExternalLabMessagesPdfError(String messageUuid, String outcome, String error, Date start, Date end);

}
