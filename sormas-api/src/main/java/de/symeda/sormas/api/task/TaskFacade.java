package de.symeda.sormas.api.task;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;

@Remote
public interface TaskFacade {

    TaskDto saveTask(TaskDto dto);
	
	List<TaskDto> getAllAfter(Date date, String userUuid);
	
	List<TaskDto> getAllByCase(CaseReferenceDto caseRef);
	
	List<TaskDto> getAllByContact(ContactReferenceDto contactRef);
	
	List<TaskDto> getAllByEvent(EventReferenceDto eventRef);
	
	List<TaskDto> getAllPendingByCase(CaseReferenceDto caseDataDto);

	List<TaskDto> getByUuids(List<String> uuids);
	
	List<DashboardTaskDto> getAllByUserForDashboard(TaskStatus taskStatus, Date from, Date to, String userUuid);
	
	long getPendingTaskCountByCase(CaseReferenceDto caseDto);
	
	long getPendingTaskCountByContact(ContactReferenceDto contactDto);
	
	long getPendingTaskCountByEvent(EventReferenceDto eventDto);
	
	long getPendingTaskCount(String userUuid);

	TaskDto getByUuid(String uuid);

	List<String> getAllUuids(String userUuid);
	
	void deleteTask(TaskDto taskDto, String userUuid);

	List<TaskIndexDto> getIndexList(String userUuid, TaskCriteria taskCriteria);
	
	void sendNewAndDueTaskMessages();
}
