package de.symeda.sormas.backend.contact.transformers;

import java.util.List;

import org.hibernate.transform.ResultTransformer;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.contact.ContactCategory;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactListEntryDto;
import de.symeda.sormas.api.contact.ContactStatus;

public class ContactListEntryDtoResultTransformer implements ResultTransformer {

	@Override
	public Object transformTuple(Object[] objects, String[] strings) {
		return new ContactListEntryDto(
			(String) objects[0],
			(ContactStatus) objects[1],
			(Disease) objects[2],
			(ContactClassification) objects[3],
			(ContactCategory) objects[4],
			(boolean) objects[5]);
	}

	@Override
	public List transformList(List list) {
		return list;
	}
}
