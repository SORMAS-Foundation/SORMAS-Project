package de.symeda.sormas.ui.contact;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.user.UserReferenceDto;

/**
 * @author Christopher Riedel
 */
public class ContactBulkEditData extends EntityDto {

	private static final long serialVersionUID = -7234609753914205675L;

	public static final String CONTACT_CLASSIFICATION = "contactClassification";
	public static final String CONTACT_OFFICER = "contactOfficer";

	private ContactClassification contactClassification;
	private UserReferenceDto contactOfficer;

	public ContactClassification getContactClassification() {
		return contactClassification;
	}

	public void setContactClassification(ContactClassification contactClassification) {
		this.contactClassification = contactClassification;
	}

	public UserReferenceDto getContactOfficer() {
		return contactOfficer;
	}

	public void setContactOfficer(UserReferenceDto contactOfficer) {
		this.contactOfficer = contactOfficer;
	}
}
