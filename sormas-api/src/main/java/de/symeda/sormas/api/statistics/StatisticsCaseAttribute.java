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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.statistics;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum StatisticsCaseAttribute {

	ONSET_TIME(StatisticsCaseAttributeGroup.TIME, false, StatisticsCaseSubAttribute.YEAR,StatisticsCaseSubAttribute.QUARTER,StatisticsCaseSubAttribute.MONTH,StatisticsCaseSubAttribute.EPI_WEEK,
			StatisticsCaseSubAttribute.QUARTER_OF_YEAR,StatisticsCaseSubAttribute.MONTH_OF_YEAR,StatisticsCaseSubAttribute.EPI_WEEK_OF_YEAR,StatisticsCaseSubAttribute.DATE_RANGE),
	REPORT_TIME(StatisticsCaseAttributeGroup.TIME, false, StatisticsCaseSubAttribute.YEAR,StatisticsCaseSubAttribute.QUARTER,StatisticsCaseSubAttribute.MONTH,StatisticsCaseSubAttribute.EPI_WEEK,
			StatisticsCaseSubAttribute.QUARTER_OF_YEAR,StatisticsCaseSubAttribute.MONTH_OF_YEAR,StatisticsCaseSubAttribute.EPI_WEEK_OF_YEAR,StatisticsCaseSubAttribute.DATE_RANGE),
	REGION_DISTRICT(StatisticsCaseAttributeGroup.PLACE, true, StatisticsCaseSubAttribute.REGION,StatisticsCaseSubAttribute.DISTRICT),
	SEX(StatisticsCaseAttributeGroup.PERSON, true),
	AGE_INTERVAL_1_YEAR(StatisticsCaseAttributeGroup.PERSON, false),
	AGE_INTERVAL_5_YEARS(StatisticsCaseAttributeGroup.PERSON, false),
	AGE_INTERVAL_CHILDREN_COARSE(StatisticsCaseAttributeGroup.PERSON, false),
	AGE_INTERVAL_CHILDREN_FINE(StatisticsCaseAttributeGroup.PERSON, false),
	AGE_INTERVAL_CHILDREN_MEDIUM(StatisticsCaseAttributeGroup.PERSON, false),
	AGE_INTERVAL_BASIC(StatisticsCaseAttributeGroup.PERSON, false),
	DISEASE(StatisticsCaseAttributeGroup.CASE, true),
	CLASSIFICATION(StatisticsCaseAttributeGroup.CASE, true),
	OUTCOME(StatisticsCaseAttributeGroup.CASE, true);
	
	private final StatisticsCaseAttributeGroup attributeGroup;
	private final boolean sortByCaption;
	private final StatisticsCaseSubAttribute[] subAttributes;

	StatisticsCaseAttribute(StatisticsCaseAttributeGroup attributeGroup, boolean sortByCaption, StatisticsCaseSubAttribute ...subAttributes) {
		this.attributeGroup = attributeGroup;
		this.sortByCaption = sortByCaption;
		this.subAttributes = subAttributes;
	}
	
	public StatisticsCaseAttributeGroup getAttributeGroup() {
		return attributeGroup;
	}
	
	public boolean isSortByCaption() {
		return sortByCaption;
	}

	public StatisticsCaseSubAttribute[] getSubAttributes() {
		return subAttributes;
	}
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
	
}
