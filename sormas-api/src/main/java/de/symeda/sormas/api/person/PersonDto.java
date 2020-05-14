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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.person;

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.api.utils.Outbreaks;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.Required;

public class PersonDto extends EntityDto {

	private static final long serialVersionUID = -8558187171374254398L;

	public static final String I18N_PREFIX = "Person";

	public static final String SEX = "sex";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";

	public static final String PRESENT_CONDITION = "presentCondition";
	public static final String BIRTH_DATE = "birthdate";
	public static final String BIRTH_DATE_DD = "birthdateDD";
	public static final String BIRTH_DATE_MM = "birthdateMM";
	public static final String BIRTH_DATE_YYYY = "birthdateYYYY";

	public static final String APPROXIMATE_AGE = "approximateAge";
	public static final String APPROXIMATE_AGE_TYPE = "approximateAgeType";
	public static final String APPROXIMATE_AGE_REFERENCE_DATE = "approximateAgeReferenceDate";

	public static final String CAUSE_OF_DEATH = "causeOfDeath";
	public static final String CAUSE_OF_DEATH_DISEASE = "causeOfDeathDisease";
	public static final String CAUSE_OF_DEATH_DETAILS = "causeOfDeathDetails";
	public static final String CAUSE_OF_DEATH_DISEASE_DETAILS = "causeOfDeathDiseaseDetails";
	public static final String DEATH_DATE = "deathDate";
	public static final String DEATH_PLACE_TYPE = "deathPlaceType";
	public static final String DEATH_PLACE_DESCRIPTION = "deathPlaceDescription";
	public static final String BURIAL_DATE = "burialDate";
	public static final String BURIAL_PLACE_DESCRIPTION = "burialPlaceDescription";
	public static final String BURIAL_CONDUCTOR = "burialConductor";

	public static final String NICKNAME = "nickname";
	public static final String MOTHERS_MAIDEN_NAME = "mothersMaidenName";

	public static final String PHONE = "phone";
	public static final String PHONE_OWNER = "phoneOwner";
	public static final String ADDRESS = "address";

	public static final String EDUCATION_TYPE = "educationType";
	public static final String EDUCATION_DETAILS = "educationDetails";
	public static final String OCCUPATION_TYPE = "occupationType";
	public static final String OCCUPATION_DETAILS = "occupationDetails";
	public static final String OCCUPATION_REGION = "occupationRegion";
	public static final String OCCUPATION_DISTRICT = "occupationDistrict";
	public static final String OCCUPATION_COMMUNITY = "occupationCommunity";
	public static final String OCCUPATION_FACILITY = "occupationFacility";
	public static final String OCCUPATION_FACILITY_DETAILS = "occupationFacilityDetails";	
	
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

	// Fields are declared in the order they should appear in the import template

	@Outbreaks
	@Required
	@PersonalData
	private String firstName;
	@Outbreaks
	@Required
	@PersonalData
	private String lastName;
	private String nickname;
	private String mothersName;
	private String mothersMaidenName;
	private String fathersName;
	@Outbreaks
	private Sex sex;
	@Outbreaks
	@PersonalData
	private Integer birthdateDD;
	@Outbreaks
	private Integer birthdateMM;
	@Outbreaks
	private Integer birthdateYYYY;
	@Outbreaks
	private Integer approximateAge;
	@Outbreaks
	private ApproximateAgeType approximateAgeType;
	@Outbreaks
	private Date approximateAgeReferenceDate;
	@Diseases({Disease.CONGENITAL_RUBELLA})
	private RegionReferenceDto placeOfBirthRegion;
	@Diseases({Disease.CONGENITAL_RUBELLA})
	private DistrictReferenceDto placeOfBirthDistrict;
	@Diseases({Disease.CONGENITAL_RUBELLA})
	private CommunityReferenceDto placeOfBirthCommunity;
	@Diseases({Disease.CONGENITAL_RUBELLA})
	private FacilityReferenceDto placeOfBirthFacility;
	@Diseases({Disease.CONGENITAL_RUBELLA})
	private String placeOfBirthFacilityDetails;
	@Diseases({Disease.CONGENITAL_RUBELLA})
	private Integer gestationAgeAtBirth;
	@Diseases({Disease.CONGENITAL_RUBELLA})
	private Integer birthWeight;
	
