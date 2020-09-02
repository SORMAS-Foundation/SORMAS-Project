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

import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;

/**
 * Classification criteria that specifies that none of the sub criteria may be true in order for the whole
 * criteria to be applicable. This is used e.g. to make sure that certain sample test types have returned
 * a negative result in order to rule out specific diseases.
 */
public class ClassificationNoneOfCriteriaDto extends ClassificationCriteriaDto implements ClassificationCollectiveCriteria {

	private static final long serialVersionUID = 2199852259112272090L;

	protected List<ClassificationCriteriaDto> classificationCriteria;

	public ClassificationNoneOfCriteriaDto() {

	}

	public ClassificationNoneOfCriteriaDto(ClassificationCriteriaDto... criteria) {
		this.classificationCriteria = Arrays.asList(criteria);
	}

	@Override
	public boolean eval(CaseDataDto caze, PersonDto person, List<PathogenTestDto> sampleTests) {
		for (ClassificationCriteriaDto classificationCriteria : classificationCriteria) {
			if (classificationCriteria.eval(caze, person, sampleTests)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public String buildDescription() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<b> ").append(I18nProperties.getString(Strings.classificationNoneOf).toUpperCase()).append("</b>");
		for (int i = 0; i < classificationCriteria.size(); i++) {
			stringBuilder.append("<br/>- ");
			stringBuilder.append(classificationCriteria.get(i).buildDescription());
		}
		return stringBuilder.toString();
	}

	@Override
	public String getCriteriaName() {
		return "<b>" + I18nProperties.getString(Strings.classificationNoneOf).toUpperCase() + "</b>";
	}

	@Override
	public List<ClassificationCriteriaDto> getSubCriteria() {
		return classificationCriteria;
	}

	public List<ClassificationCriteriaDto> getClassificationCriteria() {
		return classificationCriteria;
	}

	public void setClassificationCriteria(List<ClassificationCriteriaDto> classificationCriteria) {
		this.classificationCriteria = classificationCriteria;
	}
}
