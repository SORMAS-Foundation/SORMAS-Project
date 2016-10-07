package de.symeda.sormas.backend.user;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserFacade;
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
	public List<ReferenceDto> getListAsReference(UserRole userRole) {
		return service.getListByUserRoles(userRole).stream()
				.map(f -> DtoHelper.toReferenceDto(f))
				.collect(Collectors.toList());
	}
	
	
	@Override
	public List<UserDto> getAll(UserRole... userRoles) {
		return service.getListByUserRoles(userRoles).stream()
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
	public UserDto getByUuid(String uuid) {
		return toDto(service.getByUuid(uuid));
	}
	
	@Override
	public UserDto getByUserName(String userName) {
		return toDto(service.getByUserName(userName));
	}

	@Override
	public UserDto saveUser(UserDto dto) {
		User user = toUser(dto);
		
		service.ensurePersisted(user);
		
		return toDto(user);
	}
	
	private UserDto toDto(User entity) {
		UserDto dto = new UserDto();
		dto.setUuid(entity.getUuid());
		dto.setCreationDate(entity.getCreationDate());
		dto.setChangeDate(entity.getChangeDate());
		
		dto.setActive(entity.isAktiv());
		dto.setUserName(entity.getUserName());
		dto.setFirstName(entity.getFirstName());
		dto.setLastName(entity.getLastName());
		dto.setUserEmail(entity.getUserEmail());
		dto.setPhone(entity.getPhone());
		dto.setAddress(LocationFacadeEjb.toLocationDto(entity.getAddress()));
		
		dto.setAssociatedOfficer(DtoHelper.toReferenceDto(entity.getAssociatedOfficer()));
		
		entity.getUserRoles().size();
		dto.setUserRoles(entity.getUserRoles());
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
		
		bo.setAssociatedOfficer(DtoHelper.fromReferenceDto(dto.getAssociatedOfficer(), service));

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
}
