/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.configuration.disease;

import com.vaadin.ui.Notification;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.disease.DiseaseConfigurationDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class DiseaseConfigurationController {

	public void editDiseaseConfiguration(String uuid) {
		DiseaseConfigurationDto diseaseConfigurationDto = FacadeProvider.getDiseaseConfigurationFacade().getByUuid(uuid);
		DiseaseConfigurationEditForm editForm = new DiseaseConfigurationEditForm();
		editForm.setValue(diseaseConfigurationDto);

		final CommitDiscardWrapperComponent<DiseaseConfigurationEditForm> cdw =
			new CommitDiscardWrapperComponent<>(editForm, editForm.getFieldGroup());
		cdw.addCommitListener(() -> {
			FacadeProvider.getDiseaseConfigurationFacade().saveDiseaseConfiguration(editForm.getValue());
			Notification.show(I18nProperties.getString(Strings.messageDiseaseConfigurationSaved), Notification.Type.ASSISTIVE_NOTIFICATION);
			SormasUI.get().getNavigator().navigateTo(DiseaseConfigurationView.VIEW_NAME);
		});

		VaadinUiUtil
			.showModalPopupWindow(cdw, I18nProperties.getString(Strings.edit) + " " + diseaseConfigurationDto.getDisease() + " Configuration");
	}
}
