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
package de.symeda.sormas.ui.action;

import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.action.ActionContext;
import de.symeda.sormas.api.action.ActionDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

import javax.validation.constraints.NotNull;

public class ActionController {

	public ActionController() {
	}

	/**
	 * Show a create form for an action.
	 *
	 * @param context of the action
	 * @param entityRef of the action
	 * @param callback on save
	 */
	public void create(@NotNull SormasUI ui, ActionContext context, ReferenceDto entityRef, Runnable callback) {

		ActionEditForm createForm = new ActionEditForm(true);
		createForm.setValue(createNewAction(ui.getUserProvider().getUserReference(), context, entityRef));
		final CommitDiscardWrapperComponent<ActionEditForm> editView = new CommitDiscardWrapperComponent<>(
			createForm,
			ui.getUserProvider().hasUserRight(UserRight.ACTION_CREATE),
			createForm.getFieldGroup());

		editView.addCommitListener(() -> {
			if (!createForm.getFieldGroup().isModified()) {
				ActionDto dto = createForm.getValue();
				FacadeProvider.getActionFacade().saveAction(dto);
				callback.run();
			}
		});

		VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getString(Strings.headingCreateNewAction));
	}

	/**
	 * Show an edit form for an action.
	 *
	 * @param dto of the action
	 * @param callback on save
	 */
	public void edit(SormasUI ui, ActionDto dto, Runnable callback) {

		// get fresh data
		ActionDto newDto = FacadeProvider.getActionFacade().getByUuid(dto.getUuid());

		ActionEditForm form = new ActionEditForm(false);
		form.setValue(newDto);
		final CommitDiscardWrapperComponent<ActionEditForm> editView =
			new CommitDiscardWrapperComponent<>(form, ui.getUserProvider().hasUserRight(UserRight.ACTION_EDIT), form.getFieldGroup());

		Window popupWindow = VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getString(Strings.headingEditAction));

		editView.addCommitListener(() -> {
			if (!form.getFieldGroup().isModified()) {
				ActionDto dto1 = form.getValue();
				dto1.setLastModifiedBy(FacadeProvider.getUserFacade().getCurrentUser().toReference());
				FacadeProvider.getActionFacade().saveAction(dto1);
				popupWindow.close();
				callback.run();
			}
		});

		// Add delete button if user has role
		if (ui.getUserProvider().hasUserRole(UserRole.ADMIN)) {
			editView.addDeleteListener(() -> {
				FacadeProvider.getActionFacade().deleteAction(newDto);
				UI.getCurrent().removeWindow(popupWindow);
				callback.run();
			}, I18nProperties.getString(Strings.entityAction));
		}

		editView.addDiscardListener(popupWindow::close);
	}

	private ActionDto createNewAction(@NotNull UserReferenceDto userReferenceDto, ActionContext context, ReferenceDto entityRef) {
		ActionDto action = ActionDto.build(context, entityRef);
		action.setCreatorUser(userReferenceDto);
		return action;
	}
}
