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
import java.util.Date;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.auditlog.api.ChangeType;

/**
 * For saving changes on entities.
 * 
 * @author Oliver Milke
 */
@Entity
public class AuditLogEntry implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String SEQ_JPA_NAME = "Auditlog_seq";
	private static final String SEQ_SQL_NAME = "auditlog_seq";

	@Id
	@SequenceGenerator(name = SEQ_JPA_NAME, allocationSize = 1, sequenceName = SEQ_SQL_NAME)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQ_JPA_NAME)
	private Long id;

	private String clazz;
	private String uuid;
	private String editingUser;

	@Column(name = "transaction_id", nullable = false)
	private String transactionId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "detection_ts", nullable = false)
	private Date detectionTimestamp;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ChangeType changeType;

	@ElementCollection
	@CollectionTable(name = "auditlogentry_attributes", joinColumns = @JoinColumn(name = "auditlogentry_id", nullable = false))
	@MapKeyColumn(name = "attribute_key", nullable = false)
	@Column(name = "attribute_value", columnDefinition = "text")
	private Map<String, String> attributes;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	/**
	 * @return Uuid of the audited entity.
	 */
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getEditingUser() {
		return editingUser;
	}

	public void setEditingUser(String editingUser) {
		this.editingUser = editingUser;
	}

	public ChangeType getChangeType() {
		return changeType;
	}

	public void setChangeType(ChangeType changeType) {
		this.changeType = changeType;
	}

	/**
	 * @return The timestamp when the change has been detected/logged.
	 */
	public Date getDetectionTimestamp() {
		return detectionTimestamp;
	}

	public void setDetectionTimestamp(Date detectionTimestamp) {
		this.detectionTimestamp = detectionTimestamp;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
}
