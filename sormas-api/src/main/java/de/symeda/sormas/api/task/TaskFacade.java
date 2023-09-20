/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.task;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Remote;
import javax.validation.Valid;

import de.symeda.sormas.api.ArchivableFacade;
import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.PermanentlyDeletableFacade;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface TaskFacade extends PermanentlyDeletableFacade, ArchivableFacade {

	TaskDto saveTask(@Valid TaskDto dto);

	List<TaskDto> getAllActiveTasksAfter(Date date);

	List<TaskDto> getAllByCase(CaseReferenceDto caseRef);

	List<TaskDto> getAllActiveTasksAfter(Date date, Integer batchSize, String lastSynchronizedUuid);

	Page<TaskIndexDto> getIndexPage(TaskCriteria taskCriteria, Integer offset, Integer size, List<SortProperty> sortProperties);

	List<TaskDto> getAllByContact(ContactReferenceDto contactRef);

	List<TaskDto> getAllByEvent(EventReferenceDto eventRef);

	List<TaskDto> getAllPendingByCase(CaseReferenceDto caseDataDto);

	List<TaskDto> getByUuids(List<String> uuids);

	long getPendingTaskCountByContact(ContactReferenceDto contactDto);

	long getPendingTaskCountByEvent(EventReferenceDto eventDto);

	Map<String, Long> getPendingTaskCountPerUser(List<String> userUuids);

	TaskDto getByUuid(String uuid);

	List<String> getAllActiveUuids();

	long count(TaskCriteria criteria);

	List<TaskIndexDto> getIndexList(TaskCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties);

	List<TaskExportDto> getExportList(TaskCriteria criteria, Collection<String> selectedRows, int first, int max);

	void sendNewAndDueTaskMessages();

	List<String> getObsoleteUuidsSince(Date since);

	EditPermissionType getEditPermissionType(String uuid);

	List<ProcessedEntity> saveBulkTasks(
		List<String> taskUuidList,
		TaskDto updatedTempTask,
		boolean priorityChange,
		boolean assigneeChange,
		boolean taskStatusChange);

	List<String> getArchivedUuidsSince(Date since);
}
