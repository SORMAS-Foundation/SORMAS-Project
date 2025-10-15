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
package de.symeda.sormas.api.hospitalization;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.ImportIgnore;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.api.utils.Outbreaks;
import de.symeda.sormas.api.utils.YesNoUnknown;

@DependingOnFeatureType(featureType = FeatureType.CASE_SURVEILANCE)
public class HospitalizationDto extends EntityDto {

	private static final long serialVersionUID = 4846215199480684369L;

	public static final String I18N_PREFIX = "CaseHospitalization";

	public static final String ADMITTED_TO_HEALTH_FACILITY = "admittedToHealthFacility";
	public static final String ADMISSION_DATE = "admissionDate";
	public static final String DISCHARGE_DATE = "dischargeDate";
	public static final String ISOLATED = "isolated";
	public static final String ISOLATION_DATE = "isolationDate";
	public static final String LEFT_AGAINST_ADVICE = "leftAgainstAdvice";
	public static final String HOSPITALIZED_PREVIOUSLY = "hospitalizedPreviously";
	public static final String PREVIOUS_HOSPITALIZATIONS = "previousHospitalizations";
	public static final String INTENSIVE_CARE_UNIT = "intensiveCareUnit";
	public static final String INTENSIVE_CARE_UNIT_START = "intensiveCareUnitStart";
	public static final String INTENSIVE_CARE_UNIT_END = "intensiveCareUnitEnd";
	public static final String OXYGEN_PRESCRIBED = "oxygenPrescribed";
	public static final String STILL_HOSPITALIZED = "stillHospitalized";
	public static final String ICU_LENGTH_OF_STAY = "icuLengthOfStay";
	public static final String HOSPITALIZATION_REASON = "hospitalizationReason";
	public static final String OTHER_HOSPITALIZATION_REASON = "otherHospitalizationReason";
	public static final String DESCRIPTION = "description";
	public static final String CURRENTLY_HOSPITALIZED = "currentlyHospitalized";
	public static final String DURATION_OF_HOSPITALIZATION = "durationOfHospitalization";

	// Fields are declared in the order they should appear in the import template

	@Outbreaks
	private YesNoUnknown admittedToHealthFacility;
	private Date admissionDate;
	private Date dischargeDate;
	private YesNoUnknown isolated;
	private Date isolationDate;
	private YesNoUnknown leftAgainstAdvice;

	private YesNoUnknown hospitalizedPreviously;
	@Valid
	private List<PreviousHospitalizationDto> previousHospitalizations = new ArrayList<>();

	@Diseases({
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS })
	private YesNoUnknown intensiveCareUnit;
	@Diseases({
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS })
	private Date intensiveCareUnitStart;
	@Diseases({
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS })
	private Date intensiveCareUnitEnd;

	@Diseases({
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS })
	private YesNoUnknown oxygenPrescribed;
	@Diseases({
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS })
	private YesNoUnknown stillHospitalized;
	@Diseases({
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS })
	private Integer icuLengthOfStay;

