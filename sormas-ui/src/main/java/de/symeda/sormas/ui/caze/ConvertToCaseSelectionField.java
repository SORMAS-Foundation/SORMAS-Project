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

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.Grid;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.event.SimilarEventParticipantDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.contact.ContactCaseConversionSelectionGrid;
import de.symeda.sormas.ui.events.EventParticipantCaseConversionSelectionGrid;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class ConvertToCaseSelectionField extends VerticalLayout {

	private ContactCaseConversionSelectionGrid contactSelectionGrid;
	private EventParticipantCaseConversionSelectionGrid eventParticipantSelectionGrid;

	private CaseDataDto caseDataDto;
	private List<SimilarContactDto> matchingContacts;
	private List<SimilarEventParticipantDto> matchingEventParticipants;

	public ConvertToCaseSelectionField(
		CaseDataDto caseDataDto,
		List<SimilarContactDto> matchingContacts,
		List<SimilarEventParticipantDto> matchingEventParticipants) {

		this.caseDataDto = caseDataDto;
		this.matchingContacts = matchingContacts;
		this.matchingEventParticipants = matchingEventParticipants;

		setMargin(true);
		setSpacing(true);

		addComponent(VaadinUiUtil.createInfoComponent(I18nProperties.getString(Strings.infoConvertToCaseSelect)));
		addContactDetailsComponent();

		if (matchingContacts.size() > 0) {
			contactSelectionGrid = new ContactCaseConversionSelectionGrid(matchingContacts);
			contactSelectionGrid.setSelectionMode(Grid.SelectionMode.MULTI);
			contactSelectionGrid.setCaption(I18nProperties.getString(Strings.entityContacts));
			addComponent(contactSelectionGrid);
		}

		if (matchingEventParticipants.size() > 0) {
			eventParticipantSelectionGrid = new EventParticipantCaseConversionSelectionGrid(matchingEventParticipants);
			eventParticipantSelectionGrid.setSelectionMode(Grid.SelectionMode.MULTI);
			eventParticipantSelectionGrid.setCaption(I18nProperties.getString(Strings.entityEventParticipants));
			addComponent(eventParticipantSelectionGrid);
		}
	}

	private void addContactDetailsComponent() {

		HorizontalLayout contactDetailsLayout = new HorizontalLayout();
		contactDetailsLayout.setSpacing(true);

		final Label lblUuid = new Label(DataHelper.getShortUuid(caseDataDto));
		lblUuid.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.UUID));
		lblUuid.setWidthUndefined();
		contactDetailsLayout.addComponent(lblUuid);

		final Label lblFirstName = new Label(caseDataDto.getPerson().getFirstName());
		lblFirstName.setWidthUndefined();
		lblFirstName.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.FIRST_NAME));
		contactDetailsLayout.addComponent(lblFirstName);

		final Label lblLastName = new Label(caseDataDto.getPerson().getLastName());
		lblLastName.setWidthUndefined();
		lblLastName.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.LAST_NAME));
		contactDetailsLayout.addComponent(lblLastName);

		if (UiUtil.disabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
			final Label lblRegion = new Label(caseDataDto.getResponsibleRegion().buildCaption());
			lblRegion.setWidthUndefined();
			lblRegion.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.RESPONSIBLE_REGION));
			contactDetailsLayout.addComponent(lblRegion);

			final Label lblDistrict = new Label(caseDataDto.getResponsibleDistrict().buildCaption());
			lblDistrict.setWidthUndefined();
			lblDistrict.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.RESPONSIBLE_DISTRICT));
			contactDetailsLayout.addComponent(lblDistrict);
		}

		addComponent(new Panel(contactDetailsLayout));
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
}
