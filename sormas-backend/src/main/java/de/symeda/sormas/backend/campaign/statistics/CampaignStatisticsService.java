package de.symeda.sormas.backend.campaign.statistics;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import de.symeda.sormas.api.campaign.CampaignJurisdictionLevel;
import de.symeda.sormas.api.campaign.data.CampaignFormDataEntry;
import de.symeda.sormas.api.campaign.form.CampaignFormElement;
import de.symeda.sormas.api.campaign.form.CampaignFormElementType;
import de.symeda.sormas.api.campaign.statistics.CampaignStatisticsCriteria;
import de.symeda.sormas.api.campaign.statistics.CampaignStatisticsDto;
import de.symeda.sormas.api.campaign.statistics.CampaignStatisticsGroupingDto;
import de.symeda.sormas.backend.campaign.Campaign;
import de.symeda.sormas.backend.campaign.data.CampaignFormData;
import de.symeda.sormas.backend.campaign.form.CampaignFormMeta;
import de.symeda.sormas.backend.infrastructure.area.Area;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless
@LocalBean
public class CampaignStatisticsService {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	public List<CampaignStatisticsDto> getCampaignStatistics(CampaignStatisticsCriteria criteria) {

		Query campaignsStatisticsQuery = em.createNativeQuery(buildStatisticsQuery(criteria));
		final CampaignJurisdictionLevel groupingLevel = criteria.getGroupingLevel();
		Map<CampaignStatisticsGroupingDto, CampaignStatisticsDto> results = new LinkedHashMap<>();
		((Stream<Object[]>) campaignsStatisticsQuery.getResultStream()).forEach(result -> {
			CampaignStatisticsGroupingDto campaignStatisticsGroupingDto = new CampaignStatisticsGroupingDto(
				(String) result[1],
				(String) result[2],
				(String) result[3],
				shouldIncludeRegion(groupingLevel) ? (String) result[4] : "",
				shouldIncludeDistrict(groupingLevel) ? (String) result[5] : "",
				shouldIncludeCommunity(groupingLevel) ? (String) result[6] : "");
			if (!results.containsKey(campaignStatisticsGroupingDto)) {
				CampaignStatisticsDto campaignStatisticsDto =
					new CampaignStatisticsDto(campaignStatisticsGroupingDto, result[0] != null ? ((Number) result[0]).intValue() : null);
				results.put(campaignStatisticsGroupingDto, campaignStatisticsDto);
			}
			int length = result.length;
			CampaignFormDataEntry campaignFormDataEntry = new CampaignFormDataEntry((String) result[length - 2], result[length - 1]);
			results.get(campaignStatisticsGroupingDto).addStatisticsData(campaignFormDataEntry);
		});
		return results.values().stream().collect(Collectors.toList());
	}

	private String buildStatisticsQuery(CampaignStatisticsCriteria criteria) {
		String selectExpression = new StringBuilder("SELECT COUNT(").append(CampaignFormMeta.TABLE_NAME)
			.append(".")
			.append(CampaignFormMeta.UUID)
			.append(")")
			.append(" AS formCount, ")
			.append(buildSelectExpression(criteria))
			.append(buildJsonSelectExpression())
			.append(" FROM ")
			.append(CampaignFormData.TABLE_NAME)
			.toString();
		String joinExpression = new StringBuilder().append(buildJoinExpression()).append(buildJsonJoinExpression()).toString();

		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append(selectExpression).append(joinExpression);

		queryBuilder.append(" WHERE ");
		String whereExpression = buildWhereExpression(criteria);
		if (!whereExpression.isEmpty()) {
			queryBuilder.append(whereExpression).append(" AND ");
		}
		queryBuilder.append(buildJsonWhereExpression());

		queryBuilder.append(buildGroupByExpression(criteria)).append(buildJsonGroupByExpression()).append(buildOrderByExpression(criteria));

		return queryBuilder.toString();
	}

	private String buildSelectExpression(CampaignStatisticsCriteria criteria) {
		StringBuilder selectBuilder = new StringBuilder().append(buildSelectField(Campaign.TABLE_NAME, Campaign.NAME))
			.append(", ")
			.append(buildSelectField(CampaignFormMeta.TABLE_NAME, CampaignFormMeta.FORM_NAME))
			.append(", ")
			.append(buildSelectField(Area.TABLE_NAME, Area.NAME));

		CampaignJurisdictionLevel groupingLevel = criteria.getGroupingLevel();
		if (shouldIncludeRegion(groupingLevel)) {
			selectBuilder.append(", ").append(buildSelectField(Region.TABLE_NAME, Region.NAME));
		}
		if (shouldIncludeDistrict(groupingLevel)) {
			selectBuilder.append(", ").append(buildSelectField(District.TABLE_NAME, District.NAME));
		}
		if (shouldIncludeCommunity(groupingLevel)) {
			selectBuilder.append(", ").append(buildSelectField(Community.TABLE_NAME, Community.NAME));
		}

		return selectBuilder.toString();
	}

