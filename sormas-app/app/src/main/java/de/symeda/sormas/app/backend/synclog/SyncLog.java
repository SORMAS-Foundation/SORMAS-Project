/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.backend.synclog;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Mate Strysewske on 23.05.2017.
 */
@Entity(name = SyncLog.TABLE_NAME)
@DatabaseTable(tableName = SyncLog.TABLE_NAME)
public class SyncLog {

	public static final String TABLE_NAME = "synclog";

	public static final String ENTITY_NAME = "entityName";
	public static final String CONFLICT_TEXT = "conflictText";

	@Id
	@GeneratedValue
	private Long id;

	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = false)
	private Date creationDate;

	@Column
	private String entityName;

	@Column
	private String conflictText;

	public SyncLog() {
	}

	public SyncLog(String entityName, String conflictText) {
		this.entityName = entityName;
		this.conflictText = conflictText;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public String getConflictText() {
		return conflictText;
	}

	public void setConflictText(String conflictText) {
		this.conflictText = conflictText;
	}
}
