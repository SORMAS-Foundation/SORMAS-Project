/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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
package de.symeda.sormas.ui.events.sevenoneseven;

import java.util.function.Consumer;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717TimelinessDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponent;

/**
 * Side component on the event data view summarizing the 7-1-7 timeliness of the event.
 */
@SuppressWarnings("serial")
public class Event717AssessmentSideComponent extends SideComponent {

	public Event717AssessmentSideComponent(EventReferenceDto eventRef, Consumer<Runnable> actionCallback) {
		super(I18nProperties.getCaption(Captions.eventEvent717Assessment), actionCallback);

		Event717TimelinessDto timeliness = FacadeProvider.getEvent717AssessmentFacade().getTimelinessByEventUuid(eventRef.getUuid());
		if (timeliness == null) {
			Label notAssessedLabel = new Label(I18nProperties.getString(Strings.infoEvent717NotAssessed));
			notAssessedLabel.addStyleName(CssStyles.VSPACE_3);
			addComponent(notAssessedLabel);
		} else {
			Event717TimelinessPanel timelinessPanel = new Event717TimelinessPanel(false);
			timelinessPanel.setValue(timeliness);
			timelinessPanel.addStyleName(CssStyles.VSPACE_3);
			addComponent(timelinessPanel);
		}

		Button openButton = ButtonHelper.createButton(
			Captions.eventOpenEvent717Assessment,
			e -> actionCallback
				.accept(() -> SormasUI.get().getNavigator().navigateTo(Event717AssessmentView.VIEW_NAME + "/" + eventRef.getUuid())),
			ValoTheme.BUTTON_PRIMARY);
		addComponent(openButton);
	}
}
