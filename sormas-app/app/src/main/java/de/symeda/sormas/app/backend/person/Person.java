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

package de.symeda.sormas.app.backend.person;

import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_DEFAULT;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import androidx.databinding.Bindable;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.ArmedForcesRelationType;
import de.symeda.sormas.api.person.BurialConductor;
import de.symeda.sormas.api.person.CauseOfDeath;
import de.symeda.sormas.api.person.DeathPlaceType;
import de.symeda.sormas.api.person.EducationType;
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Salutation;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.app.backend.common.PseudonymizableAdo;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.Country;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;

@Entity(name = Person.TABLE_NAME)
@DatabaseTable(tableName = Person.TABLE_NAME)
public class Person extends PseudonymizableAdo {

	private static final long serialVersionUID = -1735038738114840087L;

	public static final String TABLE_NAME = "person";
	public static final String I18N_PREFIX = "Person";

	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String APPROXIMATE_AGE = "approximateAge";
	public static final String ADDRESS = "address";
	public static final String SEX = "sex";
	public static final String NICKNAME = "nickname";
	public static final String MOTHERS_MAIDEN_NAME = "mothersMaidenName";
	public static final String BIRTHDATE_YYYY = "birthdateYYYY";
	public static final String BIRTHDATE_MM = "birthdateMM";
	public static final String BIRTHDATE_DD = "birthdateDD";

