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

package de.symeda.sormas.api.person;

import java.util.Date;
import java.util.Objects;

import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.uuid.AbstractUuidDto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data transfer object for personal data of a person that has registered visit data from a external journal.")
public class JournalPersonDto extends AbstractUuidDto {

	private static final long serialVersionUID = 3561284010406711305L;
	@Schema(description = "Whether sensitive and/or personal data of this DTO is pseudonymized.")
	private boolean pseudonymized;
	@PersonalData
	@Schema(description = "First name(s) of the person")
	private String firstName;
	@PersonalData
	@Schema(description = "Last name of the person")
	private String lastName;
	@SensitiveData
	@Schema(description = "E-Mail address of the person")
	private String emailAddress;
	@SensitiveData
	@Schema(description = "Phone number of the person")
	private String phone;
	@PersonalData
	@Schema(description = "Birth day of the person")
	private Integer birthdateDD;
	@PersonalData
	@Schema(description = "Birth month of the person")
	private Integer birthdateMM;
	@PersonalData
	@Schema(description = "Birth year of the person")
	private Integer birthdateYYYY;

	private Sex sex;
	@Schema(description = "Latest date when a follow-up should be performed")
	private Date latestFollowUpEndDate;
	private FollowUpStatus followUpStatus;

	public JournalPersonDto(String uuid) {
		super(uuid);
	}

	public boolean isPseudonymized() {
		return pseudonymized;
	}

	public void setPseudonymized(boolean pseudonymized) {
		this.pseudonymized = pseudonymized;
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

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
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

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public Date getLatestFollowUpEndDate() {
		return latestFollowUpEndDate;
	}

	public void setLatestFollowUpEndDate(Date latestFollowUpEndDate) {
		this.latestFollowUpEndDate = latestFollowUpEndDate;
	}

	public FollowUpStatus getFollowUpStatus() {
		return followUpStatus;
	}

	public void setFollowUpStatus(FollowUpStatus followUpStatus) {
		this.followUpStatus = followUpStatus;
	}

	@Override
	public String toString() {
		return getUuid() + ' ' + firstName + ' ' + lastName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		JournalPersonDto that = (JournalPersonDto) o;
		return pseudonymized == that.pseudonymized
			&& Objects.equals(getUuid(), that.getUuid())
			&& Objects.equals(firstName, that.firstName)
			&& Objects.equals(lastName, that.lastName)
			&& Objects.equals(emailAddress, that.emailAddress)
			&& Objects.equals(phone, that.phone)
			&& Objects.equals(birthdateDD, that.birthdateDD)
			&& Objects.equals(birthdateMM, that.birthdateMM)
			&& Objects.equals(birthdateYYYY, that.birthdateYYYY)
			&& sex == that.sex
			&& Objects.equals(latestFollowUpEndDate, that.latestFollowUpEndDate)
			&& followUpStatus == that.followUpStatus;
	}

	@Override
	public int hashCode() {
		return Objects.hash(
			getUuid(),
			pseudonymized,
			firstName,
			lastName,
			emailAddress,
			phone,
			birthdateDD,
			birthdateMM,
			birthdateYYYY,
			sex,
			latestFollowUpEndDate,
			followUpStatus);
	}
}
