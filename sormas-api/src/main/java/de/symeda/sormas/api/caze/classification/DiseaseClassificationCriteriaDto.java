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
package de.symeda.sormas.api.caze.classification;

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;

public class DiseaseClassificationCriteriaDto extends EntityDto {

	private static final long serialVersionUID = 8800921617332187938L;

	private Disease disease;
	private ClassificationCriteriaDto suspectCriteria;
	private ClassificationCriteriaDto probableCriteria;
	private ClassificationCriteriaDto confirmedCriteria;
	private ClassificationCriteriaDto notACaseCriteria;

	public DiseaseClassificationCriteriaDto() {

	}

	public DiseaseClassificationCriteriaDto(
		Disease disease,
		Date changeDate,
		ClassificationCriteriaDto suspectCriteria,
		ClassificationCriteriaDto probableCriteria,
		ClassificationCriteriaDto confirmedCriteria,
		ClassificationCriteriaDto notACaseCriteria) {

		super(changeDate, changeDate, null);
		this.disease = disease;
		this.suspectCriteria = suspectCriteria;
		this.probableCriteria = probableCriteria;
		this.confirmedCriteria = confirmedCriteria;
		this.notACaseCriteria = notACaseCriteria;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public ClassificationCriteriaDto getSuspectCriteria() {
		return suspectCriteria;
	}

	public void setSuspectCriteria(ClassificationCriteriaDto suspectCriteria) {
		this.suspectCriteria = suspectCriteria;
	}

	public ClassificationCriteriaDto getProbableCriteria() {
		return probableCriteria;
	}

	public void setProbableCriteria(ClassificationCriteriaDto probableCriteria) {
		this.probableCriteria = probableCriteria;
	}

	public ClassificationCriteriaDto getConfirmedCriteria() {
		return confirmedCriteria;
	}

	public void setConfirmedCriteria(ClassificationCriteriaDto confirmedCriteria) {
		this.confirmedCriteria = confirmedCriteria;
	}

	public ClassificationCriteriaDto getNotACaseCriteria() {
		return notACaseCriteria;
	}

	public void setNotACaseCriteria(ClassificationCriteriaDto notACaseCriteria) {
		this.notACaseCriteria = notACaseCriteria;
	}

	public boolean hasAnyCriteria() {
		return suspectCriteria != null || probableCriteria != null || confirmedCriteria != null || notACaseCriteria != null;
	}
}
