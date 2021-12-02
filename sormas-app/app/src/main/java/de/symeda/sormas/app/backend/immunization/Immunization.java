/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.backend.immunization;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.immunization.ImmunizationManagementStatus;
import de.symeda.sormas.api.immunization.ImmunizationStatus;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.PseudonymizableAdo;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.Country;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.sormastosormas.SormasToSormasOriginInfo;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.vaccination.Vaccination;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;

@Entity(name = Immunization.TABLE_NAME)
@DatabaseTable(tableName = Immunization.TABLE_NAME)
public class Immunization extends PseudonymizableAdo {

	public static final String TABLE_NAME = "immunization";
	public static final String I18N_PREFIX = "ImmunizationData";

	public static final String PERSON = "person";
	public static final String DISEASE = "disease";
	public static final String DISEASE_DETAILS = "diseaseDetails";
	public static final String RESPONSIBLE_REGION = "responsibleRegion";

	public static final String POSITIVE_TEST_RESULT_DATE = "positivetestresultdate";
	public static final String RECOVERY_DATE = "recoveryDate";
	public static final String REPORT_DATE = "reportDate";
	public static final String START_DATE = "startDate";
	public static final String END_DATE = "endDate";
	public static final String VALID_FROM = "validFrom";
	public static final String VALID_UNTIL = "validUntil";
	public static final String IMMUNIZATION_STATUS = "immunizationStatus";
	public static final String MEANS_OF_IMMUNIZATION = "meansOfImmunization";
	public static final String IMMUNIZATION_MANAGEMENT_STATUS = "immunizationManagementStatus";

	public static final String HEALTH_FACILITY = "healthFacility_id";

