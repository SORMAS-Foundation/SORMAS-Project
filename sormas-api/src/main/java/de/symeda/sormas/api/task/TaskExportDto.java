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

package de.symeda.sormas.api.task;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.caze.BirthDateDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.ICase;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.contact.IContact;
import de.symeda.sormas.api.importexport.ExportGroup;
import de.symeda.sormas.api.importexport.ExportGroupType;
import de.symeda.sormas.api.importexport.ExportProperty;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.EmbeddedPersonalData;
import de.symeda.sormas.api.utils.Order;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableIndexDto;
import de.symeda.sormas.api.utils.pseudonymization.Pseudonymizer;
import de.symeda.sormas.api.utils.pseudonymization.valuepseudonymizers.PostalCodePseudonymizer;

public class TaskExportDto extends PseudonymizableIndexDto implements ITask, Serializable {

	private static final long serialVersionUID = 4762759594879661318L;

	public static final String I18N_PREFIX = "TaskExport";

	private static final String REGION = "region";
	private static final String DISTRICT = "district";
	private static final String COMMUNITY = "community";
	private static final String PERSON_FIRST_NAME = "personFirstName";
	private static final String PERSON_LAST_NAME = "personLastName";
	private static final String PERSON_SEX = "personSex";
	private static final String PERSON_BIRTH_DATE = "personBirthDate";
	private static final String PERSON_ADDRESS_REGION = "personAddressRegion";
	private static final String PERSON_ADDRESS_DISTRICT = "personAddressDistrict";
	private static final String PERSON_ADDRESS_COMMUNITY = "personAddressCommunity";
	private static final String PERSON_ADDRESS_FACILITY = "personAddressFacility";
	private static final String PERSON_ADDRESS_FACILITY_DETAILS = "personAddressFacilityDetails";
	private static final String PERSON_ADDRESS_CITY = "personAddressCity";
	private static final String PERSON_ADDRESS_STREET = "personAddressStreet";
	private static final String PERSON_ADDRESS_HOUSE_NUMBER = "personAddressHouseNumber";
	private static final String PERSON_ADDRESS_POSTAL_CODE = "personAddressPostalCode";
	private static final String PERSON_PHONE = "personPhone";
	private static final String PERSON_PHONE_OWNER = "personPhoneOwner";
	private static final String PERSON_EMAIL_ADDRESS = "personEmailAddress";
	private static final String PERSON_OTHER_CONTACT_DETAILS = "personOtherContactDetails";

	private final TaskContext taskContext;

	private final String cazeUuid;
	private final String contactUuid;
	private final String eventUuid;

	private final TaskType taskType;
	private final TaskPriority priority;
	private final Date dueDate;
	private final Date suggestedStart;
	private final TaskStatus taskStatus;

	private final UserReferenceDto creatorUser;
	@SensitiveData
	private final String creatorComment;
	private final UserReferenceDto assigneeUser;
	@SensitiveData
	private final String assigneeReply;

	private final String region;
	private final String district;
	@PersonalData
	@SensitiveData
	private final String community;

	@PersonalData
	@SensitiveData
	private final String personFirstName;

	@PersonalData
	@SensitiveData
	private final String personLastName;

	private final Sex personSex;

	@EmbeddedPersonalData
	private final BirthDateDto personBirthDate;

	private final String personAddressRegion;

	private final String personAddressDistrict;
	@PersonalData
	@SensitiveData
	private final String personAddressCommunity;
	@PersonalData
	@SensitiveData
	private final String personAddressFacility;
	@PersonalData
	@SensitiveData
	private final String personAddressFacilityDetails;
	@PersonalData
	@SensitiveData
	private final String personAddressCity;
	@PersonalData
	@SensitiveData
	private final String personAddressStreet;
	@PersonalData
	@SensitiveData
	private final String personAddressHouseNumber;
	@PersonalData
	@SensitiveData
	@Pseudonymizer(PostalCodePseudonymizer.class)
	private final String personAddressPostalCode;
	@SensitiveData
	private final String personPhone;
	@SensitiveData
	private final String personPhoneOwner;
	@SensitiveData
	private final String personEmailAddress;
	@SensitiveData
	private final String personOtherContactDetails;

	private final boolean inJurisdiction;

