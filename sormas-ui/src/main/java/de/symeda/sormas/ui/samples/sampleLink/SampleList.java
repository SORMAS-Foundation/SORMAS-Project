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
package de.symeda.sormas.ui.samples.sampleLink;

import java.util.List;

import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sample.SampleAssociationType;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.sample.SampleListEntryDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.PaginationList;

@SuppressWarnings("serial")
public class SampleList extends PaginationList<SampleListEntryDto> {

	private static final int MAX_DISPLAYED_ENTRIES = 5;

	private final SampleCriteria sampleCriteria;
	private final Label noSamplesLabel;
	private final boolean isEditAllowed;

	public SampleList(SampleCriteria sampleCriteria, boolean isEditAllowed) {
		super(MAX_DISPLAYED_ENTRIES);
		this.sampleCriteria = sampleCriteria;
		noSamplesLabel = new Label(buildNoSamplesCaption(sampleCriteria.getSampleAssociationType()));
		this.isEditAllowed = isEditAllowed;
	}

	@Override
	public void reload() {
		List<SampleListEntryDto> samples = FacadeProvider.getSampleFacade().getEntriesList(sampleCriteria, 0, maxDisplayedEntries * 20);

		setEntries(samples);
		if (!samples.isEmpty()) {
			showPage(1);
		} else {
			listLayout.removeAllComponents();
			updatePaginationLayout();
			listLayout.addComponent(noSamplesLabel);
		}
	}

	@Override
	protected void drawDisplayedEntries() {
		for (SampleListEntryDto sample : getDisplayedEntries()) {
			SampleListEntry listEntry = new SampleListEntry(sample);

			String sampleUuid = sample.getUuid();
			if (UiUtil.permitted(isEditAllowed, UserRight.SAMPLE_EDIT)) {
				listEntry.addEditButton(
					"edit-sample-" + sampleUuid,
					(ClickListener) event -> ControllerProvider.getSampleController().navigateToData(sampleUuid));
			} else {
				listEntry.addViewButton(
					"view-sample" + sampleUuid,
					(ClickListener) event -> ControllerProvider.getSampleController().navigateToData(sampleUuid));
			}

			listEntry.setEnabled(isEditAllowed);

			if (UserProvider.getCurrent().getUserRights().contains(UserRight.EXTERNAL_MESSAGE_VIEW)) {
				addViewLabMessageButton(listEntry);
			}
			listLayout.addComponent(listEntry);
		}
	}

	private void addViewLabMessageButton(SampleListEntry listEntry) {
		if (UiUtil.permitted(UserRight.EXTERNAL_MESSAGE_VIEW)) {
			List<ExternalMessageDto> labMessages =
				FacadeProvider.getExternalMessageFacade().getForSample(listEntry.getSampleListEntryDto().toReference());
			if (!labMessages.isEmpty()) {
				listEntry.addAssociatedLabMessagesListener(
					clickEvent -> ControllerProvider.getExternalMessageController().showLabMessagesSlider(labMessages));
			}
		}
	}

	private String buildNoSamplesCaption(SampleAssociationType sampleAssociationType) {
		String caption;
		switch (sampleAssociationType) {
		case PERSON:
			caption = Captions.sampleNoSamplesForPerson;
			break;
		case CASE:
			caption = Captions.sampleNoSamplesForCase;
			break;
		case CONTACT:
			caption = Captions.sampleNoSamplesForContact;
			break;
		case EVENT_PARTICIPANT:
			caption = Captions.sampleNoSamplesForEventParticipant;
			break;
		default:
			caption = "";
		}
		return I18nProperties.getCaption(caption);
	}
}
