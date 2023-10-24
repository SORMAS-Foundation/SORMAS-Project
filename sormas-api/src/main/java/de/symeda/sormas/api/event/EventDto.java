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

package de.symeda.sormas.api.event;

import java.util.Date;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.exposure.WorkEnvironment;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.format.ImportExportFormat;
import de.symeda.sormas.api.importexport.format.ImportFormat;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.sormastosormas.S2SIgnoreProperty;
import de.symeda.sormas.api.sormastosormas.SormasToSormasConfig;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareableDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.api.utils.HideForCountriesExcept;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.YesNoUnknown;

@DependingOnFeatureType(featureType = FeatureType.EVENT_SURVEILLANCE)
public class EventDto extends SormasToSormasShareableDto {

	private static final long serialVersionUID = 2430932452606853497L;

	public static final long APPROXIMATE_JSON_SIZE_IN_BYTES = 13356;

	public static final String I18N_PREFIX = "Event";

	public static final String EVENT_STATUS = "eventStatus";
	public static final String EVENT_INVESTIGATION_STATUS = "eventInvestigationStatus";
	public static final String RISK_LEVEL = "riskLevel";
	public static final String SPECIFIC_RISK = "specificRisk";
	public static final String EVENT_INVESTIGATION_START_DATE = "eventInvestigationStartDate";
	public static final String EVENT_INVESTIGATION_END_DATE = "eventInvestigationEndDate";
	public static final String EVENT_PARTICIPANTS = "eventParticipants";
	public static final String PARTICIPANTS_COUNT = "participantCount";
	public static final String EVENT_ACTIONS = "eventActions";
	public static final String EVENT_MANAGEMENT_STATUS = "eventManagementStatus";
	public static final String EXTERNAL_ID = "externalId";
	public static final String EXTERNAL_TOKEN = "externalToken";
	public static final String EVENT_TITLE = "eventTitle";
	public static final String EVENT_DESC = "eventDesc";
	public static final String NOSOCOMIAL = "nosocomial";
	public static final String START_DATE = "startDate";
	public static final String END_DATE = "endDate";
	public static final String MULTI_DAY_EVENT = "multiDayEvent";
	public static final String REPORT_DATE_TIME = "reportDateTime";
	public static final String REPORTING_USER = "reportingUser";
	public static final String EVOLUTION_DATE = "evolutionDate";
	public static final String EVOLUTION_COMMENT = "evolutionComment";
	public static final String EVENT_LOCATION = "eventLocation";
	public static final String TYPE_OF_PLACE = "typeOfPlace";
	public static final String MEANS_OF_TRANSPORT = "meansOfTransport";
	public static final String MEANS_OF_TRANSPORT_DETAILS = "meansOfTransportDetails";
	public static final String CONNECTION_NUMBER = "connectionNumber";
	public static final String TRAVEL_DATE = "travelDate";
	public static final String WORK_ENVIRONMENT = "workEnvironment";
	public static final String SRC_TYPE = "srcType";
	public static final String SRC_INSTITUTIONAL_PARTNER_TYPE = "srcInstitutionalPartnerType";
	public static final String SRC_INSTITUTIONAL_PARTNER_TYPE_DETAILS = "srcInstitutionalPartnerTypeDetails";
	public static final String SRC_FIRST_NAME = "srcFirstName";
	public static final String SRC_LAST_NAME = "srcLastName";
	public static final String SRC_TEL_NO = "srcTelNo";
	public static final String SRC_EMAIL = "srcEmail";
	public static final String SRC_MEDIA_WEBSITE = "srcMediaWebsite";
	public static final String SRC_MEDIA_NAME = "srcMediaName";
	public static final String SRC_MEDIA_DETAILS = "srcMediaDetails";
	public static final String DISEASE = "disease";
	public static final String DISEASE_DETAILS = "diseaseDetails";
	public static final String DISEASE_VARIANT = "diseaseVariant";
	public static final String DISEASE_VARIANT_DETAILS = "diseaseVariantDetails";
	public static final String RESPONSIBLE_USER = "responsibleUser";
	public static final String TYPE_OF_PLACE_TEXT = "typeOfPlaceText";
	public static final String REPORT_LAT = "reportLat";
	public static final String REPORT_LON = "reportLon";
	public static final String TRANSREGIONAL_OUTBREAK = "transregionalOutbreak";
	public static final String DISEASE_TRANSMISSION_MODE = "diseaseTransmissionMode";
	public static final String SUPERORDINATE_EVENT = "superordinateEvent";
	public static final String INFECTION_PATH_CERTAINTY = "infectionPathCertainty";
	public static final String HUMAN_TRANSMISSION_MODE = "humanTransmissionMode";
	public static final String PARENTERAL_TRANSMISSION_MODE = "parenteralTransmissionMode";
	public static final String MEDICALLY_ASSOCIATED_TRANSMISSION_MODE = "medicallyAssociatedTransmissionMode";
	public static final String EPIDEMIOLOGICAL_EVIDENCE = "epidemiologicalEvidence";
	public static final String EPIDEMIOLOGICAL_EVIDENCE_DETAILS = "epidemiologicalEvidenceDetails";
	public static final String LABORATORY_DIAGNOSTIC_EVIDENCE = "laboratoryDiagnosticEvidence";
	public static final String LABORATORY_DIAGNOSTIC_EVIDENCE_DETAILS = "laboratoryDiagnosticEvidenceDetails";
	public static final String INTERNAL_TOKEN = "internalToken";
	public static final String EVENT_GROUP = "eventGroup";
	public static final String EVENT_IDENTIFICATION_SOURCE = "eventIdentificationSource";
	public static final String DELETION_REASON = "deletionReason";
	public static final String OTHER_DELETION_REASON = "otherDeletionReason";

