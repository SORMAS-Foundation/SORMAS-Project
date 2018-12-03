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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.user;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.utils.ValidationException;

/**
 * These are also used as user groups in the server realm
 */
public enum UserRole {

	ADMIN(false, false),
	NATIONAL_USER(false, false),	
	SURVEILLANCE_SUPERVISOR(true, false),	
	SURVEILLANCE_OFFICER(false, true),	
	HOSPITAL_INFORMANT(false, false),	
	COMMUNITY_INFORMANT(false, false),
	CASE_SUPERVISOR(true, false),	
	CASE_OFFICER(false, true),	
	CONTACT_SUPERVISOR(true, false),	
	CONTACT_OFFICER(false, true),	
	EVENT_OFFICER(true, false),	
	LAB_USER(false, false),
	NATIONAL_OBSERVER(false, false),
	STATE_OBSERVER(false, false),
	DISTRICT_OBSERVER(false, false)
	;
	
	public static final String _SYSTEM = "SYSTEM";
	public static final String _USER = "USER";
	public static final String _ADMIN = ADMIN.name();
	public static final String _NATIONAL_USER = NATIONAL_USER.name();
	public static final String _SURVEILLANCE_SUPERVISOR = SURVEILLANCE_SUPERVISOR.name();
	public static final String _SURVEILLANCE_OFFICER = SURVEILLANCE_OFFICER.name();
	public static final String _HOSPITAL_INFORMANT = HOSPITAL_INFORMANT.name();
	public static final String _COMMUNITY_INFORMANT = COMMUNITY_INFORMANT.name();
	public static final String _CASE_SUPERVISOR = CASE_SUPERVISOR.name();
	public static final String _CASE_OFFICER = CASE_OFFICER.name();
	public static final String _CONTACT_SUPERVISOR = CONTACT_SUPERVISOR.name();
	public static final String _CONTACT_OFFICER = CONTACT_OFFICER.name();
	public static final String _EVENT_OFFICER = EVENT_OFFICER.name();
	public static final String _LAB_USER = LAB_USER.name();
	public static final String _NATIONAL_OBSERVER = NATIONAL_OBSERVER.name();
	
	private final boolean supervisor;
	private final boolean officer;
	private HashSet<UserRight> defaultUserRights = null;
	
