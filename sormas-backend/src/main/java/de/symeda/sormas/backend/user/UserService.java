package de.symeda.sormas.backend.user;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.backend.common.AbstractAdoService;

@Stateless
@LocalBean
public class UserService extends AbstractAdoService<User> {
	
	public UserService() {
		super(User.class);
	}
}
