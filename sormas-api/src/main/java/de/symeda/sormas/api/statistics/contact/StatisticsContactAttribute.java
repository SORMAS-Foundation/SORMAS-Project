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
package de.symeda.sormas.api.statistics.contact;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.statistics.StatisticsSubAttributeEnum;

public enum StatisticsContactAttribute {

	REPORT_TIME(StatisticsContactAttributeGroup.TIME, false, false, StatisticsSubAttributeEnum.YEAR,StatisticsSubAttributeEnum.QUARTER,StatisticsSubAttributeEnum.MONTH,StatisticsSubAttributeEnum.EPI_WEEK,
			StatisticsSubAttributeEnum.QUARTER_OF_YEAR,StatisticsSubAttributeEnum.MONTH_OF_YEAR,StatisticsSubAttributeEnum.EPI_WEEK_OF_YEAR,StatisticsSubAttributeEnum.DATE_RANGE),
	REGION_DISTRICT(StatisticsContactAttributeGroup.PLACE, true, false, StatisticsSubAttributeEnum.REGION,StatisticsSubAttributeEnum.DISTRICT),
	SEX(StatisticsContactAttributeGroup.PERSON, true, true),
	AGE_INTERVAL_1_YEAR(StatisticsContactAttributeGroup.PERSON, false, true),
	AGE_INTERVAL_5_YEARS(StatisticsContactAttributeGroup.PERSON, false, true),
	AGE_INTERVAL_CHILDREN_COARSE(StatisticsContactAttributeGroup.PERSON, false, true),
	AGE_INTERVAL_CHILDREN_FINE(StatisticsContactAttributeGroup.PERSON, false, true),
	AGE_INTERVAL_CHILDREN_MEDIUM(StatisticsContactAttributeGroup.PERSON, false, true),
	AGE_INTERVAL_BASIC(StatisticsContactAttributeGroup.PERSON, false, true),
	
	DISEASE(StatisticsContactAttributeGroup.CONTACT, true, false),
	CLASSIFICATION(StatisticsContactAttributeGroup.CONTACT, true, false),
	FOLLOW_UP_STATUS(StatisticsContactAttributeGroup.CONTACT, true, false), 
	REPORTING_USER_ROLE(StatisticsContactAttributeGroup.CONTACT, true, false);
	
	private final StatisticsContactAttributeGroup attributeGroup;
	private final boolean sortByCaption;
	private final boolean unknownValueAllowed;
	private final StatisticsSubAttributeEnum[] subAttributes;

	StatisticsContactAttribute(StatisticsContactAttributeGroup attributeGroup, boolean sortByCaption, boolean unknownValueAllowed, StatisticsSubAttributeEnum ...subAttributes) {
		this.attributeGroup = attributeGroup;
		this.sortByCaption = sortByCaption;
		this.unknownValueAllowed = unknownValueAllowed;
		this.subAttributes = subAttributes;
	}
	
	public StatisticsContactAttributeGroup getAttributeGroup() {
		return attributeGroup;
	}
	
	public boolean isSortByCaption() {
		return sortByCaption;
	}

	public boolean isUnknownValueAllowed() {
		return unknownValueAllowed;
	}
	
	public StatisticsSubAttributeEnum[] getSubAttributes() {
		return subAttributes;
	}
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
	
	public boolean isAgeGroup() {
		return this == AGE_INTERVAL_1_YEAR || this == AGE_INTERVAL_5_YEARS || this == AGE_INTERVAL_BASIC || this == AGE_INTERVAL_CHILDREN_COARSE
				|| this == AGE_INTERVAL_CHILDREN_FINE || this == AGE_INTERVAL_CHILDREN_MEDIUM;
	}
	
	public boolean isPopulationData() {
		return this == REGION_DISTRICT || this == SEX || this.isAgeGroup();
	}
	
}
