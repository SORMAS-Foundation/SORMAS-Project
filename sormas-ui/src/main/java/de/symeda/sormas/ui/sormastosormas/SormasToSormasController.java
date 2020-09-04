/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.sormastosormas;

import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOptionsDto;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class SormasToSormasController {

	public SormasToSormasController() {
	}

	public void shareCaseToSormas(CaseReferenceDto caze, SormasToSormasListComponent listComponent) {
		shareToSormas((options) -> FacadeProvider.getSormasToSormasFacade().shareCase(caze.getUuid(), options), listComponent, true);
	}

	public void shareContactToSormas(ContactReferenceDto contact, SormasToSormasListComponent listComponent) {
		shareToSormas((options) -> FacadeProvider.getSormasToSormasFacade().shareContact(contact.getUuid(), options), listComponent, false);
	}

	private void shareToSormas(HandleShareWithOptions handleShareWithOptions, SormasToSormasListComponent listComponent, boolean isForCase) {
		SormasToSormasOptionsForm optionsForm = new SormasToSormasOptionsForm(isForCase);
		optionsForm.setValue(new SormasToSormasOptionsDto());

		CommitDiscardWrapperComponent<SormasToSormasOptionsForm> optionsCommitDiscard =
			new CommitDiscardWrapperComponent<>(optionsForm, optionsForm.getFieldGroup());
		optionsCommitDiscard.getCommitButton().setCaption(I18nProperties.getCaption(Captions.sormasToSormasShare));
		optionsCommitDiscard.setWidth(100, Sizeable.Unit.PERCENTAGE);

		Window optionsPopup = VaadinUiUtil.showPopupWindow(optionsCommitDiscard, I18nProperties.getCaption(Captions.sormasToSormasDialogTitle));

		optionsCommitDiscard.addCommitListener(() -> {
			SormasToSormasOptionsDto options = optionsForm.getValue();

			try {
				handleShareWithOptions.handle(options);

				optionsPopup.close();

				if (options.isHandOverOwnership()) {
					SormasUI.refreshView();
				} else {
					listComponent.reloadList();
				}
			} catch (SormasToSormasException ex) {
				Label messageLabel = new Label(ex.getMessage(), ContentMode.HTML);
				messageLabel.setWidth(100, Sizeable.Unit.PERCENTAGE);
				VaadinUiUtil.showPopupWindow(new VerticalLayout(messageLabel), I18nProperties.getCaption(Captions.sormasToSormasErrorDialogTitle));
			}
		});

		optionsCommitDiscard.addDiscardListener(() -> {
			optionsPopup.close();
		});
	}

	private interface HandleShareWithOptions {

		void handle(SormasToSormasOptionsDto options) throws SormasToSormasException;
	}
}
