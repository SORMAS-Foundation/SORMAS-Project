package de.symeda.sormas.backend.user;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserFacade;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.event.EventService;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.location.LocationFacadeEjb;
import de.symeda.sormas.backend.location.LocationFacadeEjb.LocationFacadeEjbLocal;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.DistrictFacadeEjb;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.region.RegionFacadeEjb;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "UserFacade")
public class UserFacadeEjb implements UserFacade {
	
	@EJB
	private UserService userService;
	@EJB
	private LocationFacadeEjbLocal locationFacade;
	@EJB
	private RegionService regionService;
	@EJB
	private DistrictService districtService;
	@EJB
	private FacilityService facilityService;
	@EJB
	private CaseService caseService;
	@EJB
	private ContactService contactService;
	@EJB
	private EventService eventService;
	
	@Override
	public List<UserReferenceDto> getAssignableUsersByRegion(RegionReferenceDto regionRef, UserRole ...assignableRoles) {
		Region region = regionService.getByReferenceDto(regionRef);
		
		return userService.getAllByRegionAndUserRoles(region, assignableRoles).stream()
				.map(f -> toReferenceDto(f))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<UserReferenceDto> getAssignableUsersByDistrict(DistrictReferenceDto districtRef, boolean includeSupervisors, UserRole ...assignableRoles) {
		District district = districtService.getByReferenceDto(districtRef);
		
		return userService.getAllByDistrict(district, includeSupervisors, assignableRoles).stream()
				.map(f -> toReferenceDto(f))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<UserReferenceDto> getForWeeklyReportDetails(DistrictReferenceDto districtRef) {
		District district = districtService.getByReferenceDto(districtRef);
		
		return userService.getForWeeklyReportDetails(district).stream()
				.map(u -> toReferenceDto(u))
				.collect(Collectors.toList());
	}

	@Override
	public List<UserDto> getAll(UserRole... roles) {
		
		//TODO user region of the current user
		
		return userService.getAllByRegionAndUserRoles(null, roles).stream()
				.map(f -> toDto(f))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<UserDto> getAllAfter(Date date) {
		return userService.getAllAfter(date, null).stream()
			.map(c -> toDto(c))
			.collect(Collectors.toList());
	}
	
	@Override
	public List<UserReferenceDto> getAllAfterAsReference(Date date) {
		return userService.getAllAfter(date, null).stream()
			.map(c -> toReferenceDto(c))
			.collect(Collectors.toList());
	}

	@Override
	public UserDto getByUuid(String uuid) {
		return toDto(userService.getByUuid(uuid));
	}
	
	@Override
	public UserDto getByUserName(String userName) {
		return toDto(userService.getByUserName(userName));
	}
	
	@Override
	public UserReferenceDto getByUserNameAsReference(String userName) {
		return toReferenceDto(userService.getByUserName(userName));
	}

	@Override
	public UserDto saveUser(UserDto dto) {
		User user = fromDto(dto);
		
		userService.ensurePersisted(user);
		
		return toDto(user);
	}
	
	@Override
	public int getNumberOfInformantsByFacility(FacilityReferenceDto facilityRef) {
		Facility facility = facilityService.getByReferenceDto(facilityRef);
		
		return (int) userService.getNumberOfInformantsByFacility(facility);
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
		dto.setAddress(LocationFacadeEjb.toDto(entity.getAddress()));
		
		dto.setRegion(RegionFacadeEjb.toReferenceDto(entity.getRegion()));
		dto.setDistrict(DistrictFacadeEjb.toReferenceDto(entity.getDistrict()));
		dto.setHealthFacility(FacilityFacadeEjb.toReferenceDto(entity.getHealthFacility()));
		dto.setAssociatedOfficer(toReferenceDto(entity.getAssociatedOfficer()));
		dto.setLaboratory(FacilityFacadeEjb.toReferenceDto(entity.getLaboratory()));
		
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
	
	private User fromDto(UserDto source) {
		
		User target = userService.getByUuid(source.getUuid());
		if (target==null) {
			target = userService.createUser();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}
		DtoHelper.validateDto(source, target);

		target.setAktiv(source.isActive());
		target.setFirstName(source.getFirstName());
		target.setLastName(source.getLastName());
		target.setPhone(source.getPhone());
		target.setAddress(locationFacade.fromDto(source.getAddress()));
		
		target.setUserName(source.getUserName());
		target.setUserEmail(source.getUserEmail());
		
		target.setRegion(regionService.getByReferenceDto(source.getRegion()));
		target.setDistrict(districtService.getByReferenceDto(source.getDistrict()));
		target.setHealthFacility(facilityService.getByReferenceDto(source.getHealthFacility()));
		target.setAssociatedOfficer(userService.getByReferenceDto(source.getAssociatedOfficer()));
		target.setLaboratory(facilityService.getByReferenceDto(source.getLaboratory()));

		target.setUserRoles(source.getUserRoles());

		return target;
	}
	
	@Override
	public boolean isLoginUnique(String uuid, String userName) {
		return userService.isLoginUnique(uuid, userName);
	}


	@Override
	public String resetPassword(String uuid) {
		return userService.resetPassword(uuid);
	}
	
	@LocalBean
	@Stateless
	public static class UserFacadeEjbLocal extends UserFacadeEjb {
	}
}