	private UserRole(boolean supervisor, boolean officer) {
		this.supervisor = supervisor;
		this.officer = officer;
	}
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	};
	
	public String toShortString() {
		return I18nProperties.getEnumCaptionShort(this);
	}

	public boolean isSupervisor() {
		return supervisor;
	}
	
	public boolean isOfficer() {
		return officer;
	}
	
	public HashSet<UserRight> getDefaultUserRights() {
		if (defaultUserRights == null) {
			defaultUserRights = new HashSet<UserRight>();
			for (UserRight userRight : UserRight.values()) {
				if (userRight.isDefaultForRole(this)) {
					getDefaultUserRights().add(userRight);
				}
			}
		}
		return defaultUserRights;
	}
		
	public boolean hasDefaultRight(UserRight userRight) {
		return getDefaultUserRights().contains(userRight);
	}
	
	public void addAssignableRoles(Collection<UserRole> collection) {
		switch (this) {
		case ADMIN:
			for(UserRole role : UserRole.values()) {
				collection.add(role);
			}
			break;
		case NATIONAL_USER:
			collection.add(SURVEILLANCE_SUPERVISOR);
			collection.add(CASE_SUPERVISOR);
			collection.add(CONTACT_SUPERVISOR);
			collection.add(CASE_OFFICER);
			collection.add(CONTACT_OFFICER);
			collection.add(SURVEILLANCE_OFFICER);
			collection.add(LAB_USER);
			collection.add(NATIONAL_OBSERVER);
			collection.add(STATE_OBSERVER);
			collection.add(DISTRICT_OBSERVER);
			break;
		case SURVEILLANCE_SUPERVISOR:
			collection.add(SURVEILLANCE_OFFICER);
			collection.add(HOSPITAL_INFORMANT);
			collection.add(COMMUNITY_INFORMANT);
			break;
		case CASE_SUPERVISOR:
			collection.add(CASE_OFFICER);
			break;
		case CONTACT_SUPERVISOR:
			collection.add(CONTACT_OFFICER);
			break;
		case EVENT_OFFICER:
			collection.add(EVENT_OFFICER);
			break;
		case LAB_USER:
			collection.add(LAB_USER);
			break;
		default:
			break;
		}
	}
	
	public static HashSet<UserRole> getAssignableRoles(Collection<UserRole> roles) {
		HashSet<UserRole> result = new HashSet<UserRole>();
		for (UserRole role : roles) {
			role.addAssignableRoles(result);
		}
		return result;
	}

	public Collection<UserRole> getCombinableRoles() {
		switch(this) {
		case ADMIN:
			return Arrays.asList(
					SURVEILLANCE_SUPERVISOR, CASE_SUPERVISOR, CONTACT_SUPERVISOR,
					EVENT_OFFICER, LAB_USER,
					NATIONAL_USER, NATIONAL_OBSERVER
					);
		case NATIONAL_USER:
			return Arrays.asList(LAB_USER, ADMIN);
		case NATIONAL_OBSERVER:
			return Arrays.asList(ADMIN);
		case STATE_OBSERVER:
			return Collections.emptyList();
		case DISTRICT_OBSERVER:
			return Collections.emptyList();
		case CASE_SUPERVISOR:
		case CONTACT_SUPERVISOR:
		case SURVEILLANCE_SUPERVISOR:
		case EVENT_OFFICER:
			return Arrays.asList(
					SURVEILLANCE_SUPERVISOR, CASE_SUPERVISOR, CONTACT_SUPERVISOR,
					EVENT_OFFICER, LAB_USER, ADMIN
					);
		case LAB_USER:
			return Arrays.asList(
					SURVEILLANCE_SUPERVISOR, CASE_SUPERVISOR, CONTACT_SUPERVISOR,
					EVENT_OFFICER, LAB_USER, NATIONAL_USER, ADMIN
					);
		case SURVEILLANCE_OFFICER:
		case CASE_OFFICER:
		case CONTACT_OFFICER:
			return Arrays.asList(
					SURVEILLANCE_OFFICER, CASE_OFFICER, CONTACT_OFFICER
					);
		case HOSPITAL_INFORMANT:
			return Arrays.asList(HOSPITAL_INFORMANT);
		case COMMUNITY_INFORMANT:
			return Arrays.asList(COMMUNITY_INFORMANT);
		default:
			throw new UnsupportedOperationException("getCombinableRoles not implemented for user role: " + this);
		}
	}

	public static boolean isSupervisor(Collection<UserRole> roles) {
		for (UserRole role : roles) {
			if (role.isSupervisor()) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isOfficer(Collection<UserRole> roles) {
		for (UserRole role : roles) {
			if (role.isOfficer()) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isAdmin(Collection<UserRole> roles) {
		return roles.contains(UserRole.ADMIN);
	}
	
	public static boolean isInformant(Collection<UserRole> roles) {
		return roles.contains(UserRole.HOSPITAL_INFORMANT) || roles.contains(UserRole.COMMUNITY_INFORMANT);
	}
	
	public static boolean isLabUser(Collection<UserRole> roles) {
		return roles.contains(UserRole.LAB_USER);
	}
	
	public static UserRole getFirstDifferentUserRole(Collection<UserRole> roles, UserRole ignoredUserRole, Collection<UserRole> ignoredRoles) {
		
		for (UserRole userRole : roles) {
			if (!ignoredRoles.contains(userRole) && ignoredUserRole != userRole) {
				return userRole;
			}
		}
		return null;
	}
	
	public static void validate(Collection<UserRole> roles) throws UserRoleValidationException {
		
		for (UserRole userRole : roles) {
			UserRole forbiddenUserRole = getFirstDifferentUserRole(roles, userRole, userRole.getCombinableRoles());
			if (forbiddenUserRole != null) {
				throw new UserRoleValidationException(userRole, forbiddenUserRole);
			}
		}
	}
	
	@SuppressWarnings("serial")
	public static class UserRoleValidationException extends ValidationException {
		private final UserRole checkedUserRole;
		private final UserRole forbiddenUserRole;
		
		public UserRoleValidationException(UserRole checkedUserRole, UserRole forbiddenUserRole) {
			super(checkedUserRole + " cannot be combined with " + forbiddenUserRole);
			this.checkedUserRole = checkedUserRole;
			this.forbiddenUserRole = forbiddenUserRole;
		}

		public UserRole getCheckedUserRole() {
			return checkedUserRole;
		}

		public UserRole getForbiddenUserRole() {
			return forbiddenUserRole;
		}
	}
}
