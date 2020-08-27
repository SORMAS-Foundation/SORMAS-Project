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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
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
import de.symeda.sormas.api.campaign.form.CampaignFormMetaDto;
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
	private CampaignFormMetaFacadeEjb.CampaignFormMetaFacadeEjbLocal campaignFormMetaFacade;

	@EJB
	private RegionService regionService;

	@EJB
	private DistrictService districtService;

	@EJB
	private CommunityService communityService;

	@EJB
	private UserService userService;

	public CampaignFormData fromDto(@NotNull CampaignFormDataDto source) {
		CampaignFormData target = campaignFormDataService.getByUuid(source.getUuid());
		if (target == null) {
			target = new CampaignFormData();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}

		DtoHelper.validateDto(source, target);

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

		CampaignFormData campaignFormData = fromDto(campaignFormDataDto);
		campaignFormDataService.ensurePersisted(campaignFormData);
		return toDto(campaignFormData);
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

		Predicate filter = campaignFormDataService.createCriteriaFilter(criteria, cb, root);
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
	public List<CampaignDiagramDataDto> getDiagramData(
		List<CampaignDiagramSeries> diagramSeriesList,
		CampaignDiagramCriteria campaignDiagramCriteria) {

		List<CampaignDiagramDataDto> resultData = new ArrayList<CampaignDiagramDataDto>();

		for (CampaignDiagramSeries diagramSeries : diagramSeriesList) {

			// TODO check data type of field
			// - int: as-is
			// - yes-no/other: CampaignDiagramSeries.fieldValue should be defined -> count the number of form data that match the value

			//@formatter:off
			final RegionReferenceDto region = campaignDiagramCriteria.getRegion();
			final DistrictReferenceDto district = campaignDiagramCriteria.getDistrict();
			final CampaignReferenceDto campaign = campaignDiagramCriteria.getCampaign();
			final String regionFilter = region != null ? " AND " + CampaignFormData.REGION + "." + Region.UUID + " = '" + region.getUuid() + "'": "";
			final String districtFilter = district != null ? " AND " + CampaignFormData.DISTRICT + "." + District.UUID + " = '" + district.getUuid() + "'" : "";
			final String campaignFilter = campaign != null ? " AND " + Campaign.TABLE_NAME + "." + Campaign.UUID + " = '" + campaign.getUuid() +  "'" : "";
			final String jurisdictionGrouping =
					district != null ? ", " + Community.TABLE_NAME + "." + Community.UUID + ", " + Community.TABLE_NAME + "." + Community.NAME :
					region != null ? ", " + District.TABLE_NAME + "." + District.UUID + ", " + District.TABLE_NAME + "." + District.NAME : "";
			Query seriesDataQuery = em.createNativeQuery(
					"SELECT " + CampaignFormMeta.TABLE_NAME + "." + CampaignFormMeta.UUID  + " as campaignFormUuid," + CampaignFormMeta.TABLE_NAME + "." + CampaignFormMeta.FORM_ID
							+ ", jsonData->>'" + CampaignFormDataEntry.ID + "'"
							+ ", sum((jsonData->>'" + CampaignFormDataEntry.VALUE + "')\\:\\:int)"
							+ ", " + Region.TABLE_NAME + "." + Region.UUID + ", " + Region.TABLE_NAME + "." + Region.NAME
							+ " FROM " + CampaignFormData.TABLE_NAME
							+ " LEFT JOIN " + CampaignFormMeta.TABLE_NAME + " ON " + CampaignFormData.CAMPAIGN_FORM_META + "_id = " + CampaignFormMeta.TABLE_NAME + "." + CampaignFormMeta.ID
							+ " LEFT JOIN " + Region.TABLE_NAME + " ON " + CampaignFormData.REGION + "_id = " + Region.TABLE_NAME + "." + Region.ID
							+ " LEFT JOIN " + District.TABLE_NAME + " ON " + CampaignFormData.DISTRICT + "_id = " + District.TABLE_NAME + "." + District.ID
							+ " LEFT JOIN " + Campaign.TABLE_NAME + " ON " + CampaignFormData.CAMPAIGN + "_id = " + Campaign.TABLE_NAME + "." + Campaign.ID
							+ ", json_array_elements(" + CampaignFormData.FORM_VALUES + ") jsonData"
							+ " WHERE " + CampaignFormMeta.TABLE_NAME + "." + CampaignFormMeta.FORM_ID + " = '" + diagramSeries.getFormId() + "'"
							+ " AND jsonData->>'" + CampaignFormDataEntry.ID + "' = '" + diagramSeries.getFieldId() + "'"
							+ " AND jsonData->>'" + CampaignFormDataEntry.VALUE + "' IS NOT NULL"
							+ regionFilter
							+ districtFilter
							+ campaignFilter
							+ " GROUP BY " + CampaignFormMeta.TABLE_NAME + "." + CampaignFormMeta.UUID  + "," + CampaignFormMeta.TABLE_NAME + "." + CampaignFormMeta.FORM_ID
							+ ", jsonData->>'" + CampaignFormDataEntry.ID + "'"
							+ jurisdictionGrouping
							+ ", " + Region.TABLE_NAME + "." + Region.UUID + ", " + Region.TABLE_NAME + "." + Region.NAME);
			//@formatter:on

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = seriesDataQuery.getResultList();

			resultData.addAll(
				resultList.stream()
					.map(
						(result) -> new CampaignDiagramDataDto(
							(String) result[0],
							(String) result[1],
							(String) result[2],
							(Object) result[3],
							(String) result[4],
							(String) result[5]))
					.collect(Collectors.toList()));
		}

		// TODO: 21/08/2020 extract caption directly from json with query 
		resultData.forEach(campaignDiagramDataDto -> {
			final CampaignFormMetaDto formMeta = campaignFormMetaFacade.getCampaignFormMetaByUuid(campaignDiagramDataDto.getFormMetaUuid());
			final String fieldId = campaignDiagramDataDto.getFieldId();
			final Optional<CampaignFormElement> optionalCampaignFormElement =
				formMeta.getCampaignFormElements().stream().filter(campaignFormElement -> campaignFormElement.getId().equals(fieldId)).findFirst();
			if (optionalCampaignFormElement.isPresent()) {

				campaignDiagramDataDto.setFieldCaption(optionalCampaignFormElement.get().getCaption());
			} else {
				campaignDiagramDataDto.setFieldCaption(fieldId);
			}
		});
		return resultData;
	}

	@Override
	public long count(CampaignFormDataCriteria criteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<CampaignFormData> root = cq.from(CampaignFormData.class);

		Predicate filter = campaignFormDataService.createCriteriaFilter(criteria, cb, root);
		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.count(root));
		return em.createQuery(cq).getSingleResult();
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
