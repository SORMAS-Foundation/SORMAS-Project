package de.symeda.sormas.backend.user;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserFacade;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.location.LocationFacadeEjb;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "UserFacade")
public class UserFacadeEjb implements UserFacade {
	
	@EJB
	private UserService us;
	@EJB
	private LocationFacadeEjb locationFacade;

	@Override
	public List<ReferenceDto> getListAsReference(UserRole userRole) {
		return us.getListByUserRole(userRole).stream()
				.map(f -> DtoHelper.toReferenceDto(f))
				.collect(Collectors.toList());
	}
	
	
	@Override
	public List<UserDto> getAll(UserRole... userRole) {
		return us.getListByUserRole(userRole).stream()
				.map(f -> toDto(f))
				.collect(Collectors.toList());
	}

	@Override
	public UserDto getByUuid(String uuid) {
		return toDto(us.getByUuid(uuid));
	}

	@Override
	public UserDto saveUser(UserDto dto) {
		User user = toUser(dto);
		
		// TODO #24 remove this mock
		if(dto.getUserRoles().isEmpty()) {
			user.setUserRoles(new HashSet<UserRole>(Arrays.asList(UserRole.SURVEILLANCE_OFFICER)));
		}
		user.setPassword("");
		user.setSeed("");
		
		us.ensurePersisted(user);
		
		return toDto(user);
	}
	
	private UserDto toDto(User user) {
		UserDto dto = new UserDto();
		dto.setUuid(user.getUuid());
		dto.setActive(user.isAktiv());
		dto.setUserName(user.getUserName());
		dto.setFirstName(user.getFirstName());
		dto.setLastName(user.getLastName());
		dto.setUserEmail(user.getUserEmail());
		dto.setPhone(user.getPhone());
		dto.setAddress(LocationFacadeEjb.toLocationDto(user.getAddress()));
		
		user.getUserRoles().size();
		dto.setUserRoles(user.getUserRoles());
		return dto;
	}
	
	
	private User toUser(UserDto dto) {
		User bo = us.getByUuid(dto.getUuid());
		if(bo==null) {
			bo = us.createUser();
		}
		bo.setUuid(dto.getUuid());

		bo.setAktiv(dto.isActive());
		bo.setFirstName(dto.getFirstName());
		bo.setLastName(dto.getLastName());
		bo.setPhone(dto.getPhone());
		bo.setAddress(locationFacade.fromLocationDto(dto.getAddress()));
		
		bo.setUserName(dto.getUserName());
		bo.setUserEmail(dto.getUserEmail());
		
		bo.setUserRoles(dto.getUserRoles());

		return bo;
	}
	
	@Override
	public boolean isLoginUnique(String uuid, String userName) {
		return us.isLoginUnique(uuid, userName);
	}
}
