package de.symeda.sormas.api.user;

import java.io.Serializable;

import de.symeda.sormas.api.BaseCriteria;
import de.symeda.sormas.api.utils.IgnoreForUrl;

public class UserCriteria extends BaseCriteria implements Serializable {

	private static final long serialVersionUID = 1702083604616047628L;
	
	private Boolean active;
	private UserRole userRole;
	private String freeText;
	
	public UserCriteria active(Boolean active) {
		this.active = active;
		return this;
	}
	
	public Boolean getActive() {
		return active;
	}
	
	public UserCriteria userRole(UserRole userRole) {
		this.userRole = userRole;
		return this;
	}
	
	public UserRole getUserRole() {
		return userRole;
	}
	
	public UserCriteria freeText(String freeText) {
		this.freeText = freeText;
		return this;
	}

	@IgnoreForUrl
	public String getFreeText() {
		return freeText;
	}

}
