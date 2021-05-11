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

package de.symeda.sormas.ui.caze;

import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.Grid;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.event.SimilarEventParticipantDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.contact.ContactSelectionGrid;
import de.symeda.sormas.ui.events.EventParticipantSelectionGrid;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;

public class ConvertToCaseSelectionField extends VerticalLayout {

	private ContactSelectionGrid contactSelectionGrid;
	private EventParticipantSelectionGrid eventParticipantSelectionGrid;

	private List<SimilarContactDto> matchingContacts;
	private List<SimilarEventParticipantDto> matchingEventParticipants;

	private CommitDiscardWrapperComponent<?> wrapperComponent;
	private Button convertSomeButton;

	public ConvertToCaseSelectionField(
		CaseDataDto caseDataDto,
		List<SimilarContactDto> matchingContacts,
		List<SimilarEventParticipantDto> matchingEventParticipants) {

		this.matchingContacts = matchingContacts;
		this.matchingEventParticipants = matchingEventParticipants;

		if (matchingContacts.size() > 0) {
			contactSelectionGrid = new ContactSelectionGrid(matchingContacts);
			contactSelectionGrid.setSelectionMode(Grid.SelectionMode.MULTI);
			contactSelectionGrid.setCaption(I18nProperties.getString(Strings.entityContacts));
			contactSelectionGrid.setVisible(false);
			addComponent(contactSelectionGrid);
		}

		if (matchingEventParticipants.size() > 0) {
			eventParticipantSelectionGrid = new EventParticipantSelectionGrid(matchingEventParticipants);
			eventParticipantSelectionGrid.setSelectionMode(Grid.SelectionMode.MULTI);
			eventParticipantSelectionGrid.setCaption(I18nProperties.getString(Strings.entityEventParticipants));
			eventParticipantSelectionGrid.setVisible(false);
			addComponent(eventParticipantSelectionGrid);
		}
	}

	public List<SimilarContactDto> getSelectedContacts() {
		return contactSelectionGrid != null
			? contactSelectionGrid.getSelectedRows().stream().map(item -> (SimilarContactDto) item).collect(Collectors.toList())
			: matchingContacts;
	}

	public List<SimilarEventParticipantDto> getSelectedEventParticipants() {
		return eventParticipantSelectionGrid != null
			? eventParticipantSelectionGrid.getSelectedRows().stream().map(item -> (SimilarEventParticipantDto) item).collect(Collectors.toList())
			: matchingEventParticipants;
	}

	public void setWrapperComponent(CommitDiscardWrapperComponent<?> wrapperComponent) {
		this.wrapperComponent = wrapperComponent;
		HorizontalLayout buttonsPanel = wrapperComponent.getButtonsPanel();
		int index = buttonsPanel.getComponentCount() - 1;

		getConvertSomeButton();
		buttonsPanel.addComponent(convertSomeButton, index);
		buttonsPanel.setComponentAlignment(convertSomeButton, Alignment.BOTTOM_RIGHT);
		buttonsPanel.setExpandRatio(convertSomeButton, 0);

		wrapperComponent.getCommitButton().setCaption(I18nProperties.getCaption(Captions.actionYesForAll));
		wrapperComponent.getDiscardButton().setCaption(I18nProperties.getCaption(Captions.actionNo));
	}

	public Button getConvertSomeButton() {
		if (convertSomeButton == null) {
			convertSomeButton =
				ButtonHelper.createButton("convertSome", I18nProperties.getCaption(Captions.actionYesForSome), new Button.ClickListener() {

					private static final long serialVersionUID = 1L;

					@Override
					public void buttonClick(Button.ClickEvent event) {
						toggleConvertSome();
					}
				}, ValoTheme.BUTTON_PRIMARY);
		}

		return convertSomeButton;
	}

	private void toggleConvertSome() {
		contactSelectionGrid.setVisible(true);
		eventParticipantSelectionGrid.setVisible(true);
		convertSomeButton.setVisible(false);

		wrapperComponent.getCommitButton().setCaption(I18nProperties.getCaption(Captions.actionConfirm));
		wrapperComponent.getDiscardButton().setCaption(I18nProperties.getCaption(Captions.actionCancel));
	}
}
