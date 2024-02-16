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
			(String) objects[1],
			(ContactStatus) objects[2],
			(Disease) objects[3],
			(ContactClassification) objects[4],
			(ContactCategory) objects[5],
			(Date) objects[6],
			(Date) objects[7],
			(boolean) objects[8]);
	}

	@Override
	public List transformList(List list) {
		return list;
	}
}
