package de.symeda.sormas.backend.user;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserFacade;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.location.LocationFacadeEjb;
import de.symeda.sormas.backend.location.LocationFacadeEjb.LocationFacadeEjbLocal;
import de.symeda.sormas.backend.region.DistrictFacadeEjb;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.RegionFacadeEjb;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "UserFacade")
public class UserFacadeEjb implements UserFacade {
	
	@EJB
	private UserService service;
	@EJB
	private LocationFacadeEjbLocal locationFacade;
	@EJB
	private RegionService regionService;
	@EJB
	private DistrictService districtService;
	@EJB
	private FacilityService facilityService;

	@Override
	public List<UserReferenceDto> getAssignableUsers(UserReferenceDto assigningUser, UserRole ...assignableRoles) {
		User user = service.getByReferenceDto(assigningUser);
		
		if (user != null && user.getRegion() != null) {
			return service.getAllByRegionAndUserRoles(user.getRegion(), assignableRoles).stream()
					.map(f -> toReferenceDto(f))
					.collect(Collectors.toList());
		}
		
		return Collections.emptyList();
	}

	@Override
	public List<UserDto> getAll(UserRole... roles) {
		
		//TODO user region of the current user
		
		return service.getAllByRegionAndUserRoles(null, roles).stream()
				.map(f -> toDto(f))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<UserDto> getAllAfter(Date date) {
		return service.getAllAfter(date).stream()
			.map(c -> toDto(c))
			.collect(Collectors.toList());
	}
	
	@Override
	public List<UserReferenceDto> getAllAfterAsReference(Date date) {
		return service.getAllAfter(date).stream()
			.map(c -> toReferenceDto(c))
			.collect(Collectors.toList());
	}

	@Override
	public UserDto getByUuid(String uuid) {
		return toDto(service.getByUuid(uuid));
	}
	
	@Override
	public UserDto getByUserName(String userName) {
		return toDto(service.getByUserName(userName));
	}
	
	@Override
	public UserReferenceDto getByUserNameAsReference(String userName) {
		return toReferenceDto(service.getByUserName(userName));
	}

	@Override
	public UserDto saveUser(UserDto dto) {
		User user = toUser(dto);
		
		service.ensurePersisted(user);
		
		return toDto(user);
	}
	
	public static UserDto toDto(User entity) {
		UserDto dto = new UserDto();
		DtoHelper.fillReferenceDto(dto, entity);
		
		dto.setActive(entity.isAktiv());
		dto.setUserName(entity.getUserName());
		dto.setFirstName(entity.getFirstName());
		dto.setLastName(entity.getLastName());
		dto.setUserEmail(entity.getUserEmail());
		dto.setPhone(entity.getPhone());
		dto.setAddress(LocationFacadeEjb.toLocationDto(entity.getAddress()));
		
		dto.setRegion(RegionFacadeEjb.toReferenceDto(entity.getRegion()));
		dto.setDistrict(DistrictFacadeEjb.toReferenceDto(entity.getDistrict()));
		dto.setHealthFacility(FacilityFacadeEjb.toReferenceDto(entity.getHealthFacility()));
		dto.setAssociatedOfficer(toReferenceDto(entity.getAssociatedOfficer()));
		
		entity.getUserRoles().size();
		dto.setUserRoles(entity.getUserRoles());
		return dto;
	}
	
	public static UserReferenceDto toReferenceDto(User entity) {
		if (entity == null) {
			return null;
		}
		UserReferenceDto dto = new UserReferenceDto();
		DtoHelper.fillReferenceDto(dto, entity);
		return dto;
	}	
	
	private User toUser(UserDto dto) {
		User entity = service.getByUuid(dto.getUuid());
		if(entity==null) {
			entity = service.createUser();
		}
		entity.setUuid(dto.getUuid());

		entity.setAktiv(dto.isActive());
		entity.setFirstName(dto.getFirstName());
		entity.setLastName(dto.getLastName());
		entity.setPhone(dto.getPhone());
		entity.setAddress(locationFacade.fromLocationDto(dto.getAddress()));
		
		entity.setUserName(dto.getUserName());
		entity.setUserEmail(dto.getUserEmail());
		
		entity.setRegion(regionService.getByReferenceDto(dto.getRegion()));
		entity.setDistrict(districtService.getByReferenceDto(dto.getDistrict()));
		entity.setHealthFacility(facilityService.getByReferenceDto(dto.getHealthFacility()));
		entity.setAssociatedOfficer(service.getByReferenceDto(dto.getAssociatedOfficer()));

		entity.setUserRoles(dto.getUserRoles());

		return entity;
	}
	
	@Override
	public boolean isLoginUnique(String uuid, String userName) {
		return service.isLoginUnique(uuid, userName);
	}


	@Override
	public String resetPassword(String uuid) {
		return service.resetPassword(uuid);
	}
	
	@LocalBean
	@Stateless
	public static class UserFacadeEjbLocal extends UserFacadeEjb {
	}
}
