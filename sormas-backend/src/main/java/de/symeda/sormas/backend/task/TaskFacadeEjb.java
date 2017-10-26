package de.symeda.sormas.backend.task;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventReferenceDto;
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
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventFacadeEjb;
import de.symeda.sormas.backend.event.EventService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserFacadeEjb.UserFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "TaskFacade")
public class TaskFacadeEjb implements TaskFacade {
	
	@EJB
	private TaskService taskService;
	@EJB
	private UserService userService;
	@EJB
	private CaseService caseService;
	@EJB
	private ContactService contactService;
	@EJB
	private EventService eventService;
	@EJB
	private UserFacadeEjbLocal userFacade;
	@EJB
	private CaseFacadeEjbLocal caseFacade;
	
	public Task fromDto(TaskDto source) {		
		if (source == null) {
			return null;
		}
		
		Task target = taskService.getByUuid(source.getUuid());
		if (target == null) {
			target = new Task();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		} 
		DtoHelper.validateDto(source, target);
		
		target.setAssigneeUser(userService.getByReferenceDto(source.getAssigneeUser()));
		target.setAssigneeReply(source.getAssigneeReply());
		target.setCreatorUser(userService.getByReferenceDto(source.getCreatorUser()));
		target.setCreatorComment(source.getCreatorComment());
		target.setPriority(source.getPriority());
		target.setDueDate(source.getDueDate());
		target.setSuggestedStart(source.getSuggestedStart());
		target.setPerceivedStart(source.getPerceivedStart());
		// TODO is this a good place to do this?
		if (target.getTaskStatus() != source.getTaskStatus()) {
			target.setStatusChangeDate(new Date());
		} else {
			target.setStatusChangeDate(source.getStatusChangeDate());
		}
		target.setTaskStatus(source.getTaskStatus());
		target.setTaskType(source.getTaskType());
		
		target.setClosedLat(source.getClosedLat());
		target.setClosedLon(source.getClosedLon());
		target.setClosedLatLonAccuracy(source.getClosedLatLonAccuracy());
		
		target.setTaskContext(source.getTaskContext());
		if (source.getTaskContext() != null) {
			switch (source.getTaskContext()) {
			case CASE:
				target.setCaze(caseService.getByReferenceDto(source.getCaze()));
				target.setContact(null);
				target.setEvent(null);
				break;
			case CONTACT:
				target.setCaze(null);
				target.setContact(contactService.getByReferenceDto(source.getContact()));
				target.setEvent(null);
				break;
			case EVENT:
				target.setCaze(null);
				target.setContact(null);
				target.setEvent(eventService.getByReferenceDto(source.getEvent()));
				break;
			case GENERAL:
				target.setCaze(null);
				target.setContact(null);
				target.setEvent(null);
				break;
			default:
				throw new UnsupportedOperationException(source.getTaskContext() + " is not implemented");
			}
		} else {
			target.setCaze(null);
			target.setContact(null);
			target.setEvent(null);
		}
		
		return target;
	}
	
	public TaskDto toDto(Task task) {
		
		if (task == null) {
			return null;
		}

		TaskDto target = new TaskDto();
		Task source = task;
		
		target.setCreationDate(source.getCreationDate());
		target.setChangeDate(source.getChangeDate());
		target.setUuid(source.getUuid());
		
		target.setAssigneeUser(UserFacadeEjb.toReferenceDto(source.getAssigneeUser()));
		target.setAssigneeReply(source.getAssigneeReply());
		target.setCreatorUser(UserFacadeEjb.toReferenceDto(source.getCreatorUser()));
		target.setCreatorComment(source.getCreatorComment());
		target.setPriority(source.getPriority());
		target.setDueDate(source.getDueDate());
		target.setSuggestedStart(source.getSuggestedStart());
		target.setPerceivedStart(source.getPerceivedStart());
		target.setStatusChangeDate(source.getStatusChangeDate());
		target.setTaskContext(source.getTaskContext());
		target.setTaskStatus(source.getTaskStatus());
		target.setTaskType(source.getTaskType());	
		target.setCaze(CaseFacadeEjb.toReferenceDto(source.getCaze()));
		target.setContact(ContactFacadeEjb.toReferenceDto(source.getContact()));
		target.setEvent(EventFacadeEjb.toReferenceDto(source.getEvent()));
		
		target.setClosedLat(source.getClosedLat());
		target.setClosedLon(source.getClosedLon());
		target.setClosedLatLonAccuracy(source.getClosedLatLonAccuracy());

		return target;
	}

