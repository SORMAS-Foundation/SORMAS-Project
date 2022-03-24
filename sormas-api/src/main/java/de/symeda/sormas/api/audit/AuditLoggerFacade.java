package de.symeda.sormas.api.audit;

import javax.ejb.Remote;

@Remote
public interface AuditLoggerFacade {

	void logRestCall();
}
