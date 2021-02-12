/*
 * ******************************************************************************
 * * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * *
 * * This program is free software: you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation, either version 3 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program. If not, see <https://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.symeda.sormas.backend.campaign.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.campaign.data.CampaignFormDataCriteria;
import de.symeda.sormas.api.campaign.data.CampaignFormDataDto;
import de.symeda.sormas.api.campaign.data.CampaignFormDataEntry;
import de.symeda.sormas.api.campaign.data.CampaignFormDataFacade;
import de.symeda.sormas.api.campaign.data.CampaignFormDataIndexDto;
import de.symeda.sormas.api.campaign.data.CampaignFormDataReferenceDto;
import de.symeda.sormas.api.campaign.diagram.CampaignDiagramCriteria;
import de.symeda.sormas.api.campaign.diagram.CampaignDiagramDataDto;
import de.symeda.sormas.api.campaign.diagram.CampaignDiagramSeries;
import de.symeda.sormas.api.campaign.form.CampaignFormElement;
import de.symeda.sormas.api.campaign.form.CampaignFormElementType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.PopulationDataCriteria;
import de.symeda.sormas.api.infrastructure.PopulationDataDto;
import de.symeda.sormas.api.region.AreaReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.campaign.Campaign;
import de.symeda.sormas.backend.campaign.CampaignFacadeEjb;
import de.symeda.sormas.backend.campaign.CampaignService;
import de.symeda.sormas.backend.campaign.form.CampaignFormMeta;
import de.symeda.sormas.backend.campaign.form.CampaignFormMetaFacadeEjb;
import de.symeda.sormas.backend.campaign.form.CampaignFormMetaService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.infrastructure.PopulationDataFacadeEjb;
import de.symeda.sormas.backend.region.Area;
import de.symeda.sormas.backend.region.AreaService;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.CommunityFacadeEjb;
import de.symeda.sormas.backend.region.CommunityService;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.DistrictFacadeEjb;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.region.RegionFacadeEjb;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "CampaignFormDataFacade")
public class CampaignFormDataFacadeEjb implements CampaignFormDataFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private CampaignFormDataService campaignFormDataService;

	@EJB
	private CampaignService campaignService;

	@EJB
	private CampaignFormMetaService campaignFormMetaService;

	@EJB
	private RegionService regionService;

	@EJB
	private DistrictService districtService;

	@EJB
	private CommunityService communityService;

	@EJB
	private UserService userService;

	@EJB
	private PopulationDataFacadeEjb.PopulationDataFacadeEjbLocal populationDataFacadeEjb;

	@EJB
	private AreaService areaService;

	@EJB
	private RegionFacadeEjb.RegionFacadeEjbLocal regionFacadeEjb;

	public CampaignFormData fromDto(@NotNull CampaignFormDataDto source, boolean checkChangeDate) {
		CampaignFormData target =
			DtoHelper.fillOrBuildEntity(source, campaignFormDataService.getByUuid(source.getUuid()), CampaignFormData::new, checkChangeDate);

		target.setFormValues(source.getFormValues());
		target.setCampaign(campaignService.getByReferenceDto(source.getCampaign()));
		target.setCampaignFormMeta(campaignFormMetaService.getByReferenceDto(source.getCampaignFormMeta()));
		target.setFormDate(source.getFormDate());
		target.setRegion(regionService.getByReferenceDto(source.getRegion()));
		target.setDistrict(districtService.getByReferenceDto(source.getDistrict()));
		target.setCommunity(communityService.getByReferenceDto(source.getCommunity()));
		target.setCreatingUser(userService.getByReferenceDto(source.getCreatingUser()));

		return target;
	}

	public CampaignFormDataDto toDto(CampaignFormData source) {
		if (source == null) {
			return null;
		}

		CampaignFormDataDto target = new CampaignFormDataDto();
		DtoHelper.fillDto(target, source);

		target.setFormValues(source.getFormValues());
		target.setCampaign(CampaignFacadeEjb.toReferenceDto(source.getCampaign()));
		target.setCampaignFormMeta(CampaignFormMetaFacadeEjb.toReferenceDto(source.getCampaignFormMeta()));
		target.setFormDate(source.getFormDate());
		target.setRegion(RegionFacadeEjb.toReferenceDto(source.getRegion()));
		target.setDistrict(DistrictFacadeEjb.toReferenceDto(source.getDistrict()));
		target.setCommunity(CommunityFacadeEjb.toReferenceDto(source.getCommunity()));
		target.setCreatingUser(UserFacadeEjb.toReferenceDto(source.getCreatingUser()));

		return target;
	}

	@Override
	public CampaignFormDataDto saveCampaignFormData(CampaignFormDataDto campaignFormDataDto) throws ValidationRuntimeException {

		CampaignFormData campaignFormData = fromDto(campaignFormDataDto, true);
		CampaignFormDataEntry.removeNullValueEntries(campaignFormData.getFormValues());

		validate(campaignFormDataDto);

		campaignFormDataService.ensurePersisted(campaignFormData);
		return toDto(campaignFormData);
	}

	private void validate(CampaignFormDataDto campaignFormDataDto) {
		if (campaignFormDataDto.getRegion() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validRegion));
		}
		if (campaignFormDataDto.getDistrict() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validDistrict));
		}
		if (campaignFormDataDto.getCommunity() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validCommunity));
		}
	}

	@Override
	public List<CampaignFormDataDto> getByUuids(List<String> uuids) {
		return campaignFormDataService.getByUuids(uuids).stream().map(c -> convertToDto(c)).collect(Collectors.toList());
	}

	@Override
	public void deleteCampaignFormData(String campaignFormDataUuid) {
		if (!userService.hasRight(UserRight.CAMPAIGN_FORM_DATA_DELETE)) {
			throw new UnsupportedOperationException("User " + userService.getCurrentUser().getUuid() + "is not allowed to delete Campaign Form Data");
		}

		CampaignFormData campaignFormData = campaignFormDataService.getByUuid(campaignFormDataUuid);
		campaignFormDataService.delete(campaignFormData);
	}

	private CampaignFormDataDto convertToDto(CampaignFormData source) {
		CampaignFormDataDto dto = toDto(source);
		return dto;
	}

	@Override
	public CampaignFormDataDto getCampaignFormDataByUuid(String uuid) {
		return toDto(campaignFormDataService.getByUuid(uuid));
	}

	@Override
	public boolean isArchived(String campaignFormDataUuid) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<CampaignFormData> from = cq.from(CampaignFormData.class);

		cq.where(cb.and(cb.equal(from.get(CampaignFormData.ARCHIVED), true), cb.equal(from.get(AbstractDomainObject.UUID), campaignFormDataUuid)));
		cq.select(cb.count(from));
		long count = em.createQuery(cq).getSingleResult();

		return count > 0;
	}

	@Override
	public boolean exists(String uuid) {
		return campaignFormDataService.exists(uuid);
	}

	@Override
	public CampaignFormDataReferenceDto getReferenceByUuid(String uuid) {
		return toReferenceDto(campaignFormDataService.getByUuid(uuid));
	}

	@Override
	public CampaignFormDataDto getExistingData(CampaignFormDataCriteria criteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CampaignFormData> cq = cb.createQuery(CampaignFormData.class);
		Root<CampaignFormData> root = cq.from(CampaignFormData.class);

		Predicate filter = CriteriaBuilderHelper
			.and(cb, campaignFormDataService.createCriteriaFilter(criteria, cb, root), campaignFormDataService.createUserFilter(cb, cq, root));
		if (filter != null) {
			cq.where(filter);
		}

		cq.orderBy(cb.desc(root.get(CampaignFormData.CHANGE_DATE)));

		CampaignFormData resultEntity;
		try {
			resultEntity = em.createQuery(cq).setMaxResults(1).getSingleResult();
		} catch (NoResultException e) {
			resultEntity = null;
		}

		return resultEntity != null ? toDto(resultEntity) : null;
	}

	@Override
	public List<CampaignFormDataIndexDto> getIndexList(
		CampaignFormDataCriteria criteria,
		Integer first,
		Integer max,
		List<SortProperty> sortProperties) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CampaignFormDataIndexDto> cq = cb.createQuery(CampaignFormDataIndexDto.class);
		Root<CampaignFormData> root = cq.from(CampaignFormData.class);
		Join<CampaignFormData, Campaign> campaignJoin = root.join(CampaignFormData.CAMPAIGN, JoinType.LEFT);
		Join<CampaignFormData, CampaignFormMeta> campaignFormMetaJoin = root.join(CampaignFormData.CAMPAIGN_FORM_META, JoinType.LEFT);
		Join<CampaignFormData, Region> regionJoin = root.join(CampaignFormData.REGION, JoinType.LEFT);
		Join<CampaignFormData, District> districtJoin = root.join(CampaignFormData.DISTRICT, JoinType.LEFT);
		Join<CampaignFormData, Community> communityJoin = root.join(CampaignFormData.COMMUNITY, JoinType.LEFT);

		cq.multiselect(
			root.get(CampaignFormData.UUID),
			campaignJoin.get(Campaign.NAME),
			campaignFormMetaJoin.get(CampaignFormMeta.FORM_NAME),
			criteria.getCampaignFormMeta() != null ? root.get(CampaignFormData.FORM_VALUES) : cb.nullLiteral(String.class),
			regionJoin.get(Region.NAME),
			districtJoin.get(District.NAME),
			communityJoin.get(Community.NAME),
			root.get(CampaignFormData.FORM_DATE));

		Predicate filter = CriteriaBuilderHelper
			.and(cb, campaignFormDataService.createCriteriaFilter(criteria, cb, root), campaignFormDataService.createUserFilter(cb, cq, root));
		if (filter != null) {
			cq.where(filter);
		}

		if (sortProperties != null && sortProperties.size() > 0) {
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case CampaignFormDataIndexDto.UUID:
				case CampaignFormDataIndexDto.FORM_DATE:
					expression = root.get(sortProperty.propertyName);
					break;
				case CampaignFormDataIndexDto.CAMPAIGN:
					expression = campaignJoin.get(Campaign.NAME);
					break;
				case CampaignFormDataIndexDto.FORM:
					expression = campaignFormMetaJoin.get(CampaignFormMeta.FORM_NAME);
					break;
				case CampaignFormDataIndexDto.REGION:
					expression = regionJoin.get(Region.NAME);
					break;
				case CampaignFormDataIndexDto.DISTRICT:
					expression = districtJoin.get(District.NAME);
					break;
				case CampaignFormDataIndexDto.COMMUNITY:
					expression = communityJoin.get(Community.NAME);
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(cb.desc(root.get(CampaignFormData.CHANGE_DATE)));
		}

		List<CampaignFormDataIndexDto> result;
		if (first != null && max != null) {
			result = em.createQuery(cq).setFirstResult(first).setMaxResults(max).getResultList();
		} else {
			result = em.createQuery(cq).getResultList();
		}

		return result;
	}

	@Override
	public List<String> getAllActiveUuids() {
		if (userService.getCurrentUser() == null) {
			return Collections.emptyList();
		}

		return campaignFormDataService.getAllActiveUuids();
	}

	@Override
	public List<CampaignFormDataDto> getAllActiveAfter(Date date) {
		if (userService.getCurrentUser() == null) {
			return Collections.emptyList();
		}
		return campaignFormDataService.getAllActiveAfter(date).stream().map(c -> convertToDto(c)).collect(Collectors.toList());
	}

	public List<CampaignDiagramDataDto> getDiagramDataByAgeGroup(
		CampaignDiagramSeries diagramSeriesTotal,
		CampaignDiagramSeries diagramSeries,
		CampaignDiagramCriteria campaignDiagramCriteria) {
		List<CampaignDiagramDataDto> resultData = new ArrayList<>();
		final AreaReferenceDto area = campaignDiagramCriteria.getArea();
		final RegionReferenceDto region = campaignDiagramCriteria.getRegion();
		final DistrictReferenceDto district = campaignDiagramCriteria.getDistrict();
		if (Objects.isNull(area)) {
			List<Area> areas = areaService.getAll();
			areas.forEach(areaItem -> {
				Integer population = populationDataFacadeEjb.getAreaPopulation(areaItem.getUuid(), diagramSeriesTotal.getPopulationGroup());
				if (population == 0) {
					resultData.add(
						new CampaignDiagramDataDto(
							areaItem.getName(),
							0,
							areaItem.getUuid(),
							areaItem.getName(),
							diagramSeries.getFieldId(),
							diagramSeries.getFormId(),
							false));
				} else {
					resultData.add(
						new CampaignDiagramDataDto(
							areaItem.getName(),
							population,
							areaItem.getUuid(),
							areaItem.getName(),
							diagramSeries.getFieldId(),
							diagramSeries.getFormId(),
							true));
				}
			});
		} else if (Objects.isNull(region)) {
			List<RegionReferenceDto> regions = regionFacadeEjb.getAllActiveByArea(area.getUuid());
			if (regions.isEmpty()) {
				resultData.add(
					new CampaignDiagramDataDto(
						area.getCaption(),
						0,
						area.getUuid(),
						area.getCaption(),
						diagramSeries.getFieldId(),
						diagramSeries.getFormId(),
						false));
			} else {
				regions.stream().forEach(regionReferenceDto -> {
					PopulationDataCriteria criteria = new PopulationDataCriteria();
					criteria.sexIsNull(true);
					criteria.region(regionReferenceDto);
					criteria.ageGroup(diagramSeriesTotal.getPopulationGroup());
					List<PopulationDataDto> populationDataDto = populationDataFacadeEjb.getPopulationData(criteria);
					Integer populationSum = 0;
					if (!populationDataDto.isEmpty()) {
						populationSum = populationDataDto.stream().mapToInt(e -> e.getPopulation()).sum();
						resultData.add(
							new CampaignDiagramDataDto(
								regionReferenceDto.getCaption(),
								populationSum,
								regionReferenceDto.getUuid(),
								regionReferenceDto.getCaption(),
								diagramSeries.getFieldId(),
								diagramSeries.getFormId(),
								true));
					} else {
						resultData.add(
							new CampaignDiagramDataDto(
								regionReferenceDto.getCaption(),
								0,
								regionReferenceDto.getUuid(),
								regionReferenceDto.getCaption(),
								diagramSeries.getFieldId(),
								diagramSeries.getFormId(),
								false));
					}
				});
			}
		} else if (Objects.isNull(district)) {

			List<DistrictReferenceDto> districts = districtService.getAllActiveByRegion(regionService.getByUuid(region.getUuid()))
				.stream()
				.map(district1 -> new DistrictReferenceDto(district1.getUuid(), district1.getName(), district1.getExternalID()))
				.collect(Collectors.toList());
			if (districts.isEmpty()) {
				resultData.add(
					new CampaignDiagramDataDto(
						region.getCaption(),
						0,
						region.getUuid(),
						region.getCaption(),
						diagramSeries.getFieldId(),
						diagramSeries.getFormId(),
						false));
			} else {
				districts.stream().forEach(districtReferenceDto -> {
					PopulationDataCriteria criteria = new PopulationDataCriteria();
					criteria.sexIsNull(true);
					criteria.district(districtReferenceDto);
					criteria.region(region);
					criteria.ageGroup(diagramSeriesTotal.getPopulationGroup());
					List<PopulationDataDto> populationDataDtoList = populationDataFacadeEjb.getPopulationData(criteria);
					Integer populationSum = 0;
					if (!populationDataDtoList.isEmpty()) {
						populationSum = populationDataDtoList.stream().mapToInt(e -> e.getPopulation()).sum();
						resultData.add(
							new CampaignDiagramDataDto(
								districtReferenceDto.getCaption(),
								populationSum,
								districtReferenceDto.getUuid(),
								districtReferenceDto.getCaption(),
								diagramSeries.getFieldId(),
								diagramSeries.getFormId(),
								true));
					} else {
						resultData.add(
							new CampaignDiagramDataDto(
								districtReferenceDto.getCaption(),
								populationSum,
								districtReferenceDto.getUuid(),
								districtReferenceDto.getCaption(),
								diagramSeries.getFieldId(),
								diagramSeries.getFormId(),
								false));
					}
				});
			}
		} else {
			resultData.add(
				new CampaignDiagramDataDto(
					district.getCaption(),
					0,
					district.getUuid(),
					district.getCaption(),
					diagramSeries.getFieldId(),
					diagramSeries.getFormId(),
					true));
		}
		return resultData;
	}

	@Override
	public List<CampaignDiagramDataDto> getDiagramData(List<CampaignDiagramSeries> diagramSeries, CampaignDiagramCriteria campaignDiagramCriteria) {

		List<CampaignDiagramDataDto> resultData = new ArrayList<>();
		final AreaReferenceDto area = campaignDiagramCriteria.getArea();
		final RegionReferenceDto region = campaignDiagramCriteria.getRegion();
		final DistrictReferenceDto district = campaignDiagramCriteria.getDistrict();
		final CampaignReferenceDto campaign = campaignDiagramCriteria.getCampaign();

		for (CampaignDiagramSeries series : diagramSeries) {
			//@formatter:off

				final String areaFilter = area != null ? " AND " + Area.TABLE_NAME + "." + Area.UUID + " = '" + area.getUuid() + "'" : "";
				final String regionFilter = region != null ? " AND " + CampaignFormData.REGION + "." + Region.UUID + " = '" + region.getUuid() + "'" : "";
				final String districtFilter = district != null ? " AND " + CampaignFormData.DISTRICT + "." + District.UUID + " = '" + district.getUuid() + "'" : "";
				final String campaignFilter = campaign != null ? " AND " + Campaign.TABLE_NAME + "." + Campaign.UUID + " = '" + campaign.getUuid() + "'" : "";
				//@formatter:on

			// SELECT
			StringBuilder selectBuilder = new StringBuilder("SELECT ").append(CampaignFormMeta.TABLE_NAME)
				.append(".")
				.append(CampaignFormMeta.UUID)
				.append(" as formUuid,")
				.append(CampaignFormMeta.TABLE_NAME)
				.append(".")
				.append(CampaignFormMeta.FORM_ID)
				.append(" as formId");

			if (series.getFieldId() != null) {
				selectBuilder.append(", jsonData->>'")
					.append(CampaignFormDataEntry.ID)
					.append("' as fieldId, jsonMeta->>'")
					.append(CampaignFormElement.CAPTION)
					.append("' as fieldCaption,")
					.append("CASE WHEN (jsonMeta ->> '")
					.append(CampaignFormElement.TYPE)
					.append("') = '")
					.append(CampaignFormElementType.NUMBER.toString())
					.append("' THEN sum(cast_to_int(jsonData->>'")
					.append(CampaignFormDataEntry.VALUE)
					.append("', 0)) ELSE sum(CASE WHEN(jsonData->>'")
					.append(CampaignFormDataEntry.VALUE)
					.append("') = '")
					.append(series.getReferenceValue())
					.append("' THEN 1 ELSE 0 END) END as sumValue,");
			} else {
				selectBuilder.append(", null as fieldId, null as fieldCaption, count(formId) as sumValue,");
			}

			final String jurisdictionGrouping;
			switch (campaignDiagramCriteria.getCampaignJurisdictionLevelGroupBy()) {
			case REGION:
				appendInfrastructureSelection(selectBuilder, Region.TABLE_NAME, Region.NAME);
				break;
			case DISTRICT:
				appendInfrastructureSelection(selectBuilder, District.TABLE_NAME, District.NAME);
				break;
			case COMMUNITY:
				appendInfrastructureSelection(selectBuilder, Community.TABLE_NAME, Community.NAME);
				break;
			case AREA:
			default:
				appendInfrastructureSelection(selectBuilder, Area.TABLE_NAME, Area.NAME);
			}

			// JOINS
			StringBuilder joinBuilder = new StringBuilder(" LEFT JOIN ").append(CampaignFormMeta.TABLE_NAME)
				.append(" ON ")
				.append(CampaignFormData.CAMPAIGN_FORM_META)
				.append("_id = ")
				.append(CampaignFormMeta.TABLE_NAME)
				.append(".")
				.append(CampaignFormMeta.ID)
				.append(" LEFT JOIN ")
				.append(Region.TABLE_NAME)
				.append(" ON ")
				.append(CampaignFormData.REGION)
				.append("_id =")
				.append(Region.TABLE_NAME)
				.append(".")
				.append(Region.ID)
				.append(" LEFT JOIN ")
				.append(Area.TABLE_NAME)
				.append(" ON ")
				.append(Region.AREA)
				.append("_id = ")
				.append(Area.TABLE_NAME)
				.append(".")
				.append(Area.ID)
				.append(" LEFT JOIN ")
				.append(District.TABLE_NAME)
				.append(" ON ")
				.append(CampaignFormData.DISTRICT)
				.append("_id = ")
				.append(District.TABLE_NAME)
				.append(".")
				.append(District.ID)
				.append(" LEFT JOIN ")
				.append(Community.TABLE_NAME)
				.append(" ON ")
				.append(CampaignFormData.COMMUNITY)
				.append("_id = ")
				.append(Community.TABLE_NAME)
				.append(".")
				.append(Community.ID)
				.append(" LEFT JOIN ")
				.append(Campaign.TABLE_NAME)
				.append(" ON ")
				.append(CampaignFormData.CAMPAIGN)
				.append("_id = ")
				.append(Campaign.TABLE_NAME)
				.append(".")
				.append(Campaign.ID);

			if (series.getFieldId() != null) {
				joinBuilder.append(", json_array_elements(")
					.append(CampaignFormData.FORM_VALUES)
					.append(") as jsonData, json_array_elements(")
					.append(CampaignFormMeta.CAMPAIGN_FORM_ELEMENTS)
					.append(") as jsonMeta");
			}

			// WHERE
			StringBuilder whereBuilder =
				new StringBuilder(" WHERE ").append(CampaignFormMeta.TABLE_NAME).append(".").append(CampaignFormMeta.FORM_ID).append(" = ?0");

			if (series.getFieldId() != null) {
				whereBuilder.append(" AND jsonData->>'")
					.append(CampaignFormDataEntry.ID)
					.append("' = ?1")
					.append(" AND jsonData->>'")
					.append(CampaignFormDataEntry.VALUE)
					.append("' IS NOT NULL AND jsonData->>'")
					.append(CampaignFormDataEntry.ID)
					.append("' = jsonMeta->>'")
					.append(CampaignFormElement.ID)
					.append("'");
			}

			whereBuilder.append(areaFilter).append(regionFilter).append(districtFilter).append(campaignFilter);

			// GROUP BY
			StringBuilder groupByBuilder = new StringBuilder(" GROUP BY ").append(CampaignFormMeta.TABLE_NAME)
				.append(".")
				.append(CampaignFormMeta.UUID)
				.append(",")
				.append(CampaignFormMeta.TABLE_NAME)
				.append(".")
				.append(CampaignFormMeta.FORM_ID);

			if (series.getFieldId() != null) {
				groupByBuilder.append(", jsonData->>'")
					.append(CampaignFormDataEntry.ID)
					.append("', jsonMeta->>'")
					.append(CampaignFormElement.CAPTION)
					.append("', jsonMeta->>'")
					.append(CampaignFormElement.TYPE)
					.append("'");
			}

			switch (campaignDiagramCriteria.getCampaignJurisdictionLevelGroupBy()) {
			case REGION:
				jurisdictionGrouping = ", " + Region.TABLE_NAME + "." + Region.UUID + ", " + Region.TABLE_NAME + "." + Region.NAME;
				break;
			case DISTRICT:
				jurisdictionGrouping = ", " + District.TABLE_NAME + "." + District.UUID + ", " + District.TABLE_NAME + "." + District.NAME;
				break;
			case COMMUNITY:
				jurisdictionGrouping = ", " + Community.TABLE_NAME + "." + Community.UUID + ", " + Community.TABLE_NAME + "." + Community.NAME;
				break;
			case AREA:
			default:
				jurisdictionGrouping = ", " + Area.TABLE_NAME + "." + Area.UUID + ", " + Area.TABLE_NAME + "." + Area.NAME;
			}

			groupByBuilder.append(jurisdictionGrouping);

			//@formatter:off
			Query seriesDataQuery = em.createNativeQuery(
					selectBuilder.toString() + " FROM " + CampaignFormData.TABLE_NAME + joinBuilder + whereBuilder + groupByBuilder);
			//@formatter:on

			seriesDataQuery.setParameter(0, series.getFormId());

			if (series.getFieldId() != null) {
				seriesDataQuery.setParameter(1, series.getFieldId());
			}

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = seriesDataQuery.getResultList();

			resultData.addAll(
				resultList.stream()
					.map(
						(result) -> new CampaignDiagramDataDto(
							(String) result[0],
							(String) result[1],
							(String) result[2],
							(String) result[3],
							(Number) result[4],
							(String) result[5],
							(String) result[6],
							series.getStack()))
					.collect(Collectors.toList()));
		}

		return resultData;
	}

	private void appendInfrastructureSelection(StringBuilder sb, String tableNameField, String nameField) {
		sb.append(tableNameField).append(".").append(AbstractDomainObject.UUID).append(", ").append(tableNameField).append(".").append(nameField);
	}

	@Override
	public long count(CampaignFormDataCriteria criteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<CampaignFormData> root = cq.from(CampaignFormData.class);

		Predicate filter = CriteriaBuilderHelper
			.and(cb, campaignFormDataService.createCriteriaFilter(criteria, cb, root), campaignFormDataService.createUserFilter(cb, cq, root));
		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.count(root));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public void overwriteCampaignFormData(CampaignFormDataDto existingData, CampaignFormDataDto newData) {
		DtoHelper.copyDtoValues(existingData, newData, true);
		saveCampaignFormData(existingData);
	}

	private CampaignFormDataReferenceDto toReferenceDto(CampaignFormData source) {
		if (source == null) {
			return null;
		}

		return source.toReference();
	}

	@LocalBean
	@Stateless
	public static class CampaignFormDataFacadeEjbLocal extends CampaignFormDataFacadeEjb {
	}
}
