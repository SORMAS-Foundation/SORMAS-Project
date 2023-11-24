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

package de.symeda.sormas.app.backend.location;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_BIG;
import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;

import java.text.DecimalFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.apache.commons.lang3.StringUtils;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import androidx.databinding.Bindable;

import de.symeda.sormas.api.infrastructure.area.AreaType;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.person.PersonAddressType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.backend.common.EmbeddedAdo;
import de.symeda.sormas.app.backend.common.JoinTableReference;
import de.symeda.sormas.app.backend.common.PseudonymizableAdo;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.Continent;
import de.symeda.sormas.app.backend.region.Country;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.region.Subcontinent;

@Entity(name = Location.TABLE_NAME)
@DatabaseTable(tableName = Location.TABLE_NAME)
@EmbeddedAdo
public class Location extends PseudonymizableAdo {

	private static final long serialVersionUID = 392776645668778670L;

	public static final String TABLE_NAME = "location";
	public static final String I18N_PREFIX = "Location";
	public static final String COMMUNITY = "community";
	public static final String PERSON = "person";

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String details;
	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String city;
	@Column
	private AreaType areaType;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Continent continent;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Subcontinent subcontinent;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Country country;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Region region;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private District district;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Community community;

	@DatabaseField
	private Double latitude;
	@DatabaseField
	private Double longitude;
	@DatabaseField
	private Float latLonAccuracy;

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String postalCode;
	@Column(length = CHARACTER_LIMIT_BIG)
	private String street;
	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String houseNumber;
	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String additionalInformation;
	@Column
	private PersonAddressType addressType;
	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String addressTypeDetails;
	@Column
	private FacilityType facilityType;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Facility facility;
	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String facilityDetails;

	@Column(columnDefinition = "text")
	private String contactPersonFirstName;
	@Column(columnDefinition = "text")
	private String contactPersonLastName;
	@Column(columnDefinition = "text")
	private String contactPersonPhone;
	@Column(columnDefinition = "text")
	private String contactPersonEmail;

	/**
	 * Dirty fix for person-location association; doing this with a JoinTable is not
	 * easy in SQLite; only locations that are part of the addresses field of a person
	 * have this association.
	 */
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Person person;

	@Bindable
	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	@Bindable
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public AreaType getAreaType() {
		return areaType;
	}

	public void setAreaType(AreaType areaType) {
		this.areaType = areaType;
	}

	public Continent getContinent() {
		return continent;
	}

	public void setContinent(Continent continent) {
		this.continent = continent;
	}

	public Subcontinent getSubcontinent() {
		return subcontinent;
	}

	public void setSubcontinent(Subcontinent subcontinent) {
		this.subcontinent = subcontinent;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}

	public Community getCommunity() {
		return community;
	}

	public void setCommunity(Community community) {
		this.community = community;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	@Bindable
	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	@Bindable
	public String getHouseNumber() {
		return houseNumber;
	}

	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}

	@Bindable
	public String getAdditionalInformation() {
		return additionalInformation;
	}

	public void setAdditionalInformation(String additionalInformation) {
		this.additionalInformation = additionalInformation;
	}

	public PersonAddressType getAddressType() {
		return addressType;
	}

	public void setAddressType(PersonAddressType addressType) {
		this.addressType = addressType;
	}

	public String getAddressTypeDetails() {
		return addressTypeDetails;
	}

	public void setAddressTypeDetails(String addressTypeDetails) {
		this.addressTypeDetails = addressTypeDetails;
	}

	public FacilityType getFacilityType() {
		return facilityType;
	}

	public void setFacilityType(FacilityType facilityType) {
		this.facilityType = facilityType;
	}

	public Facility getFacility() {
		return facility;
	}

	public void setFacility(Facility facility) {
		this.facility = facility;
	}

	public String getFacilityDetails() {
		return facilityDetails;
	}

	public void setFacilityDetails(String facilityDetails) {
		this.facilityDetails = facilityDetails;
	}

	public String getContactPersonFirstName() {
		return contactPersonFirstName;
	}

	public void setContactPersonFirstName(String contactPersonFirstName) {
		this.contactPersonFirstName = contactPersonFirstName;
	}

	public String getContactPersonLastName() {
		return contactPersonLastName;
	}

	public void setContactPersonLastName(String contactPersonLastName) {
		this.contactPersonLastName = contactPersonLastName;
	}

	public String getContactPersonPhone() {
		return contactPersonPhone;
	}

	public void setContactPersonPhone(String contactPersonPhone) {
		this.contactPersonPhone = contactPersonPhone;
	}

	public String getContactPersonEmail() {
		return contactPersonEmail;
	}

	public void setContactPersonEmail(String contactPersonEmail) {
		this.contactPersonEmail = contactPersonEmail;
	}

