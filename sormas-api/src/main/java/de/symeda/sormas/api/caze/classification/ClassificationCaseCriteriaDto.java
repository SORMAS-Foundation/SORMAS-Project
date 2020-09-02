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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.utils.YesNoUnknown;

@JsonSubTypes({
	@JsonSubTypes.Type(value = ClassificationEpiDataCriteriaDto.class, name = "ClassificationEpiDataCriteriaDto"),
	@JsonSubTypes.Type(value = ClassificationNotInStartDateRangeCriteriaDto.class, name = "ClassificationNotInStartDateRangeCriteriaDto"),
	@JsonSubTypes.Type(value = ClassificationPathogenTestCriteriaDto.class, name = "ClassificationPathogenTestCriteriaDto"),
	@JsonSubTypes.Type(value = ClassificationSymptomsCriteriaDto.class, name = "ClassificationSymptomsCriteriaDto"), })
public class ClassificationCaseCriteriaDto extends ClassificationCriteriaDto {

	private static final long serialVersionUID = 2640725590302569043L;

	protected String propertyId;
	protected List<Object> propertyValues;

	public ClassificationCaseCriteriaDto() {

	}

	public ClassificationCaseCriteriaDto(String propertyId, Object... propertyValues) {
		this.propertyId = propertyId;
		this.propertyValues = Arrays.asList(propertyValues);
	}

	protected Class<? extends EntityDto> getInvokeClass() {
		return CaseDataDto.class;
	}

	protected Object getInvokeObject(CaseDataDto caze) {
		return caze;
	}

	@Override
	public boolean eval(CaseDataDto caze, PersonDto person, List<PathogenTestDto> sampleTests) {

		try {
			Method method = getInvokeClass().getMethod("get" + propertyId.substring(0, 1).toUpperCase() + propertyId.substring(1));
			Object value = method.invoke(getInvokeObject(caze));
			return propertyValues.contains(value);
		} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	protected StringBuilder appendDescValues(StringBuilder stringBuilder) {

		if (propertyValues.size() == 1 && propertyValues.get(0) instanceof YesNoUnknown) {
			return stringBuilder;
		}

		stringBuilder.append(" ");
		for (int i = 0; i < propertyValues.size(); i++) {
			if (i > 0) {
				stringBuilder.append(", ");
			}

			stringBuilder.append(propertyValues.get(i).toString());
		}

		return stringBuilder;
	}

	@Override
	public String buildDescription() {

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, propertyId));
		appendDescValues(stringBuilder);
		return stringBuilder.toString();
	}

	public String getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(String propertyId) {
		this.propertyId = propertyId;
	}

	public List<Object> getPropertyValues() {
		return propertyValues;
	}

	public void setPropertyValues(List<Object> propertyValues) {
		this.propertyValues = propertyValues;
	}
}
