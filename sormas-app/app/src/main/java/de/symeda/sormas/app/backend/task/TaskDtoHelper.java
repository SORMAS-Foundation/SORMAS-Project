package de.symeda.sormas.app.backend.task;

import java.util.List;

import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDtoHelper;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.contact.ContactDtoHelper;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventDtoHelper;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.user.UserDtoHelper;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

/**
 * Created by Stefan Szczesny on 24.10.2016.
 */
public class TaskDtoHelper extends AdoDtoHelper<Task, TaskDto> {

    @Override
    protected Class<Task> getAdoClass() {
        return Task.class;
    }

    @Override
    protected Class<TaskDto> getDtoClass() { return TaskDto.class; }

    @Override
    protected Call<List<TaskDto>> pullAllSince(long since) {
        return RetroProvider.getTaskFacade().pullAllSince(since);
    }

    @Override
    protected Call<Integer> pushAll(List<TaskDto> taskDtos) {
        return RetroProvider.getTaskFacade().pushAll(taskDtos);
    }

    @Override
    public void fillInnerFromDto(Task target, TaskDto source) {

        target.setTaskContext(source.getTaskContext());
        target.setCaze(DatabaseHelper.getCaseDao().getByReferenceDto(source.getCaze()));
        target.setContact(DatabaseHelper.getContactDao().getByReferenceDto(source.getContact()));
        target.setEvent(DatabaseHelper.getEventDao().getByReferenceDto(source.getEvent()));

        target.setTaskType(source.getTaskType());
        target.setTaskStatus(source.getTaskStatus());
        target.setDueDate(source.getDueDate());
        target.setPriority(source.getPriority());
        target.setSuggestedStart(source.getSuggestedStart());
        target.setStatusChangeDate(source.getStatusChangeDate());
        target.setPerceivedStart(source.getPerceivedStart());

        target.setCreatorUser(DatabaseHelper.getUserDao().getByReferenceDto(source.getCreatorUser()));
        target.setCreatorComment(source.getCreatorComment());
        target.setAssigneeUser(DatabaseHelper.getUserDao().getByReferenceDto(source.getAssigneeUser()));
        target.setAssigneeReply(source.getAssigneeReply());
    }

    @Override
    public void fillInnerFromAdo(TaskDto dto, Task ado) {

        dto.setTaskContext(ado.getTaskContext());
        if (ado.getCaze() != null) {
            Case caze = DatabaseHelper.getCaseDao().queryForId(ado.getCaze().getId());
            dto.setCaze(CaseDtoHelper.toReferenceDto(caze));
        } else {
            dto.setCaze(null);
        }
        if (ado.getContact() != null) {
            Contact contact = DatabaseHelper.getContactDao().queryForId(ado.getContact().getId());
            dto.setContact(ContactDtoHelper.toReferenceDto(contact));
        } else {
            dto.setContact(null);
        }
        if (ado.getEvent() != null) {
            Event event = DatabaseHelper.getEventDao().queryForId(ado.getEvent().getId());
            dto.setEvent(EventDtoHelper.toReferenceDto(event));
        } else {
            dto.setEvent(null);
        }
        dto.setTaskType(ado.getTaskType());
        dto.setTaskStatus(ado.getTaskStatus());
        dto.setDueDate(ado.getDueDate());
        dto.setPriority(ado.getPriority());
        dto.setSuggestedStart(ado.getSuggestedStart());
        dto.setStatusChangeDate(ado.getStatusChangeDate());
        dto.setPerceivedStart(ado.getPerceivedStart());

        if (ado.getCreatorUser() != null) {
            User user = DatabaseHelper.getUserDao().queryForId(ado.getCreatorUser().getId());
            dto.setCreatorUser(UserDtoHelper.toReferenceDto(user));
        } else {
            dto.setCreatorUser(null);
        }
        dto.setCreatorComment(ado.getCreatorComment());
        if (ado.getAssigneeUser() != null) {
            User user = DatabaseHelper.getUserDao().queryForId(ado.getAssigneeUser().getId());
            dto.setAssigneeUser(UserDtoHelper.toReferenceDto(user));
        } else {
            dto.setAssigneeUser(null);
        }
        dto.setAssigneeReply(ado.getAssigneeReply());
    }
}
