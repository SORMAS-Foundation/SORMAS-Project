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

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;

/**
 * Classification criteria are used to automatically classify cases and to be able to display
 * the underlying rules in the UI. A specific criteria can be evaluated using the eval method
 * which returns whether this criteria is applicable or not for the given case. A set of
 * criteria can be evaluated in order to determine whether the case should be classified as
 * suspect, probable, confirmed or not classified at all.
 */
@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY)
@JsonSubTypes({
	@JsonSubTypes.Type(value = ClassificationAllOfCriteriaDto.class, name = "ClassificationAllOfCriteriaDto"),
	@JsonSubTypes.Type(value = ClassificationCaseCriteriaDto.class, name = "ClassificationCaseCriteriaDto"),
	@JsonSubTypes.Type(value = ClassificationNoneOfCriteriaDto.class, name = "ClassificationNoneOfCrieriaDto"),
	@JsonSubTypes.Type(value = ClassificationPersonAgeBetweenYearsCriteriaDto.class, name = "ClassificationPersonAgeBetweenYearsCriteriaDto"),
	@JsonSubTypes.Type(value = ClassificationPathogenTestPositiveResultCriteriaDto.class,
		name = "ClassificationPathogenTestPositiveResultCriteriaDto"),
	@JsonSubTypes.Type(value = ClassificationPathogenTestNegativeResultCriteriaDto.class,
		name = "ClassificationPathogenTestNegativeResultCriteriaDto"),
	@JsonSubTypes.Type(value = ClassificationPathogenTestOtherPositiveResultCriteriaDto.class,
		name = "ClassificationPathogenTestOtherPositiveResultCriteriaDto"),
	@JsonSubTypes.Type(value = ClassificationXOfCriteriaDto.class, name = "ClassificationXOfCriteriaDto"), })
public abstract class ClassificationCriteriaDto implements Serializable {

	protected String type = getClass().getSimpleName();

	private static final long serialVersionUID = -3686569295881034008L;

	public abstract boolean eval(CaseDataDto caze, PersonDto person, List<PathogenTestDto> sampleTests);

	public abstract String buildDescription();

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
