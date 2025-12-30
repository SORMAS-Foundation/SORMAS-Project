/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum FormType {

	CASE_CREATE,
    CASE_EDIT,
    PERSON_EDIT,
    HOSPITALIZATION_EDIT,
    SYMPTOMS_EDIT,
    EPIDEMIOLOGICAL_EDIT,
    PERSON_LOCATION_EDIT,
    SAMPLE_CREATE,
    SAMPLE_EDIT,
    PATHOGEN_TEST_CREATE,
    PATHOGEN_TEST_EDIT,
    TASK_EDIT,
    CLINICAL_VISIT_EDIT,
    HEALTH_CONDITION_EDIT,
    PRESCRIPTION_EDIT,
    TREATMENT_EDIT,
    IMMUNIZATION_EDIT,
    VACCINATION_EDIT,
    CONTACT_EDIT,
    PORT_HEALTH_INFO_EDIT,
    MATERNAL_HISTORY_EDIT,
    EVENT_EDIT,
    EPI_LOCATION_EDIT,
    INVESTIGATION_NOTES_EDIT,
    FOLLOW_UP_VISITS;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

	public String getName() {
		return this.name();
	}
}

