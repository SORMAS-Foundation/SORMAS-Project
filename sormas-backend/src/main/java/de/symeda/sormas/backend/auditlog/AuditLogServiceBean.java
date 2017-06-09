package de.symeda.sormas.backend.auditlog;

import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import de.symeda.auditlog.api.ChangeEvent;
import de.symeda.sormas.backend.util.ModelConstants;

/**
 * Turns {@link ChangeEvent}s to {@link AuditLogEntry} and saves it.
 * 
 * @author Oliver Milke
 */
@Stateless
public class AuditLogServiceBean {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME_AUDITLOG)
	private EntityManager entityManager;

	@TransactionAttribute(TransactionAttributeType.MANDATORY)
	public void receiveChanges(@Observes(during = TransactionPhase.IN_PROGRESS) ChangeEvent event) {

		Date changeDate = AuditLogDateHelper.from(event.getChangeDate());

		AuditLogEntry log = new AuditLogEntry();
		log.setAttributes(event.getNewValues());
		log.setDetectionTimestamp(changeDate);
		log.setChangeType(event.getChangeType());
		log.setEditingUser(event.getUserId());
		log.setTransactionId(event.getTransactionId());
		log.setUuid(event.getOid().getEntityUuid());
		log.setClazz(event.getOid().getEntityClass().getName());

		this.entityManager.persist(log);
	}
}
