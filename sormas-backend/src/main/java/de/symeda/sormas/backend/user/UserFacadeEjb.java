package de.symeda.sormas.backend.user;

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

	@Override
	public List<ReferenceDto> getListAsReference(UserRole userRole) {
		return us.getListByUserRole(userRole).stream()
				.map(f -> DtoHelper.toReferenceDto(f))
				.collect(Collectors.toList());
	}
	
	
	@Override
	public List<UserDto> getAll(UserRole userRole) {
		return us.getListByUserRole(userRole).stream()
				.map(f -> toDto(f))
				.collect(Collectors.toList());
	}

	private UserDto toDto(User user) {
		UserDto dto = new UserDto();
		dto.setActive(user.isAktiv());
		dto.setUserName(user.getUserName());
		dto.setFirstName(user.getFirstName());
		dto.setLastName(user.getLastName());
		dto.setUserEmail(user.getUserEmail());
		dto.setPhone(user.getPhone());
		dto.setAddress(LocationFacadeEjb.toLocationDto(user.getAddress()));
		return dto;
	}

	@Override
	public UserDto getByUuid(String uuid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserDto saveUser(UserDto dto) {
		// TODO Auto-generated method stub
		return null;
	}
}
