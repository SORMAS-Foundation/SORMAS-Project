package de.symeda.sormas.backend.contact.transformers;

import java.util.Date;
import java.util.List;

import org.hibernate.transform.ResultTransformer;

import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactListEntryDto;
import de.symeda.sormas.api.contact.ContactStatus;

public class ContactListEntryDtoResultTransformer implements ResultTransformer {

	@Override
	public Object transformTuple(Object[] objects, String[] strings) {
		return new ContactListEntryDto(
			(String) objects[0],
			(ContactClassification) objects[1],
			(ContactStatus) objects[2],
			(Date) objects[3],
			(boolean) objects[4]);
	}

	@Override
	public List transformList(List list) {
		return list;
	}
}
