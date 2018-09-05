package de.symeda.sormas.backend.region;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.region.CommunityCriteria;
import de.symeda.sormas.api.region.CommunityDto;
import de.symeda.sormas.api.region.CommunityFacade;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "CommunityFacade")
public class CommunityFacadeEjb implements CommunityFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	protected EntityManager em;

	@EJB
	private CommunityService communityService;
	@EJB
	private UserService userService;
	@EJB
	private DistrictService districtService;

	@Override
	public List<CommunityReferenceDto> getAllByDistrict(String districtUuid) {
		
		District district = districtService.getByUuid(districtUuid);
		
		return district.getCommunities().stream()
				.map(f -> toReferenceDto(f))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<CommunityDto> getAllAfter(Date date) {
		return communityService.getAllAfter(date, null).stream()
			.map(c -> toDto(c))
			.collect(Collectors.toList());
	}

	@Override
	public List<CommunityDto> getIndexList(CommunityCriteria criteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Community> cq = cb.createQuery(Community.class);
		Root<Community> community = cq.from(Community.class);
		Join<Community, District> district = community.join(Community.DISTRICT, JoinType.LEFT);
		Join<District, Region> region = district.join(District.REGION, JoinType.LEFT);

		if (criteria != null) {
			Predicate filter = communityService.buildCriteriaFilter(criteria, cb, community);
			if (filter != null) {
				cq.where(filter);
			}
		}
		
		cq.select(community);
		cq.orderBy(cb.asc(region.get(Region.NAME)), cb.asc(district.get(District.NAME)));
		cq.distinct(true);
		
		List<Community> communities = em.createQuery(cq).getResultList();
		return communities.stream().map(c -> toDto(c)).collect(Collectors.toList());
	}
	
	@Override
	public List<String> getAllUuids(String userUuid) {
		
		User user = userService.getByUuid(userUuid);
		
		if (user == null) {
			return Collections.emptyList();
		}
		
		return communityService.getAllUuids(user);
	}
	
	@Override
	public CommunityDto getByUuid(String uuid) {
		return toDto(communityService.getByUuid(uuid));
	}
	
	@Override
	public List<CommunityDto> getByUuids(List<String> uuids) {
		return communityService.getByUuids(uuids)
				.stream()
				.map(c -> toDto(c))
				.collect(Collectors.toList());
	}
	
	@Override
	public CommunityReferenceDto getCommunityReferenceByUuid(String uuid) {
		return toReferenceDto(communityService.getByUuid(uuid));
	}
	
	@Override
	public void saveCommunity(CommunityDto dto) {
		Community community = communityService.getByUuid(dto.getUuid());
		
		community = fillOrBuildEntity(dto, community);
		
		if (community.getDistrict() == null) {
			throw new ValidationRuntimeException("You have to specify a valid district");
		}
		
		communityService.ensurePersisted(community);
	}
	
	public static CommunityReferenceDto toReferenceDto(Community entity) {
		if (entity == null) {
			return null;
		}
		CommunityReferenceDto dto = new CommunityReferenceDto(entity.getUuid(), entity.toString());
		return dto;
	}
	
	private CommunityDto toDto(Community entity) {
		if (entity == null) {
			return null;
		}
		CommunityDto dto = new CommunityDto();
		DtoHelper.fillDto(dto, entity);
		
		dto.setName(entity.getName());
		dto.setDistrict(DistrictFacadeEjb.toReferenceDto(entity.getDistrict()));

		return dto;
	}
	
	private Community fillOrBuildEntity(@NotNull CommunityDto source, Community target) {
		if (target == null) {
			target = new Community();
			target.setUuid(source.getUuid());
		}
		
		DtoHelper.validateDto(source, target);
		
		target.setName(source.getName());
		target.setDistrict(districtService.getByReferenceDto(source.getDistrict()));
		
		return target;
	}
	
	@LocalBean
	@Stateless
	public static class CommunityFacadeEjbLocal extends CommunityFacadeEjb {
	}
}
