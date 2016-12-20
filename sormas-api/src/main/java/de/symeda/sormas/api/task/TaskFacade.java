package de.symeda.sormas.api.task;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactIndexDto;

@Remote
public interface TaskFacade {

    TaskDto saveTask(TaskDto dto);
	
	List<TaskDto> getAllAfter(Date date, String userUuid);
	
	List<TaskDto> getAllPendingForCase(CaseDataDto caseDataDto);

	List<TaskDto> getAllPendingForContact(ContactIndexDto contactDto);
	
	long getPendingTaskCount(String userUuid);

	TaskDto getByUuid(String uuid);

}