	@Enumerated(EnumType.STRING)
	private Disease disease;
	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String diseaseDetails;
	@DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false, maxForeignAutoRefreshLevel = 3)
	private Person person;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date reportDate;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private User reportingUser;
	@DatabaseField
	private boolean archived;
	@Enumerated(EnumType.STRING)
	private ImmunizationStatus immunizationStatus;
	@Enumerated(EnumType.STRING)
	private MeansOfImmunization meansOfImmunization;
	@Column(columnDefinition = "text")
	private String meansOfImmunizationDetails;
	@Enumerated(EnumType.STRING)
	private ImmunizationManagementStatus immunizationManagementStatus;
	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String externalId;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Region responsibleRegion;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private District responsibleDistrict;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Community responsibleCommunity;
	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3)
	private Country country;

	@Enumerated(EnumType.STRING)
	private FacilityType facilityType;
	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3)
	private Facility healthFacility;
	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String healthFacilityDetails;

	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date startDate;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date endDate;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date validFrom;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date validUntil;
	@DatabaseField
	private Integer numberOfDoses;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown previousInfection;

	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date lastInfectionDate;
	@Column(columnDefinition = "text")
	private String additionalDetails;

	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date positiveTestResultDate;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date recoveryDate;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Case relatedCase;

	private List<Vaccination> vaccinations = new ArrayList<>();

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private SormasToSormasOriginInfo sormasToSormasOriginInfo;
	@DatabaseField
	private boolean ownershipHandedOver;

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public User getReportingUser() {
		return reportingUser;
	}

	public void setReportingUser(User reportingUser) {
		this.reportingUser = reportingUser;
	}

	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	public ImmunizationStatus getImmunizationStatus() {
		return immunizationStatus;
	}

	public void setImmunizationStatus(ImmunizationStatus immunizationStatus) {
		this.immunizationStatus = immunizationStatus;
	}

	public MeansOfImmunization getMeansOfImmunization() {
		return meansOfImmunization;
	}

	public void setMeansOfImmunization(MeansOfImmunization meansOfImmunization) {
		this.meansOfImmunization = meansOfImmunization;
	}

	public ImmunizationManagementStatus getImmunizationManagementStatus() {
		return immunizationManagementStatus;
	}

	public void setImmunizationManagementStatus(ImmunizationManagementStatus immunizationManagementStatus) {
		this.immunizationManagementStatus = immunizationManagementStatus;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public Region getResponsibleRegion() {
		return responsibleRegion;
	}

	public void setResponsibleRegion(Region responsibleRegion) {
		this.responsibleRegion = responsibleRegion;
	}

	public District getResponsibleDistrict() {
		return responsibleDistrict;
	}

	public void setResponsibleDistrict(District responsibleDistrict) {
		this.responsibleDistrict = responsibleDistrict;
	}

	public Community getResponsibleCommunity() {
		return responsibleCommunity;
	}

	public void setResponsibleCommunity(Community responsibleCommunity) {
		this.responsibleCommunity = responsibleCommunity;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Integer getNumberOfDoses() {
		return numberOfDoses;
	}

	public void setNumberOfDoses(Integer numberOfDoses) {
		this.numberOfDoses = numberOfDoses;
	}

	public YesNoUnknown getPreviousInfection() {
		return previousInfection;
	}

	public void setPreviousInfection(YesNoUnknown previousInfection) {
		this.previousInfection = previousInfection;
	}

	public Date getLastInfectionDate() {
		return lastInfectionDate;
	}

	public void setLastInfectionDate(Date lastInfectionDate) {
		this.lastInfectionDate = lastInfectionDate;
	}

	public String getAdditionalDetails() {
		return additionalDetails;
	}

	public void setAdditionalDetails(String additionalDetails) {
		this.additionalDetails = additionalDetails;
	}

	public Date getPositiveTestResultDate() {
		return positiveTestResultDate;
	}

	public void setPositiveTestResultDate(Date positiveTestResultDate) {
		this.positiveTestResultDate = positiveTestResultDate;
	}

	public Date getRecoveryDate() {
		return recoveryDate;
	}

	public void setRecoveryDate(Date recoveryDate) {
		this.recoveryDate = recoveryDate;
	}

	public Case getRelatedCase() {
		return relatedCase;
	}

	public void setRelatedCase(Case relatedCase) {
		this.relatedCase = relatedCase;
	}

	public String getMeansOfImmunizationDetails() {
		return meansOfImmunizationDetails;
	}

	public void setMeansOfImmunizationDetails(String meansOfImmunizationDetails) {
		this.meansOfImmunizationDetails = meansOfImmunizationDetails;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public List<Vaccination> getVaccinations() {
		return vaccinations;
	}

	public void setVaccinations(List<Vaccination> vaccinations) {
		this.vaccinations = vaccinations;
	}

	public FacilityType getFacilityType() {
		return facilityType;
	}

	public void setFacilityType(FacilityType facilityType) {
		this.facilityType = facilityType;
	}

	public Facility getHealthFacility() {
		return healthFacility;
	}

	public void setHealthFacility(Facility healthFacility) {
		this.healthFacility = healthFacility;
	}

	public String getHealthFacilityDetails() {
		return healthFacilityDetails;
	}

	public void setHealthFacilityDetails(String healthFacilityDetails) {
		this.healthFacilityDetails = healthFacilityDetails;
	}

	public Date getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	public Date getValidUntil() {
		return validUntil;
	}

	public void setValidUntil(Date validUntil) {
		this.validUntil = validUntil;
	}

	public String getDiseaseDetails() {
		return diseaseDetails;
	}

	public void setDiseaseDetails(String diseaseDetails) {
		this.diseaseDetails = diseaseDetails;
	}

	public SormasToSormasOriginInfo getSormasToSormasOriginInfo() {
		return sormasToSormasOriginInfo;
	}

	public void setSormasToSormasOriginInfo(SormasToSormasOriginInfo sormasToSormasOriginInfo) {
		this.sormasToSormasOriginInfo = sormasToSormasOriginInfo;
	}

	public boolean isOwnershipHandedOver() {
		return ownershipHandedOver;
	}

	public void setOwnershipHandedOver(boolean ownershipHandedOver) {
		this.ownershipHandedOver = ownershipHandedOver;
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}

	public void update(Immunization immunization) {
		this.setDiseaseDetails(immunization.getDiseaseDetails());
		this.setPerson(immunization.getPerson());
		this.setReportDate(immunization.getReportDate());
		this.setReportingUser(immunization.getReportingUser());
		this.setArchived(immunization.isArchived());
		this.setImmunizationStatus(immunization.getImmunizationStatus());
		this.setMeansOfImmunization(immunization.getMeansOfImmunization());
		this.setMeansOfImmunizationDetails(immunization.getMeansOfImmunizationDetails());
		this.setImmunizationManagementStatus(immunization.getImmunizationManagementStatus());
		this.setExternalId(immunization.getExternalId());
		this.setResponsibleRegion(immunization.getResponsibleRegion());
		this.setResponsibleDistrict(immunization.getResponsibleDistrict());
		this.setResponsibleCommunity(immunization.getResponsibleCommunity());
		this.setCountry(immunization.getCountry());
		this.setStartDate(immunization.getStartDate());
		this.setEndDate(immunization.getEndDate());
		this.setNumberOfDoses(immunization.getNumberOfDoses());
		this.setPreviousInfection(immunization.getPreviousInfection());
		this.setLastInfectionDate(immunization.getLastInfectionDate());
		this.setAdditionalDetails(immunization.getAdditionalDetails());
		this.setPositiveTestResultDate(immunization.getPositiveTestResultDate());
		this.setRecoveryDate(immunization.getRecoveryDate());
		this.setRelatedCase(immunization.getRelatedCase());
		this.setFacilityType(immunization.getFacilityType());
		this.setHealthFacility(immunization.getHealthFacility());
		this.setHealthFacilityDetails(immunization.getHealthFacilityDetails());
		this.setStartDate(immunization.getStartDate());
		this.setEndDate(immunization.getEndDate());
		this.setValidFrom(immunization.getValidFrom());
		this.setValidUntil(immunization.getValidUntil());
	}
}
