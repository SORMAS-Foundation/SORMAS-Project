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

import de.symeda.sormas.api.campaign.data.CampaignFormDataCriteria;
import de.symeda.sormas.api.campaign.data.CampaignFormDataDto;
import de.symeda.sormas.api.campaign.data.CampaignFormDataFacade;
import de.symeda.sormas.api.campaign.data.CampaignFormDataIndexDto;
import de.symeda.sormas.api.campaign.data.CampaignFormDataReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.campaign.Campaign;
import de.symeda.sormas.backend.campaign.CampaignFacadeEjb;
import de.symeda.sormas.backend.campaign.CampaignService;
import de.symeda.sormas.backend.campaign.form.CampaignFormFacadeEjb;
import de.symeda.sormas.backend.campaign.form.CampaignFormService;
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
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Stateless(name = "CampaignFormDataFacade")
public class CampaignFormDataFacadeEjb implements CampaignFormDataFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private CampaignFormDataService campaignFormDataService;

	@EJB
	private CampaignService campaignService;

	@EJB
	private CampaignFormService campaignFormService;

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

		target.setFormValuesList(source.getFormValues());
		target.setCampaign(campaignService.getByReferenceDto(source.getCampaign()));
		target.setCampaignForm(campaignFormService.getByReferenceDto(source.getCampaignForm()));
		target.setFormDate(source.getFormDate());
		target.setRegion(regionService.getByReferenceDto(source.getRegion()));
		target.setDistrict(districtService.getByReferenceDto(source.getDistrict()));
		target.setCommunity(communityService.getByReferenceDto(source.getCommunity()));

		return target;
	}

	public CampaignFormDataDto toDto(CampaignFormData source) {
		if (source == null) {
			return null;
		}

		CampaignFormDataDto target = new CampaignFormDataDto();
		DtoHelper.fillDto(target, source);

		target.setFormValues(source.getFormValuesList());
		target.setCampaign(CampaignFacadeEjb.toReferenceDto(source.getCampaign()));
		target.setCampaignForm(CampaignFormFacadeEjb.toReferenceDto(source.getCampaignForm()));
		target.setFormDate(source.getFormDate());
		target.setRegion(RegionFacadeEjb.toReferenceDto(source.getRegion()));
		target.setDistrict(DistrictFacadeEjb.toReferenceDto(source.getDistrict()));
		target.setCommunity(CommunityFacadeEjb.toReferenceDto(source.getCommunity()));

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
		Join<CampaignFormData, Region> regionJoin = root.join(CampaignFormData.REGION, JoinType.LEFT);
		Join<CampaignFormData, District> districtJoin = root.join(CampaignFormData.DISTRICT, JoinType.LEFT);
		Join<CampaignFormData, Community> communityJoin = root.join(CampaignFormData.COMMUNITY, JoinType.LEFT);

		cq.multiselect(
			root.get(CampaignFormData.UUID),
			campaignJoin.get(Campaign.NAME),
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

		if (first != null && max != null) {
			return em.createQuery(cq).setFirstResult(first).setMaxResults(max).getResultList();
		} else {
			return em.createQuery(cq).getResultList();
		}
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