	private String buildSelectField(String tableName, String fieldName) {
		StringBuilder selectFieldBuilder = new StringBuilder();
		selectFieldBuilder.append(tableName).append(".").append(fieldName).append(" AS ").append(tableName).append("_").append(fieldName);
		return selectFieldBuilder.toString();
	}

	private String buildJoinExpression() {
		StringBuilder joinBuilder = new StringBuilder();
		joinBuilder.append(buildLeftJoinCondition(CampaignFormData.CAMPAIGN, Campaign.TABLE_NAME, Campaign.ID));
		joinBuilder.append(buildLeftJoinCondition(CampaignFormData.CAMPAIGN_FORM_META, CampaignFormMeta.TABLE_NAME, CampaignFormMeta.ID));
		joinBuilder.append(buildLeftJoinCondition(CampaignFormData.REGION, Region.TABLE_NAME, Region.ID));
		joinBuilder.append(buildLeftJoinCondition(CampaignFormData.DISTRICT, District.TABLE_NAME, District.ID));
		joinBuilder.append(buildLeftJoinCondition(CampaignFormData.COMMUNITY, Community.TABLE_NAME, Community.ID));
		joinBuilder.append(" LEFT JOIN ")
			.append(Area.TABLE_NAME)
			.append(" ON ")
			.append(Region.TABLE_NAME)
			.append(".")
			.append(Region.AREA)
			.append("_id = ")
			.append(Area.TABLE_NAME)
			.append(".")
			.append(Area.ID);
		return joinBuilder.toString();
	}

	private String buildLeftJoinCondition(String fieldPart, String joinedTableName, String joinedFieldName) {
		StringBuilder joinConditionBuilder = new StringBuilder(" LEFT JOIN ");
		joinConditionBuilder.append(joinedTableName)
			.append(" ON ")
			.append(CampaignFormData.TABLE_NAME)
			.append(".")
			.append(fieldPart)
			.append("_id = ")
			.append(joinedTableName)
			.append(".")
			.append(joinedFieldName);
		return joinConditionBuilder.toString();
	}

	private String buildWhereExpression(CampaignStatisticsCriteria criteria) {
		StringBuilder whereBuilder = new StringBuilder();
		if (criteria.getCampaign() != null) {
			whereBuilder.append(Campaign.TABLE_NAME)
				.append(".")
				.append(Campaign.UUID)
				.append(" = '")
				.append(criteria.getCampaign().getUuid())
				.append("'");
		}
		if (criteria.getCampaignFormMeta() != null) {
			if (whereBuilder.length() > 0) {
				whereBuilder.append(" AND ");
			}
			whereBuilder.append(CampaignFormMeta.TABLE_NAME)
				.append(".")
				.append(CampaignFormMeta.UUID)
				.append(" = '")
				.append(criteria.getCampaignFormMeta().getUuid())
				.append("'");
		}
		if (criteria.getRegion() != null) {
			if (whereBuilder.length() > 0) {
				whereBuilder.append(" AND ");
			}
			whereBuilder.append(Region.TABLE_NAME).append(".").append(Region.UUID).append(" = '").append(criteria.getRegion().getUuid()).append("'");
		}
		if (criteria.getDistrict() != null) {
			if (whereBuilder.length() > 0) {
				whereBuilder.append(" AND ");
			}
			whereBuilder.append(District.TABLE_NAME)
				.append(".")
				.append(District.UUID)
				.append(" = '")
				.append(criteria.getDistrict().getUuid())
				.append("'");
		}
		if (criteria.getCommunity() != null) {
			if (whereBuilder.length() > 0) {
				whereBuilder.append(" AND ");
			}
			whereBuilder.append(Community.TABLE_NAME)
				.append(".")
				.append(Community.UUID)
				.append(" = '")
				.append(criteria.getCommunity().getUuid())
				.append("'");
		}
		return whereBuilder.toString();
	}

	private String buildGroupByExpression(CampaignStatisticsCriteria criteria) {
		CampaignJurisdictionLevel groupingLevel = criteria.getGroupingLevel();
		StringBuilder groupByFilter = new StringBuilder(" GROUP BY ");
		groupByFilter.append(Campaign.TABLE_NAME)
			.append(".")
			.append(Campaign.NAME)
			.append(", ")
			.append(CampaignFormMeta.TABLE_NAME)
			.append(".")
			.append(CampaignFormMeta.FORM_NAME)
			.append(", ")
			.append(Area.TABLE_NAME)
			.append(".")
			.append(Area.NAME);
		if (shouldIncludeRegion(groupingLevel)) {
			groupByFilter.append(", ").append(Region.TABLE_NAME).append(".").append(Region.NAME);
		}
		if (shouldIncludeDistrict(groupingLevel)) {
			groupByFilter.append(", ").append(District.TABLE_NAME).append(".").append(District.NAME);
		}
		if (shouldIncludeCommunity(groupingLevel)) {
			groupByFilter.append(", ").append(Community.TABLE_NAME).append(".").append(Community.NAME);
		}

		return groupByFilter.toString();
	}

