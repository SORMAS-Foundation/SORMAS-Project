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
package de.symeda.sormas.api.statistics.caze;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.statistics.StatisticsAttribute;
import de.symeda.sormas.api.statistics.StatisticsAttributeEnum;
import de.symeda.sormas.api.statistics.StatisticsGroupingKey;
import de.symeda.sormas.api.statistics.StatisticsSubAttribute;
import de.symeda.sormas.api.statistics.StatisticsSubAttributeEnum;

public class StatisticsHelper extends de.symeda.sormas.api.statistics.StatisticsHelper {

	public static final String VALUE_UNKNOWN = "VALUE_UNKNOWN";
	public static final String TOTAL = "total";
	public static final String UNKNOWN = "unknown";
	
	private static final Map<StatisticsCaseAttribute, StatisticsAttributeEnum> attributesMap = new HashMap<StatisticsCaseAttribute, StatisticsAttributeEnum> () {{
		put(StatisticsCaseAttribute.ONSET_TIME, StatisticsAttributeEnum.TIME);
		put(StatisticsCaseAttribute.REPORT_TIME, StatisticsAttributeEnum.TIME);
		put(StatisticsCaseAttribute.REGION_DISTRICT, StatisticsAttributeEnum.REGION_DISTRICT);
		put(StatisticsCaseAttribute.SEX, StatisticsAttributeEnum.SEX);
		put(StatisticsCaseAttribute.AGE_INTERVAL_1_YEAR, StatisticsAttributeEnum.AGE_INTERVAL_1_YEAR);
		put(StatisticsCaseAttribute.AGE_INTERVAL_5_YEARS, StatisticsAttributeEnum.AGE_INTERVAL_5_YEARS);
		put(StatisticsCaseAttribute.AGE_INTERVAL_CHILDREN_COARSE, StatisticsAttributeEnum.AGE_INTERVAL_CHILDREN_COARSE);
		put(StatisticsCaseAttribute.AGE_INTERVAL_CHILDREN_FINE, StatisticsAttributeEnum.AGE_INTERVAL_CHILDREN_FINE);
		put(StatisticsCaseAttribute.AGE_INTERVAL_CHILDREN_MEDIUM, StatisticsAttributeEnum.AGE_INTERVAL_CHILDREN_MEDIUM);
		put(StatisticsCaseAttribute.AGE_INTERVAL_BASIC, StatisticsAttributeEnum.AGE_INTERVAL_BASIC);
		put(StatisticsCaseAttribute.DISEASE, StatisticsAttributeEnum.DISEASE);
		put(StatisticsCaseAttribute.CLASSIFICATION, null);
		put(StatisticsCaseAttribute.OUTCOME, null);
		put(StatisticsCaseAttribute.REPORTING_USER_ROLE, StatisticsAttributeEnum.USER_ROLE);
	}};

	public static StatisticsGroupingKey buildGroupingKey(Object attributeValue, StatisticsCaseAttribute attribute, StatisticsSubAttributeEnum subAttribute, Function<Integer, RegionReferenceDto> regionProvider, Function<Integer, DistrictReferenceDto> districtProvider) {
		if (isNullOrUnknown(attributeValue)) {
			return null;
		}
		
		switch (attribute) {
			case CLASSIFICATION:
				return CaseClassification.valueOf(attributeValue.toString());
			case OUTCOME:
				return CaseOutcome.valueOf(attributeValue.toString());
			default:
				return buildGroupingKey(attributeValue, getEnum(attribute), subAttribute, regionProvider, districtProvider);
		}
	}

	public static List<StatisticsGroupingKey> getTimeGroupingKeys(StatisticsCaseAttribute attribute, StatisticsSubAttributeEnum subAttribute) {
		
		Date oldestCaseDate = null;
		switch (attribute) {
			case ONSET_TIME:
				oldestCaseDate = FacadeProvider.getCaseFacade().getOldestCaseOnsetDate();
				break;
			case REPORT_TIME:
				oldestCaseDate = FacadeProvider.getCaseFacade().getOldestCaseReportDate();
				break;
		}

		return getTimeGroupingKeys(subAttribute, oldestCaseDate);
	}
	
	@SuppressWarnings("unchecked")
	public static List<StatisticsGroupingKey> getAttributeGroupingKeys(StatisticsCaseAttribute attribute, StatisticsSubAttributeEnum subAttribute) {

		if (subAttribute != null) {
			switch (attribute) {
				case REPORT_TIME:
				case ONSET_TIME:
					return getTimeGroupingKeys(attribute, subAttribute);
				default:
					return getAttributeGroupingKeys(getEnum(attribute), subAttribute);
			}
		}
		else {
			switch (attribute) {
				case CLASSIFICATION: {
					ArrayList<StatisticsGroupingKey> classificationList = new ArrayList<>();
					for (CaseClassification classification : CaseClassification.values()) {
						classificationList.add(classification);
					}
					return classificationList;
				}
				case OUTCOME:
					ArrayList<StatisticsGroupingKey> outcomeList = new ArrayList<>();
					for (CaseOutcome outcome : CaseOutcome.values()) {
						outcomeList.add(outcome);
					}
					return outcomeList;
				default:
					return getAttributeGroupingKeys(getEnum(attribute), subAttribute);
			}
		}
	}

	
	public static StatisticsCaseAttribute getEnum (StatisticsAttribute attribute) {
		return (StatisticsCaseAttribute) StatisticsAttribute.getEnum(attribute);
	}
	
	public static StatisticsSubAttributeEnum getEnum (StatisticsSubAttribute attribute) {
		return (StatisticsSubAttributeEnum) StatisticsSubAttribute.getEnum(attribute);
	}
	
	public static StatisticsAttributeEnum getEnum (StatisticsCaseAttribute attribute) {
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