	@Override
	public TaskDto saveTask(TaskDto dto) {
		Task ado = fromDto(dto);
		taskService.ensurePersisted(ado);
		
		// once we have to handle additional logic this should be moved to it's own function or even class 
		if (ado.getTaskType() == TaskType.CASE_INVESTIGATION) {
			caseFacade.updateCaseInvestigationProcess(ado.getCaze());
		}
		
		return toDto(ado);	
	}
	
	@Override
	public List<String> getAllUuids(String userUuid) {
		
		User user = userService.getByUuid(userUuid);
		
		if (user == null) {
			return Collections.emptyList();
		}
		
		return taskService.getAllUuids(user);
	}
	
	@Override
	public List<TaskDto> getAllAfter(Date date, String userUuid) {
		User user = userService.getByUuid(userUuid);
		
		if (user == null) {
			return Collections.emptyList();
		}
		
		return taskService.getAllAfter(date, user).stream()
			.map(c -> toDto(c))
			.collect(Collectors.toList());
	}
	
	@Override
	public List<TaskDto> getAllByCase(CaseReferenceDto caseRef) {
		if(caseRef == null) {
			return Collections.emptyList();
		}

		Case caze = caseService.getByUuid(caseRef.getUuid());
		
		return taskService.findBy(new TaskCriteria().cazeEquals(caze))
				.stream()
				.map(c -> toDto(c))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<TaskDto> getAllByContact(ContactReferenceDto contactRef) {
		if(contactRef == null) {
			return Collections.emptyList();
		}

		Contact contact = contactService.getByUuid(contactRef.getUuid());
		
		return taskService.findBy(new TaskCriteria().contactEquals(contact))
				.stream()
				.map(c -> toDto(c))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<TaskDto> getAllByEvent(EventReferenceDto eventRef) {
		if(eventRef == null) {
			return Collections.emptyList();
		}

		Event event = eventService.getByUuid(eventRef.getUuid());
		
		return taskService.findBy(new TaskCriteria().eventEquals(event))
				.stream()
				.map(c -> toDto(c))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<TaskDto> getByUuids(List<String> uuids) {
		return taskService.getByUuids(uuids)
				.stream()
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
		
		return taskService.findBy(new TaskCriteria().cazeEquals(caze).taskStatusEquals(TaskStatus.PENDING))
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
		
		return taskService.findBy(new TaskCriteria().contactEquals(contact).taskStatusEquals(TaskStatus.PENDING))
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
		
		return taskService.getCount(new TaskCriteria().cazeEquals(caze).taskStatusEquals(TaskStatus.PENDING));
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
		
		return taskService.getCount(new TaskCriteria().contactEquals(contact).taskStatusEquals(TaskStatus.PENDING));
	}
	
	@Override
	public long getPendingTaskCountByEvent(EventDto eventDto) {
		if(eventDto == null) {
			return 0;
		}
		
		Event event = eventService.getByUuid(eventDto.getUuid());
		
		if(event == null) {
			return 0;
		}
		
		return taskService.getCount(new TaskCriteria().eventEquals(event).taskStatusEquals(TaskStatus.PENDING));
	}
	
	@Override
	public long getPendingTaskCount(String userUuid) {
		// TODO cache...
		User user = userService.getByUuid(userUuid);
		return taskService.getCount(new TaskCriteria().taskStatusEquals(TaskStatus.PENDING).assigneeUserEquals(user));
	}

	@Override
	public TaskDto getByUuid(String uuid) {
		return toDto(taskService.getByUuid(uuid));
	}
}

