package de.symeda.sormas.api.person;

import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.SensitiveData;

import java.io.Serializable;
import java.util.Date;

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
	@SensitiveData
	private Date latestFollowUpEndDate;
	@SensitiveData
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
}
