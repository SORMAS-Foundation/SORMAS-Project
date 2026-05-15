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
package de.symeda.sormas.backend.epidata;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.sormas.api.epidata.CaseImportedStatus;
import de.symeda.sormas.api.epidata.ClusterType;
import de.symeda.sormas.api.exposure.InfectionSource;
import de.symeda.sormas.api.exposure.ModeOfTransmission;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.activityascase.ActivityAsCase;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.NotExposedToApi;
import de.symeda.sormas.backend.exposure.Exposure;
import de.symeda.sormas.backend.infrastructure.country.Country;

@Entity
public class EpiData extends AbstractDomainObject {

	private static final long serialVersionUID = -8294812479501735785L;

	public static final String TABLE_NAME = "epidata";

	public static final String CONTACT_WITH_SOURCE_CASE_KNOWN = "contactWithSourceCaseKnown";
	public static final String EXPOSURES = "exposures";
	public static final String ACTIVITIES_AS_CASE = "activitiesAsCase";
	public static final String OTHER_DETAILS = "otherDetails";

	private YesNoUnknown exposureDetailsKnown;
	private YesNoUnknown activityAsCaseDetailsKnown;
	private YesNoUnknown contactWithSourceCaseKnown;
	private YesNoUnknown highTransmissionRiskArea;
	private YesNoUnknown largeOutbreaksArea;
	private YesNoUnknown areaInfectedAnimals;

	private YesNoUnknown importedCase;
	private CaseImportedStatus caseImportedStatus;
	private ClusterType clusterType;
	private String clusterTypeText;
	private boolean clusterRelated;

	// Giardiasis & Cryptosporidiosis specific
	private InfectionSource infectionSource;
	private String infectionSourceText;
	private ModeOfTransmission modeOfTransmission;
	private String modeOfTransmissionType;

	private Country country;

	private List<Exposure> exposures = new ArrayList<>();
	private List<ActivityAsCase> activitiesAsCase = new ArrayList<>();
	@NotExposedToApi
	private Date changeDateOfEmbeddedLists;

	private String otherDetails;

	private YesNoUnknown airportWorker;
	private YesNoUnknown healthcareProfessional;
	private String placeOfInfection;
	private String residenceAtOnset;

	private Date exposureInvestigationFromDate;
	private Date exposureInvestigationToDate;
	private Date activityAsCaseFromDate;
	private Date activityAsCaseToDate;

	private FoodHistory foodHistory;

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getExposureDetailsKnown() {
		return exposureDetailsKnown;
	}

	public void setExposureDetailsKnown(YesNoUnknown exposureDetailsKnown) {
		this.exposureDetailsKnown = exposureDetailsKnown;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getActivityAsCaseDetailsKnown() {
		return activityAsCaseDetailsKnown;
	}

	public void setActivityAsCaseDetailsKnown(YesNoUnknown activityAsCaseDetailsKnown) {
		this.activityAsCaseDetailsKnown = activityAsCaseDetailsKnown;
	}

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = Exposure.EPI_DATA)
	public List<Exposure> getExposures() {
		return exposures;
	}

