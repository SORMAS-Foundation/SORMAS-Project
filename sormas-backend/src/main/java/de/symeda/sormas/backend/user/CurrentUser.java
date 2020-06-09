package de.symeda.sormas.backend.user;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class CurrentUser {

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
