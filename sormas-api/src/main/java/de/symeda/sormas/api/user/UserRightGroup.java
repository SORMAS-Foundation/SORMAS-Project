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

import de.symeda.sormas.api.i18n.I18nProperties;

public enum UserRightGroup {

	GENERAL,
	DATA_PROTECTION,
	DASHBOARD,
	TASK,
	PERSON,
	CASE,
	CASE_MANAGEMENT,
	PORT_HEALTH,
	AGGREGATED_REPORTING,
	CONTACT,
	VISIT,
	EVENT,
	SAMPLE,
	IMMUNIZATION,
	ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION,
	TRAVEL_ENTRY,
	CAMPAIGN,
	ENVIRONMENT,
	SELF_REPORT,
	STATISTICS,
	USER,
	INFRASTRUCTURE,
	DOCUMENT,
	EXTERNAL_EMAILS,
	SURVEY,

	EXPORT,
	CONFIGURATION,
	EXTERNAL;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
