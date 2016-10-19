package de.symeda.sormas.api.task;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

@Remote
public interface TaskFacade {

    TaskDto saveTask(TaskDto dto);

	List<TaskDto> getAllAfter(Date date, String userUuid);

	long getTaskCount(String userUuid);

}
