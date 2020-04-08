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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.statistics.StatisticsAttribute;
import de.symeda.sormas.api.statistics.StatisticsAttributeEnum;
import de.symeda.sormas.api.statistics.StatisticsGroupingKey;
import de.symeda.sormas.api.statistics.StatisticsSubAttribute;
import de.symeda.sormas.api.statistics.StatisticsSubAttributeEnum;
import de.symeda.sormas.api.statistics.contact.StatisticsContactAttribute;
import de.symeda.sormas.api.contact.FollowUpStatus;

public class StatisticsHelper extends de.symeda.sormas.api.statistics.StatisticsHelper {

	public static final String VALUE_UNKNOWN = "VALUE_UNKNOWN";
	public static final String TOTAL = "total";
	public static final String UNKNOWN = "unknown";
	
	private static final Map<StatisticsContactAttribute, StatisticsAttributeEnum> attributesMap = new HashMap<StatisticsContactAttribute, StatisticsAttributeEnum> () {{
		put(StatisticsContactAttribute.REPORT_TIME, StatisticsAttributeEnum.TIME);
		put(StatisticsContactAttribute.REGION_DISTRICT, StatisticsAttributeEnum.REGION_DISTRICT);
		put(StatisticsContactAttribute.SEX, StatisticsAttributeEnum.SEX);
		put(StatisticsContactAttribute.AGE_INTERVAL_1_YEAR, StatisticsAttributeEnum.AGE_INTERVAL_1_YEAR);
		put(StatisticsContactAttribute.AGE_INTERVAL_5_YEARS, StatisticsAttributeEnum.AGE_INTERVAL_5_YEARS);
		put(StatisticsContactAttribute.AGE_INTERVAL_CHILDREN_COARSE, StatisticsAttributeEnum.AGE_INTERVAL_CHILDREN_COARSE);
		put(StatisticsContactAttribute.AGE_INTERVAL_CHILDREN_FINE, StatisticsAttributeEnum.AGE_INTERVAL_CHILDREN_FINE);
		put(StatisticsContactAttribute.AGE_INTERVAL_CHILDREN_MEDIUM, StatisticsAttributeEnum.AGE_INTERVAL_CHILDREN_MEDIUM);
		put(StatisticsContactAttribute.AGE_INTERVAL_BASIC, StatisticsAttributeEnum.AGE_INTERVAL_BASIC);
		put(StatisticsContactAttribute.DISEASE, StatisticsAttributeEnum.DISEASE);
		put(StatisticsContactAttribute.CLASSIFICATION, null);
		put(StatisticsContactAttribute.FOLLOW_UP_STATUS, null);
		put(StatisticsContactAttribute.REPORTING_USER_ROLE, StatisticsAttributeEnum.USER_ROLE);
	}};
	
	public static StatisticsGroupingKey buildGroupingKey(Object attributeValue, StatisticsContactAttribute attribute, StatisticsSubAttributeEnum subAttribute, Function<Integer, RegionReferenceDto> regionProvider, Function<Integer, DistrictReferenceDto> districtProvider) {
		if (isNullOrUnknown(attributeValue)) {
			return null;
		}
		
		switch (attribute) {
			case CLASSIFICATION:
				return ContactClassification.valueOf(attributeValue.toString());
			case FOLLOW_UP_STATUS:
				return FollowUpStatus.valueOf(attributeValue.toString());
			default:
				return buildGroupingKey(attributeValue, getEnum(attribute), subAttribute, regionProvider, districtProvider);
		}
	}

	public static List<StatisticsGroupingKey> getTimeGroupingKeys(StatisticsContactAttribute attribute, StatisticsSubAttributeEnum subAttribute) {
		
		Date oldestContactDate = null;
		switch (attribute) {
			case REPORT_TIME:
				oldestContactDate = FacadeProvider.getContactFacade().getOldestContactReportDate();
				break;
		}

		return getTimeGroupingKeys(subAttribute, oldestContactDate);
	}	
	
	@SuppressWarnings("unchecked")
	public static List<StatisticsGroupingKey> getAttributeGroupingKeys(StatisticsContactAttribute attribute, StatisticsSubAttributeEnum subAttribute) {
		
		if (subAttribute != null) {
			switch (attribute) {
				case REPORT_TIME:
					return getTimeGroupingKeys(attribute, subAttribute);
				default:
					return getAttributeGroupingKeys(getEnum(attribute), subAttribute);
			}
		}
		else {
			switch (attribute) {
				case CLASSIFICATION:
					ArrayList<StatisticsGroupingKey> classificationList = new ArrayList<>();
					for (ContactClassification classification : ContactClassification.values()) {
						classificationList.add(classification);
					}
					return classificationList;
				case FOLLOW_UP_STATUS:
					ArrayList<StatisticsGroupingKey> statusList = new ArrayList<>();
					for (FollowUpStatus status : FollowUpStatus.values()) {
						statusList.add(status);
					}
					return statusList;
				default:
					return getAttributeGroupingKeys(getEnum(attribute), subAttribute);
			}
		}
	}

	
	public static StatisticsContactAttribute getEnum (StatisticsAttribute attribute) {
		return (StatisticsContactAttribute) StatisticsAttribute.getEnum(attribute);
	}

	public static StatisticsSubAttributeEnum getEnum (StatisticsSubAttribute attribute) {
		return (StatisticsSubAttributeEnum) StatisticsSubAttribute.getEnum(attribute);
	}

	public static StatisticsAttributeEnum getEnum (StatisticsContactAttribute attribute) {
		return attributesMap.get(attribute);
	}
	
	
	public static class getAttributeValues implements StatisticsAttribute.IValuesGetter {
	
		public Collection<? extends StatisticsGroupingKey> get (StatisticsAttribute attribute) {
			return getAttributeValues(attribute);
		}
	}

	public static class getSubAttributeValues implements StatisticsSubAttribute.IValuesGetter {
		
		public Collection<? extends StatisticsGroupingKey> get (StatisticsSubAttribute subAttribute, StatisticsAttribute attribute) {		
			return getTimeGroupingKeys(getEnum(attribute), getEnum(subAttribute));
		}
	}
}
