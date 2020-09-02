/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
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

		ChangeEvent data = new ChangeEvent(
			EntityId.getOidFromHasUuid(o),
			Collections.emptySortedMap(),
			ChangeType.DELETE,
			LocalDateTime.now(),
			userId,
			transactionId);
		event.fire(data);
	}
}
