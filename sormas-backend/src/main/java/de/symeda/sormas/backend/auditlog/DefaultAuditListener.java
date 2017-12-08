package de.symeda.sormas.backend.auditlog;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import de.symeda.auditlog.api.AuditListener;
import de.symeda.auditlog.api.Auditor;
import de.symeda.auditlog.api.ChangeEvent;
import de.symeda.auditlog.api.ChangeType;
import de.symeda.auditlog.api.Current;
import de.symeda.auditlog.api.EntityId;
import de.symeda.auditlog.api.TransactionId;
import de.symeda.auditlog.api.UserId;
import de.symeda.sormas.api.HasUuid;

/**
 * Entity life cycle listener that can detect changes on entities.
 * 
 * @author Oliver Milke
 * @since 13.01.2016
 */
public class DefaultAuditListener implements Serializable, AuditListener {

	private static final long serialVersionUID = 1L;

	@Inject
	@Current
	private Auditor auditor;

	@Inject
	@Current
	private TransactionId transactionId;

	@Inject
	@Current
	private UserId userId;

	@Inject
	Event<ChangeEvent> event;

	@Override
	@PrePersist
	@PreUpdate
	public void prePersist(HasUuid o) {

		ChangeEvent data = new ChangeEvent(this.auditor.detectChanges(o), EntityId.getOidFromHasUuid(o), LocalDateTime.now(), userId, transactionId);
		event.fire(data);
	}

	@Override
	@PostLoad
	public void postLoad(HasUuid o) {

		auditor.register(o);
	}

	@Override
	@PreRemove
	public void preRemove(HasUuid o) {

		ChangeEvent data = new ChangeEvent(EntityId.getOidFromHasUuid(o), Collections.emptySortedMap(), ChangeType.DELETE, LocalDateTime.now(), userId, transactionId);
		event.fire(data);
	}
}
