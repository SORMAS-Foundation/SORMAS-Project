package de.symeda.sormas.backend.user;

import java.io.Serializable;

import javax.transaction.TransactionScoped;

@TransactionScoped
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
