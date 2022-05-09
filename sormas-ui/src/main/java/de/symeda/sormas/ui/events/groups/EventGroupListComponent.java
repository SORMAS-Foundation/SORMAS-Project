/*
 * ******************************************************************************
 * * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * *
 * * This program is free software: you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation, either version 3 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program. If not, see <https://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.symeda.sormas.ui.events.groups;

import java.util.function.Consumer;

import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponent;

public class EventGroupListComponent extends SideComponent {

	public EventGroupListComponent(EventReferenceDto eventReference, Consumer<Runnable> actionCallback) {

		super(I18nProperties.getCaption(Captions.eventGroups), actionCallback);

		addCreateButton(
			I18nProperties.getCaption(Captions.linkEventGroup),
			() -> ControllerProvider.getEventGroupController().create(eventReference),
			UserRight.EVENTGROUP_CREATE,
			UserRight.EVENTGROUP_LINK);

		EventGroupList eventList = new EventGroupList(eventReference);
		addComponent(eventList);
		eventList.reload();
	}
}
