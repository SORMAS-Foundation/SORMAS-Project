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
package de.symeda.sormas.api.epidata;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.ImportIgnore;
import de.symeda.sormas.api.activityascase.ActivityAsCaseDto;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.exposure.InfectionSource;
import de.symeda.sormas.api.exposure.ModeOfTransmission;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.api.utils.HideForCountriesExcept;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;

@DependingOnFeatureType(featureType = {
	FeatureType.CASE_SURVEILANCE,
	FeatureType.CONTACT_TRACING })
public class EpiDataDto extends PseudonymizableDto {

	private static final long serialVersionUID = 6292411396563549093L;

	public static final String I18N_PREFIX = "EpiData";

	public static final String EXPOSURE_DETAILS_KNOWN = "exposureDetailsKnown";
	public static final String ACTIVITY_AS_CASE_DETAILS_KNOWN = "activityAsCaseDetailsKnown";
	public static final String CONTACT_WITH_SOURCE_CASE_KNOWN = "contactWithSourceCaseKnown";
	public static final String EXPOSURES = "exposures";
	public static final String ACTIVITIES_AS_CASE = "activitiesAsCase";
	public static final String AREA_INFECTED_ANIMALS = "areaInfectedAnimals";
	public static final String HIGH_TRANSMISSION_RISK_AREA = "highTransmissionRiskArea";
	public static final String LARGE_OUTBREAKS_AREA = "largeOutbreaksArea";
	public static final String CASE_IMPORTED_STATUS = "caseImportedStatus";
	public static final String CLUSTER_TYPE = "clusterType";
	public static final String CLUSTER_TYPE_TEXT = "clusterTypeText";
	public static final String CLUSTER_RELATED = "clusterRelated";
	public static final String MODE_OF_TRANSMISSION = "modeOfTransmission";
	public static final String MODE_OF_TRANSMISSION_TYPE = "modeOfTransmissionType";
	public static final String INFECTION_SOURCE = "infectionSource";
	public static final String INFECTION_SOURCE_TEXT = "infectionSourceText";
	public static final String IMPORTED_CASE = "importedCase";
	public static final String COUNTRY = "country";

	private YesNoUnknown exposureDetailsKnown;
	private YesNoUnknown activityAsCaseDetailsKnown;
	private YesNoUnknown contactWithSourceCaseKnown;
	private YesNoUnknown highTransmissionRiskArea;
	private YesNoUnknown largeOutbreaksArea;
	@Diseases({
		Disease.MEASLES })
	private CaseImportedStatus caseImportedStatus;

	@Diseases({
		Disease.GIARDIASIS })
	private YesNoUnknown importedCase;

	@HideForCountriesExcept(countries = {
		CountryHelper.COUNTRY_CODE_LUXEMBOURG })
	@Diseases({
		Disease.MEASLES })
	private ClusterType clusterType;

	@HideForCountriesExcept(countries = {
		CountryHelper.COUNTRY_CODE_LUXEMBOURG })
	@Diseases({
		Disease.MEASLES })
	private boolean clusterRelated;

	@HideForCountriesExcept(countries = {
		CountryHelper.COUNTRY_CODE_LUXEMBOURG })
	@Diseases({
		Disease.MEASLES })
	private String clusterTypeText;

	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.NEW_INFLUENZA,
		Disease.ANTHRAX,
		Disease.POLIO,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown areaInfectedAnimals;

	@Diseases({
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS })
	private ModeOfTransmission modeOfTransmission;

	@Diseases({
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS })
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String modeOfTransmissionType;

	@Diseases({
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS })
	private InfectionSource infectionSource;
	@Diseases({
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS })
	private String infectionSourceText;

	@Diseases({
		Disease.GIARDIASIS })
	private CountryReferenceDto country;

	@Valid
	private List<ExposureDto> exposures = new ArrayList<>();

	@Valid
	private List<ActivityAsCaseDto> activitiesAsCase = new ArrayList<>();