	@Column(nullable = false)
	private String firstName;
	@Column(nullable = false)
	private String lastName;
	@Enumerated(EnumType.STRING)
	private Salutation salutation;
	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String otherSalutation;
	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String birthName;
	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String nickname;
	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String mothersMaidenName;
	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String mothersName;
	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String fathersName;
	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String namesOfGuardians;
	@Column
	private Integer birthdateDD;
	@Column
	private Integer birthdateMM;
	@Column
	private Integer birthdateYYYY;
	@Column
	private Integer approximateAge;
	@Enumerated(EnumType.STRING)
	private ApproximateAgeType approximateAgeType;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date approximateAgeReferenceDate;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3)
	private Region placeOfBirthRegion;
	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3)
	private District placeOfBirthDistrict;
	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3)
	private Community placeOfBirthCommunity;
	@Enumerated(EnumType.STRING)
	private FacilityType placeOfBirthFacilityType;
	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3)
	private Facility placeOfBirthFacility;
	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String placeOfBirthFacilityDetails;

	@Column
	private Integer gestationAgeAtBirth;
	@Column
	private Integer birthWeight;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 2)
	private Location address;

	@Enumerated(EnumType.STRING)
	private Sex sex;

	@Enumerated(EnumType.STRING)
	private PresentCondition presentCondition;
	@Enumerated(EnumType.STRING)
	private CauseOfDeath causeOfDeath;
	@Column
	private String causeOfDeathDetails;
	@Enumerated(EnumType.STRING)
	private Disease causeOfDeathDisease;
	//@Deprecated
	//private String causeOfDeathDiseaseDetails;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date deathDate;
	@Enumerated(EnumType.STRING)
	private DeathPlaceType deathPlaceType;
	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String deathPlaceDescription;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date burialDate;
	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String burialPlaceDescription;
	@Enumerated(EnumType.STRING)
	private BurialConductor burialConductor;

	@Enumerated(EnumType.STRING)
	private EducationType educationType;
	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String educationDetails;

	@Enumerated(EnumType.STRING)
	private OccupationType occupationType;
	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String occupationDetails;
	@Enumerated
	private ArmedForcesRelationType armedForcesRelationType;

	@Column
	private String passportNumber;
	@Column
	private String nationalHealthId;

	private List<Location> addresses = new ArrayList<>();
	private List<PersonContactDetail> personContactDetails = new ArrayList<>();

	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String externalId;

	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String externalToken;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3)
	private Country birthCountry;
	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3)
	private Country citizenship;
	@Column(columnDefinition = "text")
	private String additionalDetails;

	public Person() {
	}

	@Bindable
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Bindable
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Salutation getSalutation() {
		return salutation;
	}

	public void setSalutation(Salutation salutation) {
		this.salutation = salutation;
	}

	public String getOtherSalutation() {
		return otherSalutation;
	}

	public void setOtherSalutation(String otherSalutation) {
		this.otherSalutation = otherSalutation;
	}

	public String getBirthName() {
		return birthName;
	}

	public void setBirthName(String birthName) {
		this.birthName = birthName;
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

	@Bindable
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

	public Location getAddress() {
		return address;
	}

	public void setAddress(Location address) {
		this.address = address;
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

	public Date getDeathDate() {
		return deathDate;
	}

	public void setDeathDate(Date deathDate) {
		this.deathDate = deathDate;
	}

	public Date getBurialDate() {
		return burialDate;
	}

	public void setBurialDate(Date burialDate) {
		this.burialDate = burialDate;
	}

	public BurialConductor getBurialConductor() {
		return burialConductor;
	}

	public void setBurialConductor(BurialConductor burialConductor) {
		this.burialConductor = burialConductor;
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

	public String getBurialPlaceDescription() {
		return burialPlaceDescription;
	}

	public void setBurialPlaceDescription(String burialPlaceDescription) {
		this.burialPlaceDescription = burialPlaceDescription;
	}

	public OccupationType getOccupationType() {
		return occupationType;
	}

	public void setOccupationType(OccupationType occupationType) {
		this.occupationType = occupationType;
	}

	@Bindable
	public String getOccupationDetails() {
		return occupationDetails;
	}

	public void setOccupationDetails(String occupationDetails) {
		this.occupationDetails = occupationDetails;
	}

	public ArmedForcesRelationType getArmedForcesRelationType() {
		return armedForcesRelationType;
	}

	public void setArmedForcesRelationType(ArmedForcesRelationType armedForcesRelationType) {
		this.armedForcesRelationType = armedForcesRelationType;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getFirstName() != null ? getFirstName() : "").append(" ").append((getLastName() != null ? getLastName() : "").toUpperCase());
		return builder.toString();
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}

	public Date getApproximateAgeReferenceDate() {
		return approximateAgeReferenceDate;
	}

	public void setApproximateAgeReferenceDate(Date approximateAgeReferenceDate) {
		this.approximateAgeReferenceDate = approximateAgeReferenceDate;
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

	public String getNamesOfGuardians() {
		return namesOfGuardians;
	}

	public void setNamesOfGuardians(String namesOfGuardians) {
		this.namesOfGuardians = namesOfGuardians;
	}

	public Region getPlaceOfBirthRegion() {
		return placeOfBirthRegion;
	}

	public void setPlaceOfBirthRegion(Region placeOfBirthRegion) {
		this.placeOfBirthRegion = placeOfBirthRegion;
	}

	public District getPlaceOfBirthDistrict() {
		return placeOfBirthDistrict;
	}

	public void setPlaceOfBirthDistrict(District placeOfBirthDistrict) {
		this.placeOfBirthDistrict = placeOfBirthDistrict;
	}

	public Community getPlaceOfBirthCommunity() {
		return placeOfBirthCommunity;
	}

	public void setPlaceOfBirthCommunity(Community placeOfBirthCommunity) {
		this.placeOfBirthCommunity = placeOfBirthCommunity;
	}

	public Facility getPlaceOfBirthFacility() {
		return placeOfBirthFacility;
	}

	public void setPlaceOfBirthFacility(Facility placeOfBirthFacility) {
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

	public FacilityType getPlaceOfBirthFacilityType() {
		return placeOfBirthFacilityType;
	}

	public void setPlaceOfBirthFacilityType(FacilityType placeOfBirthFacilityType) {
		this.placeOfBirthFacilityType = placeOfBirthFacilityType;
	}

	public List<Location> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<Location> addresses) {
		this.addresses = addresses;
	}

	public List<PersonContactDetail> getPersonContactDetails() {
		return personContactDetails;
	}

	public void setPersonContactDetails(List<PersonContactDetail> personContactDetails) {
		this.personContactDetails = personContactDetails;
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

	public Country getBirthCountry() {
		return birthCountry;
	}

	public void setBirthCountry(Country birthCountry) {
		this.birthCountry = birthCountry;
	}

	public Country getCitizenship() {
		return citizenship;
	}

	public void setCitizenship(Country citizenship) {
		this.citizenship = citizenship;
	}

	public String getAdditionalDetails() {
		return additionalDetails;
	}

	public void setAdditionalDetails(String additionalDetails) {
		this.additionalDetails = additionalDetails;
	}
}