	//@formatter:off
	public TaskExportDto(String uuid, TaskContext taskContext,
						 String cazeUuid, String contactUuid, String eventUuid,
						 TaskType taskType, TaskPriority priority, Date dueDate, Date suggestedStart, TaskStatus taskStatus,
						 String creatorUserUuid, String creatorUserFirstName, String creatorUserLastName,
						 String creatorComment,
						 String assigneeUserUuid, String assigneeUserFirstName, String assigneeUserLastName,
						 String assigneeReply,
						 String region, String district, String community,
						 String personFirstName, String personLastName, Sex personSex,
						 Integer personBirthdateDD, Integer personBirthdateMM, Integer personBirthdateYYYY,
						 String personAddressRegion, String personAddressDistrict, String personAddressCommunity,
						 String personAddressFacility, String personAddressFacilityDetails,
						 String personAddressCity, String personAddressStreet, String personAddressHouseNumber,
						 String personAddressPostalCode,
						 String personPhone, String personPhoneOwner, String personEmailAddress, String personOtherContactDetails,
						 boolean inJurisdiction) {
		//@formatter:on
		super(uuid);
		this.taskContext = taskContext;
		this.cazeUuid = cazeUuid;
		this.contactUuid = contactUuid;
		this.eventUuid = eventUuid;
		this.taskType = taskType;
		this.priority = priority;
		this.dueDate = dueDate;
		this.suggestedStart = suggestedStart;
		this.taskStatus = taskStatus;
		this.creatorUser = new UserReferenceDto(creatorUserUuid, creatorUserFirstName, creatorUserLastName);
		this.creatorComment = creatorComment;
		this.assigneeUser = new UserReferenceDto(assigneeUserUuid, assigneeUserFirstName, assigneeUserLastName);
		this.assigneeReply = assigneeReply;
		this.region = region;
		this.district = district;
		this.community = community;
		this.personFirstName = personFirstName;
		this.personLastName = personLastName;
		this.personSex = personSex;
		this.personBirthDate = new BirthDateDto(personBirthdateDD, personBirthdateMM, personBirthdateYYYY);
		this.personAddressRegion = personAddressRegion;
		this.personAddressDistrict = personAddressDistrict;
		this.personAddressCommunity = personAddressCommunity;
		this.personAddressFacility = personAddressFacility;
		this.personAddressFacilityDetails = personAddressFacilityDetails;
		this.personAddressCity = personAddressCity;
		this.personAddressStreet = personAddressStreet;
		this.personAddressHouseNumber = personAddressHouseNumber;
		this.personAddressPostalCode = personAddressPostalCode;
		this.personPhone = personPhone;
		this.personPhoneOwner = personPhoneOwner;
		this.personEmailAddress = personEmailAddress;
		this.personOtherContactDetails = personOtherContactDetails;
		this.inJurisdiction = inJurisdiction;
	}

	@Order(0)
	@ExportProperty(TaskDto.UUID)
	@ExportGroup(ExportGroupType.CORE)
	public String getUuid() {
		return super.getUuid();
	}

	@Order(1)
	@ExportProperty(TaskDto.TASK_CONTEXT)
	@ExportGroup(ExportGroupType.CORE)
	public TaskContext getTaskContext() {
		return taskContext;
	}

	@Order(2)
	@ExportProperty(TaskDto.CAZE)
	@ExportGroup(ExportGroupType.CORE)
	public String getCazeUuid() {
		return cazeUuid;
	}

	@Order(3)
	@ExportProperty(TaskDto.CONTACT)
	@ExportGroup(ExportGroupType.CORE)
	public String getContactUuid() {
		return contactUuid;
	}

	@Order(4)
	@ExportProperty(TaskDto.EVENT)
	@ExportGroup(ExportGroupType.CORE)
	public String getEventUuid() {
		return eventUuid;
	}

	@Order(5)
	@ExportProperty(TaskDto.TASK_TYPE)
	@ExportGroup(ExportGroupType.CORE)
	public TaskType getTaskType() {
		return taskType;
	}

	@Order(6)
	@ExportProperty(TaskDto.PRIORITY)
	@ExportGroup(ExportGroupType.CORE)
	public TaskPriority getPriority() {
		return priority;
	}

	@Order(7)
	@ExportProperty(TaskDto.DUE_DATE)
	@ExportGroup(ExportGroupType.CORE)
	public Date getDueDate() {
		return dueDate;
	}

	@Order(8)
	@ExportProperty(TaskDto.SUGGESTED_START)
	@ExportGroup(ExportGroupType.CORE)
	public Date getSuggestedStart() {
		return suggestedStart;
	}

	@Order(9)
	@ExportProperty(TaskDto.TASK_STATUS)
	@ExportGroup(ExportGroupType.CORE)
	public TaskStatus getTaskStatus() {
		return taskStatus;
	}

	@Order(10)
	@ExportProperty(TaskDto.CREATOR_USER)
	@ExportGroup(ExportGroupType.CORE)
	public UserReferenceDto getCreatorUser() {
		return creatorUser;
	}

	@Order(11)
	@ExportProperty(TaskDto.CREATOR_COMMENT)
	@ExportGroup(ExportGroupType.CORE)
	public String getCreatorComment() {
		return creatorComment;
	}

