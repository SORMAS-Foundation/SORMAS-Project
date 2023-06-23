/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.action;

import static de.symeda.sormas.api.action.ActionContext.EVENT;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;

import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.action.ActionCriteria;
import de.symeda.sormas.api.action.ActionDto;
import de.symeda.sormas.api.action.ActionFacade;
import de.symeda.sormas.api.action.ActionStatEntry;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.event.EventActionExportDto;
import de.symeda.sormas.api.event.EventActionIndexDto;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.AccessDeniedException;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.FacadeHelper;
import de.symeda.sormas.backend.event.EventFacadeEjb;
import de.symeda.sormas.backend.event.EventService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "ActionFacade")
@RightsAllowed(UserRight._EVENT_VIEW)
public class ActionFacadeEjb implements ActionFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private ActionService actionService;
	@EJB
	private UserService userService;
	@EJB
	private EventService eventService;

	public Action fillOrBuildEntity(ActionDto source, Action target, boolean checkChangeDate) {
		if (source == null) {
			return null;
		}

		target = DtoHelper.fillOrBuildEntity(source, target, Action::new, checkChangeDate);

		target.setLastModifiedBy(userService.getByReferenceDto(source.getLastModifiedBy()));
		target.setReply(source.getReply());
		target.setCreatorUser(userService.getByReferenceDto(source.getCreatorUser()));
		target.setTitle(source.getTitle());
		target.setDescription(source.getDescription());
		target.setPriority(source.getPriority());
		target.setDate(source.getDate());
		if (target.getActionStatus() != source.getActionStatus() && target.getId() != null) {
			target.setStatusChangeDate(new Date());
		} else {
			target.setStatusChangeDate(source.getStatusChangeDate());
		}
		target.setActionStatus(source.getActionStatus());
		target.setActionMeasure(source.getActionMeasure());

		target.setActionContext(source.getActionContext());
		if (source.getActionContext() != null) {
			if (source.getActionContext() == EVENT) {
				target.setEvent(eventService.getByReferenceDto(source.getEvent()));
			} else {
				throw new UnsupportedOperationException(source.getActionContext() + " is not implemented");
			}
		} else {
			target.setEvent(null);
		}

		return target;
	}

	public ActionDto toDto(Action source) {

		if (source == null) {
			return null;
		}

		ActionDto target = new ActionDto();

		DtoHelper.fillDto(target, source);

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
	@RightsAllowed({
		UserRight._ACTION_CREATE,
		UserRight._ACTION_EDIT })
	public ActionDto saveAction(@Valid ActionDto dto) {

		Action existingAction = actionService.getByUuid(dto.getUuid());
		FacadeHelper.checkCreateAndEditRights(existingAction, userService, UserRight.ACTION_CREATE, UserRight.ACTION_EDIT);
		Action ado = fillOrBuildEntity(dto, existingAction, true);
		actionService.ensurePersisted(ado);
		return toDto(ado);
	}

	@Override
	public ActionDto getByUuid(String uuid) {
		return toDto(actionService.getByUuid(uuid));
	}

	@Override
	@RightsAllowed(UserRight._ACTION_DELETE)
	public void deleteAction(ActionDto actionDto) {
		Action action = actionService.getByUuid(actionDto.getUuid());

		if (!actionService.inJurisdictionOrOwned(action)) {
			throw new AccessDeniedException(I18nProperties.getString(Strings.messageActionOutsideJurisdictionDeletionDenied));
		}

		actionService.deletePermanent(action);
	}

	@Override
	public List<String> getAllActiveUuids() {

		User user = userService.getCurrentUser();
		if (user == null) {
			return Collections.emptyList();
		}

		return actionService.getAllActiveUuids();
	}

	@Override
	public List<ActionDto> getAllActiveActionsAfter(Date date) {

		return actionService.getAllAfter(date, null, null).stream().map(this::toDto).collect(Collectors.toList());
	}

	@Override
	public List<ActionDto> getByUuids(List<String> uuids) {
		return actionService.getByUuids(uuids).stream().map(this::toDto).collect(Collectors.toList());
	}

	@Override
	public List<ActionDto> getActionList(ActionCriteria actionCriteria, Integer first, Integer max) {
		return actionService.getActionList(actionCriteria, first, max).stream().map(this::toDto).collect(Collectors.toList());
	}

	@Override
	public List<ActionDto> getActionList(ActionCriteria actionCriteria, Integer first, Integer max, List<SortProperty> sortProperties) {
		return actionService.getActionList(actionCriteria, first, max, sortProperties).stream().map(this::toDto).collect(Collectors.toList());
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
	public Page<EventActionIndexDto> getEventActionIndexPage(
		EventCriteria criteria,
		Integer offset,
		Integer size,
		List<SortProperty> sortProperties) {
		List<EventActionIndexDto> eventActionIndexList = getEventActionList(criteria, offset, size, sortProperties);
		long totalElementCount = countEventActions(criteria);
		return new Page<>(eventActionIndexList, offset, size, totalElementCount);

	}

	public Page<ActionDto> getActionPage(ActionCriteria criteria, Integer offset, Integer size, List<SortProperty> sortProperties) {
		List<ActionDto> actionList = getActionList(criteria, offset, size, sortProperties);
		long totalElementCount = countActions(criteria);
		return new Page<>(actionList, offset, size, totalElementCount);
	}

	@Override
	public List<EventActionExportDto> getEventActionExportList(EventCriteria criteria, Integer first, Integer max) {
		return actionService.getEventActionExportList(criteria, first, max);
	}

	@Override
	public long countEventActions(EventCriteria criteria) {
		return actionService.countEventActions(criteria);
	}

	@Override
	public long countActions(ActionCriteria criteria) {
		return actionService.countActions(criteria);
	}

	@Override
	public boolean isInJurisdiction(String uuid) {
		return actionService.inJurisdictionOrOwned(actionService.getByUuid(uuid));
	}

	@Override
	public EditPermissionType getEditPermissionType(String uuid) {
		if (!isInJurisdiction(uuid)) {
			return EditPermissionType.OUTSIDE_JURISDICTION;
		}

		return EditPermissionType.ALLOWED;
	}

	@LocalBean
	@Stateless
	public static class ActionFacadeEjbLocal extends ActionFacadeEjb {

	}
}
