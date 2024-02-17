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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.VaccinationStatus;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareableDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.api.utils.EmbeddedPersonalData;
import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.api.utils.Outbreaks;
import de.symeda.sormas.api.utils.SensitiveData;

@DependingOnFeatureType(featureType = FeatureType.EVENT_SURVEILLANCE)
public class EventParticipantDto extends SormasToSormasShareableDto implements IsEventParticipant {

	private static final long serialVersionUID = -8725734604520880084L;

	public static final long APPROXIMATE_JSON_SIZE_IN_BYTES = 46200;

	public static final String I18N_PREFIX = "EventParticipant";

	public static final String EVENT = "event";
	public static final String PERSON = "person";
	public static final String INVOLVEMENT_DESCRIPTION = "involvementDescription";
	public static final String RESULTING_CASE = "resultingCase";
	public static final String REPORTING_USER = "reportingUser";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String VACCINATION_STATUS = "vaccinationStatus";
	public static final String DELETION_REASON = "deletionReason";
	public static final String OTHER_DELETION_REASON = "otherDeletionReason";

	private UserReferenceDto reportingUser;
	@NotNull(message = Validations.requiredField)
	private EventReferenceDto event;
	@NotNull(message = Validations.validPerson)
	@EmbeddedPersonalData
	@Valid
	private PersonDto person;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String involvementDescription;
	private CaseReferenceDto resultingCase; // read-only
	private RegionReferenceDto region;
	private DistrictReferenceDto district;

	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.MEASLES,
		Disease.POLIO,
		Disease.YELLOW_FEVER,
		Disease.CSM,
		Disease.RABIES,
		Disease.UNSPECIFIED_VHF,
		Disease.ANTHRAX,
		Disease.CORONAVIRUS,
		Disease.OTHER })
	@Outbreaks
	private VaccinationStatus vaccinationStatus;

	private boolean deleted;
	private DeletionReason deletionReason;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String otherDeletionReason;

	public static EventParticipantDto build(EventReferenceDto event, UserReferenceDto reportingUser) {
		EventParticipantDto eventParticipant = new EventParticipantDto();
		eventParticipant.setUuid(DataHelper.createUuid());
		eventParticipant.setEvent(event);
		eventParticipant.setReportingUser(reportingUser);

		return eventParticipant;
	}

	public static EventParticipantDto buildFromCase(
		CaseReferenceDto caseReferenceDto,
		PersonDto person,
		EventReferenceDto event,
		UserReferenceDto reportingUser) {

		EventParticipantDto eventParticipantDto = build(event, reportingUser);
		eventParticipantDto.setPerson(person);
		eventParticipantDto.setResultingCase(caseReferenceDto);

		return eventParticipantDto;
	}

	public static EventParticipantDto buildFromPerson(PersonDto person, EventReferenceDto event, UserReferenceDto reportingUser) {
		EventParticipantDto eventParticipantDto = build(event, reportingUser);
		eventParticipantDto.setPerson(person);

		return eventParticipantDto;
	}

	@Override
	public UserReferenceDto getReportingUser() {
		return reportingUser;
	}

	@Override
	public void setReportingUser(UserReferenceDto reportingUser) {
		this.reportingUser = reportingUser;
	}

	public EventReferenceDto getEvent() {
		return event;
	}

	public void setEvent(EventReferenceDto event) {
		this.event = event;
	}

	public PersonDto getPerson() {
		return person;
	}

	public void setPerson(PersonDto person) {
		this.person = person;
	}

	public String getInvolvementDescription() {
		return involvementDescription;
	}

	public void setInvolvementDescription(String involvementDescription) {
		this.involvementDescription = involvementDescription;
	}

	public EventParticipantReferenceDto toReference() {
		return new EventParticipantReferenceDto(getUuid());
	}

	public CaseReferenceDto getResultingCase() {
		return resultingCase;
	}

	/**
	 * This should only be called when filling the DTO in the server backend!
	 */
	public void setResultingCase(CaseReferenceDto resultingCase) {
		this.resultingCase = resultingCase;
	}

	public RegionReferenceDto getRegion() {
		return region;
	}

	public void setRegion(RegionReferenceDto region) {
		this.region = region;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public void setDistrict(DistrictReferenceDto district) {
		this.district = district;
	}

	public VaccinationStatus getVaccinationStatus() {
		return vaccinationStatus;
	}

	public void setVaccinationStatus(VaccinationStatus vaccinationStatus) {
		this.vaccinationStatus = vaccinationStatus;
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
