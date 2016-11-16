package de.symeda.sormas.api.contact;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

@Remote
public interface ContactFacade {

	List<ContactDto> getAllContactsAfter(Date date, String userUuid);

	List<ContactIndexDto> getIndexList(String userUuid);

	ContactDto getContactByUuid(String uuid);
    
	ContactDto saveContact(ContactDto dto);
}
