/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package de.symeda.sormas.backend.infrastructure.community;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.community.CommunityCriteria;
import de.symeda.sormas.api.infrastructure.community.CommunityDto;
import de.symeda.sormas.api.infrastructure.community.CommunityFacade;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.AbstractInfrastructureFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb.DistrictFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.district.DistrictService;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "CommunityFacade")
@RightsAllowed(UserRight._INFRASTRUCTURE_VIEW)
public class CommunityFacadeEjb
	extends AbstractInfrastructureFacadeEjb<Community, CommunityDto, CommunityDto, CommunityReferenceDto, CommunityService, CommunityCriteria>
	implements CommunityFacade {

	@EJB
	private DistrictFacadeEjbLocal districtFacade;
	@EJB
	private DistrictService districtService;

	public CommunityFacadeEjb() {
	}

	@Inject
	protected CommunityFacadeEjb(CommunityService service, FeatureConfigurationFacadeEjbLocal featureConfiguration) {
		super(
			Community.class,
			CommunityDto.class,
			service,
			featureConfiguration,
			Validations.importCommunityAlreadyExists,
			Strings.messageCommunityArchivingNotPossible,
			Strings.messageCommunityDearchivingNotPossible);
	}

	@Override
	@PermitAll
	public List<CommunityReferenceDto> getAllActiveByDistrict(String districtUuid) {
		return toRefDtos(districtService.getByUuid(districtUuid).getCommunities().stream().filter(c -> !c.isArchived()));
	}

	@Override
	public List<CommunityDto> getIndexList(CommunityCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {
		return toDtos(service.getIndexList(criteria, first, max, sortProperties).stream());
	}

	public Page<CommunityDto> getIndexPage(CommunityCriteria communityCriteria, Integer offset, Integer size, List<SortProperty> sortProperties) {
		List<CommunityDto> communityList = getIndexList(communityCriteria, offset, size, sortProperties);
		long totalElementCount = count(communityCriteria);
		return new Page<>(communityList, offset, size, totalElementCount);
	}

	@Override
	@RightsAllowed(UserRight._STATISTICS_ACCESS)
	public CommunityReferenceDto getCommunityReferenceById(long id) {
		return toReferenceDto(service.getById(id));
	}

	@Override
	@RightsAllowed(UserRight._STATISTICS_ACCESS)
	public Map<String, String> getDistrictUuidsForCommunities(List<CommunityReferenceDto> communities) {
		return service.getDistrictUuidsForCommunities(communities);
	}

	@Override
	protected List<Community> findDuplicates(CommunityDto dto, boolean includeArchived) {
		// todo this does not work in edge cases (see #6752) as names are not guaranteed to be unique or and collide
		//  it would be really good to use external ID here but it is not unique in the DB
		return service.getByName(dto.getName(), districtService.getByReferenceDto(dto.getDistrict()), includeArchived);
	}

	@Override
	@PermitAll
	public List<CommunityReferenceDto> getByName(String name, DistrictReferenceDto districtRef, boolean includeArchivedEntities) {
		return toRefDtos(service.getByName(name, districtService.getByReferenceDto(districtRef), includeArchivedEntities).stream());
	}

	@Override
	@PermitAll
	public List<CommunityReferenceDto> getReferencesByExternalId(String externalId, boolean includeArchivedEntities) {
		return toRefDtos(service.getByExternalId(externalId, includeArchivedEntities).stream());
	}

	@Override
	@PermitAll
	public List<CommunityReferenceDto> getReferencesByName(String name, boolean includeArchived) {
		return getByName(name, null, false);
	}

	@Override
	public boolean isUsedInOtherInfrastructureData(Collection<String> communityUuids) {
		return service.isUsedInInfrastructureData(communityUuids, Facility.COMMUNITY, Facility.class);
	}

	@Override
	public boolean hasArchivedParentInfrastructure(Collection<String> communityUuids) {
		return service.hasArchivedParentInfrastructure(communityUuids);
	}

	@Override
	public CommunityDto toDto(Community entity) {

		if (entity == null) {
			return null;
		}
		CommunityDto dto = new CommunityDto();
		DtoHelper.fillDto(dto, entity);

		dto.setName(entity.getName());
		dto.setGrowthRate(entity.getGrowthRate());
		dto.setDistrict(DistrictFacadeEjb.toReferenceDto(entity.getDistrict()));
		dto.setRegion(RegionFacadeEjb.toReferenceDto(entity.getDistrict().getRegion()));
		dto.setExternalID(entity.getExternalID());
		applyToDtoInheritance(dto, entity);

		return dto;
	}

	@Override
	protected CommunityReferenceDto toRefDto(Community community) {
		return toReferenceDto(community);
	}

	public static CommunityReferenceDto toReferenceDto(Community entity) {

		if (entity == null) {
			return null;
		}
		return new CommunityReferenceDto(entity.getUuid(), entity.getName(), entity.getExternalID());
	}

	@Override
	protected Community fillOrBuildEntity(@NotNull CommunityDto source, Community target, boolean checkChangeDate, boolean allowUuidOverwrite) {
		target = DtoHelper.fillOrBuildEntity(source, target, Community::new, checkChangeDate, allowUuidOverwrite);

		target.setName(source.getName());
		target.setGrowthRate(source.getGrowthRate());
		target.setDistrict(districtService.getByReferenceDto(source.getDistrict()));
		target.setExternalID(source.getExternalID());
		applyFillOrBuildEntityInheritance(target, source);

		return target;
	}

	@Override
	protected boolean checkDefaultEligible(CommunityDto dto) {
		return dto.getDistrict().equals(districtFacade.getDefaultInfrastructureReference());
	}

	@LocalBean
	@Stateless
	public static class CommunityFacadeEjbLocal extends CommunityFacadeEjb {

		public CommunityFacadeEjbLocal() {
		}

		@Inject
		protected CommunityFacadeEjbLocal(CommunityService service, FeatureConfigurationFacadeEjbLocal featureConfiguration) {
			super(service, featureConfiguration);
		}
	}
}
