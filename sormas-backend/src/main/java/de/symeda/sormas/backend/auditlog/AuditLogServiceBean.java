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
