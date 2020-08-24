package de.symeda.sormas.api.visit;

import java.io.Serializable;

import de.symeda.sormas.api.BaseCriteria;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.utils.IgnoreForUrl;

public class VisitCriteria extends BaseCriteria implements Serializable {

	private static final long serialVersionUID = -4565935488912148448L;

	private ContactReferenceDto contact;

	private CaseReferenceDto caze;

	@IgnoreForUrl
	public ContactReferenceDto getContact() {
		return contact;
	}

	public VisitCriteria contact(ContactReferenceDto contact) {
		this.contact = contact;
		return this;
	}

	@IgnoreForUrl
	public CaseReferenceDto getCaze() {
		return caze;
	}

	public VisitCriteria caze(CaseReferenceDto caze) {
		this.caze = caze;
		return this;
	}

	@IgnoreForUrl
	public boolean isEmpty() {
		return contact == null && caze == null;
	}
}
