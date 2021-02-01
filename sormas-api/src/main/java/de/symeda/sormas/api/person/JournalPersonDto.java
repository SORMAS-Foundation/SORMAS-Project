package de.symeda.sormas.api.person;

import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.SensitiveData;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.SensitiveData;

public class JournalPersonDto implements Serializable {

	private static final long serialVersionUID = 3561284010406711305L;

	private String uuid;
	private boolean pseudonymized;
	@PersonalData
	private String firstName;
	@PersonalData
	private String lastName;
	@SensitiveData
	private String emailAddress;
	@SensitiveData
	private String phone;
	@PersonalData
	private Integer birthdateDD;
	@PersonalData
	private Integer birthdateMM;
	@PersonalData
	private Integer birthdateYYYY;

	private Sex sex;
	private Date latestFollowUpEndDate;
	private FollowUpStatus followUpStatus;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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
		return uuid + ' ' + firstName + ' ' + lastName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		JournalPersonDto that = (JournalPersonDto) o;
		return pseudonymized == that.pseudonymized
			&& Objects.equals(uuid, that.uuid)
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
			uuid,
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
