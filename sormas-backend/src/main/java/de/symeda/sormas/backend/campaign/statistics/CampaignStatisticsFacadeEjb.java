package de.symeda.sormas.backend.campaign.statistics;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import de.symeda.sormas.api.campaign.CampaignJurisdictionLevel;
import de.symeda.sormas.api.campaign.statistics.CampaignStatisticsCriteria;
import de.symeda.sormas.api.campaign.statistics.CampaignStatisticsDto;
import de.symeda.sormas.api.campaign.statistics.CampaignStatisticsFacade;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.campaign.Campaign;
import de.symeda.sormas.backend.campaign.data.CampaignFormData;
import de.symeda.sormas.backend.campaign.form.CampaignFormMeta;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "CampaignStatisticsFacade")
public class CampaignStatisticsFacadeEjb implements CampaignStatisticsFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Override
	public List<CampaignStatisticsDto> queryCampaignStatistics(
		CampaignStatisticsCriteria criteria,
		Integer first,
		Integer max,
		List<SortProperty> sortProperties) {

		Query campaignsStatisticsQuery = em.createNativeQuery(buildStatisticsQuery(criteria));
		return ((Stream<Object[]>) campaignsStatisticsQuery.getResultStream()).map(
			result -> new CampaignStatisticsDto(
				(String) result[1],
				(String) result[2],
				result.length > 3 ? (String) result[3] : "",
				result.length > 4 ? (String) result[4] : "",
				result.length > 5 ? (String) result[5] : "",
				result[0] != null ? ((Number) result[0]).longValue() : null))
			.collect(Collectors.toList());
	}

	@Override
	public long count(CampaignStatisticsCriteria criteria) {
		Query campaignsStatisticsQuery = em.createNativeQuery(buildStatisticsQuery(criteria));
		return campaignsStatisticsQuery.getResultStream().count();
	}

	private String buildStatisticsQuery(CampaignStatisticsCriteria criteria) {
		String selectExpression = buildSelectExpression(criteria);
		String joinExpression = buildJoinExpression();

		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append(selectExpression).append(joinExpression);

		String whereExpression = buildWhereExpression(criteria);
		if (!whereExpression.isEmpty()) {
			queryBuilder.append(" WHERE ");
			queryBuilder.append(whereExpression);
		}

		queryBuilder.append(buildGroupByExpression(criteria));

		return queryBuilder.toString();
	}

	private String buildSelectExpression(CampaignStatisticsCriteria criteria) {
		StringBuilder selectBuilder = new StringBuilder("SELECT COUNT(").append(CampaignFormMeta.TABLE_NAME)
			.append(".")
			.append(CampaignFormMeta.UUID)
			.append(")")
			.append(", ")
			.append(buildSelectField(Campaign.TABLE_NAME, Campaign.NAME))
			.append(", ")
			.append(buildSelectField(CampaignFormMeta.TABLE_NAME, CampaignFormMeta.FORM_NAME));

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
		selectBuilder.append(" FROM ").append(CampaignFormData.TABLE_NAME);

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
			.append(CampaignFormMeta.FORM_NAME);
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

	@LocalBean
	@Stateless
	public static class CampaignStatisticsFacadeEjbLocal extends CampaignStatisticsFacadeEjb {
	}
}
