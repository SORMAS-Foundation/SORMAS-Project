/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.user;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Immutable;

import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.backend.common.AbstractDomainObject;

/**
 * This is a <strong>slim read-only copy</strong> of {@link User} to load user data without instantiating large JPA entity trees.<br />
 * The main reason this is not done by DTO projection is because the collections like {@code userRoles} cannot be queried there.
 */
@Entity
@Immutable
@Table(name = "users")
public class UserReference extends AbstractDomainObject {

	private static final long serialVersionUID = 9025694116880610101L;

	public static final String ACTIVE = "active";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String USER_ROLES = "userRoles";
	public static final String JURISDICTION_LEVEL = "jurisdictionLevel";

	private boolean active;
	private String firstName;
	private String lastName;
	private Set<UserRole> userRoles;
	private JurisdictionLevel jurisdictionLevel;

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@ManyToMany(cascade = {}, fetch = FetchType.EAGER)
	@JoinTable(name = User.TABLE_NAME_USERROLES, joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "userrole_id"))
	public Set<UserRole> getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(Set<UserRole> userRoles) {
		this.userRoles = userRoles;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public JurisdictionLevel getJurisdictionLevel() {
		return jurisdictionLevel;
	}

	public void setJurisdictionLevel(JurisdictionLevel jurisdictionLevel) {
		this.jurisdictionLevel = jurisdictionLevel;
	}

	@Transient
	public String getName() {
		return firstName + " " + lastName;
	}
}
