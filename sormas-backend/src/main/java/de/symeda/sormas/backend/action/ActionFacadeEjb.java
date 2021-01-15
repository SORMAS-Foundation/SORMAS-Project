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
package de.symeda.sormas.backend.action;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import de.symeda.sormas.api.action.ActionCriteria;
import de.symeda.sormas.api.action.ActionDto;
import de.symeda.sormas.api.action.ActionFacade;
import de.symeda.sormas.api.action.ActionStatEntry;
import de.symeda.sormas.api.event.EventActionExportDto;
import de.symeda.sormas.api.event.EventActionIndexDto;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.event.EventFacadeEjb;
import de.symeda.sormas.backend.event.EventService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "ActionFacade")
public class ActionFacadeEjb implements ActionFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private ActionService actionService;
	@EJB
	private UserService userService;
	@EJB
	private EventService eventService;

	public Action fromDto(ActionDto source) {

		if (source == null) {
			return null;
		}

		boolean creation = false;
		Action target = actionService.getByUuid(source.getUuid());
		if (target == null) {
			creation = true;
			target = new Action();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}
		DtoHelper.validateDto(source, target);

		target.setLastModifiedBy(userService.getByReferenceDto(source.getLastModifiedBy()));
		target.setReply(source.getReply());
		target.setCreatorUser(userService.getByReferenceDto(source.getCreatorUser()));
		target.setTitle(source.getTitle());
		target.setDescription(source.getDescription());
		target.setPriority(source.getPriority());
		target.setDate(source.getDate());
		if (target.getActionStatus() != source.getActionStatus() && !creation) {
			target.setStatusChangeDate(new Date());
		} else {
			target.setStatusChangeDate(source.getStatusChangeDate());
		}
		target.setActionStatus(source.getActionStatus());
		target.setActionMeasure(source.getActionMeasure());

		target.setActionContext(source.getActionContext());
		if (source.getActionContext() != null) {
			switch (source.getActionContext()) {
			case EVENT:
				target.setEvent(eventService.getByReferenceDto(source.getEvent()));
				break;
			default:
				throw new UnsupportedOperationException(source.getActionContext() + " is not implemented");
			}
		} else {
			target.setEvent(null);
		}

		return target;
	}

	public ActionDto toDto(Action action) {

		if (action == null) {
			return null;
		}

		ActionDto target = new ActionDto();
		Action source = action;

		target.setCreationDate(source.getCreationDate());
		target.setChangeDate(source.getChangeDate());
		target.setUuid(source.getUuid());

		target.setCreatorUser(UserFacadeEjb.toReferenceDto(source.getCreatorUser()));
		target.setTitle(source.getTitle());
		target.setDescription(source.getDescription());
		target.setReply(source.getReply());
		target.setLastModifiedBy(UserFacadeEjb.toReferenceDto(source.getLastModifiedBy()));
		target.setPriority(source.getPriority());
		target.setDate(source.getDate());
		target.setStatusChangeDate(source.getStatusChangeDate());
		target.setActionContext(source.getActionContext());
		target.setActionStatus(source.getActionStatus());
		target.setEvent(EventFacadeEjb.toReferenceDto(source.getEvent()));
		target.setActionMeasure(source.getActionMeasure());

		return target;
	}

	@Override
	public ActionDto saveAction(ActionDto dto) {

		Action ado = fromDto(dto);
		actionService.ensurePersisted(ado);
		return toDto(ado);
	}

	@Override
	public ActionDto getByUuid(String uuid) {
		return toDto(actionService.getByUuid(uuid));
	}

	@Override
	public void deleteAction(ActionDto actionDto) {

		if (!userService.hasRight(UserRight.ACTION_DELETE)) {
			throw new UnsupportedOperationException("User " + userService.getCurrentUser().getUuid() + " is not allowed to delete action.");
		}

		Action action = actionService.getByUuid(actionDto.getUuid());
		actionService.delete(action);
	}

	@Override
	public List<String> getAllUuids() {

		User user = userService.getCurrentUser();
		if (user == null) {
			return Collections.emptyList();
		}

		return actionService.getAllUuids(user);
	}

	@Override
	public List<ActionDto> getAllActionsAfter(Date date) {

		User user = userService.getCurrentUser();
		if (user == null) {
			return Collections.emptyList();
		}

		return actionService.getAllActionsAfter(date, user).stream().map(c -> toDto(c)).collect(Collectors.toList());
	}

	@Override
	public List<ActionDto> getByUuids(List<String> uuids) {
		return actionService.getByUuids(uuids).stream().map(c -> toDto(c)).collect(Collectors.toList());
	}

	@Override
	public List<ActionDto> getActionList(ActionCriteria actionCriteria, Integer first, Integer max) {
		return actionService.getActionList(actionCriteria, first, max).stream().map(c -> toDto(c)).collect(Collectors.toList());
	}

	@Override
	public List<ActionStatEntry> getActionStats(ActionCriteria actionCriteria) {
		return actionService.getActionStats(actionCriteria);
	}

	@Override
	public List<EventActionIndexDto> getEventActionList(EventCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {
		return actionService.getEventActionIndexList(criteria, first, max, sortProperties);
	}

	@Override
	public List<EventActionExportDto> getEventActionExportList(EventCriteria criteria, Integer first, Integer max) {
		return actionService.getEventActionExportList(criteria, first, max);
	}

	@Override
	public long countEventAction(EventCriteria criteria) {
		return actionService.countEventActions(criteria);
	}

	@LocalBean
	@Stateless
	public static class ActionFacadeEjbLocal extends ActionFacadeEjb {

	}
}