	private HospitalizationReasonType hospitalizationReason;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String otherHospitalizationReason;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_BIG, message = Validations.textTooLong)
	private String description;
	private YesNoUnknown currentlyHospitalized;

	@Diseases({
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS })
	private Integer durationOfHospitalization;

	public static HospitalizationDto build() {
		HospitalizationDto hospitalization = new HospitalizationDto();
		hospitalization.setUuid(DataHelper.createUuid());
		return hospitalization;
	}

	public Date getAdmissionDate() {
		return admissionDate;
	}

	public void setAdmissionDate(Date admissionDate) {
		this.admissionDate = admissionDate;
	}

	public Date getDischargeDate() {
		return dischargeDate;
	}

	public void setDischargeDate(Date dischargeDate) {
		this.dischargeDate = dischargeDate;
	}

	public YesNoUnknown getIsolated() {
		return isolated;
	}

	public void setIsolated(YesNoUnknown isolated) {
		this.isolated = isolated;
	}

	public Date getIsolationDate() {
		return isolationDate;
	}

	public void setIsolationDate(Date isolationDate) {
		this.isolationDate = isolationDate;
	}

	@ImportIgnore
	public YesNoUnknown getHospitalizedPreviously() {
		return hospitalizedPreviously;
	}

	public void setHospitalizedPreviously(YesNoUnknown hospitalizedPreviously) {
		this.hospitalizedPreviously = hospitalizedPreviously;
	}

	@ImportIgnore
	public List<PreviousHospitalizationDto> getPreviousHospitalizations() {
		return previousHospitalizations;
	}

	public void setPreviousHospitalizations(List<PreviousHospitalizationDto> previousHospitalizations) {
		this.previousHospitalizations = previousHospitalizations;
	}

	public YesNoUnknown getAdmittedToHealthFacility() {
		return admittedToHealthFacility;
	}

	public void setAdmittedToHealthFacility(YesNoUnknown admittedToHealthFacility) {
		this.admittedToHealthFacility = admittedToHealthFacility;
	}

	public YesNoUnknown getIntensiveCareUnit() {
		return intensiveCareUnit;
	}

	public void setIntensiveCareUnit(YesNoUnknown intensiveCareUnit) {
		this.intensiveCareUnit = intensiveCareUnit;
	}

	public Date getIntensiveCareUnitStart() {
		return intensiveCareUnitStart;
	}

	public void setIntensiveCareUnitStart(Date intensiveCareUnitStart) {
		this.intensiveCareUnitStart = intensiveCareUnitStart;
	}

	public Date getIntensiveCareUnitEnd() {
		return intensiveCareUnitEnd;
	}

	public void setIntensiveCareUnitEnd(Date intensiveCareUnitEnd) {
		this.intensiveCareUnitEnd = intensiveCareUnitEnd;
	}

	public YesNoUnknown getLeftAgainstAdvice() {
		return leftAgainstAdvice;
	}

	public void setLeftAgainstAdvice(YesNoUnknown leftAgainstAdvice) {
		this.leftAgainstAdvice = leftAgainstAdvice;
	}

	public HospitalizationReasonType getHospitalizationReason() {
		return hospitalizationReason;
	}

	public void setHospitalizationReason(HospitalizationReasonType hospitalizationReason) {
		this.hospitalizationReason = hospitalizationReason;
	}

	public String getOtherHospitalizationReason() {
		return otherHospitalizationReason;
	}

	public void setOtherHospitalizationReason(String otherHospitalizationReason) {
		this.otherHospitalizationReason = otherHospitalizationReason;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public YesNoUnknown getCurrentlyHospitalized() {
		return currentlyHospitalized;
	}

	public void setCurrentlyHospitalized(YesNoUnknown currentlyHospitalized) {
		this.currentlyHospitalized = currentlyHospitalized;
	}

	public YesNoUnknown getOxygenPrescribed() {
		return oxygenPrescribed;
	}

	public void setOxygenPrescribed(YesNoUnknown oxygenPrescribed) {
		this.oxygenPrescribed = oxygenPrescribed;
	}

	public YesNoUnknown getStillHospitalized() {
		return stillHospitalized;
	}

	public void setStillHospitalized(YesNoUnknown stillHospitalized) {
		this.stillHospitalized = stillHospitalized;
	}

	public Integer getIcuLengthOfStay() {
		return icuLengthOfStay;
	}

	public void setIcuLengthOfStay(Integer icuLengthOfStay) {
		this.icuLengthOfStay = icuLengthOfStay;
	}

	public Integer getDurationOfHospitalization() {
		return durationOfHospitalization;
	}

	public void setDurationOfHospitalization(Integer durationOfHospitalization) {
		this.durationOfHospitalization = durationOfHospitalization;
	}
}
