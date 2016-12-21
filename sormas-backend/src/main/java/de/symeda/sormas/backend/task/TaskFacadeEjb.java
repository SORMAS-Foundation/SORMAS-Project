package de.symeda.sormas.backend.task;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskFacade;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactFacadeEjb;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserFacadeEjb.UserFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserService;

@Stateless(name = "TaskFacade")
public class TaskFacadeEjb implements TaskFacade {
	
	@EJB
	private TaskService service;
	@EJB
	private UserService userService;
	@EJB
	private CaseService caseService;
	@EJB
	private ContactService contactService;
	@EJB
	private UserFacadeEjbLocal userFacade;
	@EJB
	private CaseFacadeEjbLocal caseFacade;
	
	public Task fromDto(TaskDto dto) {		
		if (dto == null) {
			return null;
		}
		
		Task task = service.getByUuid(dto.getUuid());
		if (task == null) {
			task = new Task();
			task.setUuid(dto.getUuid());
			if (dto.getCreationDate() != null) {
				task.setCreationDate(new Timestamp(dto.getCreationDate().getTime()));
			}
		} 
		
		Task a = task;
		TaskDto b = dto;
		
		a.setAssigneeUser(userService.getByReferenceDto(b.getAssigneeUser()));
		a.setAssigneeReply(b.getAssigneeReply());
		a.setCreatorUser(userService.getByReferenceDto(b.getCreatorUser()));
		a.setCreatorComment(b.getCreatorComment());
		a.setPriority(b.getPriority());
		a.setDueDate(b.getDueDate());
		a.setSuggestedStart(b.getSuggestedStart());
		a.setPerceivedStart(b.getPerceivedStart());
		// TODO is this a good place to do this?
		if (a.getTaskStatus() != b.getTaskStatus()) {
			a.setStatusChangeDate(new Date());
		} else {
			a.setStatusChangeDate(b.getStatusChangeDate());
		}
		a.setTaskStatus(b.getTaskStatus());
		a.setTaskType(b.getTaskType());
		
		a.setTaskContext(b.getTaskContext());
		if (b.getTaskContext() != null) {
			switch (b.getTaskContext()) {
			case CASE:
				a.setCaze(caseService.getByReferenceDto(b.getCaze()));
	//			a.setEvent(null);
				a.setContact(null);
				break;
			case CONTACT:
				a.setCaze(null);
				a.setContact(contactService.getByReferenceDto(b.getContact()));
				break;
			default:
				throw new UnsupportedOperationException(b.getTaskContext() + " is not implemented");
			}
		} else {
			a.setCaze(null);
//			a.setEvent(null);
			a.setContact(null);
		}
		
		return task;
	}
	
	public TaskDto toDto(Task task) {
		
		if (task == null) {
			return null;
		}

		TaskDto a = new TaskDto();
		Task b = task;
		
		a.setCreationDate(b.getCreationDate());
		a.setChangeDate(b.getChangeDate());
		a.setUuid(b.getUuid());
		
		a.setAssigneeUser(UserFacadeEjb.toReferenceDto(b.getAssigneeUser()));
		a.setAssigneeReply(b.getAssigneeReply());
		a.setCreatorUser(UserFacadeEjb.toReferenceDto(b.getCreatorUser()));
		a.setCreatorComment(b.getCreatorComment());
		a.setPriority(b.getPriority());
		a.setDueDate(b.getDueDate());
		a.setSuggestedStart(b.getSuggestedStart());
		a.setPerceivedStart(b.getPerceivedStart());
		a.setStatusChangeDate(b.getStatusChangeDate());
		a.setTaskContext(b.getTaskContext());
		a.setTaskStatus(b.getTaskStatus());
		a.setTaskType(b.getTaskType());	
		a.setCaze(CaseFacadeEjb.toReferenceDto(b.getCaze()));
		a.setContact(ContactFacadeEjb.toReferenceDto(b.getContact()));

		return a;
	}

	@Override
	public TaskDto saveTask(TaskDto dto) {
		Task ado = fromDto(dto);
		service.ensurePersisted(ado);
		
		// once we have to handle additional logic this should be moved to it's own function or even class 
		if (ado.getTaskType() == TaskType.CASE_INVESTIGATION) {
			caseFacade.updateCaseInvestigationProcess(ado.getCaze());
		}
		
		return toDto(ado);	
	}
	
	@Override
	public List<TaskDto> getAllAfter(Date date, String userUuid) {
		User user = userService.getByUuid(userUuid);
		
		if (user == null) {
			return Collections.emptyList();
		}
		
		return service.getAllAfter(date, user).stream()
			.map(c -> toDto(c))
			.collect(Collectors.toList());
	}
	
	@Override
	public List<TaskDto> getAllPendingByCase(CaseDataDto caseDataDto) {
		if(caseDataDto == null) {
			return Collections.emptyList();
		}
		
		Case caze = caseService.getByUuid(caseDataDto.getUuid());
		
		if(caze == null) {
			return Collections.emptyList();
		}
		
		return service.findBy(new TaskCriteria().cazeEquals(caze).taskStatusEquals(TaskStatus.PENDING))
				.stream()
				.map(c -> toDto(c))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<TaskDto> getAllPendingByContact(ContactIndexDto contactDto) {
		if(contactDto == null) {
			return Collections.emptyList();
		}
		
		Contact contact = contactService.getByUuid(contactDto.getUuid());
		
		if(contact == null) {
			return Collections.emptyList();
		}
		
		return service.findBy(new TaskCriteria().contactEquals(contact).taskStatusEquals(TaskStatus.PENDING))
				.stream()
				.map(c -> toDto(c))
				.collect(Collectors.toList());
	}
	
	@Override
	public long getPendingTaskCountByCase(CaseDataDto caseDataDto) {
		if(caseDataDto == null) {
			return 0;
		}
		
		Case caze = caseService.getByUuid(caseDataDto.getUuid());
		
		if(caze == null) {
			return 0;
		}
		
		return service.getCount(new TaskCriteria().cazeEquals(caze).taskStatusEquals(TaskStatus.PENDING));
	}
	
	@Override
	public long getPendingTaskCountByContact(ContactIndexDto contactDto) {
		if(contactDto == null) {
			return 0;
		}
		
		Contact contact = contactService.getByUuid(contactDto.getUuid());
		
		if(contact == null) {
			return 0;
		}
		
		return service.getCount(new TaskCriteria().contactEquals(contact).taskStatusEquals(TaskStatus.PENDING));
	}
	
	@Override
	public long getPendingTaskCount(String userUuid) {
		// TODO cache...
		User user = userService.getByUuid(userUuid);
		return service.getCount(new TaskCriteria().taskStatusEquals(TaskStatus.PENDING).assigneeUserEquals(user));
	}

	@Override
	public TaskDto getByUuid(String uuid) {
		return toDto(service.getByUuid(uuid));
	}
}

