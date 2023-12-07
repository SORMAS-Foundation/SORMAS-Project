/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.configuration.customizableenum;

import com.vaadin.ui.Notification;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.customizableenum.CustomizableEnumValueDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class CustomizableEnumController {

	public void createCustomizableEnumValue() {

		CustomizableEnumValueCreateForm createForm = new CustomizableEnumValueCreateForm();
		createForm.setValue(CustomizableEnumValueDto.build());

		final CommitDiscardWrapperComponent<CustomizableEnumValueCreateForm> cdw =
			new CommitDiscardWrapperComponent<>(createForm, createForm.getFieldGroup());
		cdw.addCommitListener(() -> {
			FacadeProvider.getCustomizableEnumFacade().save(createForm.getValue());
			Notification.show(I18nProperties.getString(Strings.messageEntryCreated), Notification.Type.ASSISTIVE_NOTIFICATION);
			SormasUI.get().getNavigator().navigateTo(CustomizableEnumValuesView.VIEW_NAME);
		});

		VaadinUiUtil.showModalPopupWindow(cdw, I18nProperties.getString(Strings.headingCreateEntry));
	}

	public void editCustomizableEnumValue(String uuid) {

		CustomizableEnumValueDto customizableEnumValue = FacadeProvider.getCustomizableEnumFacade().getByUuid(uuid);
		CustomizableEnumValueEditForm editForm = new CustomizableEnumValueEditForm();
		editForm.setValue(customizableEnumValue);

		final CommitDiscardWrapperComponent<CustomizableEnumValueEditForm> cdw =
			new CommitDiscardWrapperComponent<>(editForm, editForm.getFieldGroup());
		cdw.addCommitListener(() -> {
			FacadeProvider.getCustomizableEnumFacade().save(editForm.getValue());
			Notification.show(I18nProperties.getString(Strings.messageCustomizableEnumValueSaved), Notification.Type.ASSISTIVE_NOTIFICATION);
			SormasUI.get().getNavigator().navigateTo(CustomizableEnumValuesView.VIEW_NAME);
		});

		VaadinUiUtil.showModalPopupWindow(cdw, I18nProperties.getString(Strings.edit) + " " + customizableEnumValue.getCaption());
	}

}
