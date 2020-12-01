package de.symeda.sormas.api.externaljournal.patientdiary;

import java.io.Serializable;

public class PatientDiaryPersonDto implements Serializable {

    private static final long serialVersionUID = 1300043011538769976L;

    private String personUUID;
    private String firstName;
    private String lastName;
    private String gender;
    private String birthday;
    private PatientDiaryContactInformation contactInformation;
    private String endDate;

    public String getPersonUUID() {
        return personUUID;
    }

    public void setPersonUUID(String personUUID) {
        this.personUUID = personUUID;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public PatientDiaryContactInformation getContactInformation() {
        return contactInformation;
    }

    public void setContactInformation(PatientDiaryContactInformation contactInformation) {
        this.contactInformation = contactInformation;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
