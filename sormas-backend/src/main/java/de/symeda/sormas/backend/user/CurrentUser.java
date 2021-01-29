package de.symeda.sormas.backend.user;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;

@RequestScoped
// FIXME @TransactionScoped would be better for performance, but is not support by novatec.bean-test (see their github #4)
public class CurrentUser implements Serializable {

	private static final long serialVersionUID = 1L;

	private User user;

	public CurrentUser() {
	}

	public CurrentUser(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}
}
