package de.symeda.auditlog.api;

import java.io.Serializable;

/**
 * Describes the currently logged in user.
 * 
 * @author Oliver Milke
 * @since 12.11.2015
 */
public class UserId implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;

	public UserId() {
	}

	public UserId(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
