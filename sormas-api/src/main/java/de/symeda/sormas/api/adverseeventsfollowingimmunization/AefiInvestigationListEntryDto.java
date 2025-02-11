/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.adverseeventsfollowingimmunization;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableIndexDto;

public class AefiInvestigationListEntryDto extends PseudonymizableIndexDto implements Serializable, Cloneable {

	public static final String I18N_PREFIX = "AefiInvestigationListEntry";

	public static final String UUID = "uuid";
	public static final String INVESTIGATION_CASE_ID = "investigationCaseId";
	public static final String INVESTIGATION_DATE = "investigationDate";
	public static final String INVESTIGATION_STAGE = "investigationStage";
	public static final String STATUS_ON_DATE_OF_INVESTIGATION = "statusOnDateOfInvestigation";
	public static final String PRIMARY_VACCINE_NAME = "primaryVaccine";
	public static final String PRIMARY_VACCINE_DETAILS = "primaryVaccineDetails";
	public static final String PRIMARY_VACCINE_DOSE = "primaryVaccineDose";
	public static final String PRIMARY_VACCINE_VACCINATION_DATE = "primaryVaccineVaccinationDate";
	public static final String INVESTIGATION_STATUS = "investigationStatus";
	public static final String AEFI_CLASSIFICATION = "aefiClassification";

	private String investigationCaseId;
	private Date investigationDate;
	private AefiInvestigationStage investigationStage;
	private PatientStatusAtAefiInvestigation statusOnDateOfInvestigation;
	private Vaccine primaryVaccine;
	private String primaryVaccineDetails;
	private String primaryVaccineDose;
	private Date primaryVaccineVaccinationDate;
	private AefiInvestigationStatus investigationStatus;
	private AefiClassification aefiClassification;

	public AefiInvestigationListEntryDto(
		String uuid,
		String investigationCaseId,
		Date investigationDate,
		AefiInvestigationStage investigationStage,
		PatientStatusAtAefiInvestigation statusOnDateOfInvestigation,
		Vaccine primaryVaccine,
		String primaryVaccineDetails,
		String primaryVaccineDose,
		Date primaryVaccineVaccinationDate,
		AefiInvestigationStatus investigationStatus,
		AefiClassification aefiClassification) {

		super(uuid);
		this.investigationCaseId = investigationCaseId;
		this.investigationDate = investigationDate;
		this.investigationStage = investigationStage;
		this.statusOnDateOfInvestigation = statusOnDateOfInvestigation;
		this.primaryVaccine = primaryVaccine;
		this.primaryVaccineDetails = primaryVaccineDetails;
		this.primaryVaccineDose = primaryVaccineDose;
		this.primaryVaccineVaccinationDate = primaryVaccineVaccinationDate;
		this.investigationStatus = investigationStatus;
		this.aefiClassification = aefiClassification;
	}

	public String getInvestigationCaseId() {
		return investigationCaseId;
	}

	public void setInvestigationCaseId(String investigationCaseId) {
		this.investigationCaseId = investigationCaseId;
	}

	public Date getInvestigationDate() {
		return investigationDate;
	}

	public void setInvestigationDate(Date investigationDate) {
		this.investigationDate = investigationDate;
	}

	public AefiInvestigationStage getInvestigationStage() {
		return investigationStage;
	}

	public void setInvestigationStage(AefiInvestigationStage investigationStage) {
		this.investigationStage = investigationStage;
	}

	public PatientStatusAtAefiInvestigation getStatusOnDateOfInvestigation() {
		return statusOnDateOfInvestigation;
	}

	public void setStatusOnDateOfInvestigation(PatientStatusAtAefiInvestigation statusOnDateOfInvestigation) {
		this.statusOnDateOfInvestigation = statusOnDateOfInvestigation;
	}

	public Vaccine getPrimaryVaccine() {
		return primaryVaccine;
	}

	public void setPrimaryVaccine(Vaccine primaryVaccine) {
		this.primaryVaccine = primaryVaccine;
	}

	public String getPrimaryVaccineDetails() {
		return primaryVaccineDetails;
	}

	public void setPrimaryVaccineDetails(String primaryVaccineDetails) {
		this.primaryVaccineDetails = primaryVaccineDetails;
	}

	public String getPrimaryVaccineDose() {
		return primaryVaccineDose;
	}

	public void setPrimaryVaccineDose(String primaryVaccineDose) {
		this.primaryVaccineDose = primaryVaccineDose;
	}

	public Date getPrimaryVaccineVaccinationDate() {
		return primaryVaccineVaccinationDate;
	}

	public void setPrimaryVaccineVaccinationDate(Date primaryVaccineVaccinationDate) {
		this.primaryVaccineVaccinationDate = primaryVaccineVaccinationDate;
	}

	public AefiInvestigationStatus getInvestigationStatus() {
		return investigationStatus;
	}

	public void setInvestigationStatus(AefiInvestigationStatus investigationStatus) {
		this.investigationStatus = investigationStatus;
	}

	public AefiClassification getAefiClassification() {
		return aefiClassification;
	}

	public void setAefiClassification(AefiClassification aefiClassification) {
		this.aefiClassification = aefiClassification;
	}
}
