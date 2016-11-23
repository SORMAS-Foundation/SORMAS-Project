package de.symeda.sormas.api.contact;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.caze.CaseReferenceDto;

@Remote
public interface ContactFacade {

	List<ContactDto> getAllContactsAfter(Date date, String userUuid);

	List<ContactIndexDto> getIndexList(String userUuid);

	List<ContactIndexDto> getIndexListByCase(String userUuid, CaseReferenceDto caseRef);

	ContactDto getContactByUuid(String uuid);
    
	ContactDto saveContact(ContactDto dto);

	ContactReferenceDto getReferenceByUuid(String uuid);
}