	private EventReferenceDto superordinateEvent;
	@NotNull(message = Validations.validEventStatus)
	private EventStatus eventStatus;
	private RiskLevel riskLevel;
	private SpecificRisk specificRisk;
	private EventInvestigationStatus eventInvestigationStatus;
	private Date eventInvestigationStartDate;
	private Date eventInvestigationEndDate;
	private EventManagementStatus eventManagementStatus;
	@S2SIgnoreProperty(configProperty = SormasToSormasConfig.SORMAS2SORMAS_IGNORE_EXTERNAL_ID)
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String externalId;
	@S2SIgnoreProperty(configProperty = SormasToSormasConfig.SORMAS2SORMAS_IGNORE_EXTERNAL_TOKEN)
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String externalToken;
	@NotBlank(message = Validations.validEventTitle)
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String eventTitle;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_BIG, message = Validations.textTooLong)
	private String eventDesc;
	private YesNoUnknown nosocomial;
	private Date startDate;
	private Date endDate;
	@NotNull(message = Validations.validReportDateTime)
	private Date reportDateTime;
	private UserReferenceDto reportingUser;
	private Date evolutionDate;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String evolutionComment;
	@Valid
	@NotNull(message = Validations.validLocation)
	private LocationDto eventLocation;
	private TypeOfPlace typeOfPlace;
	private MeansOfTransport meansOfTransport;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String meansOfTransportDetails;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String connectionNumber;
	private Date travelDate;

	private WorkEnvironment workEnvironment;

	private EventSourceType srcType;
	private InstitutionalPartnerType srcInstitutionalPartnerType;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String srcInstitutionalPartnerTypeDetails;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String srcFirstName;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String srcLastName;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String srcTelNo;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String srcEmail;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String srcMediaWebsite;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String srcMediaName;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_BIG, message = Validations.textTooLong)
	private String srcMediaDetails;
	private Disease disease;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String diseaseDetails;
	private DiseaseVariant diseaseVariant;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String diseaseVariantDetails;
	@SensitiveData
	private UserReferenceDto responsibleUser;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String typeOfPlaceText;
	@Min(value = -90, message = Validations.numberTooSmall)
	@Max(value = 90, message = Validations.numberTooBig)
	private Double reportLat;
	@Min(value = -180, message = Validations.numberTooSmall)
	@Max(value = 180, message = Validations.numberTooBig)
	private Double reportLon;
	private Float reportLatLonAccuracy;
	private YesNoUnknown transregionalOutbreak;
	private DiseaseTransmissionMode diseaseTransmissionMode;

	@HideForCountriesExcept
	private InfectionPathCertainty infectionPathCertainty;
	@HideForCountriesExcept
	private HumanTransmissionMode humanTransmissionMode;
	@HideForCountriesExcept
	private ParenteralTransmissionMode parenteralTransmissionMode;
	@HideForCountriesExcept
	private MedicallyAssociatedTransmissionMode medicallyAssociatedTransmissionMode;

	@HideForCountriesExcept
	private YesNoUnknown epidemiologicalEvidence;
	@HideForCountriesExcept
	private Map<EpidemiologicalEvidenceDetail, Boolean> epidemiologicalEvidenceDetails;
	@HideForCountriesExcept
	private YesNoUnknown laboratoryDiagnosticEvidence;
	@HideForCountriesExcept
	private Map<LaboratoryDiagnosticEvidenceDetail, Boolean> laboratoryDiagnosticEvidenceDetails;

	@HideForCountriesExcept
	@S2SIgnoreProperty(configProperty = SormasToSormasConfig.SORMAS2SORMAS_IGNORE_INTERNAL_TOKEN)
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String internalToken;

	private EventIdentificationSource eventIdentificationSource;
	private boolean deleted;
	private DeletionReason deletionReason;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String otherDeletionReason;

	public static EventDto build() {
		EventDto event = new EventDto();
		event.setUuid(DataHelper.createUuid());

		event.setEventStatus(EventStatus.SIGNAL);
		event.setEventInvestigationStatus(EventInvestigationStatus.PENDING);
		event.setEventLocation(LocationDto.build());
		event.setReportDateTime(new Date());

		return event;
	}

	public static EventDto build(CountryReferenceDto country, UserDto user) {
		EventDto event = build();

		event.getEventLocation().setCountry(country);
		event.getEventLocation().setRegion(user.getRegion());
		event.setReportingUser(user.toReference());

		return event;
	}

	public static EventDto build(CountryReferenceDto country, UserDto user, Disease disease) {
		EventDto event = build(country, user);

		event.setDisease(disease);

		return event;
	}

	public EventStatus getEventStatus() {
		return eventStatus;
	}

	public void setEventStatus(EventStatus eventStatus) {
		this.eventStatus = eventStatus;
	}

	public RiskLevel getRiskLevel() {
		return riskLevel;
	}

	public void setRiskLevel(RiskLevel riskLevel) {
		this.riskLevel = riskLevel;
	}

	public SpecificRisk getSpecificRisk() {
		return specificRisk;
	}

	public void setSpecificRisk(SpecificRisk specificRisk) {
		this.specificRisk = specificRisk;
	}

	public EventInvestigationStatus getEventInvestigationStatus() {
		return eventInvestigationStatus;
	}

	public void setEventInvestigationStatus(EventInvestigationStatus eventInvestigationStatus) {
		this.eventInvestigationStatus = eventInvestigationStatus;
	}

	public Date getEventInvestigationStartDate() {
		return eventInvestigationStartDate;
	}

	public void setEventInvestigationStartDate(Date eventInvestigationStartDate) {
		this.eventInvestigationStartDate = eventInvestigationStartDate;
	}

	public Date getEventInvestigationEndDate() {
		return eventInvestigationEndDate;
	}

	public void setEventInvestigationEndDate(Date eventInvestigationEndDate) {
		this.eventInvestigationEndDate = eventInvestigationEndDate;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getExternalToken() {
		return externalToken;
	}

	public void setExternalToken(String externalToken) {
		this.externalToken = externalToken;
	}

	public String getEventTitle() {
		return eventTitle;
	}

	public void setEventTitle(String eventTitle) {
		this.eventTitle = eventTitle;
	}

	public String getEventDesc() {
		return eventDesc;
	}

	public void setEventDesc(String eventDesc) {
		this.eventDesc = eventDesc;
	}

	public YesNoUnknown getNosocomial() {
		return nosocomial;
	}

	public void setNosocomial(YesNoUnknown nosocomial) {
		this.nosocomial = nosocomial;
	}

	public Date getStartDate() {
		return startDate;
	}

	@ImportFormat(ImportExportFormat.DATE_TIME)
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	@ImportFormat(ImportExportFormat.DATE_TIME)
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public boolean isMultiDayEvent() {
		return endDate != null;
	}

	public void setMultiDayEvent(boolean ignored) {
		// do nothing
	}

	public Date getReportDateTime() {
		return reportDateTime;
	}

	public void setReportDateTime(Date reportDateTime) {
		this.reportDateTime = reportDateTime;
	}

	@Override
	public UserReferenceDto getReportingUser() {
		return reportingUser;
	}

	@Override
	public void setReportingUser(UserReferenceDto reportingUser) {
		this.reportingUser = reportingUser;
	}

	public Date getEvolutionDate() {
		return evolutionDate;
	}

	public void setEvolutionDate(Date evolutionDate) {
		this.evolutionDate = evolutionDate;
	}

	public String getEvolutionComment() {
		return evolutionComment;
	}

	public void setEvolutionComment(String evolutionComment) {
		this.evolutionComment = evolutionComment;
	}

	public TypeOfPlace getTypeOfPlace() {
		return typeOfPlace;
	}

	public void setTypeOfPlace(TypeOfPlace typeOfPlace) {
		this.typeOfPlace = typeOfPlace;
	}

	public MeansOfTransport getMeansOfTransport() {
		return meansOfTransport;
	}

	public void setMeansOfTransport(MeansOfTransport meansOfTransport) {
		this.meansOfTransport = meansOfTransport;
	}

	public String getMeansOfTransportDetails() {
		return meansOfTransportDetails;
	}

	public void setMeansOfTransportDetails(String meansOfTransportDetails) {
		this.meansOfTransportDetails = meansOfTransportDetails;
	}

	public String getConnectionNumber() {
		return connectionNumber;
	}

	public void setConnectionNumber(String connectionNumber) {
		this.connectionNumber = connectionNumber;
	}

	public Date getTravelDate() {
		return travelDate;
	}

	public void setTravelDate(Date travelDate) {
		this.travelDate = travelDate;
	}

	public WorkEnvironment getWorkEnvironment() {
		return workEnvironment;
	}

	public void setWorkEnvironment(WorkEnvironment workEnvironment) {
		this.workEnvironment = workEnvironment;
	}

	public EventSourceType getSrcType() {
		return srcType;
	}

	public void setSrcType(EventSourceType srcType) {
		this.srcType = srcType;
	}

	public InstitutionalPartnerType getSrcInstitutionalPartnerType() {
		return srcInstitutionalPartnerType;
	}

	public void setSrcInstitutionalPartnerType(InstitutionalPartnerType srcInstitutionalPartnerType) {
		this.srcInstitutionalPartnerType = srcInstitutionalPartnerType;
	}

	public String getSrcInstitutionalPartnerTypeDetails() {
		return srcInstitutionalPartnerTypeDetails;
	}

	public void setSrcInstitutionalPartnerTypeDetails(String srcInstitutionalPartnerTypeDetails) {
		this.srcInstitutionalPartnerTypeDetails = srcInstitutionalPartnerTypeDetails;
	}

	public String getSrcFirstName() {
		return srcFirstName;
	}

	public void setSrcFirstName(String srcFirstName) {
		this.srcFirstName = srcFirstName;
	}

	public String getSrcLastName() {
		return srcLastName;
	}

	public void setSrcLastName(String srcLastName) {
		this.srcLastName = srcLastName;
	}

	public String getSrcTelNo() {
		return srcTelNo;
	}

	public void setSrcTelNo(String srcTelNo) {
		this.srcTelNo = srcTelNo;
	}

	public String getSrcEmail() {
		return srcEmail;
	}

	public void setSrcEmail(String srcEmail) {
		this.srcEmail = srcEmail;
	}

	public String getSrcMediaWebsite() {
		return srcMediaWebsite;
	}

	public void setSrcMediaWebsite(String srcMediaWebsite) {
		this.srcMediaWebsite = srcMediaWebsite;
	}

	public String getSrcMediaName() {
		return srcMediaName;
	}

	public void setSrcMediaName(String srcMediaName) {
		this.srcMediaName = srcMediaName;
	}

	public String getSrcMediaDetails() {
		return srcMediaDetails;
	}

	public void setSrcMediaDetails(String srcMediaDetails) {
		this.srcMediaDetails = srcMediaDetails;
	}

	public LocationDto getEventLocation() {
		return eventLocation;
	}

	public void setEventLocation(LocationDto eventLocation) {
		this.eventLocation = eventLocation;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public String getDiseaseDetails() {
		return diseaseDetails;
	}

	public void setDiseaseDetails(String diseaseDetails) {
		this.diseaseDetails = diseaseDetails;
	}

	public DiseaseVariant getDiseaseVariant() {
		return diseaseVariant;
	}

	public void setDiseaseVariant(DiseaseVariant diseaseVariant) {
		this.diseaseVariant = diseaseVariant;
	}

	public String getDiseaseVariantDetails() {
		return diseaseVariantDetails;
	}

	public void setDiseaseVariantDetails(String diseaseVariantDetails) {
		this.diseaseVariantDetails = diseaseVariantDetails;
	}

	public UserReferenceDto getResponsibleUser() {
		return responsibleUser;
	}

	public void setResponsibleUser(UserReferenceDto responsibleUser) {
		this.responsibleUser = responsibleUser;
	}

	public String getTypeOfPlaceText() {
		return typeOfPlaceText;
	}

	public void setTypeOfPlaceText(String typeOfPlaceText) {
		this.typeOfPlaceText = typeOfPlaceText;
	}

	public Double getReportLat() {
		return reportLat;
	}

	public void setReportLat(Double reportLat) {
		this.reportLat = reportLat;
	}

	public Double getReportLon() {
		return reportLon;
	}

	public void setReportLon(Double reportLon) {
		this.reportLon = reportLon;
	}

	public Float getReportLatLonAccuracy() {
		return reportLatLonAccuracy;
	}

	public void setReportLatLonAccuracy(Float reportLatLonAccuracy) {
		this.reportLatLonAccuracy = reportLatLonAccuracy;
	}

	public YesNoUnknown getTransregionalOutbreak() {
		return transregionalOutbreak;
	}

	public void setTransregionalOutbreak(YesNoUnknown transregionalOutbreak) {
		this.transregionalOutbreak = transregionalOutbreak;
	}

	public DiseaseTransmissionMode getDiseaseTransmissionMode() {
		return diseaseTransmissionMode;
	}

	public void setDiseaseTransmissionMode(DiseaseTransmissionMode diseaseTransmissionMode) {
		this.diseaseTransmissionMode = diseaseTransmissionMode;
	}

	public EventReferenceDto getSuperordinateEvent() {
		return superordinateEvent;
	}

	public void setSuperordinateEvent(EventReferenceDto superordinateEvent) {
		this.superordinateEvent = superordinateEvent;
	}

	public InfectionPathCertainty getInfectionPathCertainty() {
		return infectionPathCertainty;
	}

	public void setInfectionPathCertainty(InfectionPathCertainty infectionPathCertainty) {
		this.infectionPathCertainty = infectionPathCertainty;
	}

	public HumanTransmissionMode getHumanTransmissionMode() {
		return humanTransmissionMode;
	}

	public void setHumanTransmissionMode(HumanTransmissionMode humanTransmissionMode) {
		this.humanTransmissionMode = humanTransmissionMode;
	}

	public ParenteralTransmissionMode getParenteralTransmissionMode() {
		return parenteralTransmissionMode;
	}

	public void setParenteralTransmissionMode(ParenteralTransmissionMode parenteralTransmissionMode) {
		this.parenteralTransmissionMode = parenteralTransmissionMode;
	}

	public MedicallyAssociatedTransmissionMode getMedicallyAssociatedTransmissionMode() {
		return medicallyAssociatedTransmissionMode;
	}

	public void setMedicallyAssociatedTransmissionMode(MedicallyAssociatedTransmissionMode medicallyAssociatedTransmissionMode) {
		this.medicallyAssociatedTransmissionMode = medicallyAssociatedTransmissionMode;
	}

	public EventManagementStatus getEventManagementStatus() {
		return eventManagementStatus;
	}

	public void setEventManagementStatus(EventManagementStatus eventManagementStatus) {
		this.eventManagementStatus = eventManagementStatus;
	}

	public YesNoUnknown getEpidemiologicalEvidence() {
		return epidemiologicalEvidence;
	}

	public void setEpidemiologicalEvidence(YesNoUnknown epidemiologicalEvidence) {
		this.epidemiologicalEvidence = epidemiologicalEvidence;
	}

	public YesNoUnknown getLaboratoryDiagnosticEvidence() {
		return laboratoryDiagnosticEvidence;
	}

	public void setLaboratoryDiagnosticEvidence(YesNoUnknown laboratoryDiagnosticEvidence) {
		this.laboratoryDiagnosticEvidence = laboratoryDiagnosticEvidence;
	}

	public Map<EpidemiologicalEvidenceDetail, Boolean> getEpidemiologicalEvidenceDetails() {
		return epidemiologicalEvidenceDetails;
	}

	public void setEpidemiologicalEvidenceDetails(Map<EpidemiologicalEvidenceDetail, Boolean> epidemiologicalEvidenceDetails) {
		this.epidemiologicalEvidenceDetails = epidemiologicalEvidenceDetails;
	}

	public Map<LaboratoryDiagnosticEvidenceDetail, Boolean> getLaboratoryDiagnosticEvidenceDetails() {
		return laboratoryDiagnosticEvidenceDetails;
	}

	public void setLaboratoryDiagnosticEvidenceDetails(Map<LaboratoryDiagnosticEvidenceDetail, Boolean> laboratoryDiagnosticEvidenceDetails) {
		this.laboratoryDiagnosticEvidenceDetails = laboratoryDiagnosticEvidenceDetails;
	}

	public String getInternalToken() {
		return internalToken;
	}

	public void setInternalToken(String internalToken) {
		this.internalToken = internalToken;
	}

	public EventIdentificationSource getEventIdentificationSource() {
		return eventIdentificationSource;
	}

	public void setEventIdentificationSource(EventIdentificationSource eventIdentificationSource) {
		this.eventIdentificationSource = eventIdentificationSource;
	}

	public EventReferenceDto toReference() {
		return new EventReferenceDto(getUuid());
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public DeletionReason getDeletionReason() {
		return deletionReason;
	}

	public void setDeletionReason(DeletionReason deletionReason) {
		this.deletionReason = deletionReason;
	}

	public String getOtherDeletionReason() {
		return otherDeletionReason;
	}

	public void setOtherDeletionReason(String otherDeletionReason) {
		this.otherDeletionReason = otherDeletionReason;
	}
}