	public YesNoUnknown getExposureDetailsKnown() {
		return exposureDetailsKnown;
	}

	public void setExposureDetailsKnown(YesNoUnknown exposureDetailsKnown) {
		this.exposureDetailsKnown = exposureDetailsKnown;
	}

	public YesNoUnknown getActivityAsCaseDetailsKnown() {
		return activityAsCaseDetailsKnown;
	}

	public void setActivityAsCaseDetailsKnown(YesNoUnknown activityAsCaseDetailsKnown) {
		this.activityAsCaseDetailsKnown = activityAsCaseDetailsKnown;
	}

	@ImportIgnore
	public List<ExposureDto> getExposures() {
		return exposures;
	}

	public void setExposures(List<ExposureDto> exposures) {
		this.exposures = exposures;
	}

	@ImportIgnore
	public List<ActivityAsCaseDto> getActivitiesAsCase() {
		return activitiesAsCase;
	}

	public void setActivitiesAsCase(List<ActivityAsCaseDto> activitiesAsCase) {
		this.activitiesAsCase = activitiesAsCase;
	}

	public YesNoUnknown getContactWithSourceCaseKnown() {
		return contactWithSourceCaseKnown;
	}

	public void setContactWithSourceCaseKnown(YesNoUnknown contactWithSourceCaseKnown) {
		this.contactWithSourceCaseKnown = contactWithSourceCaseKnown;
	}

	public YesNoUnknown getHighTransmissionRiskArea() {
		return highTransmissionRiskArea;
	}

	public void setHighTransmissionRiskArea(YesNoUnknown highTransmissionRiskArea) {
		this.highTransmissionRiskArea = highTransmissionRiskArea;
	}

	public YesNoUnknown getLargeOutbreaksArea() {
		return largeOutbreaksArea;
	}

	public void setLargeOutbreaksArea(YesNoUnknown largeOutbreaksArea) {
		this.largeOutbreaksArea = largeOutbreaksArea;
	}

	public YesNoUnknown getAreaInfectedAnimals() {
		return areaInfectedAnimals;
	}

	public void setAreaInfectedAnimals(YesNoUnknown areaInfectedAnimals) {
		this.areaInfectedAnimals = areaInfectedAnimals;
	}

	public static EpiDataDto build() {

		EpiDataDto epiData = new EpiDataDto();
		epiData.setUuid(DataHelper.createUuid());
		return epiData;
	}

	public CaseImportedStatus getCaseImportedStatus() {
		return caseImportedStatus;
	}

	public void setCaseImportedStatus(CaseImportedStatus caseImportedStatus) {
		this.caseImportedStatus = caseImportedStatus;
	}

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

	public boolean isClusterRelated() {
		return clusterRelated;
	}

	public void setClusterRelated(boolean clusterRelated) {
		this.clusterRelated = clusterRelated;
	}

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

	public YesNoUnknown getImportedCase() {
		return importedCase;
	}

	public void setImportedCase(YesNoUnknown importedCase) {
		this.importedCase = importedCase;
	}

	public CountryReferenceDto getCountry() {
		return country;
	}

	public void setCountry(CountryReferenceDto country) {
		this.country = country;
	}

	@Override
	public EpiDataDto clone() throws CloneNotSupportedException {
		EpiDataDto clone = (EpiDataDto) super.clone();
		List<ActivityAsCaseDto> activityAsCaseDtos = new ArrayList<>();
		for (ActivityAsCaseDto activityAsCase : getActivitiesAsCase()) {
			activityAsCaseDtos.add(activityAsCase.clone());
		}
		clone.getActivitiesAsCase().clear();
		clone.getActivitiesAsCase().addAll(activityAsCaseDtos);

		List<ExposureDto> exposureDtos = new ArrayList<>();
		for (ExposureDto exposure : getExposures()) {
			exposureDtos.add(exposure.clone());
		}
		clone.getExposures().clear();
		clone.getExposures().addAll(exposureDtos);

		return clone;
	}
}
