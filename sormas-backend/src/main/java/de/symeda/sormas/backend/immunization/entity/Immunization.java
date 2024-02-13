/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.immunization.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import de.symeda.sormas.api.immunization.ImmunizationManagementStatus;
import de.symeda.sormas.api.immunization.ImmunizationStatus;
import de.symeda.sormas.api.immunization.IsImmunization;

@Entity(name = "immunization")
@Table(name = "immunization")
public class Immunization extends BaseImmunization implements IsImmunization {

	public static final String TABLE_NAME = "immunization";

	public static final String DISEASE = "disease";
	public static final String PERSON = "person";
	public static final String PERSON_ID = "personId";
	public static final String REPORT_DATE = "reportDate";
	public static final String REPORTING_USER = "reportingUser";
	public static final String IMMUNIZATION_STATUS = "immunizationStatus";
	public static final String MEANS_OF_IMMUNIZATION = "meansOfImmunization";
	public static final String IMMUNIZATION_MANAGEMENT_STATUS = "immunizationManagementStatus";
	public static final String EXTERNAL_ID = "externalId";
	public static final String RESPONSIBLE_REGION = "responsibleRegion";
	public static final String RESPONSIBLE_DISTRICT = "responsibleDistrict";
	public static final String RESPONSIBLE_COMMUNITY = "responsibleCommunity";
	public static final String FACILITY_TYPE = "facilityType";
	public static final String HEALTH_FACILITY = "healthFacility";
	public static final String HEALTH_FACILITY_DETAILS = "healthFacilityDetails";
	public static final String START_DATE = "startDate";
	public static final String END_DATE = "endDate";
	public static final String NUMBER_OF_DOSES = "numberOfDoses";
	public static final String NUMBER_OF_DOSES_DETAILS = "numberOfDosesDetails";
	public static final String PREVIOUS_INFECTION = "previousInfection";
	public static final String LAST_INFECTION_DATE = "lastInfectionDate";
	public static final String ADDITIONAL_DETAILS = "additionalDetails";
	public static final String POSITIVE_TEST_RESULT_DATE = "positiveTestResultDate";
	public static final String RECOVERY_DATE = "recoveryDate";
	public static final String VALID_UNTIL = "validUntil";
	public static final String RELATED_CASE = "relatedCase";
	public static final String VACCINATIONS = "vaccinations";
	public static final String SORMAS_TO_SORMAS_ORIGIN_INFO = "sormasToSormasOriginInfo";
	public static final String SORMAS_TO_SORMAS_SHARES = "sormasToSormasShares";

	public static Immunization build() {
		Immunization immunization = new Immunization();
		immunization.setImmunizationStatus(ImmunizationStatus.PENDING);
		immunization.setImmunizationManagementStatus(ImmunizationManagementStatus.SCHEDULED);
		return immunization;
	}
}
