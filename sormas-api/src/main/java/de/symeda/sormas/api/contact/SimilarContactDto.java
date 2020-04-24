package de.symeda.sormas.api.contact;

import java.io.Serializable;

public class SimilarContactDto implements Serializable {

    private String firstName;
    private String lastName;

    private String contactUUID;
    private ContactClassification contactClassification;
    private ContactStatus contactStatus;
    private FollowUpStatus followUpStatus;

    public SimilarContactDto(String firstName, String lastName, String contactUUID, ContactClassification contactClassification,
                             ContactStatus contactStatus, FollowUpStatus followUpStatus) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.contactUUID = contactUUID;
        this.contactClassification = contactClassification;
        this.contactStatus = contactStatus;
        this.followUpStatus = followUpStatus;
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

    public String getContactUUID() {
        return contactUUID;
    }

    public void setContactUUID(String contactUUID) {
        this.contactUUID = contactUUID;
    }

    public ContactClassification getContactClassification() {
        return contactClassification;
    }

    public void setContactClassification(ContactClassification contactClassification) {
        this.contactClassification = contactClassification;
    }

    public ContactStatus getContactStatus() {
        return contactStatus;
    }

    public void setContactStatus(ContactStatus contactStatus) {
        this.contactStatus = contactStatus;
    }

    public FollowUpStatus getFollowUpStatus() {
        return followUpStatus;
    }

    public void setFollowUpStatus(FollowUpStatus followUpStatus) {
        this.followUpStatus = followUpStatus;
    }
}
