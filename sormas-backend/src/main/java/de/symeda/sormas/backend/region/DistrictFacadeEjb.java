package de.symeda.sormas.backend.region;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.api.region.DistrictFacade;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "DistrictFacade")
public class DistrictFacadeEjb implements DistrictFacade {

	@EJB
	private DistrictService districtService;
	@EJB
	private UserService userService;
	@EJB
	private RegionService regionService;

	@Override
	public List<DistrictReferenceDto> getAllAsReference() {
		return districtService.getAll(District.NAME, true).stream()
				.map(f -> toReferenceDto(f))
				.collect(Collectors.toList());
	}

	@Override
	public List<DistrictReferenceDto> getAllByRegion(String regionUuid) {
		
		Region region = regionService.getByUuid(regionUuid);
		
		return region.getDistricts().stream()
				.map(f -> toReferenceDto(f))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<DistrictDto> getAllAfter(Date date) {
		return districtService.getAllAfter(date, null).stream()
			.map(c -> toDto(c))
			.collect(Collectors.toList());
	}
	
	@Override
	public List<String> getAllUuids(String userUuid) {
		
		User user = userService.getByUuid(userUuid);
		
		if (user == null) {
			return Collections.emptyList();
		}
		
		return districtService.getAllUuids(user);
	}
	
	@Override	
	public int getCountByRegion(String regionUuid) {
		Region region = regionService.getByUuid(regionUuid);
		
		return districtService.getCountByRegion(region);
	}
	
	@Override
	public DistrictDto getDistrictByUuid(String uuid) {
		return toDto(districtService.getByUuid(uuid));
	}
	
	@Override
	public DistrictReferenceDto getDistrictReferenceByUuid(String uuid) {
		return toReferenceDto(districtService.getByUuid(uuid));
	}
	
	@Override
	public DistrictReferenceDto getDistrictReferenceById(int id) {
		return toReferenceDto(districtService.getById(id));
	}
	
	public static DistrictReferenceDto toReferenceDto(District entity) {
		if (entity == null) {
			return null;
		}
		DistrictReferenceDto dto = new DistrictReferenceDto(entity.getUuid(), entity.toString());
		return dto;
	}
	
	public static DistrictDto toDto(District entity) {
		if (entity == null) {
			return null;
		}
		DistrictDto dto = new DistrictDto();
		DtoHelper.fillDto(dto, entity);
		
		dto.setName(entity.getName());
		dto.setEpidCode(entity.getEpidCode());
		dto.setPopulation(entity.getPopulation());
		dto.setGrowthRate(entity.getGrowthRate());
		dto.setRegion(RegionFacadeEjb.toReferenceDto(entity.getRegion()));

		return dto;
	}	
	
	@LocalBean
	@Stateless
	public static class DistrictFacadeEjbLocal extends DistrictFacadeEjb	 {
	}
}