	@Outbreaks
	private PresentCondition presentCondition;
	private Date deathDate;
	private CauseOfDeath causeOfDeath;
	private Disease causeOfDeathDisease;
	private String causeOfDeathDetails;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.POLIO, Disease.UNSPECIFIED_VHF, Disease.CORONAVIRUS,
			Disease.UNDEFINED, Disease.OTHER })
	private DeathPlaceType deathPlaceType;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.POLIO, Disease.UNSPECIFIED_VHF, Disease.CORONAVIRUS,
			Disease.UNDEFINED, Disease.OTHER })
	private String deathPlaceDescription;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.POLIO, Disease.CORONAVIRUS, Disease.UNSPECIFIED_VHF,
			Disease.UNDEFINED, Disease.OTHER })
	private Date burialDate;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.POLIO, Disease.CORONAVIRUS, Disease.UNSPECIFIED_VHF,
			Disease.UNDEFINED, Disease.OTHER })
	private String burialPlaceDescription;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.POLIO, Disease.CORONAVIRUS, Disease.UNSPECIFIED_VHF,
			Disease.UNDEFINED, Disease.OTHER })
	private BurialConductor burialConductor;
	private String phone;
	private String phoneOwner;
	private LocationDto address;
	private String emailAddress;

	private EducationType educationType;
	private String educationDetails;
	
	private OccupationType occupationType;
	private String occupationDetails;
	private RegionReferenceDto occupationRegion;
	private DistrictReferenceDto occupationDistrict;
	private CommunityReferenceDto occupationCommunity;
	private FacilityReferenceDto occupationFacility;
	private String occupationFacilityDetails;
	private String generalPractitionerDetails;
	private String passportNumber;
	private String nationalHealthId;

	public Integer getBirthdateDD() {
		return birthdateDD;
	}

	public void setBirthdateDD(Integer birthdateDD) {
		this.birthdateDD = birthdateDD;
	}

	public Integer getBirthdateMM() {
		return birthdateMM;
	}

	public void setBirthdateMM(Integer birthdateMM) {
		this.birthdateMM = birthdateMM;
	}

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

	public ApproximateAgeType getApproximateAgeType() {
		return approximateAgeType;
	}

	public void setApproximateAgeType(ApproximateAgeType approximateAgeType) {
		this.approximateAgeType = approximateAgeType;
	}

	public Date getApproximateAgeReferenceDate() {
		return approximateAgeReferenceDate;
	}

	public void setApproximateAgeReferenceDate(Date approximateAgeReferenceDate) {
		this.approximateAgeReferenceDate = approximateAgeReferenceDate;
	}

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

	public Date getBurialDate() {
		return burialDate;
	}

	public void setBurialDate(Date burialDate) {
		this.burialDate = burialDate;
	}

	public String getBurialPlaceDescription() {
		return burialPlaceDescription;
	}

	public void setBurialPlaceDescription(String burialPlaceDescription) {
		this.burialPlaceDescription = burialPlaceDescription;
	}

	public BurialConductor getBurialConductor() {
		return burialConductor;
	}

	public void setBurialConductor(BurialConductor burialConductor) {
		this.burialConductor = burialConductor;
	}

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

	public Disease getCauseOfDeathDisease() {
		return causeOfDeathDisease;
	}

	public void setCauseOfDeathDisease(Disease causeOfDeathDisease) {
		this.causeOfDeathDisease = causeOfDeathDisease;
	}

	public Date getDeathDate() {
		return deathDate;
	}

	public void setDeathDate(Date deathDate) {
		this.deathDate = deathDate;
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

	public LocationDto getAddress() {
		return address;
	}

	public void setAddress(LocationDto address) {
		this.address = address;
	}

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

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
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

	public FacilityReferenceDto getOccupationFacility() {
		return occupationFacility;
	}

	public void setOccupationFacility(FacilityReferenceDto occupationFacility) {
		this.occupationFacility = occupationFacility;
	}

	public RegionReferenceDto getOccupationRegion() {
		return occupationRegion;
	}

	public void setOccupationRegion(RegionReferenceDto occupationRegion) {
		this.occupationRegion = occupationRegion;
	}

	public DistrictReferenceDto getOccupationDistrict() {
		return occupationDistrict;
	}

	public void setOccupationDistrict(DistrictReferenceDto occupationDistrict) {
		this.occupationDistrict = occupationDistrict;
	}

	public CommunityReferenceDto getOccupationCommunity() {
		return occupationCommunity;
	}

	public void setOccupationCommunity(CommunityReferenceDto occupationCommunity) {
		this.occupationCommunity = occupationCommunity;
	}

	public String getOccupationFacilityDetails() {
		return occupationFacilityDetails;
	}

	public void setOccupationFacilityDetails(String occupationFacilityDetails) {
		this.occupationFacilityDetails = occupationFacilityDetails;
	}

	public String getMothersName() {
		return mothersName;
	}

	public void setMothersName(String mothersName) {
		this.mothersName = mothersName;
	}

	public String getFathersName() {
		return fathersName;
	}

	public void setFathersName(String fathersName) {
		this.fathersName = fathersName;
	}

	public RegionReferenceDto getPlaceOfBirthRegion() {
		return placeOfBirthRegion;
	}

	public void setPlaceOfBirthRegion(RegionReferenceDto placeOfBirthRegion) {
		this.placeOfBirthRegion = placeOfBirthRegion;
	}

	public DistrictReferenceDto getPlaceOfBirthDistrict() {
		return placeOfBirthDistrict;
	}

	public void setPlaceOfBirthDistrict(DistrictReferenceDto placeOfBirthDistrict) {
		this.placeOfBirthDistrict = placeOfBirthDistrict;
	}

	public CommunityReferenceDto getPlaceOfBirthCommunity() {
		return placeOfBirthCommunity;
	}

	public void setPlaceOfBirthCommunity(CommunityReferenceDto placeOfBirthCommunity) {
		this.placeOfBirthCommunity = placeOfBirthCommunity;
	}

	public FacilityReferenceDto getPlaceOfBirthFacility() {
		return placeOfBirthFacility;
	}

	public void setPlaceOfBirthFacility(FacilityReferenceDto placeOfBirthFacility) {
		this.placeOfBirthFacility = placeOfBirthFacility;
	}

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

	public String getGeneralPractitionerDetails() {
		return generalPractitionerDetails;
	}

	public void setGeneralPractitionerDetails(String generalPractitionerDetails) {
		this.generalPractitionerDetails = generalPractitionerDetails;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getPassportNumber() {
		return passportNumber;
	}

	public void setPassportNumber(String passportNumber) {
		this.passportNumber = passportNumber;
	}

	public String getNationalHealthId() {
		return nationalHealthId;
	}

	public void setNationalHealthId(String nationalHealthId) {
		this.nationalHealthId = nationalHealthId;
	}

	@Override
	public String toString() {
		return buildCaption(firstName, lastName);
	}

	public PersonReferenceDto toReference() {
		return new PersonReferenceDto(getUuid(), firstName, lastName);
	}

	public static String buildCaption(String firstName, String lastName) {
		return DataHelper.toStringNullable(firstName) + " " + DataHelper.toStringNullable(lastName).toUpperCase();
	}
	
	public static PersonDto build() {
		PersonDto person = new PersonDto();
		person.setUuid(DataHelper.createUuid());
		person.setAddress(LocationDto.build());
		return person;
	}

}
