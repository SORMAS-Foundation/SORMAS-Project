package de.symeda.sormas.backend.auditlog;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.transaction.TransactionScoped;

import de.symeda.auditlog.api.Auditor;
import de.symeda.auditlog.api.Current;
import de.symeda.auditlog.api.TransactionId;
import de.symeda.auditlog.api.UserId;

/**
 * Liefert für Auditlog relevante Umgebung.
 * 
 * @author Oliver Milke
 * @since 13.01.2016
 */
@Stateless
public class AuditContextProducer {

	@Resource
	private SessionContext context;

	/**
	 * Liefert anhand des EJB-Kontextes die Information, welchem Benutzer eine Änderung zugeordnet werden soll.
	 */
	@Produces
	@Current
	@Dependent
	public UserId getCurrentlyLoggedInUser() {

		final String name = context.getCallerPrincipal() == null ? "SYSTEM" : context.getCallerPrincipal().getName();
		return new UserId(name);
	}

	@Produces
	@Current
	@TransactionScoped
	public Auditor provideAuditor() {

		return new Auditor();
	}

	@Produces
	@Current
	@TransactionScoped
	public TransactionId provideTransactionId() {

		return new TransactionId();
	}
}