	@Order(12)
	@ExportProperty(TaskDto.ASSIGNEE_USER)
	@ExportGroup(ExportGroupType.CORE)
	public UserReferenceDto getAssigneeUser() {
		return assigneeUser;
	}

	@Order(13)
	@ExportProperty(TaskDto.ASSIGNEE_REPLY)
	@ExportGroup(ExportGroupType.CORE)
	public String getAssigneeReply() {
		return assigneeReply;
	}

	@Order(20)
	@ExportProperty(REGION)
	@ExportGroup(ExportGroupType.LOCATION)
	public String getRegion() {
		return region;
	}

	@Order(21)
	@ExportProperty(DISTRICT)
	@ExportGroup(ExportGroupType.LOCATION)
	public String getDistrict() {
		return district;
	}

	@Order(22)
	@ExportProperty(COMMUNITY)
	@ExportGroup(ExportGroupType.LOCATION)
	public String getCommunity() {
		return community;
	}

	@Order(30)
	@ExportProperty(PERSON_FIRST_NAME)
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getPersonFirstName() {
		return personFirstName;
	}

	@Order(31)
	@ExportProperty(PERSON_LAST_NAME)
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getPersonLastName() {
		return personLastName;
	}

	@Order(32)
	@ExportProperty(PERSON_SEX)
	@ExportGroup(ExportGroupType.SENSITIVE)
	public Sex getPersonSex() {
		return personSex;
	}

	@Order(33)
	@ExportProperty(PERSON_BIRTH_DATE)
	@ExportGroup(ExportGroupType.SENSITIVE)
	public BirthDateDto getPersonBirthDate() {
		return personBirthDate;
	}

	@Order(35)
	@ExportProperty(PERSON_ADDRESS_REGION)
	@ExportGroup(ExportGroupType.PERSON)
	public String getPersonAddressRegion() {
		return personAddressRegion;
	}

	@Order(36)
	@ExportProperty(PERSON_ADDRESS_DISTRICT)
	@ExportGroup(ExportGroupType.PERSON)
	public String getPersonAddressDistrict() {
		return personAddressDistrict;
	}

	@Order(37)
	@ExportProperty(PERSON_ADDRESS_COMMUNITY)
	@ExportGroup(ExportGroupType.PERSON)
	public String getPersonAddressCommunity() {
		return personAddressCommunity;
	}

	@Order(38)
	@ExportProperty(PERSON_ADDRESS_FACILITY)
	@ExportGroup(ExportGroupType.PERSON)
	public String getPersonAddressFacility() {
		return personAddressFacility;
	}

	@Order(39)
	@ExportProperty(PERSON_ADDRESS_FACILITY_DETAILS)
	@ExportGroup(ExportGroupType.PERSON)
	public String getPersonAddressFacilityDetails() {
		return personAddressFacilityDetails;
	}

	@Order(40)
	@ExportProperty(PERSON_ADDRESS_CITY)
	@ExportGroup(ExportGroupType.PERSON)
	public String getPersonAddressCity() {
		return personAddressCity;
	}

	@Order(41)
	@ExportProperty(PERSON_ADDRESS_STREET)
	@ExportGroup(ExportGroupType.PERSON)
	public String getPersonAddressStreet() {
		return personAddressStreet;
	}

	@Order(42)
	@ExportProperty(PERSON_ADDRESS_HOUSE_NUMBER)
	@ExportGroup(ExportGroupType.PERSON)
	public String getPersonAddressHouseNumber() {
		return personAddressHouseNumber;
	}

	@Order(43)
	@ExportProperty(PERSON_ADDRESS_POSTAL_CODE)
	@ExportGroup(ExportGroupType.PERSON)
	public String getPersonAddressPostalCode() {
		return personAddressPostalCode;
	}

	@Order(50)
	@ExportProperty(PERSON_PHONE)
	@ExportGroup(ExportGroupType.PERSON)
	public String getPersonPhone() {
		return personPhone;
	}

	@Order(51)
	@ExportProperty(PERSON_PHONE_OWNER)
	@ExportGroup(ExportGroupType.PERSON)
	public String getPersonPhoneOwner() {
		return personPhoneOwner;
	}

	@Order(52)
	@ExportProperty(PERSON_EMAIL_ADDRESS)
	@ExportGroup(ExportGroupType.PERSON)
	public String getPersonEmailAddress() {
		return personEmailAddress;
	}

	@Order(53)
	@ExportProperty(PERSON_OTHER_CONTACT_DETAILS)
	@ExportGroup(ExportGroupType.PERSON)
	public String getPersonOtherContactDetails() {
		return personOtherContactDetails;
	}

	public boolean isInJurisdiction() {
		return inJurisdiction;
	}

	@Override
	public ICase getCaze() {
		return new CaseReferenceDto(cazeUuid);
	}

	@Override
	public IContact getContact() {
		return new ContactReferenceDto(contactUuid);
	}
}
