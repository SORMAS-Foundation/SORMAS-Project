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
package de.symeda.sormas.ui.samples;

import java.util.List;

import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.sample.SampleIndexDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.PaginationList;

@SuppressWarnings("serial")
public class SampleList extends PaginationList<SampleIndexDto> {

	private final SampleCriteria sampleCriteria = new SampleCriteria();

	public SampleList(ContactReferenceDto contactRef) {
		super(5);
		sampleCriteria.contact(contactRef);
	}

	public SampleList(CaseReferenceDto caseRef) {
		super(5);
		sampleCriteria.caze(caseRef);
	}

	public SampleList(EventParticipantReferenceDto eventParticipantRef) {
		super(5);
		sampleCriteria.eventParticipant(eventParticipantRef);
	}

	@Override
	public void reload() {
		List<SampleIndexDto> samples = FacadeProvider.getSampleFacade().getIndexList(sampleCriteria, 0, maxDisplayedEntries * 20, null);

		setEntries(samples);
		if (!samples.isEmpty()) {
			showPage(1);
		} else {
			updatePaginationLayout();
			Label noSamplesLabel = new Label(
				I18nProperties.getCaption(
					sampleCriteria.getCaze() != null
						? Captions.sampleNoSamplesForCase
						: sampleCriteria.getContact() != null ? Captions.sampleNoSamplesForContact : Captions.sampleNoSamplesForContact));
			listLayout.addComponent(noSamplesLabel);
		}
	}

	@Override
	protected void drawDisplayedEntries() {
		List<SampleIndexDto> displayedEntries = getDisplayedEntries();
		for (int i = 0, displayedEntriesSize = displayedEntries.size(); i < displayedEntriesSize; i++) {
			SampleIndexDto sample = displayedEntries.get(i);
			SampleListEntry listEntry = new SampleListEntry(sample);
			if (UserProvider.getCurrent().hasUserRight(UserRight.SAMPLE_EDIT)) {
				listEntry.addEditListener(
					i,
					(ClickListener) event -> ControllerProvider.getSampleController().navigateToData(listEntry.getSample().getUuid()));
			}
			listLayout.addComponent(listEntry);
		}
	}
}
