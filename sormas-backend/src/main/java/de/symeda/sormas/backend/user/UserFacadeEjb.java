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
import de.symeda.sormas.backend.location.LocationFacadeEjb;
import de.symeda.sormas.backend.location.LocationFacadeEjb.LocationFacadeEjbLocal;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "UserFacade")
public class UserFacadeEjb implements UserFacade {
	
	@EJB
	private UserService service;
	@EJB
	private LocationFacadeEjbLocal locationFacade;

	@Override
	public List<UserReferenceDto> getAllAsReference(UserRole userRole) {
		return service.getAllByUserRoles(userRole).stream()
				.map(f -> toReferenceDto(f))
				.collect(Collectors.toList());
	}
	
	
	@Override
	public List<UserReferenceDto> getAssignableUsers(UserReferenceDto assigningUser) {
		User user = service.getByReferenceDto(assigningUser);
		
		if (user != null && user.getRegion() != null) {
			return service.getAllByRegion(user.getRegion()).stream()
					.map(f -> toReferenceDto(f))
					.collect(Collectors.toList());
		}
		
		return Collections.emptyList();
	}


	@Override
	public List<UserDto> getAll(UserRole... userRoles) {
		return service.getAllByUserRoles(userRoles).stream()
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
	
	public UserDto toDto(User entity) {
		UserDto dto = new UserDto();
		DtoHelper.fillDto(dto, entity);
		
		dto.setActive(entity.isAktiv());
		dto.setUserName(entity.getUserName());
		dto.setFirstName(entity.getFirstName());
		dto.setLastName(entity.getLastName());
		dto.setUserEmail(entity.getUserEmail());
		dto.setPhone(entity.getPhone());
		dto.setAddress(LocationFacadeEjb.toLocationDto(entity.getAddress()));
		
		dto.setAssociatedOfficer(toReferenceDto(entity.getAssociatedOfficer()));
		
		entity.getUserRoles().size();
		dto.setUserRoles(entity.getUserRoles());
		return dto;
	}
	
	public UserReferenceDto toReferenceDto(User entity) {
		if (entity == null) {
			return null;
		}
		UserReferenceDto dto = new UserReferenceDto();
		DtoHelper.fillReferenceDto(dto, entity);
		return dto;
	}	
	
	private User toUser(UserDto dto) {
		User bo = service.getByUuid(dto.getUuid());
		if(bo==null) {
			bo = service.createUser();
		}
		bo.setUuid(dto.getUuid());

		bo.setAktiv(dto.isActive());
		bo.setFirstName(dto.getFirstName());
		bo.setLastName(dto.getLastName());
		bo.setPhone(dto.getPhone());
		bo.setAddress(locationFacade.fromLocationDto(dto.getAddress()));
		
		bo.setUserName(dto.getUserName());
		bo.setUserEmail(dto.getUserEmail());
		
		bo.setAssociatedOfficer(service.getByReferenceDto(dto.getAssociatedOfficer()));

		bo.setUserRoles(dto.getUserRoles());

		return bo;
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
