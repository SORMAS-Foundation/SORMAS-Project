package de.symeda.sormas.api.contact;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.MapCaseDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;

@Remote
public interface ContactFacade {

	List<ContactDto> getAllContactsAfter(Date date, String userUuid);
	
	List<ContactDto> getFollowUpBetween(Date fromDate, Date toDate, DistrictReferenceDto districtRef, Disease disease, String userUuid);
	
	List<ContactDto> getAllByCase(CaseReferenceDto caseRef);
	
	ContactDto getContactByUuid(String uuid);
    
	ContactDto saveContact(ContactDto dto);
	
	ContactDto updateFollowUpUntilAndStatus(ContactDto dto);
	
	List<ContactReferenceDto> getSelectableContacts(UserReferenceDto user);

	ContactReferenceDto getReferenceByUuid(String uuid);

	List<String> getAllUuids(String userUuid);

	void generateContactFollowUpTasks();

	List<ContactDto> getByUuids(List<String> uuids);
	
	List<MapContactDto> getContactsForMap(DistrictReferenceDto districtRef, Disease disease, Date fromDate, Date toDate, String userUuid, List<MapCaseDto> mapCaseDtos);
	
	void deleteContact(ContactReferenceDto contactRef, String userUuid);
	
	List<ContactIndexDto> getIndexList(String userUuid, CaseReferenceDto caseRef);
}