	public void setExposures(List<Exposure> exposures) {
		this.exposures = exposures;
	}

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = Exposure.EPI_DATA)
	public List<ActivityAsCase> getActivitiesAsCase() {
		return activitiesAsCase;
	}

	public void setActivitiesAsCase(List<ActivityAsCase> activitiesAsCase) {
		this.activitiesAsCase = activitiesAsCase;
	}

	/**
	 * This change date has to be set whenever exposures are modified
	 */
	public Date getChangeDateOfEmbeddedLists() {
		return changeDateOfEmbeddedLists;
	}

	public void setChangeDateOfEmbeddedLists(Date changeDateOfEmbeddedLists) {
		this.changeDateOfEmbeddedLists = changeDateOfEmbeddedLists;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getAreaInfectedAnimals() {
		return areaInfectedAnimals;
	}

	public void setAreaInfectedAnimals(YesNoUnknown areaInfectedAnimals) {
		this.areaInfectedAnimals = areaInfectedAnimals;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getHighTransmissionRiskArea() {
		return highTransmissionRiskArea;
	}

	public void setHighTransmissionRiskArea(YesNoUnknown highTransmissionRiskArea) {
		this.highTransmissionRiskArea = highTransmissionRiskArea;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getLargeOutbreaksArea() {
		return largeOutbreaksArea;
	}

	public void setLargeOutbreaksArea(YesNoUnknown largeOutbreaksArea) {
		this.largeOutbreaksArea = largeOutbreaksArea;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getContactWithSourceCaseKnown() {
		return contactWithSourceCaseKnown;
	}

	public void setContactWithSourceCaseKnown(YesNoUnknown contactWithSourceCaseKnown) {
		this.contactWithSourceCaseKnown = contactWithSourceCaseKnown;
	}

	@Enumerated(EnumType.STRING)
	public CaseImportedStatus getCaseImportedStatus() {
		return caseImportedStatus;
	}

	public void setCaseImportedStatus(CaseImportedStatus caseImportedStatus) {
		this.caseImportedStatus = caseImportedStatus;
	}

	@Enumerated(EnumType.STRING)
	public ClusterType getClusterType() {
		return clusterType;
	}

	public void setClusterType(ClusterType clusterType) {
		this.clusterType = clusterType;
	}

	public String getClusterTypeText() {
		return clusterTypeText;
	}

	public void setClusterTypeText(String clusterTypeText) {
		this.clusterTypeText = clusterTypeText;
	}

	@Column(nullable = false)
	public boolean isClusterRelated() {
		return clusterRelated;
	}

	public void setClusterRelated(boolean clusterRelated) {
		this.clusterRelated = clusterRelated;
	}

	@Enumerated(EnumType.STRING)
	public ModeOfTransmission getModeOfTransmission() {
		return modeOfTransmission;
	}

	public void setModeOfTransmission(ModeOfTransmission modeOfTransmission) {
		this.modeOfTransmission = modeOfTransmission;
	}

	public String getModeOfTransmissionType() {
		return modeOfTransmissionType;
	}

	public void setModeOfTransmissionType(String modeOfTransmissionType) {
		this.modeOfTransmissionType = modeOfTransmissionType;
	}

	@Enumerated(EnumType.STRING)
	public InfectionSource getInfectionSource() {
		return infectionSource;
	}

	public void setInfectionSource(InfectionSource infectionSource) {
		this.infectionSource = infectionSource;
	}

	public String getInfectionSourceText() {
		return infectionSourceText;
	}

	public void setInfectionSourceText(String infectionSourceText) {
		this.infectionSourceText = infectionSourceText;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getImportedCase() {
		return importedCase;
	}

	public void setImportedCase(YesNoUnknown importedCase) {
		this.importedCase = importedCase;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	@Column(columnDefinition = "text")
	public String getOtherDetails() {
		return otherDetails;
	}

	public void setOtherDetails(String otherDetails) {
		this.otherDetails = otherDetails;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getAirportWorker() {
		return airportWorker;
	}

	public void setAirportWorker(YesNoUnknown airportWorker) {
		this.airportWorker = airportWorker;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getHealthcareProfessional() {
		return healthcareProfessional;
	}

	public void setHealthcareProfessional(YesNoUnknown healthcareProfessional) {
		this.healthcareProfessional = healthcareProfessional;
	}

	@Column(columnDefinition = "text")
	public String getPlaceOfInfection() {
		return placeOfInfection;
	}

	public void setPlaceOfInfection(String placeOfInfection) {
		this.placeOfInfection = placeOfInfection;
	}

	@Column(columnDefinition = "text")
	public String getResidenceAtOnset() {
		return residenceAtOnset;
	}

	public void setResidenceAtOnset(String residenceAtOnset) {
		this.residenceAtOnset = residenceAtOnset;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getExposureInvestigationFromDate() {
		return exposureInvestigationFromDate;
	}

	public void setExposureInvestigationFromDate(Date exposureInvestigationFromDate) {
		this.exposureInvestigationFromDate = exposureInvestigationFromDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getExposureInvestigationToDate() {
		return exposureInvestigationToDate;
	}

	public void setExposureInvestigationToDate(Date exposureInvestigationToDate) {
		this.exposureInvestigationToDate = exposureInvestigationToDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getActivityAsCaseFromDate() {
		return activityAsCaseFromDate;
	}

	public void setActivityAsCaseFromDate(Date activityAsCaseFromDate) {
		this.activityAsCaseFromDate = activityAsCaseFromDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getActivityAsCaseToDate() {
		return activityAsCaseToDate;
	}

	public void setActivityAsCaseToDate(Date activityAsCaseToDate) {
		this.activityAsCaseToDate = activityAsCaseToDate;
	}

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	public FoodHistory getFoodHistory() {
		return foodHistory;
	}

	public void setFoodHistory(FoodHistory foodHistory) {
		this.foodHistory = foodHistory;
	}

}
