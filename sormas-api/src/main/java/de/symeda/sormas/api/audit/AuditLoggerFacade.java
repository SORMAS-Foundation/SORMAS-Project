package de.symeda.sormas.api.audit;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

@Remote
public interface AuditLoggerFacade {

	void logRestCall(String path, String method);

	void logFailedUiLogin(String caller, String method, String pathInfo);

	void logFailedRestLogin(String authorizationHeader, String method, String pathInfo);

	void logGetExternalMessagesSuccess(Date since, List<String> externalLabMessages, Date start, Date end, String authAlias);

	void logExternalMessagesHtmlSuccess(String uuid, int length, Date start, Date end, String authAlias);

	void logExternalMessagesPdfSuccess(String uuid, int length, Date start, Date end, String authAlias);

	void logGetExternalMessagesError(String outcome, String error, Date start, Date end, String authAlias);

	void logExternalMessagesHtmlError(String messageUuid, String outcome, String error, Date start, Date end, String authAlias);

	void logExternalMessagesPdfError(String messageUuid, String outcome, String error, Date start, Date end, String authAlias);

}
