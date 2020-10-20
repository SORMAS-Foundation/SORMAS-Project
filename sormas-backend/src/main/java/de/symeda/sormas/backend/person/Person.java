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
package de.symeda.sormas.backend.person;

import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_DEFAULT;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.BurialConductor;
import de.symeda.sormas.api.person.CauseOfDeath;
import de.symeda.sormas.api.person.DeathPlaceType;
import de.symeda.sormas.api.person.EducationType;
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.person.SymptomJournalStatus;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;

@Entity
@Audited
public class Person extends AbstractDomainObject {

	private static final long serialVersionUID = -1735038738114840087L;

	public static final String TABLE_NAME = "person";
	public static final String PERSON_LOCATIONS_TABLE_NAME = "person_locations";

	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String NICKNAME = "nickname";
	public static final String MOTHERS_MAIDEN_NAME = "mothersMaidenName";
	public static final String APPROXIMATE_AGE = "approximateAge";
	public static final String APPROXIMATE_AGE_TYPE = "approximateAgeType";
	public static final String APPROXIMATE_AGE_REFERENCE_DATE = "approximateAgeReferenceDate";
	public static final String BIRTHDATE_DD = "birthdateDD";
	public static final String BIRTHDATE_MM = "birthdateMM";
	public static final String BIRTHDATE_YYYY = "birthdateYYYY";
	public static final String CAUSE_OF_DEATH_DISEASE = "causeOfDeathDisease";
	public static final String DEATH_PLACE_TYPE = "deathPlaceType";
	public static final String DEATH_PLACE_DESCRIPTION = "deathPlaceDescription";
	public static final String BURIAL_DATE = "burialDate";
	public static final String BURIAL_PLACE_DESCRIPTION = "burialPlaceDescription";
	public static final String BURIAL_CONDUCTOR = "burialConductor";
	public static final String ADDRESS = "address";
	public static final String SEX = "sex";
	public static final String DEATH_DATE = "deathDate";
	public static final String PRESENT_CONDITION = "presentCondition";
	public static final String EDUCATION_TYPE = "educationType";
	public static final String EDUCATION_DETAILS = "educationDetails";
	public static final String OCCUPATION_TYPE = "occupationType";
	public static final String OCCUPATION_DETAILS = "occupationDetails";
	public static final String PHONE = "phone";
	public static final String PHONE_OWNER = "phoneOwner";
	public static final String FATHERS_NAME = "fathersName";
	public static final String MOTHERS_NAME = "mothersName";
	public static final String PLACE_OF_BIRTH_REGION = "placeOfBirthRegion";
	public static final String PLACE_OF_BIRTH_DISTRICT = "placeOfBirthDistrict";
	public static final String PLACE_OF_BIRTH_COMMUNITY = "placeOfBirthCommunity";
	public static final String PLACE_OF_BIRTH_FACILITY = "placeOfBirthFacility";
	public static final String PLACE_OF_BIRTH_FACILITY_DETAILS = "placeOfBirthFacilityDetails";
	public static final String GESTATION_AGE_AT_BIRTH = "gestationAgeAtBirth";
	public static final String BIRTH_WEIGHT = "birthWeight";
	public static final String GENERAL_PRACTITIONER_DETAILS = "generalPractitionerDetails";
	public static final String PASSPORT_NUMBER = "passportNumber";
	public static final String NATIONAL_HEALTH_ID = "nationalHealthId";
	public static final String EMAIL_ADDRESS = "emailAddress";
	public static final String PLACE_OF_BIRTH_FACILITY_TYPE = "placeOfBirthFacilityType";
	public static final String ADDRESSES = "addresses";
	public static final String EVENT_PARTICIPANTS = "eventParticipants";
	public static final String CONTACTS = "contacts";

	public static final String SYMPTOM_JOURNAL_STATUS = "symptomJournalStatus";
	public static final String EXTERNAL_ID = "externalId";

	private String firstName;
	private String lastName;
	private String nickname;
	private String mothersName;
	private String mothersMaidenName;
	private String fathersName;

	private Integer approximateAge;
	private ApproximateAgeType approximateAgeType;
	private Date approximateAgeReferenceDate;

