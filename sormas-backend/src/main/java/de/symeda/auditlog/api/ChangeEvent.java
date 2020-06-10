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
package de.symeda.auditlog.api;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Event object that describes the changes of an entity.
 * 
 * @author Oliver Milke
 * @since 13.01.2016
 */
public class ChangeEvent {

	private final EntityId oid;
	private final Map<String, String> newValues;
	private final ChangeType changeType;
	private final LocalDateTime changeDate;
	private final String userId;
	private final String transactionId;

	public ChangeEvent(Map<String, String> newValues, ChangeType changeType) {

		this.oid = null;
		this.newValues = newValues;
		this.changeType = changeType;
		this.changeDate = null;
		this.userId = null;
		this.transactionId = null;
	}

	public ChangeEvent(ChangeEvent changeEvent, EntityId oid, LocalDateTime changeDate, UserId userId, TransactionId transactionId) {

		this.oid = oid;
		this.newValues = changeEvent.getNewValues();
		this.changeType = changeEvent.getChangeType();
		this.changeDate = changeDate;
		this.userId = userId.getName();
		this.transactionId = transactionId.getTransactionId();
	}

	public ChangeEvent(
		EntityId oid,
		Map<String, String> newValues,
		ChangeType changeType,
		LocalDateTime changeDate,
		UserId userId,
		TransactionId transactionId) {

		this.oid = oid;
		this.newValues = newValues;
		this.changeType = changeType;
		this.changeDate = changeDate;
		this.userId = userId.getName();
		this.transactionId = transactionId.getTransactionId();
	}

	/**
	 * Returns the {@link TransactionId} to describe the current transaction (user + transaction).
	 * 
	 * @return
	 */
	public String getTransactionId() {
		return transactionId;
	}

	/**
	 * Returns the {@link EntityId} to describe the changes entity.
	 * 
	 * @return
	 */
	public EntityId getOid() {
		return oid;
	}

	public ChangeType getChangeType() {
		return changeType;
	}

	public LocalDateTime getChangeDate() {
		return changeDate;
	}

	public String getUserId() {
		return userId;
	}

	/**
	 * Returns the list of all changed attributes with the <code>key</code> of the map being the name of the changed attribute
	 * and the <code>value</code> being its new value.
	 * 
	 * @return
	 */
	public Map<String, String> getNewValues() {
		return newValues;
	}
}
