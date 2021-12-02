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

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import de.symeda.sormas.api.caze.VaccinationStatus;
import de.symeda.sormas.app.backend.common.PseudonymizableAdo;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.sormastosormas.SormasToSormasOriginInfo;
import de.symeda.sormas.app.backend.user.User;

@Entity(name = EventParticipant.TABLE_NAME)
@DatabaseTable(tableName = EventParticipant.TABLE_NAME)
public class EventParticipant extends PseudonymizableAdo {

	private static final long serialVersionUID = -9006001699517297107L;

	public static final String TABLE_NAME = "eventParticipants";
	public static final String I18N_PREFIX = "EventParticipant";

	public static final String EVENT = "event";
	public static final String PERSON = "person";
	public static final String INVOLVEMENT_DESCRIPTION = "involvementDescription";
	public static final String RESULTING_CASE_UUID = "resultingCaseUuid";
	public static final String RESPONSIBLE_REGION = "responsibleRegion";
	public static final String RESPONSIBLE_DISTRICT = "responsibleDistrict";

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private User reportingUser;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false)
	private Event event;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false)
	private Person person;

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String involvementDescription;

	@DatabaseField
	private String resultingCaseUuid;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private SormasToSormasOriginInfo sormasToSormasOriginInfo;
	@DatabaseField
	private boolean ownershipHandedOver;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Region responsibleRegion;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private District responsibleDistrict;

	@Enumerated(EnumType.STRING)
	private VaccinationStatus vaccinationStatus;

	public User getReportingUser() {
		return reportingUser;
	}

	public void setReportingUser(User reportingUser) {
		this.reportingUser = reportingUser;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public String getInvolvementDescription() {
		return involvementDescription;
	}

	public void setInvolvementDescription(String involvementDescription) {
		this.involvementDescription = involvementDescription;
	}

	@Override
	public String toString() {
		return getPerson().toString();
	}

	@Override
	public boolean isModifiedOrChildModified() {
		boolean modified = super.isModifiedOrChildModified();
		return person.isModifiedOrChildModified() || modified;
	}

	@Override
	public boolean isUnreadOrChildUnread() {
		boolean unread = super.isUnreadOrChildUnread();
		return person.isUnreadOrChildUnread() || unread;
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}

	public String getResultingCaseUuid() {
		return resultingCaseUuid;
	}

	public void setResultingCaseUuid(String resultingCaseUuid) {
		this.resultingCaseUuid = resultingCaseUuid;
	}

	public VaccinationStatus getVaccinationStatus() {
		return vaccinationStatus;
	}

	public void setVaccinationStatus(VaccinationStatus vaccinationStatus) {
		this.vaccinationStatus = vaccinationStatus;
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
}