	private CauseOfDeath causeOfDeath;
	private String causeOfDeathDetails;
	private Disease causeOfDeathDisease;
	private DeathPlaceType deathPlaceType;
	private String deathPlaceDescription;
	private Date burialDate;
	private String burialPlaceDescription;
	private BurialConductor burialConductor;

	private Location address;
	private String phone;
	private String phoneOwner;
	private String emailAddress;

	private Sex sex;

	private PresentCondition presentCondition;
	private Integer birthdateDD;
	private Integer birthdateMM;
	private Integer birthdateYYYY;
	private Region placeOfBirthRegion;
	private District placeOfBirthDistrict;
	private Community placeOfBirthCommunity;
	private Facility placeOfBirthFacility;
	private String placeOfBirthFacilityDetails;
	private Integer gestationAgeAtBirth;
	private Integer birthWeight;
	private Date deathDate;

	private EducationType educationType;
	private String educationDetails;

	private OccupationType occupationType;
	private String occupationDetails;
	private String generalPractitionerDetails;
	private String passportNumber;
	private String nationalHealthId;
	private FacilityType placeOfBirthFacilityType;
	private Set<Location> addresses = new HashSet<>();
	private Date changeDateOfEmbeddedLists;

	private SymptomJournalStatus symptomJournalStatus;

	private boolean hasCovidApp;
	private boolean covidCodeDelivered;
	private String externalId;

	private Set<EventParticipant> eventParticipants = new HashSet<>();
	private Set<Contact> contacts = new HashSet<>();

	@Column(nullable = false, length = COLUMN_LENGTH_DEFAULT)
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Column(nullable = false, length = COLUMN_LENGTH_DEFAULT)
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getMothersMaidenName() {
		return mothersMaidenName;
	}

	public void setMothersMaidenName(String mothersMaidenName) {
		this.mothersMaidenName = mothersMaidenName;
	}

	@Column(name = "birthdate_dd")
	public Integer getBirthdateDD() {
		return birthdateDD;
	}

	public void setBirthdateDD(Integer birthdateDD) {
		this.birthdateDD = birthdateDD;
	}

	@Column(name = "birthdate_mm")
	public Integer getBirthdateMM() {
		return birthdateMM;
	}

	public void setBirthdateMM(Integer birthdateMM) {
		this.birthdateMM = birthdateMM;
	}

	@Column(name = "birthdate_yyyy")
	public Integer getBirthdateYYYY() {
		return birthdateYYYY;
	}

	public void setBirthdateYYYY(Integer birthdateYYYY) {
		this.birthdateYYYY = birthdateYYYY;
	}

	public Integer getApproximateAge() {
		return approximateAge;
	}

	public void setApproximateAge(Integer approximateAge) {
		this.approximateAge = approximateAge;
	}

	@Enumerated(EnumType.STRING)
	public ApproximateAgeType getApproximateAgeType() {
		return approximateAgeType;
	}

	public void setApproximateAgeType(ApproximateAgeType approximateAgeType) {
		this.approximateAgeType = approximateAgeType;
	}

	@Temporal(TemporalType.DATE)
	public Date getApproximateAgeReferenceDate() {
		return approximateAgeReferenceDate;
	}

	public void setApproximateAgeReferenceDate(Date approximateAgeReferenceDate) {
		this.approximateAgeReferenceDate = approximateAgeReferenceDate;
	}

	@Enumerated(EnumType.STRING)
	public DeathPlaceType getDeathPlaceType() {
		return deathPlaceType;
	}

	public void setDeathPlaceType(DeathPlaceType deathPlaceType) {
		this.deathPlaceType = deathPlaceType;
	}

	public String getDeathPlaceDescription() {
		return deathPlaceDescription;
	}

	public void setDeathPlaceDescription(String deathPlaceDescription) {
		this.deathPlaceDescription = deathPlaceDescription;
	}

	public String getBurialPlaceDescription() {
		return burialPlaceDescription;
	}

	public void setBurialPlaceDescription(String burialPlaceDescription) {
		this.burialPlaceDescription = burialPlaceDescription;
	}

