package de.symeda.sormas.api.externaljournal;

import java.io.Serializable;
import java.util.Date;

public class ExternalPatientDto implements Serializable {

    private static final long serialVersionUID = 1300043011538769976L;

    private String firstName;
    private String lastName;
    private String gender;
    private String birthday;
    private ExternalPatientContactInformation contactInformation;
    private Date endDate;

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

    public ExternalPatientContactInformation getContactInformation() {
        return contactInformation;
    }

    public void setContactInformation(ExternalPatientContactInformation contactInformation) {
        this.contactInformation = contactInformation;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
