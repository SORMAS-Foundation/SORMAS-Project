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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.samples;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class PathogenTestListComponent extends VerticalLayout {

	private PathogenTestList list;
	private Button createButton;

	public PathogenTestListComponent(SampleReferenceDto sampleRef, BiConsumer<PathogenTestDto, Runnable> onSavedPathogenTest,
			Supplier<Boolean> createOrEditAllowedCallback) {
		setWidth(100, Unit.PERCENTAGE);

		HorizontalLayout componentHeader = new HorizontalLayout();
		componentHeader.setWidth(100, Unit.PERCENTAGE);
		addComponent(componentHeader);

		list = new PathogenTestList(sampleRef, onSavedPathogenTest, createOrEditAllowedCallback);
		addComponent(list);
		list.reload();

		Label testsHeader = new Label(I18nProperties.getString(Strings.headingTests));
		testsHeader.addStyleName(CssStyles.H3);
		componentHeader.addComponent(testsHeader);

		if (UserProvider.getCurrent().hasUserRight(UserRight.PATHOGEN_TEST_CREATE)) {
			createButton = new Button(I18nProperties.getCaption(Captions.pathogenTestNewTest));
			createButton.setId("pathogenTestNewTest");
			createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			createButton.setIcon(VaadinIcons.PLUS_CIRCLE);
			createButton.addClickListener(e -> {
				if (createOrEditAllowedCallback.get()) {
					ControllerProvider.getPathogenTestController().create(sampleRef, 0, list::reload, onSavedPathogenTest);
				} else {
					Notification.show(null, I18nProperties.getString(Strings.messageFormHasErrorsPathogenTest), Type.ERROR_MESSAGE);
				}
			});
			componentHeader.addComponent(createButton);
			componentHeader.setComponentAlignment(createButton, Alignment.MIDDLE_RIGHT);
		}
	}

	public void reload() {
		list.reload();
	}

}