	private String buildOrderByExpression(CampaignStatisticsCriteria criteria) {
		CampaignJurisdictionLevel groupingLevel = criteria.getGroupingLevel();
		StringBuilder orderByFilter = new StringBuilder(" ORDER BY ");
		orderByFilter.append(Campaign.TABLE_NAME)
			.append(".")
			.append(Campaign.NAME)
			.append(", ")
			.append(CampaignFormMeta.TABLE_NAME)
			.append(".")
			.append(CampaignFormMeta.FORM_NAME)
			.append(", ")
			.append(Area.TABLE_NAME)
			.append(".")
			.append(Area.NAME);
		if (shouldIncludeRegion(groupingLevel)) {
			orderByFilter.append(", ").append(Region.TABLE_NAME).append(".").append(Region.NAME);
		}
		if (shouldIncludeDistrict(groupingLevel)) {
			orderByFilter.append(", ").append(District.TABLE_NAME).append(".").append(District.NAME);
		}
		if (shouldIncludeCommunity(groupingLevel)) {
			orderByFilter.append(", ").append(Community.TABLE_NAME).append(".").append(Community.NAME);
		}

		return orderByFilter.toString();
	}

	private boolean shouldIncludeRegion(CampaignJurisdictionLevel groupingLevel) {
		return CampaignJurisdictionLevel.REGION.equals(groupingLevel)
			|| CampaignJurisdictionLevel.DISTRICT.equals(groupingLevel)
			|| CampaignJurisdictionLevel.COMMUNITY.equals(groupingLevel);
	}

	private boolean shouldIncludeDistrict(CampaignJurisdictionLevel groupingLevel) {
		return CampaignJurisdictionLevel.DISTRICT.equals(groupingLevel) || CampaignJurisdictionLevel.COMMUNITY.equals(groupingLevel);
	}

	private boolean shouldIncludeCommunity(CampaignJurisdictionLevel groupingLevel) {
		return CampaignJurisdictionLevel.COMMUNITY.equals(groupingLevel);
	}

	private String buildJsonSelectExpression() {
		System.out.println(">>>>>>>>>>>>>>>>>>> xxxxxxxxxxxxxxxxxxxxx");
		
		StringBuilder jsonQueryExpression = new StringBuilder();
		jsonQueryExpression.append(", jsonData->>'")
			.append(CampaignFormElement.ID)
			.append("' as fieldId, ")
			.append("CASE WHEN (jsonMeta ->> '")
			.append(CampaignFormElement.TYPE)
			.append("') = '")
			.append(CampaignFormElementType.NUMBER.toString())
			.append("' THEN sum(cast_to_int(jsonData->>'")
			.append(CampaignFormDataEntry.VALUE)
			.append("', 0))")
			.append(" WHEN (jsonMeta ->> '")
			.append(CampaignFormElement.TYPE)
			.append("') = '")
			.append(CampaignFormElementType.YES_NO.toString())
			.append("' THEN sum(CASE WHEN(jsonData->>'")
			.append(CampaignFormDataEntry.VALUE)
			.append("') = 'true' THEN 1 ELSE 0 END) END as sumValue");
		return jsonQueryExpression.toString();
	}

	private String buildJsonJoinExpression() {
		return new StringBuilder().append(", json_array_elements(")
			.append(CampaignFormData.FORM_VALUES)
			.append(") as jsonData, json_array_elements(")
			.append(CampaignFormMeta.CAMPAIGN_FORM_ELEMENTS)
			.append(") as jsonMeta")
			.toString();
	}

	private String buildJsonWhereExpression() {
		return new StringBuilder().append("jsonData->>'")
			.append(CampaignFormDataEntry.VALUE)
			.append("' IS NOT NULL AND jsonData->>'")
			.append(CampaignFormDataEntry.ID)
			.append("' = jsonMeta->>'")
			.append(CampaignFormElement.ID)
			.append("'")
			.toString();
	}

	private String buildJsonGroupByExpression() {
		return new StringBuilder(", ").append(CampaignFormMeta.TABLE_NAME)
			.append(".")
			.append(CampaignFormMeta.UUID)
			.append(", jsonData->>'")
			.append(CampaignFormDataEntry.ID)
			.append("', jsonMeta->>'")
			.append(CampaignFormElement.TYPE)
			.append("'")
			.toString();
	}
}
