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

import java.util.Collection;
import java.util.EnumSet;
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

	ADMIN(false, false, false, false, JurisdictionLevel.NONE),
	NATIONAL_USER(false, false, false, false, JurisdictionLevel.NATION),
	SURVEILLANCE_SUPERVISOR(true, false, false, false, JurisdictionLevel.REGION),
	SURVEILLANCE_OFFICER(false, true, false, false, JurisdictionLevel.DISTRICT),
	HOSPITAL_INFORMANT(false, false, true, false, JurisdictionLevel.HEALTH_FACILITY),
	COMMUNITY_INFORMANT(false, false, true, false, JurisdictionLevel.COMMUNITY),
	CASE_SUPERVISOR(true, false, false, false, JurisdictionLevel.REGION),
	CASE_OFFICER(false, true, false, false, JurisdictionLevel.DISTRICT),
	CONTACT_SUPERVISOR(true, false, false, false, JurisdictionLevel.REGION),
	CONTACT_OFFICER(false, true, false, false, JurisdictionLevel.DISTRICT),
	EVENT_OFFICER(true, false, false, false, JurisdictionLevel.REGION),
	LAB_USER(false, false, false, false, JurisdictionLevel.LABORATORY),
	EXTERNAL_LAB_USER(false, false, false, false, JurisdictionLevel.EXTERNAL_LABORATORY),
	NATIONAL_OBSERVER(false, false, false, false, JurisdictionLevel.NATION),
	STATE_OBSERVER(false, false, false, false, JurisdictionLevel.REGION),
	DISTRICT_OBSERVER(false, false, false, false, JurisdictionLevel.DISTRICT),
	NATIONAL_CLINICIAN(false, false, false, false, JurisdictionLevel.NATION),
	POE_INFORMANT(false, false, false, true, JurisdictionLevel.POINT_OF_ENTRY),
	POE_SUPERVISOR(true, false, false, true, JurisdictionLevel.REGION),
	POE_NATIONAL_USER(false, false, false, true, JurisdictionLevel.NATION),
	IMPORT_USER(false, false, false, false, JurisdictionLevel.NONE),
	REST_EXTERNAL_VISITS_USER(false, false, false, false, JurisdictionLevel.NONE),
	REST_USER(false, false, false, false, JurisdictionLevel.NONE);

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

	private Set<UserRight> defaultUserRights = null;

	private final boolean supervisor;
	private final boolean hasOptionalHealthFacility;
	private final boolean hasAssociatedOfficer;
	private final boolean portHealthUser;

	private final JurisdictionLevel jurisdictionLevel;

	UserRole(
		boolean supervisor,
		boolean hasOptionalHealthFacility,
		boolean hasAssociatedOfficer,
		boolean portHealthUser,
		JurisdictionLevel jurisdictionLevel) {

		this.supervisor = supervisor;
		this.hasOptionalHealthFacility = hasOptionalHealthFacility;
		this.hasAssociatedOfficer = hasAssociatedOfficer;
		this.portHealthUser = portHealthUser;
		this.jurisdictionLevel = jurisdictionLevel;
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

	public boolean hasAssociatedOfficer() {
		return hasAssociatedOfficer;
	}

	public boolean isPortHealthUser() {
		return portHealthUser;
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

	public JurisdictionLevel getJurisdictionLevel() {
		return jurisdictionLevel;
	}

	/**
	 * Expects the roles have been validated.
	 * 
	 * @param roles
	 * @return
	 */
	public static JurisdictionLevel getJurisdictionLevel(Collection<UserRole> roles) {

		for (UserRole role : roles) {
			JurisdictionLevel jurisdictionLevel = role.getJurisdictionLevel();
			if (jurisdictionLevel != JurisdictionLevel.NONE && jurisdictionLevel != JurisdictionLevel.LABORATORY) {
				return jurisdictionLevel;
			}
		}

		return JurisdictionLevel.NONE;
	}

	public static boolean isSupervisor(Collection<UserRole> roles) {

		for (UserRole role : roles) {
			if (role.isSupervisor()) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasAssociatedOfficer(Collection<UserRole> roles) {

		for (UserRole role : roles) {
			if (role.hasAssociatedOfficer()) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasOptionalHealthFacility(Collection<UserRole> roles) {

		for (UserRole role : roles) {
			if (role.hasOptionalHealthFacility) {
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

	public static void validate(Collection<UserRole> roles) throws UserRoleValidationException {
		UserRole previousCheckedRole = null;
		for (UserRole userRole : roles) {
			final JurisdictionLevel jurisdictionLevel = userRole.getJurisdictionLevel();
			if (jurisdictionLevel != JurisdictionLevel.NONE && jurisdictionLevel != JurisdictionLevel.LABORATORY) {
				if (previousCheckedRole != null && previousCheckedRole.getJurisdictionLevel() != jurisdictionLevel) {
					throw new UserRoleValidationException(userRole, previousCheckedRole);
				} else {
					previousCheckedRole = userRole;
				}
			}
		}
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
