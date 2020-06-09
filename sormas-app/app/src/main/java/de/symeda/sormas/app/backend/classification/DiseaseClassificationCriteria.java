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

package de.symeda.sormas.app.backend.classification;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.apache.commons.lang3.StringUtils;

import com.j256.ormlite.table.DatabaseTable;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;

@Entity(name = DiseaseClassificationCriteria.TABLE_NAME)
@DatabaseTable(tableName = DiseaseClassificationCriteria.TABLE_NAME)
public class DiseaseClassificationCriteria extends AbstractDomainObject {

	public static final String TABLE_NAME = "diseaseClassificationCriteria";
	public static final String I18N_PREFIX = "DiseaseClassificationCriteria";

	public static final String DISEASE = "disease";

	@Enumerated(EnumType.STRING)
	private Disease disease;

	/**
	 * Contains HTML that can be used to build a visual representation of the
	 * suspect criteria used to classify a case with this disease.
	 */
	@Column
	private String suspectCriteria;

	/**
	 * Contains HTML that can be used to build a visual representation of the
	 * probable criteria used to classify a case with this disease.
	 */
	@Column
	private String probableCriteria;

	/**
	 * Contains HTML that can be used to build a visual representation of the
	 * confirmed criteria used to classify a case with this disease.
	 */
	@Column
	private String confirmedCriteria;

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public String getSuspectCriteria() {
		return suspectCriteria;
	}

	public void setSuspectCriteria(String suspectCriteria) {
		this.suspectCriteria = suspectCriteria;
	}

	public String getProbableCriteria() {
		return probableCriteria;
	}

	public void setProbableCriteria(String probableCriteria) {
		this.probableCriteria = probableCriteria;
	}

	public String getConfirmedCriteria() {
		return confirmedCriteria;
	}

	public void setConfirmedCriteria(String confirmedCriteria) {
		this.confirmedCriteria = confirmedCriteria;
	}

	@Override
	public String toString() {
		return DatabaseHelper.getContext().getString(R.string.heading_classification_for) + " " + disease.toString();
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}

	public boolean hasAnyCriteria() {
		return !StringUtils.isEmpty(suspectCriteria) || !StringUtils.isEmpty(probableCriteria) || !StringUtils.isEmpty(confirmedCriteria);
	}
}