	@OneToOne(cascade = CascadeType.ALL)
	public Location getAddress() {
		if (address == null) {
			address = new Location();
		}
		return address;
	}

	public void setAddress(Location address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPhoneOwner() {
		return phoneOwner;
	}

	public void setPhoneOwner(String phoneOwner) {
		this.phoneOwner = phoneOwner;
	}

	@Enumerated(EnumType.STRING)
	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public PresentCondition getPresentCondition() {
		return presentCondition;
	}

	public void setPresentCondition(PresentCondition presentCondition) {
		this.presentCondition = presentCondition;
	}

	@Temporal(TemporalType.DATE)
	public Date getDeathDate() {
		return deathDate;
	}

	public void setDeathDate(Date deathDate) {
		this.deathDate = deathDate;
	}

	@Temporal(TemporalType.DATE)
	public Date getBurialDate() {
		return burialDate;
	}

	public void setBurialDate(Date burialDate) {
		this.burialDate = burialDate;
	}

	@Enumerated(EnumType.STRING)
	public BurialConductor getBurialConductor() {
		return burialConductor;
	}

	public void setBurialConductor(BurialConductor burialConductor) {
		this.burialConductor = burialConductor;
	}

	@Enumerated(EnumType.STRING)
	public CauseOfDeath getCauseOfDeath() {
		return causeOfDeath;
	}

	public void setCauseOfDeath(CauseOfDeath causeOfDeath) {
		this.causeOfDeath = causeOfDeath;
	}

	public String getCauseOfDeathDetails() {
		return causeOfDeathDetails;
	}

	public void setCauseOfDeathDetails(String causeOfDeathDetails) {
		this.causeOfDeathDetails = causeOfDeathDetails;
	}

	@Enumerated(EnumType.STRING)
	public Disease getCauseOfDeathDisease() {
		return causeOfDeathDisease;
	}

	public void setCauseOfDeathDisease(Disease causeOfDeathDisease) {
		this.causeOfDeathDisease = causeOfDeathDisease;
	}

	@Enumerated(EnumType.STRING)
	public EducationType getEducationType() {
		return educationType;
	}

	public void setEducationType(EducationType educationType) {
		this.educationType = educationType;
	}

	public String getEducationDetails() {
		return educationDetails;
	}

	public void setEducationDetails(String educationDetails) {
		this.educationDetails = educationDetails;
	}

	@Enumerated(EnumType.STRING)
	public OccupationType getOccupationType() {
		return occupationType;
	}

	public void setOccupationType(OccupationType occupationType) {
		this.occupationType = occupationType;
	}

	public String getOccupationDetails() {
		return occupationDetails;
	}

	public void setOccupationDetails(String occupationDetails) {
		this.occupationDetails = occupationDetails;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getMothersName() {
		return mothersName;
	}

	public void setMothersName(String mothersName) {
		this.mothersName = mothersName;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getFathersName() {
		return fathersName;
	}

	public void setFathersName(String fathersName) {
		this.fathersName = fathersName;
	}

	@ManyToOne(cascade = {})
	public Region getPlaceOfBirthRegion() {
		return placeOfBirthRegion;
	}

	public void setPlaceOfBirthRegion(Region placeOfBirthRegion) {
		this.placeOfBirthRegion = placeOfBirthRegion;
	}

	@ManyToOne(cascade = {})
	public District getPlaceOfBirthDistrict() {
		return placeOfBirthDistrict;
	}

	public void setPlaceOfBirthDistrict(District placeOfBirthDistrict) {
		this.placeOfBirthDistrict = placeOfBirthDistrict;
	}

	@ManyToOne(cascade = {})
	public Community getPlaceOfBirthCommunity() {
		return placeOfBirthCommunity;
	}

	public void setPlaceOfBirthCommunity(Community placeOfBirthCommunity) {
		this.placeOfBirthCommunity = placeOfBirthCommunity;
	}

	@ManyToOne(cascade = {})
	public Facility getPlaceOfBirthFacility() {
		return placeOfBirthFacility;
	}

	public void setPlaceOfBirthFacility(Facility placeOfBirthFacility) {
		this.placeOfBirthFacility = placeOfBirthFacility;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getPlaceOfBirthFacilityDetails() {
		return placeOfBirthFacilityDetails;
	}

	public void setPlaceOfBirthFacilityDetails(String placeOfBirthFacilityDetails) {
		this.placeOfBirthFacilityDetails = placeOfBirthFacilityDetails;
	}

	public Integer getGestationAgeAtBirth() {
		return gestationAgeAtBirth;
	}

	public void setGestationAgeAtBirth(Integer gestationAgeAtBirth) {
		this.gestationAgeAtBirth = gestationAgeAtBirth;
	}

	public Integer getBirthWeight() {
		return birthWeight;
	}

	public void setBirthWeight(Integer birthWeight) {
		this.birthWeight = birthWeight;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getGeneralPractitionerDetails() {
		return generalPractitionerDetails;
	}

	public void setGeneralPractitionerDetails(String generalPractitionerDetails) {
		this.generalPractitionerDetails = generalPractitionerDetails;
	}

	@Column
	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	@Column
	public String getPassportNumber() {
		return passportNumber;
	}

	public void setPassportNumber(String passportNumber) {
		this.passportNumber = passportNumber;
	}

	@Column
	public String getNationalHealthId() {
		return nationalHealthId;
	}

	public void setNationalHealthId(String nationalHealthId) {
		this.nationalHealthId = nationalHealthId;
	}

	@Enumerated(EnumType.STRING)
	public FacilityType getPlaceOfBirthFacilityType() {
		return placeOfBirthFacilityType;
	}

	public void setPlaceOfBirthFacilityType(FacilityType placeOfBirthFacilityType) {
		this.placeOfBirthFacilityType = placeOfBirthFacilityType;
	}

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = PERSON_LOCATIONS_TABLE_NAME,
		joinColumns = @JoinColumn(name = "person_id"),
		inverseJoinColumns = @JoinColumn(name = "location_id"))
	public Set<Location> getAddresses() {
		return addresses;
	}

	public void setAddresses(Set<Location> addresses) {
		this.addresses = addresses;
	}

	/**
	 * This change date has to be set whenever one of the embedded lists is modified: !oldList.equals(newList)
	 *
	 * @return
	 */
	public Date getChangeDateOfEmbeddedLists() {
		return changeDateOfEmbeddedLists;
	}

	public void setChangeDateOfEmbeddedLists(Date changeDateOfEmbeddedLists) {
		this.changeDateOfEmbeddedLists = changeDateOfEmbeddedLists;
	}

	@Enumerated(EnumType.STRING)
	public SymptomJournalStatus getSymptomJournalStatus() {
		return symptomJournalStatus;
	}

	public void setSymptomJournalStatus(SymptomJournalStatus symptomJournalStatus) {
		this.symptomJournalStatus = symptomJournalStatus;
	}

	@Column
	public boolean isHasCovidApp() {
		return hasCovidApp;
	}

	public void setHasCovidApp(boolean hasCovidApp) {
		this.hasCovidApp = hasCovidApp;
	}

	@Column
	public boolean isCovidCodeDelivered() {
		return covidCodeDelivered;
	}

	public void setCovidCodeDelivered(boolean covidCodeDelivered) {
		this.covidCodeDelivered = covidCodeDelivered;
	}

	@Column
	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public void setEventParticipants(Set<EventParticipant> eventParticipants) {
		this.eventParticipants = eventParticipants;
	}

	@OneToMany(cascade = {}, mappedBy = EventParticipant.PERSON, fetch = FetchType.LAZY)
	public Set<EventParticipant> getEventParticipants() {
		return eventParticipants;
	}

	public void setContacts(Set<Contact> contacts) {
		this.contacts = contacts;
	}

	@OneToMany(cascade = {}, mappedBy = Contact.PERSON, fetch = FetchType.LAZY)
	public Set<Contact> getContacts() {
		return contacts;
	}

	@Override
	public String toString() {
		return PersonDto.buildCaption(firstName, lastName);
	}
}
