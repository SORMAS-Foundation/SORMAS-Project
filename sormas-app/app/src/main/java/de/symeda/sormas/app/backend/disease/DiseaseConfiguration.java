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

package de.symeda.sormas.app.backend.disease;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;

@Entity(name = DiseaseConfiguration.TABLE_NAME)
@DatabaseTable(tableName = DiseaseConfiguration.TABLE_NAME)
public class DiseaseConfiguration extends AbstractDomainObject {

	private static final long serialVersionUID = -7653585175036656526L;

	public static final String TABLE_NAME = "diseaseConfiguration";
	public static final String I18N_PREFIX = "DiseaseConfiguration";

	public static final String DISEASE = "disease";

	@Enumerated(EnumType.STRING)
	private Disease disease;

	@DatabaseField
	private Boolean active;

	@DatabaseField
	private Boolean primaryDisease;

	@DatabaseField
	private Boolean caseBased;

	@DatabaseField
	private Boolean followUpEnabled;

	@Column
	private Integer followUpDuration;

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Boolean getPrimaryDisease() {
		return primaryDisease;
	}

	public void setPrimaryDisease(Boolean primaryDisease) {
		this.primaryDisease = primaryDisease;
	}

	public Boolean getCaseBased() {
		return caseBased;
	}

	public void setCaseBased(Boolean caseBased) {
		this.caseBased = caseBased;
	}

	public Boolean getFollowUpEnabled() {
		return followUpEnabled;
	}

	public void setFollowUpEnabled(Boolean followUpEnabled) {
		this.followUpEnabled = followUpEnabled;
	}

	public Integer getFollowUpDuration() {
		return followUpDuration;
	}

	public void setFollowUpDuration(Integer followUpDuration) {
		this.followUpDuration = followUpDuration;
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}
}
