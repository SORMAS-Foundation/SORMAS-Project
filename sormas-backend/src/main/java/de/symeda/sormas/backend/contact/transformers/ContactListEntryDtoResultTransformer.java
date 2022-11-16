package de.symeda.sormas.backend.contact.transformers;

import java.util.Date;
import java.util.List;

import org.hibernate.transform.ResultTransformer;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.contact.ContactCategory;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactListEntryDto;
import de.symeda.sormas.api.contact.ContactStatus;

public class ContactListEntryDtoResultTransformer implements ResultTransformer {

	private static final long serialVersionUID = -2135520032690371734L;

	@Override
	public Object transformTuple(Object[] objects, String[] strings) {
		return new ContactListEntryDto(
			(String) objects[0],
			(ContactStatus) objects[1],
			(Disease) objects[2],
			(ContactClassification) objects[3],
			(ContactCategory) objects[4],
			(Date) objects[5],
			(Date) objects[6],
			(boolean) objects[7]);
	}

	@Override
	public List transformList(List list) {
		return list;
	}
}
