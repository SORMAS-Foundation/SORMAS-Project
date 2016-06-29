package de.symeda.sormas.backend.user;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.user.UserFacade;
import de.symeda.sormas.api.user.UserRole;
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
}
