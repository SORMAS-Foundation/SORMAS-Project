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
package de.symeda.sormas.backend.contact;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import de.symeda.sormas.api.AgeGroup;
import de.symeda.sormas.api.IntegerRange;
import de.symeda.sormas.api.contact.ContactStatisticsFacade;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.statistics.contact.StatisticsContactAttribute;
import de.symeda.sormas.api.statistics.contact.StatisticsContactCriteria;
import de.symeda.sormas.api.statistics.StatisticsSubAttributeEnum;
import de.symeda.sormas.api.statistics.StatisticsCountDto;
import de.symeda.sormas.api.statistics.StatisticsGroupingKey;
import de.symeda.sormas.api.statistics.contact.StatisticsHelper;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.infrastructure.PopulationData;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.DistrictFacadeEjb.DistrictFacadeEjbLocal;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.region.RegionFacadeEjb.RegionFacadeEjbLocal;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "ContactStatisticsFacade")
public class ContactStatisticsFacadeEjb implements ContactStatisticsFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	protected EntityManager em;
	
	@EJB
	private RegionService regionService;
	@EJB
	private DistrictService districtService;

	@EJB
	private RegionFacadeEjbLocal regionFacade;
	@EJB
	private DistrictFacadeEjbLocal districtFacade;

	@SuppressWarnings("unchecked")
	@Override
	public List<StatisticsCountDto> queryContactCount(StatisticsContactCriteria caseCriteria, 
			StatisticsContactAttribute rowGrouping, StatisticsSubAttributeEnum rowSubGrouping,
			StatisticsContactAttribute columnGrouping, StatisticsSubAttributeEnum columnSubGrouping,
			boolean includePopulation, boolean includeZeroValues, Integer populationReferenceYear) {

		// case counts
		Pair<String, List<Object>> caseCountQueryAndParams = buildContactCountQuery(caseCriteria, rowGrouping, rowSubGrouping, columnGrouping, columnSubGrouping);

		Query caseCountQuery = em.createNativeQuery(caseCountQueryAndParams.getKey().toString());
		for (int i = 0; i < caseCountQueryAndParams.getValue().size(); i++) {
			caseCountQuery.setParameter(i + 1, caseCountQueryAndParams.getValue().get(i));
		}
		
		Function<Integer, RegionReferenceDto> regionProvider = id -> regionFacade.getRegionReferenceById(id);
		Function<Integer, DistrictReferenceDto> districtProvider = id -> districtFacade.getDistrictReferenceById(id);
		
		List<StatisticsCountDto> caseCountResults = ((Stream<Object[]>) caseCountQuery.getResultStream())
				.map(result -> {
					Object rowKey = "".equals(result[1]) ? null : result[1];
					Object columnKey = "".equals(result[2]) ? null : result[2];
					return new StatisticsCountDto(result[0] != null ? ((Number)result[0]).intValue() : null, null,
						StatisticsHelper.buildGroupingKey(rowKey, rowGrouping, rowSubGrouping, regionProvider, districtProvider),
						StatisticsHelper.buildGroupingKey(columnKey, columnGrouping, columnSubGrouping, regionProvider, districtProvider));
				})
				.collect(Collectors.toList());
		
		if (includeZeroValues) {
			List<StatisticsGroupingKey> allRowKeys;
			if (rowGrouping != null) {
				allRowKeys = (List<StatisticsGroupingKey>) caseCriteria.getFilterValuesForGrouping(rowGrouping, rowSubGrouping);
				if (allRowKeys == null) {
					allRowKeys = StatisticsHelper.getAttributeGroupingKeys(rowGrouping, rowSubGrouping);
				}
			} else {
				allRowKeys = Arrays.asList((StatisticsGroupingKey)null);
			}
			List<StatisticsGroupingKey> allColumnKeys;
			if (columnGrouping != null) {
				allColumnKeys = (List<StatisticsGroupingKey>) caseCriteria.getFilterValuesForGrouping(columnGrouping, columnSubGrouping);
				if (allColumnKeys == null) {
					allColumnKeys = StatisticsHelper.getAttributeGroupingKeys(columnGrouping, columnSubGrouping);
				}
			} else {
				allColumnKeys = Arrays.asList((StatisticsGroupingKey)null);
			}
			
			for (StatisticsGroupingKey rowKey : allRowKeys) {
				for (StatisticsGroupingKey columnKey : allColumnKeys) {
					StatisticsCountDto zeroDto = new StatisticsCountDto(0, null, rowKey, columnKey);
					if (!caseCountResults.contains(zeroDto)) {
						caseCountResults.add(zeroDto);
					}
				}
			}
		}

		// population
		if (includePopulation) {
			Pair<String, List<Object>> populationQueryAndParams = buildPopulationQuery(caseCriteria, rowGrouping, rowSubGrouping, columnGrouping, columnSubGrouping, populationReferenceYear);
	
			Query populationQuery = em.createNativeQuery(populationQueryAndParams.getKey().toString());
			for (int i = 0; i < populationQueryAndParams.getValue().size(); i++) {
				populationQuery.setParameter(i + 1, populationQueryAndParams.getValue().get(i));
			}
			
			List<StatisticsCountDto> populationResults = ((Stream<Object[]>) populationQuery.getResultStream())
					.map(result -> {
						Object rowKey = "".equals(result[1]) ? null : result[1];
						Object columnKey = "".equals(result[2]) ? null : result[2];
						return new StatisticsCountDto(null, result[0] != null ? ((Number)result[0]).intValue() : null,
							StatisticsHelper.buildGroupingKey(rowKey, rowGrouping, rowSubGrouping, regionProvider, districtProvider),
							StatisticsHelper.buildGroupingKey(columnKey, columnGrouping, columnSubGrouping, regionProvider, districtProvider));
					})
					.collect(Collectors.toList());

			boolean rowIsPopulation = rowGrouping != null && rowGrouping.isPopulationData();
			boolean columnIsPopulation = columnGrouping != null && columnGrouping.isPopulationData();
			if (!populationResults.isEmpty()) {
				assert((populationResults.get(0).getRowKey() != null) == rowIsPopulation);
				assert((populationResults.get(0).getColumnKey() != null) == columnIsPopulation);
			}
			
			// add the population data to the case counts
			// when a key is not a population data key, we use null instead
			StatisticsCountDto searchDto = new StatisticsCountDto(null, null, null, null);
			for (StatisticsCountDto caseCountResult : caseCountResults) {
				
				if (rowIsPopulation) {
					searchDto.setRowKey(caseCountResult.getRowKey());
				}
				if (columnIsPopulation) {
					searchDto.setColumnKey(caseCountResult.getColumnKey());
				}
				
				int index = populationResults.indexOf(searchDto);
				if (index >= 0) {
					caseCountResult.setPopulation(populationResults.get(index).getPopulation());
				}
			}
		}
		
		return caseCountResults;
	}

	/**
	private void replaceIdsWithGroupingKeys(List<StatisticsContactCountDto> results, StatisticsContactAttribute groupingA,
		for (StatisticsContactCountDto result : results) {
	 * Builds SQL query string and list of parameters (for filters)
	 */
	public Pair<String, List<Object>> buildContactCountQuery(StatisticsContactCriteria caseCriteria, 
				StatisticsContactAttribute groupingA, StatisticsSubAttributeEnum subGroupingA, 
				StatisticsContactAttribute groupingB, StatisticsSubAttributeEnum subGroupingB) {
	
			// Steps to build the query:
			// 1. Join the required tables
			// 2. Build the filter query
			// 3. Add selected groupings
			// 4. Retrieve and prepare the results
	
			/////////////
			// 1. Join tables that cases are grouped by or that are used in the caseCriteria
			/////////////
	
			StringBuilder caseJoinBuilder = new StringBuilder();
	
			if (subGroupingA == StatisticsSubAttributeEnum.DISTRICT || subGroupingB == StatisticsSubAttributeEnum.DISTRICT) {
				caseJoinBuilder.append(" LEFT JOIN ").append(District.TABLE_NAME).append(" ON ").append(Contact.TABLE_NAME)
				.append(".").append(Contact.DISTRICT).append("_id").append(" = ").append(District.TABLE_NAME)
				.append(".").append(District.ID);
			} else {
				caseJoinBuilder.append(" LEFT JOIN ").append(Region.TABLE_NAME).append(" ON ").append(Contact.TABLE_NAME)
				.append(".").append(Contact.REGION).append("_id").append(" = ").append(Region.TABLE_NAME).append(".").append(Region.ID);
			}
			
	
//			if (groupingA == StatisticsContactAttribute.ONSET_TIME || groupingB == StatisticsContactAttribute.ONSET_TIME
//					|| caseCriteria.hasOnsetDate()) {
//				caseJoinBuilder.append(" LEFT JOIN ").append(Symptoms.TABLE_NAME).append(" ON ").append(Contact.TABLE_NAME)
//				.append(".").append(Contact.SYMPTOMS).append("_id").append(" = ").append(Symptoms.TABLE_NAME).append(".").append(Symptoms.ID);
//			}
	
			if (groupingA == StatisticsContactAttribute.SEX || groupingB == StatisticsContactAttribute.SEX
					|| groupingA == StatisticsContactAttribute.AGE_INTERVAL_1_YEAR
					|| groupingB == StatisticsContactAttribute.AGE_INTERVAL_1_YEAR
					|| groupingA == StatisticsContactAttribute.AGE_INTERVAL_5_YEARS
					|| groupingB == StatisticsContactAttribute.AGE_INTERVAL_5_YEARS
					|| groupingA == StatisticsContactAttribute.AGE_INTERVAL_CHILDREN_COARSE
					|| groupingB == StatisticsContactAttribute.AGE_INTERVAL_CHILDREN_COARSE
					|| groupingA == StatisticsContactAttribute.AGE_INTERVAL_CHILDREN_FINE
					|| groupingB == StatisticsContactAttribute.AGE_INTERVAL_CHILDREN_FINE
					|| groupingA == StatisticsContactAttribute.AGE_INTERVAL_CHILDREN_MEDIUM
					|| groupingB == StatisticsContactAttribute.AGE_INTERVAL_CHILDREN_MEDIUM
					|| groupingA == StatisticsContactAttribute.AGE_INTERVAL_BASIC
					|| groupingB == StatisticsContactAttribute.AGE_INTERVAL_BASIC || caseCriteria.getSexes() != null
					|| caseCriteria.getAgeIntervals() != null) {
				caseJoinBuilder.append(" LEFT JOIN ").append(Person.TABLE_NAME).append(" ON ").append(Contact.TABLE_NAME)
				.append(".").append(Contact.PERSON).append("_id").append(" = ").append(Person.TABLE_NAME).append(".")
				.append(Person.ID);
			}

			if (CollectionUtils.isNotEmpty(caseCriteria.getReportingUserRoles())
					|| groupingA == StatisticsContactAttribute.REPORTING_USER_ROLE
					|| groupingB == StatisticsContactAttribute.REPORTING_USER_ROLE) {
				caseJoinBuilder.append(" LEFT JOIN ").append(User.TABLE_NAME_USERROLES).append(" ON ")
						.append(Contact.TABLE_NAME).append(".").append(Contact.REPORTING_USER).append("_id").append(" = ")
						.append(User.TABLE_NAME_USERROLES).append(".").append(UserDto.COLUMN_NAME_USER_ID);
			}

			/////////////
			// 2. Build filter based on caseCriteria
			/////////////
	
			StringBuilder caseFilterBuilder = new StringBuilder(" WHERE ");
	
			caseFilterBuilder.append("(").append(Contact.TABLE_NAME).append(".").append(Contact.DELETED).append(" = false");
			// needed for the full join on population
			caseFilterBuilder.append(" OR ").append(Contact.TABLE_NAME).append(".").append(Contact.DELETED).append(" IS NULL ");
			caseFilterBuilder.append(")");
			List<Object> filterBuilderParameters = new ArrayList<Object>();
	
			if (CollectionUtils.isNotEmpty(caseCriteria.getReportYears())) {
				extendFilterBuilderWithDateElement(caseFilterBuilder, filterBuilderParameters, "YEAR", Contact.TABLE_NAME,
						Contact.REPORT_DATE_TIME, caseCriteria.getReportYears(), dateValue -> (dateValue.getValue()));
			}
	
			if (CollectionUtils.isNotEmpty(caseCriteria.getReportQuarters())) {
				extendFilterBuilderWithDateElement(caseFilterBuilder, filterBuilderParameters, "QUARTER", Contact.TABLE_NAME,
						Contact.REPORT_DATE_TIME, caseCriteria.getReportQuarters(), dateValue -> (dateValue.getValue()));
			}
	
			if (CollectionUtils.isNotEmpty(caseCriteria.getReportMonths())) {
				extendFilterBuilderWithDateElement(caseFilterBuilder, filterBuilderParameters, "MONTH", Contact.TABLE_NAME,
						Contact.REPORT_DATE_TIME, caseCriteria.getReportMonths(), dateValue -> (dateValue.ordinal() + 1));
			}
	
			if (CollectionUtils.isNotEmpty(caseCriteria.getReportEpiWeeks())) {
				extendFilterBuilderWithEpiWeek(caseFilterBuilder, filterBuilderParameters, Contact.TABLE_NAME, Contact.REPORT_DATE_TIME,
						caseCriteria.getReportEpiWeeks(), value -> value.getWeek());
			}
	
			if (CollectionUtils.isNotEmpty(caseCriteria.getReportQuartersOfYear())) {
				extendFilterBuilderWithQuarterOfYear(caseFilterBuilder, filterBuilderParameters, Contact.TABLE_NAME,
						Contact.REPORT_DATE_TIME, caseCriteria.getReportQuartersOfYear(),
						value -> value.getYear().getValue() * 10 + value.getQuarter().getValue());
			}
	
			if (CollectionUtils.isNotEmpty(caseCriteria.getReportMonthsOfYear())) {
				extendFilterBuilderWithMonthOfYear(caseFilterBuilder, filterBuilderParameters, Contact.TABLE_NAME,
						Contact.REPORT_DATE_TIME, caseCriteria.getReportMonthsOfYear(),
						value -> value.getYear().getValue() * 100 + (value.getMonth().ordinal() + 1));
			}
	
			if (CollectionUtils.isNotEmpty(caseCriteria.getReportEpiWeeksOfYear())) {
				extendFilterBuilderWithEpiWeekOfYear(caseFilterBuilder, filterBuilderParameters, Contact.TABLE_NAME,
						Contact.REPORT_DATE_TIME, caseCriteria.getReportEpiWeeksOfYear(),
						value -> value.getYear() * 100 + value.getWeek());
			}
	
			if (caseCriteria.getReportDateFrom() != null || caseCriteria.getReportDateTo() != null) {
				extendFilterBuilderWithDate(caseFilterBuilder, filterBuilderParameters, caseCriteria.getReportDateFrom(),
						caseCriteria.getReportDateTo(), Contact.TABLE_NAME, Contact.REPORT_DATE_TIME);
			}
	
			if (CollectionUtils.isNotEmpty(caseCriteria.getSexes()) || caseCriteria.isSexUnknown() != null) {
				if (caseFilterBuilder.length() > 0) {
					caseFilterBuilder.append(" AND ");
				}
	
				caseFilterBuilder.append("(");
				StringBuilder subFilterBuilder = new StringBuilder();
	
				if (CollectionUtils.isNotEmpty(caseCriteria.getSexes())) {
					extendFilterBuilderWithSimpleValue(subFilterBuilder, filterBuilderParameters, Person.TABLE_NAME,
							Person.SEX, caseCriteria.getSexes(), entry -> entry.name());
				}
	
				if (caseCriteria.isSexUnknown() != null) {
					if (subFilterBuilder.length() > 0) {
						subFilterBuilder.append(" OR ");
					}
					subFilterBuilder.append(Person.TABLE_NAME).append(".").append(Person.SEX).append(" IS ")
					.append(caseCriteria.isSexUnknown() == true ? "NULL" : "NOT NULL");
				}
	
				caseFilterBuilder.append(subFilterBuilder);
				caseFilterBuilder.append(")");
			}
	
			if (CollectionUtils.isNotEmpty(caseCriteria.getAgeIntervals())) {
				if (caseFilterBuilder.length() > 0) {
					caseFilterBuilder.append(" AND ");
				}
	
				caseFilterBuilder.append("(");
				StringBuilder subFilterBuilder = new StringBuilder();
	
				Integer upperRangeBoundary = null;
				boolean appendUnknown = false;
				List<Integer> agesList = new ArrayList<Integer>();
				for (IntegerRange range : caseCriteria.getAgeIntervals()) {
					if (range.getTo() == null) {
						if (range.getFrom() == null) {
							appendUnknown = true;
						} else {
							upperRangeBoundary = range.getFrom();
						}
					} else {
						agesList.addAll(
								IntStream.rangeClosed(range.getFrom(), range.getTo()).boxed().collect(Collectors.toList()));
					}
				}
	
				if (agesList.size() > 0) {
					extendFilterBuilderWithSimpleValue(subFilterBuilder, filterBuilderParameters, Contact.TABLE_NAME,
							Contact.CONTACT_AGE, agesList, value -> value);
				}
	
				if (upperRangeBoundary != null) {
					if (subFilterBuilder.length() > 0) {
						subFilterBuilder.append(" OR ");
					}
					subFilterBuilder.append(Contact.TABLE_NAME).append(".").append(Contact.CONTACT_AGE).append(" >= ?")
					.append(filterBuilderParameters.size() + 1);
					filterBuilderParameters.add(upperRangeBoundary);
				}
	
				if (appendUnknown) {
					if (subFilterBuilder.length() > 0) {
						subFilterBuilder.append(" OR ");
					}
					subFilterBuilder.append(Contact.TABLE_NAME).append(".").append(Contact.CONTACT_AGE).append(" IS NULL");
				}
	
				caseFilterBuilder.append(subFilterBuilder);
				caseFilterBuilder.append(")");
			}
	
			if (CollectionUtils.isNotEmpty(caseCriteria.getDiseases())) {
				extendFilterBuilderWithSimpleValue(caseFilterBuilder, filterBuilderParameters, Contact.TABLE_NAME, Contact.DISEASE,
						caseCriteria.getDiseases(), entry -> entry.name());
			}
	
			if (CollectionUtils.isNotEmpty(caseCriteria.getClassifications())) {
				extendFilterBuilderWithSimpleValue(caseFilterBuilder, filterBuilderParameters, Contact.TABLE_NAME,
						Contact.CONTACT_CLASSIFICATION, caseCriteria.getClassifications(), entry -> entry.name());
			}
	
			if (CollectionUtils.isNotEmpty(caseCriteria.getFollowUpStatuses())) {
				extendFilterBuilderWithSimpleValue(caseFilterBuilder, filterBuilderParameters, Contact.TABLE_NAME, Contact.FOLLOW_UP_STATUS,
						caseCriteria.getFollowUpStatuses(), entry -> entry.name());
			}
	
			if (CollectionUtils.isNotEmpty(caseCriteria.getRegions())) {
				List<Long> regionIds = regionService.getIdsByReferenceDtos(caseCriteria.getRegions());
				extendFilterBuilderWithSimpleValue(caseFilterBuilder, filterBuilderParameters, Contact.TABLE_NAME, Contact.REGION + "_id",
						regionIds, entry -> entry);
			}
	
			List<Long> districtIds;
			if (CollectionUtils.isNotEmpty(caseCriteria.getDistricts())) {
				districtIds = districtService.getIdsByReferenceDtos(caseCriteria.getDistricts());
				extendFilterBuilderWithSimpleValue(caseFilterBuilder, filterBuilderParameters, Contact.TABLE_NAME, Contact.DISTRICT + "_id",
						districtIds, entry -> entry);
			} else {
				districtIds = null;
			}

			if (CollectionUtils.isNotEmpty(caseCriteria.getReportingUserRoles())) {
				extendFilterBuilderWithSimpleValue(caseFilterBuilder, filterBuilderParameters, User.TABLE_NAME_USERROLES,
						UserDto.COLUMN_NAME_USERROLE, caseCriteria.getReportingUserRoles(), entry -> entry.name());
			}
	
			//////////////
			// 3. Add selected groupings
			/////////////
			
			String groupingSelectQueryA = null, groupingSelectQueryB = null;
			StringBuilder caseGroupByBuilder = new StringBuilder();
			StringBuilder orderByBuilder = new StringBuilder();
			String groupAAlias = "groupA";
			String groupBAlias = "groupB";
			
			if (groupingA != null || groupingB != null) {
				caseGroupByBuilder.append(" GROUP BY ");
	
				if (groupingA != null) {
					groupingSelectQueryA = buildContactGroupingSelectQuery(groupingA, subGroupingA, groupAAlias);
					caseGroupByBuilder.append(groupAAlias);
				} 
				if (groupingB != null) {
					groupingSelectQueryB = buildContactGroupingSelectQuery(groupingB, subGroupingB, groupBAlias);
					caseGroupByBuilder.append(",").append(groupBAlias);
				} 
			}
			
			//////////////
			// 4. Order results
			/////////////
	
			orderByBuilder.append(" ORDER BY ");
			if (groupingA != null) {
				orderByBuilder.append(groupAAlias).append(" NULLS LAST");
			}
			if (groupingB != null) {
				if (groupingA != null) {
					orderByBuilder.append(",");
				}
				orderByBuilder.append(groupBAlias).append(" NULLS LAST");
			}
	
			StringBuilder queryBuilder = new StringBuilder();

			queryBuilder.append("SELECT COUNT(*) AS casecount ");
	
			if (groupingSelectQueryA != null) {
				queryBuilder.append(", ").append(groupingSelectQueryA);
			} else {
				queryBuilder.append(", null\\:\\:text AS ").append(groupAAlias);
			}
			if (groupingSelectQueryB != null) {
				queryBuilder.append(", ").append(groupingSelectQueryB);
			} else {
				queryBuilder.append(", null\\:\\:text AS ").append(groupBAlias);
			}
			
			queryBuilder.append(" FROM ").append(Contact.TABLE_NAME)
			.append(caseJoinBuilder)
			.append(caseFilterBuilder)
			.append(caseGroupByBuilder);
			
			if (groupingA != null || groupingB != null) {
				queryBuilder.append(orderByBuilder);
			}
	
			return new ImmutablePair<String, List<Object>>(queryBuilder.toString(), filterBuilderParameters);
		}

	/**
	 * Builds SQL query string and list of parameters (for filters)
	 */
	public Pair<String, List<Object>> buildPopulationQuery(StatisticsContactCriteria caseCriteria, 
			StatisticsContactAttribute groupingA, StatisticsSubAttributeEnum subGroupingA, 
			StatisticsContactAttribute groupingB, StatisticsSubAttributeEnum subGroupingB,
			Integer populationReferenceYear) {

		////////
		// GROUP BY
		///////
		
		String groupAAlias = "groupA";
		String groupBAlias = "groupB";
		String groupASelect = buildPopulationGroupingSelect(groupingA, subGroupingA);
		String groupBSelect = buildPopulationGroupingSelect(groupingB, subGroupingB);
		
		StringBuilder groupByBuilder = new StringBuilder();
		if (groupASelect != null || groupBSelect != null) {
			groupByBuilder.append(" GROUP BY ");
			if (groupASelect != null) {
				groupByBuilder.append(groupAAlias);
			}
			if (groupBSelect != null) {
				if (groupASelect != null) {
					groupByBuilder.append(", ");
				}
				groupByBuilder.append(groupBAlias);
			}
		}
		
		if (groupASelect == null) {
			groupASelect = "null\\:\\:text";
		}
		groupASelect += " AS " + groupAAlias;
		
		if (groupBSelect == null) {
			groupBSelect = "null\\:\\:text";
		}
		groupBSelect += " AS " + groupBAlias;

		////////
		// WHERE
		///////
		
		StringBuilder whereBuilder = new StringBuilder();
		List<Object> filterBuilderParameters = new ArrayList<Object>();

		if (CollectionUtils.isNotEmpty(caseCriteria.getRegions())) {
			// limit to specific regions

			List<Long> regionIds = regionService.getIdsByReferenceDtos(caseCriteria.getRegions());
			extendFilterBuilderWithSimpleValue(whereBuilder, filterBuilderParameters, PopulationData.TABLE_NAME, PopulationData.REGION + "_id",
					regionIds, entry -> entry);
		}

		boolean usesDistricts;
		List<Long> districtIds;
		if (CollectionUtils.isNotEmpty(caseCriteria.getDistricts())) {
			// limit to specific districts
			
			districtIds = districtService.getIdsByReferenceDtos(caseCriteria.getDistricts());
			extendFilterBuilderWithSimpleValue(whereBuilder, filterBuilderParameters, PopulationData.TABLE_NAME, PopulationData.DISTRICT + "_id",
					districtIds, entry -> entry);
			usesDistricts = true;
		} 
		else {
			// limit either to entries with district our to entries without district
			
			districtIds = null;
			usesDistricts = subGroupingA == StatisticsSubAttributeEnum.DISTRICT || subGroupingB == StatisticsSubAttributeEnum.DISTRICT;
			
			if (whereBuilder.length() > 0) {
				whereBuilder.append(" AND ");
			}
			whereBuilder.append("(").append(PopulationData.TABLE_NAME).append(".").append(PopulationData.DISTRICT).append("_id");
			if (usesDistricts) {
				whereBuilder.append(" IS NOT NULL)");
			} else {
				// use entry with sum for all districts
				whereBuilder.append(" IS NULL)");
			}
		}
		
		
		// sex
		whereBuilder.append(" AND (");
		if (CollectionUtils.isNotEmpty(caseCriteria.getSexes())) {

			StringBuilder subFilterBuilder = new StringBuilder();
			extendFilterBuilderWithSimpleValue(subFilterBuilder, filterBuilderParameters, PopulationData.TABLE_NAME,
					PopulationData.SEX, caseCriteria.getSexes(), entry -> entry.name());
			whereBuilder.append(subFilterBuilder);
			
		} else {
			
			boolean usesSex = groupingA == StatisticsContactAttribute.SEX || groupingB == StatisticsContactAttribute.SEX;
			whereBuilder.append(PopulationData.TABLE_NAME).append(".").append(PopulationData.SEX);
			if (usesSex) {
				whereBuilder.append(" IS NOT NULL");
			} else {
				// use entry with sum for all sexes
				whereBuilder.append(" IS NULL");
			}
		}
		whereBuilder.append(")");

		// age group
		whereBuilder.append(" AND (");
		if (CollectionUtils.isNotEmpty(caseCriteria.getAgeIntervals())) {
			
			List<AgeGroup> ageGroups = caseCriteria.getAgeIntervals().stream().map(ageInterval -> {
				AgeGroup ageGroup = AgeGroup.getAgeGroupFromIntegerRange(ageInterval);
				if (ageGroup == null) {
					throw new IllegalArgumentException("Could not map integer range to age group: " + ageInterval.toString());
				}
				return ageGroup;
				}).collect(Collectors.toList());
			
			StringBuilder subFilterBuilder = new StringBuilder();
			extendFilterBuilderWithSimpleValue(subFilterBuilder, filterBuilderParameters, PopulationData.TABLE_NAME,
					PopulationData.AGE_GROUP, ageGroups, entry -> entry.name());
			whereBuilder.append(subFilterBuilder);

		} else {
			
			boolean usesAgeGroup = groupingA == StatisticsContactAttribute.AGE_INTERVAL_5_YEARS || groupingB == StatisticsContactAttribute.AGE_INTERVAL_5_YEARS;
			whereBuilder.append(PopulationData.TABLE_NAME).append(".").append(PopulationData.AGE_GROUP);
			if (usesAgeGroup) {
				whereBuilder.append(" IS NOT NULL");
			} else {
				// use entry with sum for all sexes
				whereBuilder.append(" IS NULL");
			}
		}
		whereBuilder.append(")");
		
		whereBuilder.insert(0, " WHERE ");

		////////
		// SELECT
		///////
		
		StringBuilder selectBuilder = new StringBuilder(" SELECT SUM(").append(PopulationData.POPULATION)
				.append("*EXP(growthsource.growthrate*0.01*")
				.append("(?").append(filterBuilderParameters.size() + 1)
				.append(" - date_part('year', " + PopulationData.COLLECTION_DATE).append("\\:\\:timestamp)").append("))) AS population, ");
		
		if (populationReferenceYear == null) {
			filterBuilderParameters.add(LocalDate.now().getYear());
		} else {
			filterBuilderParameters.add(populationReferenceYear);
		}
		selectBuilder.append(groupASelect).append(", ").append(groupBSelect);
		selectBuilder.append(" FROM ").append(PopulationData.TABLE_NAME);
		
		// growth rates to calculate the population
		selectBuilder.append(" LEFT JOIN ");
		if (districtIds != null || subGroupingA == StatisticsSubAttributeEnum.DISTRICT || subGroupingB == StatisticsSubAttributeEnum.DISTRICT) {
			selectBuilder.append(District.TABLE_NAME).append(" AS growthsource ON growthsource.").append(District.ID)
				.append(" = ").append(PopulationData.DISTRICT).append("_id");
		} else {
			selectBuilder.append(Region.TABLE_NAME).append(" AS growthsource ON growthsource.").append(Region.ID)
			.append(" = ").append(PopulationData.REGION).append("_id");
		}
		
		////////
		// ORDER BY
		///////

		StringBuilder orderByBuilder = new StringBuilder();
		orderByBuilder.append(" ORDER BY ").append(groupAAlias).append(" NULLS LAST, ")
			.append(groupBAlias).append(" NULLS LAST");
	

		StringBuilder queryBuilder = new StringBuilder();
		
		queryBuilder.append(selectBuilder);
		queryBuilder.append(whereBuilder);
		queryBuilder.append(groupByBuilder);
		queryBuilder.append(orderByBuilder);

		return new ImmutablePair<String, List<Object>>(queryBuilder.toString(), filterBuilderParameters);
	}

	private String buildPopulationGroupingSelect(StatisticsContactAttribute grouping,
			StatisticsSubAttributeEnum subGrouping) {
		if (grouping != null) {
			switch (grouping) {
			case REGION_DISTRICT: {
				switch (subGrouping) {
				case REGION: 
					return PopulationData.TABLE_NAME + "." + PopulationData.REGION + "_id";
				case DISTRICT: 
					return PopulationData.TABLE_NAME + "." + PopulationData.DISTRICT + "_id";
				default:
					return null;
				}
			}
			case SEX:
				return PopulationData.TABLE_NAME + "." + PopulationData.SEX;
			case AGE_INTERVAL_5_YEARS:
				return PopulationData.TABLE_NAME + "." + PopulationData.AGE_GROUP;
			default:
				return null;
			}
		}
		return null;
	}
	
	private <T> StringBuilder extendFilterBuilderWithSimpleValue(StringBuilder filterBuilder,
			List<Object> filterBuilderParameters, String tableName, String fieldName, List<T> values,
			Function<T, ?> valueMapper) {
		if (filterBuilder.length() > 0) {
			filterBuilder.append(" AND ");
		}

		filterBuilder.append(tableName).append(".").append(fieldName).append(" IN ");
		return AbstractAdoService.appendInFilterValues(filterBuilder, filterBuilderParameters, values, valueMapper);
	}

	private StringBuilder extendFilterBuilderWithDate(StringBuilder filterBuilder, List<Object> filterBuilderParameters,
			Date from, Date to, String tableName, String fieldName) {

		if (from != null || to != null) {
			if (filterBuilder.length() > 0) {
				filterBuilder.append(" AND ");
			}

			if (from != null && to != null) {
				filterBuilder.append(tableName).append(".").append(fieldName).append(" BETWEEN ?")
				.append(filterBuilderParameters.size() + 1);
				filterBuilderParameters.add(from);
				filterBuilder.append(" AND ?").append(filterBuilderParameters.size() + 1).append("");
				filterBuilderParameters.add(to);
			} else if (from != null) {
				filterBuilder.append(tableName).append(".").append(fieldName).append(" >= ?")
				.append(filterBuilderParameters.size() + 1);
				filterBuilderParameters.add(from);
			} else {
				filterBuilder.append(tableName).append(".").append(fieldName).append(" <= ?")
				.append(filterBuilderParameters.size() + 1);
				filterBuilderParameters.add(to);
			}
		}

		return filterBuilder;
	}

	private <T> StringBuilder extendFilterBuilderWithDateElement(StringBuilder filterBuilder,
			List<Object> filterBuilderParameters, String dateElementToExtract, String tableName, String fieldName,
			List<T> values, Function<T, Integer> valueMapper) {
		if (filterBuilder.length() > 0) {
			filterBuilder.append(" AND ");
		}

		filterBuilder.append("(CAST(EXTRACT(" + dateElementToExtract + " FROM ").append(tableName).append(".")
		.append(fieldName).append(")  AS integer))").append(" IN ");
		return AbstractAdoService.appendInFilterValues(filterBuilder, filterBuilderParameters, values, valueMapper);
	}

	private <T> StringBuilder extendFilterBuilderWithEpiWeek(StringBuilder filterBuilder,
			List<Object> filterBuilderParameters, String tableName, String fieldName, List<T> values,
			Function<T, Integer> valueMapper) {
		if (filterBuilder.length() > 0) {
			filterBuilder.append(" AND ");
		}

		filterBuilder.append("epi_week(").append(tableName).append(".").append(fieldName).append(")").append(" IN ");
		return AbstractAdoService.appendInFilterValues(filterBuilder, filterBuilderParameters, values, valueMapper);
	}

	private <T> StringBuilder extendFilterBuilderWithEpiWeekOfYear(StringBuilder filterBuilder,
			List<Object> filterBuilderParameters, String tableName, String fieldName, List<T> values,
			Function<T, Integer> valueMapper) {
		if (filterBuilder.length() > 0) {
			filterBuilder.append(" AND ");
		}

		filterBuilder.append("(epi_year(").append(tableName).append(".").append(fieldName).append(")").append(" * 100")
		.append(" + epi_week(").append(tableName).append(".").append(fieldName).append("))").append(" IN ");
		return AbstractAdoService.appendInFilterValues(filterBuilder, filterBuilderParameters, values, valueMapper);
	}

	private <T> StringBuilder extendFilterBuilderWithQuarterOfYear(StringBuilder filterBuilder,
			List<Object> filterBuilderParameters, String tableName, String fieldName, List<T> values,
			Function<T, Integer> valueMapper) {
		if (filterBuilder.length() > 0) {
			filterBuilder.append(" AND ");
		}

		filterBuilder.append("(CAST(EXTRACT(YEAR FROM ").append(tableName).append(".").append(fieldName).append(")")
		.append(" * 10 AS integer)) + (CAST(EXTRACT(QUARTER FROM ").append(tableName).append(".")
		.append(fieldName).append(") AS integer))").append(" IN ");
		return AbstractAdoService.appendInFilterValues(filterBuilder, filterBuilderParameters, values, valueMapper);
	}

	private <T> StringBuilder extendFilterBuilderWithMonthOfYear(StringBuilder filterBuilder,
			List<Object> filterBuilderParameters, String tableName, String fieldName, List<T> values,
			Function<T, Integer> valueMapper) {
		if (filterBuilder.length() > 0) {
			filterBuilder.append(" AND ");
		}

		filterBuilder.append("(CAST(EXTRACT(YEAR FROM ").append(tableName).append(".").append(fieldName).append(")")
		.append(" * 100 AS integer)) + (CAST(EXTRACT(MONTH FROM ").append(tableName).append(".")
		.append(fieldName).append(") AS integer))").append(" IN ");
		return AbstractAdoService.appendInFilterValues(filterBuilder, filterBuilderParameters, values, valueMapper);
	}

	private String buildContactGroupingSelectQuery(StatisticsContactAttribute grouping, StatisticsSubAttributeEnum subGrouping, String groupAlias) {
		StringBuilder groupingSelectPartBuilder = new StringBuilder();
		switch (grouping) {
		case SEX:
			groupingSelectPartBuilder.append(Person.TABLE_NAME).append(".").append(Person.SEX).append(" AS ")
			.append(groupAlias);
			break;
		case DISEASE:
			groupingSelectPartBuilder.append(Contact.TABLE_NAME).append(".").append(Contact.DISEASE).append(" AS ")
			.append(groupAlias);
			break;
		case CLASSIFICATION:
			groupingSelectPartBuilder.append(Contact.TABLE_NAME).append(".").append(Contact.CONTACT_CLASSIFICATION)
			.append(" AS ").append(groupAlias);
			break;
		case FOLLOW_UP_STATUS:
			groupingSelectPartBuilder.append(Contact.TABLE_NAME).append(".").append(Contact.FOLLOW_UP_STATUS).append(" AS ")
			.append(groupAlias);
			break;
		case REGION_DISTRICT: {
			switch (subGrouping) {
			case REGION:
				groupingSelectPartBuilder.append(Region.TABLE_NAME).append(".").append(Region.ID).append(" AS ")
				.append(groupAlias);
				break;
			case DISTRICT:
				groupingSelectPartBuilder.append(District.TABLE_NAME).append(".").append(District.ID).append(" AS ")
				.append(groupAlias);
				break;
			default:
				throw new IllegalArgumentException(subGrouping.toString());
			}
			break;
		}
		case AGE_INTERVAL_1_YEAR:
		case AGE_INTERVAL_5_YEARS:
		case AGE_INTERVAL_CHILDREN_COARSE:
		case AGE_INTERVAL_CHILDREN_FINE:
		case AGE_INTERVAL_CHILDREN_MEDIUM:
		case AGE_INTERVAL_BASIC:
			extendGroupingBuilderWithAgeInterval(groupingSelectPartBuilder, grouping, groupAlias);
			break;
//		case ONSET_TIME:
//			switch (subGrouping) {
//			case YEAR:
//				extendGroupingBuilderWithDate(groupingSelectPartBuilder, "YEAR", Symptoms.TABLE_NAME,
//						Symptoms.ONSET_DATE, groupAlias);
//				break;
//			case QUARTER:
//				extendGroupingBuilderWithDate(groupingSelectPartBuilder, "QUARTER", Symptoms.TABLE_NAME,
//						Symptoms.ONSET_DATE, groupAlias);
//				break;
//			case MONTH:
//				extendGroupingBuilderWithDate(groupingSelectPartBuilder, "MONTH", Symptoms.TABLE_NAME,
//						Symptoms.ONSET_DATE, groupAlias);
//				break;
//			case EPI_WEEK:
//				extendGroupingBuilderWithEpiWeek(groupingSelectPartBuilder, Symptoms.TABLE_NAME, Symptoms.ONSET_DATE,
//						groupAlias);
//				break;
//			case QUARTER_OF_YEAR:
//				extendGroupingBuilderWithQuarterOfYear(groupingSelectPartBuilder, Symptoms.TABLE_NAME,
//						Symptoms.ONSET_DATE, groupAlias);
//				break;
//			case MONTH_OF_YEAR:
//				extendGroupingBuilderWithMonthOfYear(groupingSelectPartBuilder, Symptoms.TABLE_NAME,
//						Symptoms.ONSET_DATE, groupAlias);
//				break;
//			case EPI_WEEK_OF_YEAR:
//				extendGroupingBuilderWithEpiWeekOfYear(groupingSelectPartBuilder, Symptoms.TABLE_NAME,
//						Symptoms.ONSET_DATE, groupAlias);
//				break;
//			default:
//				throw new IllegalArgumentException(subGrouping.toString());
//			}
//			break;
		case REPORT_TIME:
			switch (subGrouping) {
			case YEAR:
				extendGroupingBuilderWithDate(groupingSelectPartBuilder, "YEAR", Contact.TABLE_NAME, Contact.REPORT_DATE_TIME,
						groupAlias);
				break;
			case QUARTER:
				extendGroupingBuilderWithDate(groupingSelectPartBuilder, "QUARTER", Contact.TABLE_NAME, Contact.REPORT_DATE_TIME,
						groupAlias);
				break;
			case MONTH:
				extendGroupingBuilderWithDate(groupingSelectPartBuilder, "MONTH", Contact.TABLE_NAME, Contact.REPORT_DATE_TIME,
						groupAlias);
				break;
			case EPI_WEEK:
				extendGroupingBuilderWithEpiWeek(groupingSelectPartBuilder, Contact.TABLE_NAME, Contact.REPORT_DATE_TIME,
						groupAlias);
				break;
			case QUARTER_OF_YEAR:
				extendGroupingBuilderWithQuarterOfYear(groupingSelectPartBuilder, Contact.TABLE_NAME, Contact.REPORT_DATE_TIME,
						groupAlias);
				break;
			case MONTH_OF_YEAR:
				extendGroupingBuilderWithMonthOfYear(groupingSelectPartBuilder, Contact.TABLE_NAME, Contact.REPORT_DATE_TIME,
						groupAlias);
				break;
			case EPI_WEEK_OF_YEAR:
				extendGroupingBuilderWithEpiWeekOfYear(groupingSelectPartBuilder, Contact.TABLE_NAME, Contact.REPORT_DATE_TIME,
						groupAlias);
				break;
			default:
				throw new IllegalArgumentException(subGrouping.toString());
			}
			break;
		case REPORTING_USER_ROLE:
			groupingSelectPartBuilder.append(User.TABLE_NAME_USERROLES).append(".").append(UserDto.COLUMN_NAME_USERROLE)
					.append(" AS ").append(groupAlias);
			break;
		default:
			throw new IllegalArgumentException(grouping.toString());
		}
		return groupingSelectPartBuilder.toString();
	}

	
	private void extendGroupingBuilderWithDate(StringBuilder groupingBuilder, String dateToExtract, String tableName,
			String fieldName, String groupAlias) {
		groupingBuilder.append("(CAST(EXTRACT(" + dateToExtract + " FROM ").append(tableName).append(".")
		.append(fieldName).append(") AS integer)) AS ").append(groupAlias);
	}

	private void extendGroupingBuilderWithEpiWeek(StringBuilder groupingBuilder, String tableName, String fieldName,
			String groupAlias) {
		groupingBuilder.append("epi_week(").append(tableName).append(".").append(fieldName).append(") AS ")
		.append(groupAlias);
	}

	private void extendGroupingBuilderWithEpiWeekOfYear(StringBuilder groupingBuilder, String tableName,
			String fieldName, String groupAlias) {
		groupingBuilder.append("(epi_year(").append(tableName).append(".").append(fieldName).append(") * 100")
		.append(" + epi_week(").append(tableName).append(".").append(fieldName).append(")) AS ")
		.append(groupAlias);
	}

	private void extendGroupingBuilderWithQuarterOfYear(StringBuilder groupingBuilder, String tableName,
			String fieldName, String groupAlias) {
		groupingBuilder.append("((CAST(EXTRACT(YEAR FROM ").append(tableName).append(".").append(fieldName)
		.append(") * 10 AS integer)))").append(" + (CAST(EXTRACT(QUARTER FROM ").append(tableName).append(".")
		.append(fieldName).append(") AS integer)) AS ").append(groupAlias);
	}

	private void extendGroupingBuilderWithMonthOfYear(StringBuilder groupingBuilder, String tableName, String fieldName,
			String groupAlias) {
		groupingBuilder.append("((CAST(EXTRACT(YEAR FROM ").append(tableName).append(".").append(fieldName)
		.append(") * 100 AS integer)))").append(" + (CAST(EXTRACT(MONTH FROM ").append(tableName).append(".")
		.append(fieldName).append(") AS integer)) AS ").append(groupAlias);
	}

	private void extendGroupingBuilderWithAgeInterval(StringBuilder groupingBuilder, StatisticsContactAttribute grouping, String groupAlias) {
		groupingBuilder.append("CASE ");
		switch (grouping) {
		case AGE_INTERVAL_1_YEAR:
			for (int i = 0; i < 80; i++) {
				groupingBuilder.append("WHEN ").append(Contact.TABLE_NAME).append(".").append(Contact.CONTACT_AGE).append(" = ")
				.append(i < 10 ? "0" + i : i).append(" THEN ").append("'").append(i < 10 ? "0" + i : i)
				.append("' ");
			}
			break;
		case AGE_INTERVAL_5_YEARS:
			for (AgeGroup ageGroup : AgeGroup.values()) {
				addAgeGroupToStringBuilder(groupingBuilder, ageGroup);
			}
			break;
		case AGE_INTERVAL_CHILDREN_COARSE:
			addAgeIntervalToStringBuilder(groupingBuilder, 0, 14);
			for (int i = 15; i < 30; i += 5) {
				addAgeIntervalToStringBuilder(groupingBuilder, i, 4);
			}
			for (int i = 30; i < 80; i += 10) {
				addAgeIntervalToStringBuilder(groupingBuilder, i, 9);
			}
			break;
		case AGE_INTERVAL_CHILDREN_FINE:
			for (int i = 0; i < 5; i++) {
				groupingBuilder.append("WHEN ").append(Contact.TABLE_NAME).append(".").append(Contact.CONTACT_AGE).append(" = ")
				.append(i).append(" THEN ").append("'").append("0" + i).append("-").append("0" + i)
				.append("' ");
			}
			for (int i = 5; i < 30; i += 5) {
				addAgeIntervalToStringBuilder(groupingBuilder, i, 4);
			}
			for (int i = 30; i < 80; i += 10) {
				addAgeIntervalToStringBuilder(groupingBuilder, i, 9);
			}
			break;
		case AGE_INTERVAL_CHILDREN_MEDIUM:
			for (int i = 0; i < 30; i += 5) {
				addAgeIntervalToStringBuilder(groupingBuilder, i, 4);
			}
			for (int i = 30; i < 80; i += 10) {
				addAgeIntervalToStringBuilder(groupingBuilder, i, 9);
			}
			break;
		case AGE_INTERVAL_BASIC:
			addAgeIntervalToStringBuilder(groupingBuilder, 0, 0);
			addAgeIntervalToStringBuilder(groupingBuilder, 1, 3);
			addAgeIntervalToStringBuilder(groupingBuilder, 5, 9);
			groupingBuilder.append("WHEN ").append(Contact.TABLE_NAME).append(".").append(Contact.CONTACT_AGE)
			.append(" >= 15 THEN '15+' ");
			break;
		default:
			throw new IllegalArgumentException(grouping.toString());
		}

		if (grouping != StatisticsContactAttribute.AGE_INTERVAL_BASIC
				&& grouping != StatisticsContactAttribute.AGE_INTERVAL_5_YEARS) {
			groupingBuilder.append("WHEN ").append(Contact.TABLE_NAME).append(".").append(Contact.CONTACT_AGE)
				.append(" >= 80 THEN '80+' ");
		}
		groupingBuilder.append("ELSE NULL END AS " + groupAlias);
	}

	private void addAgeIntervalToStringBuilder(StringBuilder groupingBuilder, int number, int increase) {
		String lowerNumberString = number < 10 ? "0" + number : String.valueOf(number);
		String higherNumberString = number + increase < 10 ? "0" + (number + increase)
				: String.valueOf(number + increase);
		groupingBuilder.append("WHEN ").append(Contact.TABLE_NAME).append(".").append(Contact.CONTACT_AGE).append(" BETWEEN ")
		.append(number).append(" AND ").append(number + increase).append(" THEN '").append(lowerNumberString)
		.append("-").append(higherNumberString).append("' ");
	}
	
	private void addAgeGroupToStringBuilder(StringBuilder groupingBuilder, AgeGroup ageGroup) {
		IntegerRange ageRange = ageGroup.toIntegerRange();
		groupingBuilder.append("WHEN ").append(Contact.TABLE_NAME).append(".").append(Contact.CONTACT_AGE);
		if (ageRange.getTo() == null) {
			groupingBuilder.append(" >= ").append(ageRange.getFrom());
		} else {
			groupingBuilder.append(" BETWEEN ").append(ageRange.getFrom()).append(" AND ").append(ageRange.getTo());
		}
		groupingBuilder.append(" THEN '").append(ageGroup.name()).append("' ");
	}

	@LocalBean
	@Stateless
	public static class ContactStatisticsFacadeEjbLocal extends ContactStatisticsFacadeEjb {
	}
}
