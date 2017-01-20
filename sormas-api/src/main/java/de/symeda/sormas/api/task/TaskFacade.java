package de.symeda.sormas.api.task;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventReferenceDto;

@Remote
public interface TaskFacade {

    TaskDto saveTask(TaskDto dto);
	
	List<TaskDto> getAllAfter(Date date, String userUuid);
	
	List<TaskDto> getAllByCase(CaseReferenceDto caseRef);
	
	List<TaskDto> getAllByContact(ContactReferenceDto contactRef);
	
	List<TaskDto> getAllByEvent(EventReferenceDto eventRef);
	
	List<TaskDto> getAllPendingByCase(CaseDataDto caseDataDto);

	List<TaskDto> getAllPendingByContact(ContactIndexDto contactDto);
	
	long getPendingTaskCountByCase(CaseDataDto caseDataDto);
	
	long getPendingTaskCountByContact(ContactIndexDto contactDto);
	
	long getPendingTaskCountByEvent(EventDto eventDto);
	
	long getPendingTaskCount(String userUuid);

	TaskDto getByUuid(String uuid);

}
