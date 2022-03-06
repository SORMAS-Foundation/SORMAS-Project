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
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.statistics.StatisticsGroupingKey;
import de.symeda.sormas.api.utils.ValidationException;

/**
 * These are also used as user groups in the server realm
 */
@Deprecated
public enum UserRole
	implements
	StatisticsGroupingKey {

	ADMIN(false, false, false, false, JurisdictionLevel.NONE, Collections.emptyList(), Collections.emptyList()),
	NATIONAL_USER(false,
		false,
		false,
		false,
		JurisdictionLevel.NATION,
		Arrays.asList(NotificationType.TASK_START, NotificationType.TASK_DUE, NotificationType.TASK_UPDATED_ASSIGNEE),
		Arrays.asList(NotificationType.TASK_START, NotificationType.TASK_DUE, NotificationType.TASK_UPDATED_ASSIGNEE)),
	SURVEILLANCE_SUPERVISOR(true,
		false,
		false,
		false,
		JurisdictionLevel.REGION,
		Arrays.asList(
			NotificationType.CASE_CLASSIFICATION_CHANGED,
			NotificationType.DISEASE_CHANGED,
			NotificationType.CASE_INVESTIGATION_DONE,
			NotificationType.CASE_LAB_RESULT_ARRIVED,
			NotificationType.CONTACT_LAB_RESULT_ARRIVED,
			NotificationType.TASK_START,
			NotificationType.TASK_DUE,
			NotificationType.TASK_UPDATED_ASSIGNEE,
			NotificationType.VISIT_COMPLETED,
			NotificationType.CONTACT_SYMPTOMATIC,
			NotificationType.EVENT_PARTICIPANT_CASE_CLASSIFICATION_CONFIRMED,
			NotificationType.EVENT_PARTICIPANT_RELATED_TO_OTHER_EVENTS,
			NotificationType.EVENT_GROUP_CREATED,
			NotificationType.EVENT_ADDED_TO_EVENT_GROUP,
			NotificationType.EVENT_REMOVED_FROM_EVENT_GROUP),
		Arrays.asList(
			NotificationType.CASE_CLASSIFICATION_CHANGED,
			NotificationType.DISEASE_CHANGED,
			NotificationType.CASE_INVESTIGATION_DONE,
			NotificationType.CASE_LAB_RESULT_ARRIVED,
			NotificationType.CONTACT_LAB_RESULT_ARRIVED,
			NotificationType.TASK_START,
			NotificationType.TASK_DUE,
			NotificationType.TASK_UPDATED_ASSIGNEE,
			NotificationType.VISIT_COMPLETED,
			NotificationType.CONTACT_SYMPTOMATIC,
			NotificationType.EVENT_PARTICIPANT_CASE_CLASSIFICATION_CONFIRMED,
			NotificationType.EVENT_PARTICIPANT_RELATED_TO_OTHER_EVENTS,
			NotificationType.EVENT_GROUP_CREATED,
			NotificationType.EVENT_ADDED_TO_EVENT_GROUP,
			NotificationType.EVENT_REMOVED_FROM_EVENT_GROUP)),
	ADMIN_SUPERVISOR(true, false, false, false, JurisdictionLevel.REGION, Collections.emptyList(), Collections.emptyList()), // FIXME : remove this when user rights management is doable by users
	SURVEILLANCE_OFFICER(false,
		true,
		false,
		false,
		JurisdictionLevel.DISTRICT,
		Arrays.asList(
			NotificationType.EVENT_PARTICIPANT_CASE_CLASSIFICATION_CONFIRMED,
			NotificationType.EVENT_PARTICIPANT_RELATED_TO_OTHER_EVENTS,
			NotificationType.EVENT_GROUP_CREATED,
			NotificationType.EVENT_ADDED_TO_EVENT_GROUP,
			NotificationType.EVENT_REMOVED_FROM_EVENT_GROUP),
		Arrays.asList(
			NotificationType.EVENT_PARTICIPANT_CASE_CLASSIFICATION_CONFIRMED,
			NotificationType.EVENT_PARTICIPANT_RELATED_TO_OTHER_EVENTS,
			NotificationType.EVENT_GROUP_CREATED,
			NotificationType.EVENT_ADDED_TO_EVENT_GROUP,
			NotificationType.EVENT_REMOVED_FROM_EVENT_GROUP)),
	HOSPITAL_INFORMANT(false, false, true, false, JurisdictionLevel.HEALTH_FACILITY, Collections.emptyList(), Collections.emptyList()),
	COMMUNITY_OFFICER(false, true, false, false, JurisdictionLevel.COMMUNITY, Collections.emptyList(), Collections.emptyList()),
	COMMUNITY_INFORMANT(false, false, true, false, JurisdictionLevel.COMMUNITY, Collections.emptyList(), Collections.emptyList()),
	CASE_SUPERVISOR(true,
		false,
		false,
		false,
		JurisdictionLevel.REGION,
		Arrays.asList(
			NotificationType.CASE_CLASSIFICATION_CHANGED,
			NotificationType.DISEASE_CHANGED,
			NotificationType.CASE_INVESTIGATION_DONE,
			NotificationType.CASE_LAB_RESULT_ARRIVED,
			NotificationType.TASK_START,
			NotificationType.TASK_DUE,
			NotificationType.TASK_UPDATED_ASSIGNEE,
			NotificationType.VISIT_COMPLETED,
			NotificationType.EVENT_PARTICIPANT_CASE_CLASSIFICATION_CONFIRMED),
		Arrays.asList(
			NotificationType.CASE_CLASSIFICATION_CHANGED,
			NotificationType.DISEASE_CHANGED,
			NotificationType.CASE_INVESTIGATION_DONE,
			NotificationType.CASE_LAB_RESULT_ARRIVED,
			NotificationType.TASK_START,
			NotificationType.TASK_DUE,
			NotificationType.TASK_UPDATED_ASSIGNEE,
			NotificationType.VISIT_COMPLETED,
			NotificationType.EVENT_PARTICIPANT_CASE_CLASSIFICATION_CONFIRMED)),
	CASE_OFFICER(false, true, false, false, JurisdictionLevel.DISTRICT, Collections.emptyList(), Collections.emptyList()),
	CONTACT_SUPERVISOR(true,
		false,
		false,
		false,
		JurisdictionLevel.REGION,
		Arrays.asList(
			NotificationType.CASE_CLASSIFICATION_CHANGED,
			NotificationType.DISEASE_CHANGED,
			NotificationType.CONTACT_LAB_RESULT_ARRIVED,
			NotificationType.TASK_START,
			NotificationType.TASK_DUE,
			NotificationType.TASK_UPDATED_ASSIGNEE,
			NotificationType.VISIT_COMPLETED,
			NotificationType.CONTACT_SYMPTOMATIC),
		Arrays.asList(
			NotificationType.CASE_CLASSIFICATION_CHANGED,
			NotificationType.DISEASE_CHANGED,
			NotificationType.CONTACT_LAB_RESULT_ARRIVED,
			NotificationType.TASK_START,
			NotificationType.TASK_DUE,
			NotificationType.TASK_UPDATED_ASSIGNEE,
			NotificationType.VISIT_COMPLETED,
			NotificationType.CONTACT_SYMPTOMATIC)),
	CONTACT_OFFICER(false, true, false, false, JurisdictionLevel.DISTRICT, Collections.emptyList(), Collections.emptyList()),
	EVENT_OFFICER(true,
		false,
		false,
		false,
		JurisdictionLevel.REGION,
		Arrays.asList(
			NotificationType.EVENT_PARTICIPANT_LAB_RESULT_ARRIVED,
			NotificationType.TASK_START,
			NotificationType.TASK_DUE,
			NotificationType.TASK_UPDATED_ASSIGNEE,
			NotificationType.EVENT_PARTICIPANT_CASE_CLASSIFICATION_CONFIRMED,
			NotificationType.EVENT_PARTICIPANT_RELATED_TO_OTHER_EVENTS,
			NotificationType.EVENT_GROUP_CREATED,
			NotificationType.EVENT_ADDED_TO_EVENT_GROUP,
			NotificationType.EVENT_REMOVED_FROM_EVENT_GROUP),
		Arrays.asList(
			NotificationType.EVENT_PARTICIPANT_LAB_RESULT_ARRIVED,
			NotificationType.TASK_START,
			NotificationType.TASK_DUE,
			NotificationType.TASK_UPDATED_ASSIGNEE,
			NotificationType.EVENT_PARTICIPANT_CASE_CLASSIFICATION_CONFIRMED,
			NotificationType.EVENT_PARTICIPANT_RELATED_TO_OTHER_EVENTS,
			NotificationType.EVENT_GROUP_CREATED,
			NotificationType.EVENT_ADDED_TO_EVENT_GROUP,
			NotificationType.EVENT_REMOVED_FROM_EVENT_GROUP)),
	LAB_USER(false,
		false,
		false,
		false,
		JurisdictionLevel.LABORATORY,
		Collections.singletonList(NotificationType.LAB_SAMPLE_SHIPPED),
		Collections.singletonList(NotificationType.LAB_SAMPLE_SHIPPED)),
	EXTERNAL_LAB_USER(false,
		false,
		false,
		false,
		JurisdictionLevel.EXTERNAL_LABORATORY,
		Collections.singletonList(NotificationType.LAB_SAMPLE_SHIPPED),
		Collections.singletonList(NotificationType.LAB_SAMPLE_SHIPPED)),
	NATIONAL_OBSERVER(false, false, false, false, JurisdictionLevel.NATION, Collections.emptyList(), Collections.emptyList()),
	STATE_OBSERVER(false, false, false, false, JurisdictionLevel.REGION, Collections.emptyList(), Collections.emptyList()),
	DISTRICT_OBSERVER(false, false, false, false, JurisdictionLevel.DISTRICT, Collections.emptyList(), Collections.emptyList()),
	NATIONAL_CLINICIAN(false, false, false, false, JurisdictionLevel.NATION, Collections.emptyList(), Collections.emptyList()),
	POE_INFORMANT(false, false, false, true, JurisdictionLevel.POINT_OF_ENTRY, Collections.emptyList(), Collections.emptyList()),
	POE_SUPERVISOR(true,
		false,
		false,
		true,
		JurisdictionLevel.REGION,
		Arrays.asList(NotificationType.TASK_START, NotificationType.TASK_DUE, NotificationType.TASK_UPDATED_ASSIGNEE),
		Arrays.asList(NotificationType.TASK_START, NotificationType.TASK_DUE, NotificationType.TASK_UPDATED_ASSIGNEE)),
	POE_NATIONAL_USER(false, false, false, true, JurisdictionLevel.NATION, Collections.emptyList(), Collections.emptyList()),
	IMPORT_USER(false, false, false, false, JurisdictionLevel.NONE, Collections.emptyList(), Collections.emptyList()),
	REST_EXTERNAL_VISITS_USER(false, false, false, false, JurisdictionLevel.NATION, Collections.emptyList(), Collections.emptyList()),
	REST_USER(false, false, false, false, JurisdictionLevel.NATION, Collections.emptyList(), Collections.emptyList()),
	SORMAS_TO_SORMAS_CLIENT(false, false, false, false, JurisdictionLevel.NATION, Collections.emptyList(), Collections.emptyList()),
	BAG_USER(false, false, false, false, JurisdictionLevel.NONE, Collections.emptyList(), Collections.emptyList());

	/*
	 * Hint for SonarQube issues:
	 * 1. java:S115: Violation of name convention for String constants of this class is accepted: Close as false positive.
	 */

	public static final String _USER = "USER";
	public static final String _ADMIN = ADMIN.name();
	public static final String _NATIONAL_USER = NATIONAL_USER.name();
	public static final String _SURVEILLANCE_SUPERVISOR = SURVEILLANCE_SUPERVISOR.name();
	public static final String _SURVEILLANCE_OFFICER = SURVEILLANCE_OFFICER.name();
	public static final String _HOSPITAL_INFORMANT = HOSPITAL_INFORMANT.name();
	public static final String _COMMUNITY_OFFICER = COMMUNITY_OFFICER.name();
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
	public static final String _SORMAS_TO_SORMAS_CLIENT = "SORMAS_TO_SORMAS_CLIENT";
	public static final String _BAG_USER = "BAG_USER";

	private Set<UserRight> defaultUserRights = null;

	private final boolean supervisor;
	private final boolean hasOptionalHealthFacility;
	private final boolean hasAssociatedOfficer;
	private final boolean portHealthUser;

	private final JurisdictionLevel jurisdictionLevel;

	private final List<NotificationType> emailNotifications;
	private final List<NotificationType> smsNotifications;

	UserRole(
		boolean supervisor,
		boolean hasOptionalHealthFacility,
		boolean hasAssociatedOfficer,
		boolean portHealthUser,
		JurisdictionLevel jurisdictionLevel,
		List<NotificationType> emailNotifications,
		List<NotificationType> smsNotifications) {

		this.supervisor = supervisor;
		this.hasOptionalHealthFacility = hasOptionalHealthFacility;
		this.hasAssociatedOfficer = hasAssociatedOfficer;
		this.portHealthUser = portHealthUser;
		this.jurisdictionLevel = jurisdictionLevel;
		this.emailNotifications = emailNotifications;
		this.smsNotifications = smsNotifications;
	}

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

	public String toShortString() {
		return I18nProperties.getEnumCaptionShort(this);
	}

	@Deprecated
	public boolean isSupervisor() {
		return supervisor;
	}

	@Deprecated
	public boolean hasAssociatedOfficer() {
		return hasAssociatedOfficer;
	}

	@Deprecated
	public boolean isPortHealthUser() {
		return portHealthUser;
	}

	public List<NotificationType> getEmailNotifications() {
		return emailNotifications;
	}

	public List<NotificationType> getSmsNotifications() {
		return smsNotifications;
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
		case SORMAS_TO_SORMAS_CLIENT:
			collection.add(SORMAS_TO_SORMAS_CLIENT);
			break;
		case BAG_USER:
			collection.add(BAG_USER);
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

		boolean laboratoryJurisdictionPresent = false;
		for (UserRole role : roles) {
			final JurisdictionLevel jurisdictionLevel = role.getJurisdictionLevel();
			if (roles.size() == 1 || (jurisdictionLevel != JurisdictionLevel.NONE && jurisdictionLevel != JurisdictionLevel.LABORATORY)) {
				return jurisdictionLevel;
			} else if (jurisdictionLevel == JurisdictionLevel.LABORATORY) {
				laboratoryJurisdictionPresent = true;
			}
		}

		return laboratoryJurisdictionPresent ? JurisdictionLevel.LABORATORY : JurisdictionLevel.NONE;
	}

	@Deprecated
	public static boolean isSupervisor(Collection<UserRole> roles) {

		for (UserRole role : roles) {
			if (role.isSupervisor()) {
				return true;
			}
		}
		return false;
	}

	//TODO: #2804
	@Deprecated
	public static boolean hasAssociatedOfficer(Collection<UserRole> roles) {

		for (UserRole role : roles) {
			if (role.hasAssociatedOfficer()) {
				return true;
			}
		}
		return false;
	}

	//TODO: #2804
	@Deprecated
	public static boolean hasOptionalHealthFacility(Collection<UserRole> roles) {

		for (UserRole role : roles) {
			if (role.hasOptionalHealthFacility) {
				return true;
			}
		}
		return false;
	}

	@Deprecated
	//TODO: #2804
	public static boolean isPortHealthUser(Collection<UserRole> roles) {

		for (UserRole role : roles) {
			if (role.isPortHealthUser()) {
				return true;
			}
		}
		return false;
	}

	//TODO: #2804
	@Deprecated
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

	public static List<UserRole> getWithJurisdictionLevels(JurisdictionLevel... jurisdictionLevels) {
		List<UserRole> ret = new ArrayList<>();

		for (UserRole role : UserRole.values()) {
			if (ArrayUtils.contains(jurisdictionLevels, role.jurisdictionLevel)) {
				ret.add(role);
			}
		}

		return ret;
	}

	public static UserRole[] getWithEmailNotificationTypes(Set<NotificationType> notificationTypes) {
		List<UserRole> ret = new ArrayList<>();

		for (UserRole role : UserRole.values()) {
			if (role.emailNotifications.stream().anyMatch(notificationTypes::contains)) {
				ret.add(role);
			}
		}

		return ret.toArray(new UserRole[] {});
	}

	public static UserRole[] getWithSmsNotificationTypes(Set<NotificationType> notificationTypes) {
		List<UserRole> ret = new ArrayList<>();

		for (UserRole role : UserRole.values()) {
			if (role.emailNotifications.stream().anyMatch(notificationTypes::contains)) {
				ret.add(role);
			}
		}

		return ret.toArray(new UserRole[] {});
	}
}
