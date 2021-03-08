/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.backend.event;

import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_BIG;
import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_DEFAULT;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.event.DiseaseTransmissionMode;
import de.symeda.sormas.api.event.EventInvestigationStatus;
import de.symeda.sormas.api.event.EventManagementStatus;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.event.EventSourceType;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.InstitutionalPartnerType;
import de.symeda.sormas.api.event.MeansOfTransport;
import de.symeda.sormas.api.event.RiskLevel;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.exposure.WorkEnvironment;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.backend.common.PseudonymizableAdo;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.sormastosormas.SormasToSormasOriginInfo;
import de.symeda.sormas.app.backend.user.User;

@Entity(name = Event.TABLE_NAME)
@DatabaseTable(tableName = Event.TABLE_NAME)
public class Event extends PseudonymizableAdo {

	private static final long serialVersionUID = 4964495716032049582L;

	public static final String TABLE_NAME = "events";
	public static final String I18N_PREFIX = "Event";

	public static final String EVENT_STATUS = "eventStatus";
	public static final String RISK_LEVEL = "riskLevel";
	public static final String EVENT_MANAGEMENT_STATUS = "eventManagementStatus";
	public static final String EVENT_INVESTIGATION_STATUS = "eventInvestigationStatus";
	public static final String EVENT_INVESTIGATION_START_DATE = "eventInvestigationStartDate";
	public static final String EVENT_INVESTIGATION_END_DATE = "eventInvestigationEndDate";
	public static final String EVENT_PERSONS = "eventPersons";
	public static final String EVENT_TITLE = "eventTitle";
	public static final String EVENT_DESC = "eventDesc";
	public static final String START_DATE = "startDate";
	public static final String REPORT_DATE_TIME = "reportDateTime";
	public static final String REPORTING_USER = "reportingUser";
	public static final String EVOLUTION_DATE = "evolutionDate";
	public static final String EVOLUTION_COMMENT = "evolutionComment";
	public static final String EVENT_LOCATION = "eventLocation";
	public static final String TYPE_OF_PLACE = "typeOfPlace";
	public static final String MEANS_OF_TRANSPORT = "meansOfTransport";
	public static final String MEANS_OF_TRANSPORT_DETAILS = "meansOfTransportDetails";
	public static final String SRC_INSTITUTIONAL_PARTNER_TYPE = "srcInstitutionalPartnerType";
	public static final String SRC_INSTITUTIONAL_PARTNER_TYPE_DETAILS = "srcInstitutionalPartnerTypeDetails";
	public static final String SRC_FIRST_NAME = "srcFirstName";
	public static final String SRC_LAST_NAME = "srcLastName";
	public static final String SRC_TEL_NO = "srcTelNo";
	public static final String SRC_EMAIL = "srcEmail";
	public static final String DISEASE = "disease";
	public static final String DISEASE_DETAILS = "diseaseDetails";
	public static final String RESPONSIBLE_USER = "responsibleUser";
	public static final String TYPE_OF_PLACE_TEXT = "typeOfPlaceText";
	public static final String CONNECTION_NUMBER = "connectionNumber";
	public static final String TRAVEL_DATE = "travelDate";

	@DatabaseField
	private String superordinateEventUuid;

	@Deprecated
	@DatabaseField
	private String eventType;

	@Enumerated(EnumType.STRING)
	private EventStatus eventStatus;

	@Enumerated(EnumType.STRING)
	private RiskLevel riskLevel;

	@Enumerated(EnumType.STRING)
	private EventInvestigationStatus eventInvestigationStatus;

	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date eventInvestigationStartDate;

	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date eventInvestigationEndDate;

	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String externalId;

	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String externalToken;

	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String eventTitle;

	@Column(length = COLUMN_LENGTH_BIG)
	private String eventDesc;

	@Enumerated(EnumType.STRING)
	private YesNoUnknown nosocomial;

	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date startDate;

	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date endDate;

	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date reportDateTime;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private User reportingUser;

	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date evolutionDate;

	@DatabaseField
	private String evolutionComment;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 2)
	private Location eventLocation;

	@Enumerated(EnumType.STRING)
	private TypeOfPlace typeOfPlace;

	@Enumerated(EnumType.STRING)
	private MeansOfTransport meansOfTransport;

	@Column(columnDefinition = "text")
	private String meansOfTransportDetails;

	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String connectionNumber;

	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date travelDate;

	@Enumerated(EnumType.STRING)
	private WorkEnvironment workEnvironment;

	@Enumerated(EnumType.STRING)
	private EventSourceType srcType;

	@Enumerated(EnumType.STRING)
	private InstitutionalPartnerType srcInstitutionalPartnerType;

	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String srcInstitutionalPartnerTypeDetails;

	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String srcFirstName;

	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String srcLastName;

	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String srcTelNo;

	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String srcEmail;

	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String srcMediaWebsite;

	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String srcMediaName;

	@Column(length = COLUMN_LENGTH_BIG)
	private String srcMediaDetails;

	@Enumerated(EnumType.STRING)
	private Disease disease;

	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String diseaseDetails;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "surveillanceOfficer_id")
	private User responsibleUser;

	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String typeOfPlaceText;

	@DatabaseField
	private Double reportLat;
	@DatabaseField
	private Double reportLon;
	@DatabaseField
	private Float reportLatLonAccuracy;

	@Enumerated(EnumType.STRING)
	private YesNoUnknown transregionalOutbreak;
	@Enumerated(EnumType.STRING)
	private DiseaseTransmissionMode diseaseTransmissionMode;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private SormasToSormasOriginInfo sormasToSormasOriginInfo;
	@DatabaseField
	private boolean ownershipHandedOver;

	@Enumerated(EnumType.STRING)
	private EventManagementStatus eventManagementStatus;

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

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getReportDateTime() {
		return reportDateTime;
	}

	public void setReportDateTime(Date reportDateTime) {
		this.reportDateTime = reportDateTime;
	}

	public User getReportingUser() {
		return reportingUser;
	}

	public void setReportingUser(User reportingUser) {
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

	public Location getEventLocation() {
		return eventLocation;
	}

	public void setEventLocation(Location eventLocation) {
		this.eventLocation = eventLocation;
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

	public User getResponsibleUser() {
		return responsibleUser;
	}

	public void setResponsibleUser(User responsibleUser) {
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

	public String getSuperordinateEventUuid() {
		return superordinateEventUuid;
	}

	public void setSuperordinateEventUuid(String superordinateEventUuid) {
		this.superordinateEventUuid = superordinateEventUuid;
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

	@Override
	public String toString() {
		return EventReferenceDto.buildCaption(getDisease(), getDiseaseDetails(), getEventStatus(), getEventInvestigationStatus(), getStartDate());
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}

	public Float getReportLatLonAccuracy() {
		return reportLatLonAccuracy;
	}

	public void setReportLatLonAccuracy(Float reportLatLonAccuracy) {
		this.reportLatLonAccuracy = reportLatLonAccuracy;
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

	public EventManagementStatus getEventManagementStatus() {
		return eventManagementStatus;
	}

	public void setEventManagementStatus(EventManagementStatus eventManagementStatus) {
		this.eventManagementStatus = eventManagementStatus;
	}
}
