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
package de.symeda.sormas.api.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.statistics.StatisticsGroupingKey;
import de.symeda.sormas.api.utils.ValidationException;

/**
 * These are also used as user groups in the server realm
 */
public enum UserRole
	implements
	StatisticsGroupingKey {

	ADMIN(false, false, false, false),
	NATIONAL_USER(false, false, false, false),
	SURVEILLANCE_SUPERVISOR(true, false, false, false),
	SURVEILLANCE_OFFICER(false, true, false, false),
	HOSPITAL_INFORMANT(false, false, true, false),
	COMMUNITY_INFORMANT(false, false, true, false),
	CASE_SUPERVISOR(true, false, false, false),
	CASE_OFFICER(false, true, false, false),
	CONTACT_SUPERVISOR(true, false, false, false),
	CONTACT_OFFICER(false, true, false, false),
	EVENT_OFFICER(true, false, false, false),
	LAB_USER(false, false, false, false),
	EXTERNAL_LAB_USER(false, false, false, false),
	NATIONAL_OBSERVER(false, false, false, false),
	STATE_OBSERVER(false, false, false, false),
	DISTRICT_OBSERVER(false, false, false, false),
	NATIONAL_CLINICIAN(false, false, false, false),
	POE_INFORMANT(false, false, true, true),
	POE_SUPERVISOR(true, false, false, true),
	POE_NATIONAL_USER(false, false, false, true),
	IMPORT_USER(false, false, false, false),
	REST_EXTERNAL_VISITS_USER(false, false, false, false),
	REST_USER(false, false, false, false);

	/*
	 * Hint for SonarQube issues:
	 * 1. java:S115: Violation of name convention for String constants of this class is accepted: Close as false positive.
	 */

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
	public static final String _EXTERNAL_LAB_USER = EXTERNAL_LAB_USER.name();
	public static final String _NATIONAL_OBSERVER = NATIONAL_OBSERVER.name();
	public static final String _NATIONAL_CLINICIAN = NATIONAL_CLINICIAN.name();
	public static final String _POE_INFORMANT = POE_INFORMANT.name();
	public static final String _POE_SUPERVISOR = POE_SUPERVISOR.name();
	public static final String _POE_NATIONAL_USER = POE_NATIONAL_USER.name();
	public static final String _IMPORT_USER = IMPORT_USER.name();
	public static final String _REST_EXTERNAL_VISITS_USER = REST_EXTERNAL_VISITS_USER.name();
	public static final String _REST_USER = REST_USER.name();

	private static final Set<UserRole> NATIONAL_ROLES =
		EnumSet.of(UserRole.NATIONAL_OBSERVER, UserRole.NATIONAL_USER, UserRole.NATIONAL_CLINICIAN, UserRole.POE_NATIONAL_USER);

	private final boolean supervisor;
	private final boolean officer;
	private final boolean informant;

	/**
	 * Whether the user is directly responsible for managing port health cases
	 */
	private final boolean portHealthUser;

	private Set<UserRight> defaultUserRights = null;

	private static Set<UserRole> supervisorRoles = null;
	private static Set<UserRole> officerRoles = null;
	private static Set<UserRole> informantRoles = null;
	private static Set<UserRole> portHealthUserRoles = null;

	UserRole(boolean supervisor, boolean officer, boolean informant, boolean portHealthUser) {

		this.supervisor = supervisor;
		this.officer = officer;
		this.informant = informant;
		this.portHealthUser = portHealthUser;
	}

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

	public String toShortString() {
		return I18nProperties.getEnumCaptionShort(this);
	}

	public boolean isSupervisor() {
		return supervisor;
	}

	public boolean isOfficer() {
		return officer;
	}

	public boolean isInformant() {
		return informant;
	}

	public boolean isPortHealthUser() {
		return portHealthUser;
	}

	public boolean isNational() {
		return NATIONAL_ROLES.contains(this);
	}

	public Set<UserRight> getDefaultUserRights() {

		if (defaultUserRights == null) {
			defaultUserRights = EnumSet.noneOf(UserRight.class);
			for (UserRight userRight : UserRight.values()) {
				if (userRight.isDefaultForRole(this)) {
					defaultUserRights.add(userRight);
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
			for (UserRole role : UserRole.values()) {
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
			collection.add(NATIONAL_CLINICIAN);
			collection.add(POE_INFORMANT);
			collection.add(POE_SUPERVISOR);
			collection.add(POE_NATIONAL_USER);
			break;
		case POE_NATIONAL_USER:
			collection.add(POE_INFORMANT);
			collection.add(POE_SUPERVISOR);
			break;
		case NATIONAL_CLINICIAN:
			collection.add(CASE_SUPERVISOR);
			collection.add(CASE_OFFICER);
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
		case POE_SUPERVISOR:
			collection.add(POE_INFORMANT);
			break;
		case EVENT_OFFICER:
			collection.add(EVENT_OFFICER);
			break;
		case LAB_USER:
			collection.add(LAB_USER);
			break;
		case EXTERNAL_LAB_USER:
			collection.add(EXTERNAL_LAB_USER);
			break;
		case IMPORT_USER:
			collection.add(IMPORT_USER);
			break;
		case REST_EXTERNAL_VISITS_USER:
			collection.add(REST_EXTERNAL_VISITS_USER);
			break;
		case REST_USER:
			collection.add(REST_USER);
			break;
		default:
			break;
		}
	}

	public static Set<UserRole> getAssignableRoles(Collection<UserRole> roles) {
		Set<UserRole> result = EnumSet.noneOf(UserRole.class);
		for (UserRole role : roles) {
			role.addAssignableRoles(result);
		}
		return result;
	}

	public Collection<UserRole> getCombinableRoles() {

		switch (this) {
		case ADMIN:
			return Arrays.asList(
				SURVEILLANCE_SUPERVISOR,
				CASE_SUPERVISOR,
				CONTACT_SUPERVISOR,
				EVENT_OFFICER,
				LAB_USER,
				NATIONAL_USER,
				NATIONAL_OBSERVER,
				NATIONAL_CLINICIAN,
				IMPORT_USER);
		case NATIONAL_USER:
			return Arrays.asList(LAB_USER, ADMIN, NATIONAL_CLINICIAN, IMPORT_USER);
		case NATIONAL_OBSERVER:
			return Arrays.asList(ADMIN, IMPORT_USER);
		case NATIONAL_CLINICIAN:
			return Arrays.asList(ADMIN, NATIONAL_USER, IMPORT_USER);
		case CASE_SUPERVISOR:
		case CONTACT_SUPERVISOR:
		case SURVEILLANCE_SUPERVISOR:
		case EVENT_OFFICER:
			return Arrays.asList(SURVEILLANCE_SUPERVISOR, CASE_SUPERVISOR, CONTACT_SUPERVISOR, EVENT_OFFICER, LAB_USER, ADMIN, IMPORT_USER);
		case LAB_USER:
			return Arrays
				.asList(SURVEILLANCE_SUPERVISOR, CASE_SUPERVISOR, CONTACT_SUPERVISOR, EVENT_OFFICER, LAB_USER, NATIONAL_USER, ADMIN, IMPORT_USER);
		case SURVEILLANCE_OFFICER:
		case CASE_OFFICER:
		case CONTACT_OFFICER:
			return Arrays.asList(SURVEILLANCE_OFFICER, CASE_OFFICER, CONTACT_OFFICER, IMPORT_USER);
		case HOSPITAL_INFORMANT:
			return Arrays.asList(HOSPITAL_INFORMANT, IMPORT_USER);
		case COMMUNITY_INFORMANT:
			return Arrays.asList(COMMUNITY_INFORMANT, IMPORT_USER);
		case EXTERNAL_LAB_USER:
			return Arrays.asList(EXTERNAL_LAB_USER, IMPORT_USER);
		case STATE_OBSERVER:
		case DISTRICT_OBSERVER:
		case POE_INFORMANT:
		case POE_SUPERVISOR:
		case POE_NATIONAL_USER:
			return Arrays.asList(IMPORT_USER);
		case IMPORT_USER:
			final List<UserRole> userRoles = new ArrayList<>();
			for (UserRole userRole : UserRole.values()) {
				if (userRole != REST_EXTERNAL_VISITS_USER && userRole != REST_USER) {
					userRoles.add(userRole);
				}
			}
			return userRoles;
		case REST_EXTERNAL_VISITS_USER:
			return Arrays.asList(REST_EXTERNAL_VISITS_USER);
		case REST_USER:
			return Arrays.asList(REST_USER);
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

		for (UserRole role : roles) {
			if (role.isInformant()) {
				return true;
			}
		}
		return false;
	}

	public static boolean isNational(Collection<UserRole> roles) {

		for (UserRole role : roles) {
			if (role.isNational()) {
				return true;
			}
		}
		return false;
	}

	public static boolean isPortHealthUser(Collection<UserRole> roles) {

		for (UserRole role : roles) {
			if (role.isPortHealthUser()) {
				return true;
			}
		}
		return false;
	}

	public static boolean isLabUser(Collection<UserRole> roles) {
		return roles.contains(UserRole.LAB_USER) || roles.contains(UserRole.EXTERNAL_LAB_USER);
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

	public static Set<UserRole> getSupervisorRoles() {

		if (supervisorRoles == null) {
			supervisorRoles = EnumSet.noneOf(UserRole.class);
			for (UserRole userRole : values()) {
				if (userRole.isSupervisor()) {
					supervisorRoles.add(userRole);
				}
			}
		}
		return supervisorRoles;
	}

	public static Set<UserRole> getOfficerRoles() {

		if (officerRoles == null) {
			officerRoles = EnumSet.noneOf(UserRole.class);
			for (UserRole userRole : values()) {
				if (userRole.isOfficer()) {
					officerRoles.add(userRole);
				}
			}
		}
		return officerRoles;
	}

	public static Set<UserRole> getInformantRoles() {

		if (informantRoles == null) {
			informantRoles = EnumSet.noneOf(UserRole.class);
			for (UserRole userRole : values()) {
				if (userRole.isInformant()) {
					informantRoles.add(userRole);
				}
			}
		}
		return informantRoles;
	}

	public static Set<UserRole> getPortHealthUserRoles() {

		if (portHealthUserRoles == null) {
			portHealthUserRoles = EnumSet.noneOf(UserRole.class);
			for (UserRole userRole : values()) {
				if (userRole.isPortHealthUser()) {
					portHealthUserRoles.add(userRole);
				}
			}
		}
		return portHealthUserRoles;
	}

	@SuppressWarnings("serial")
	public static class UserRoleValidationException extends ValidationException {

		private final UserRole checkedUserRole;
		private final UserRole forbiddenUserRole;

		public UserRoleValidationException(UserRole checkedUserRole, UserRole forbiddenUserRole) {
			super(checkedUserRole + " " + I18nProperties.getString(Strings.messageUserRoleCombination) + " " + forbiddenUserRole);
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

	@Override
	public int keyCompareTo(StatisticsGroupingKey o) {

		if (o == null) {
			throw new NullPointerException("Can't compare to null.");
		}
		if (o.getClass() != this.getClass()) {
			throw new UnsupportedOperationException(
				"Can't compare to class " + o.getClass().getName() + " that differs from " + this.getClass().getName());
		}

		return this.toString().compareTo(o.toString());
	}
}
