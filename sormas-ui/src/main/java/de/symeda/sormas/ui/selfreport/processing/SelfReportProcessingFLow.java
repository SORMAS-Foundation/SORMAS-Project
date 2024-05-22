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

package de.symeda.sormas.ui.selfreport.processing;

import static de.symeda.sormas.ui.utils.processing.ProcessingUiHelper.showPickOrCreateEntryWindow;
import static de.symeda.sormas.ui.utils.processing.ProcessingUiHelper.showPickOrCreatePersonWindow;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.selfreport.processing.AbstractSelfReportProcessingFlow;
import de.symeda.sormas.api.selfreport.processing.SelfReportProcessingFacade;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.dataprocessing.EntitySelection;
import de.symeda.sormas.api.utils.dataprocessing.HandlerCallback;
import de.symeda.sormas.api.utils.dataprocessing.PickOrCreateEntryResult;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.processing.EntrySelectionField;

public class SelfReportProcessingFLow extends AbstractSelfReportProcessingFlow {

	public SelfReportProcessingFLow(SelfReportProcessingFacade selfReportProcessingFacade) {
		super(selfReportProcessingFacade, UiUtil.getUser());
	}

	@Override
	protected void handlePickOrCreatePerson(PersonDto person, HandlerCallback<EntitySelection<PersonDto>> callback) {
		showPickOrCreatePersonWindow(person, callback);
	}

	@Override
	protected void handlePickOrCreateCase(List<CaseSelectionDto> similarCases, HandlerCallback<PickOrCreateEntryResult> callback) {
		if (CollectionUtils.isNotEmpty(similarCases)) {
			EntrySelectionField.Options.Builder optionsBuilder = new EntrySelectionField.Options.Builder().addSelectCase(similarCases)
				.addCreateEntry(EntrySelectionField.OptionType.CREATE_CASE, FeatureType.CASE_SURVEILANCE, UserRight.CASE_CREATE, UserRight.CASE_EDIT);

			showPickOrCreateEntryWindow(optionsBuilder.build(), null, callback);
		} else {
			PickOrCreateEntryResult result = new PickOrCreateEntryResult();
			result.setNewCase(true);
			callback.done(result);
		}
	}

	@Override
	protected void handlePickOrCreateContact(List<SimilarContactDto> similarContacts, HandlerCallback<PickOrCreateEntryResult> callback) {

	}

	@Override
	protected void handleCreateContact(ContactDto contact, HandlerCallback<EntitySelection<ContactDto>> callback) {

	}

	@Override
	protected void handleCreateCase(CaseDataDto caze, HandlerCallback<EntitySelection<CaseDataDto>> callback) {

	}
}
