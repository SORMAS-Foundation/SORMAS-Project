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
package de.symeda.sormas.api.action;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;
import javax.validation.Valid;

import de.symeda.sormas.api.event.EventActionExportDto;
import de.symeda.sormas.api.event.EventActionIndexDto;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface ActionFacade {

	ActionDto saveAction(@Valid ActionDto dto);

	ActionDto getByUuid(String uuid);

	void deleteAction(ActionDto ActionDto);

	List<ActionDto> getAllActionsAfter(Date date);

	List<ActionDto> getByUuids(List<String> uuids);

	List<String> getAllUuids();

	List<ActionStatEntry> getActionStats(ActionCriteria actionCriteria);

	List<ActionDto> getActionList(ActionCriteria criteria, Integer first, Integer max);

	List<EventActionIndexDto> getEventActionList(EventCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties);

	List<EventActionExportDto> getEventActionExportList(EventCriteria criteria, Integer first, Integer max);

	long countEventAction(EventCriteria criteria);
}
