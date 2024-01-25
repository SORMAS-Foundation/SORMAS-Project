/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
 */

package de.symeda.sormas.ui.specialcaseaccess;

import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.specialcaseaccess.SpecialCaseAccessDto;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class SpecialCaseAccessController {

	public SpecialCaseAccessController() {
	}

	public void create(CaseReferenceDto caze, Runnable callback) {
		SpecialCaseAccessDto specialCaseAccess = SpecialCaseAccessDto.build(caze, UserProvider.getCurrent().getUserReference());

		openEditWindow(specialCaseAccess, Strings.headingCreateSpecailCaseAccess, true, callback);
	}

	public void edit(SpecialCaseAccessDto specialCaseAccess, Runnable callback) {
		openEditWindow(specialCaseAccess, Strings.headingEditSpecailCaseAccess, false, callback);
	}

	private static void openEditWindow(SpecialCaseAccessDto specialCaseAccess, String titleTag, boolean isCreate, Runnable callback) {
		SpecialCaseAccessForm editForm = new SpecialCaseAccessForm(isCreate);
		editForm.setValue(specialCaseAccess);

		CommitDiscardWrapperComponent<SpecialCaseAccessForm> editComponent = new CommitDiscardWrapperComponent<>(editForm, editForm.getFieldGroup());

		Window window = VaadinUiUtil.showModalPopupWindow(editComponent, I18nProperties.getString(titleTag));

		editComponent.addCommitListener(() -> {
			if (!editForm.getFieldGroup().isModified()) {
				SpecialCaseAccessDto dto = editForm.getValue();
				FacadeProvider.getSpecialCaseAccessFacade().save(dto);

				callback.run();
			}
		});

		if (!isCreate) {
			editComponent.addDeleteListener(() -> {
				FacadeProvider.getSpecialCaseAccessFacade().delete(specialCaseAccess.getUuid());
				window.close();
				callback.run();
			}, I18nProperties.getCaption(SpecialCaseAccessDto.I18N_PREFIX));
		}
	}
}