	@JoinTableReference
	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public String getCompleteString() {

		StringBuilder sb = new StringBuilder();
		if (getStreet() != null && !getStreet().isEmpty()) {
			sb.append(getStreet());
		}
		if (getHouseNumber() != null && !getHouseNumber().isEmpty()) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			sb.append(getHouseNumber());
		}
		if (getAdditionalInformation() != null && !getAdditionalInformation().isEmpty()) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(getAdditionalInformation());
		}

		if ((getCity() != null && !getCity().isEmpty()) || getCommunity() != null || getDistrict() != null || getAreaType() != null) {
			if (sb.length() > 0) {
				sb.append("\n");
			}
			if (getCity() != null && !getCity().isEmpty()) {
				sb.append(getCity());
			} else if (getCommunity() != null) {
				sb.append(getCommunity().buildCaption());
			}
			if (getDistrict() != null) {
				if (sb.length() > 0) {
					sb.append(", ");
				}
				sb.append(getDistrict().buildCaption());
			}
			if (getAreaType() != null) {
				if (sb.length() > 0) {
					sb.append(", ");
				}
				sb.append(getAreaType().toString());
			}
		}

		if (getDetails() != null && !getDetails().isEmpty()) {
			if (sb.length() > 0) {
				sb.append("\n");
			}
			sb.append(getDetails());
		}

		if (StringUtils.isNotEmpty(getContactPersonFirstName()) || StringUtils.isNotEmpty(getContactPersonLastName())) {
			sb.append("\n");
			StringBuilder contactNameRow = new StringBuilder();
			if (StringUtils.isNotEmpty(getContactPersonFirstName())) {
				contactNameRow.append(getContactPersonFirstName());
			}
			if (StringUtils.isNotEmpty(getContactPersonLastName())) {
				if (contactNameRow.length() > 0) {
					contactNameRow.append(" ");
				}
				contactNameRow.append(getContactPersonLastName());
			}
			sb.append(contactNameRow);
		}

		if (StringUtils.isNotEmpty(getContactPersonPhone()) || StringUtils.isNotEmpty(getContactPersonEmail())) {
			sb.append("\n");
			StringBuilder phoneAndEmailRow = new StringBuilder();
			if (StringUtils.isNotEmpty(getContactPersonPhone())) {
				phoneAndEmailRow.append(getContactPersonPhone());
			}
			if (StringUtils.isNotEmpty(getContactPersonEmail())) {
				if (phoneAndEmailRow.length() > 0) {
					phoneAndEmailRow.append(", ");
				}
				phoneAndEmailRow.append(getContactPersonEmail());
			}
			sb.append(phoneAndEmailRow);
		}

		String latLonString = getLatLonString();
		if (!DataHelper.isNullOrEmpty(latLonString)) {
			if (sb.length() > 0) {
				sb.append("\n");
			}
			sb.append(latLonString);
		}

		return sb.toString();
	}

	public String buildShortCaption() {
		StringBuilder sb = new StringBuilder();

		if (region != null) {
			sb.append(region.buildCaption());
		}

		if (district != null) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(district.buildCaption());
		}

		if (sb.length() == 0) {
			if (getStreet() != null && !getStreet().isEmpty()) {
				sb.append(getStreet());
			}
			if (StringUtils.isNotEmpty(city)) {
				if (sb.length() > 0) {
					sb.append(", ");
				}
				sb.append(getCity());
			}

			if (country != null) {
				if (sb.length() > 0) {
					sb.append(", ");
				}
				sb.append(country.buildCaption());
			}
		}

		return sb.toString();
	}

	public String getLatLonString() {
		return getLatLonString(getLatitude(), getLongitude(), getLatLonAccuracy());
	}

	public static String getLatLonString(Double latitude, Double longitude, Float latLonAccuracy) {
		if (latitude != null && longitude != null) {

			StringBuilder resultString = new StringBuilder();
			DecimalFormat df = new DecimalFormat("###.#####");

			try {
				String latString = df.format(latitude);
				String lonString = df.format(longitude);
				resultString.append(latString).append(", ").append(lonString);
				if (latLonAccuracy != null) {
					resultString.append(" +-").append(Math.round(latLonAccuracy)).append("m");
				}
				return resultString.toString();
			} catch (IllegalArgumentException convertException) {
				// ignore
			}
		}
		return "";
	}

	@Override
	public String buildCaption() {
		return getCompleteString();
	}

	public boolean isEmptyLocation() {
		return street == null
			&& houseNumber == null
			&& additionalInformation == null
			&& details == null
			&& city == null
			&& region == null
			&& district == null
			&& community == null;
	}

	public String getGpsLocation() {
		if (latitude == null || longitude == null) {
			return "";
		}

		if (latLonAccuracy != null) {
			return android.location.Location.convert(latitude, android.location.Location.FORMAT_DEGREES) + ", "
				+ android.location.Location.convert(longitude, android.location.Location.FORMAT_DEGREES) + " +-" + Math.round(latLonAccuracy) + "m";
		} else {
			return android.location.Location.convert(latitude, android.location.Location.FORMAT_DEGREES) + ", "
				+ android.location.Location.convert(longitude, android.location.Location.FORMAT_DEGREES);
		}
	}

	public Location asNewLocation() {

		Location location = this;
		location.setId(null);
		location.setUuid(DataHelper.createUuid());
		location.setChangeDate(new Date(0));
		location.setLocalChangeDate(new Date());
		location.setCreationDate(new Date());
		return location;
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}

	public Float getLatLonAccuracy() {
		return latLonAccuracy;
	}

	public void setLatLonAccuracy(Float latLonAccuracy) {
		this.latLonAccuracy = latLonAccuracy;
	}
}
