/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
package de.symeda.sormas.ui.externalmessage;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.ComboBoxWithPlaceholder;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class EditAssigneeComponentContainer {

	private final Button assignMeButton;
	private final ComboBoxWithPlaceholder assigneeComboBox;
	private final CommitDiscardWrapperComponent<VerticalLayout> wrapperComponent;
	private final Window window;

	public EditAssigneeComponentContainer() {

		VerticalLayout form = new VerticalLayout();
		form.setSpacing(false);

		assigneeComboBox = new ComboBoxWithPlaceholder();
		assigneeComboBox.setCaption(I18nProperties.getCaption(Captions.ExternalMessage_assignee));
		assigneeComboBox.addItems(
			FacadeProvider.getUserFacade().getUsersByRegionAndRights(UiUtil.getUser().getRegion(), null, UserRight.EXTERNAL_MESSAGE_PROCESS));
		assigneeComboBox.setNullSelectionAllowed(true);
		assigneeComboBox.setWidth(300, Sizeable.Unit.PIXELS);

		assignMeButton = ButtonHelper.createButton(Captions.assignToMe);
		CssStyles.style(assignMeButton, ValoTheme.BUTTON_LINK, CssStyles.BUTTON_COMPACT);

		form.addComponents(assigneeComboBox, assignMeButton);

		wrapperComponent = new CommitDiscardWrapperComponent<>(form);

		window = VaadinUiUtil.createPopupWindow();
		window.setCaption(I18nProperties.getString(Strings.headingEditAssignee));
		window.setContent(wrapperComponent);

		wrapperComponent.addDiscardListener(window::close);
	}

	public Button getAssignMeButton() {
		return assignMeButton;
	}

	public ComboBoxWithPlaceholder getAssigneeComboBox() {
		return assigneeComboBox;
	}

	public CommitDiscardWrapperComponent<VerticalLayout> getWrapperComponent() {
		return wrapperComponent;
	}

	public Window getWindow() {
		return window;
	}
}
