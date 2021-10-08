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

package de.symeda.sormas.ui.contact;

import java.util.List;

import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.utils.V7UuidRenderer;

public class ContactCaseConversionSelectionGrid extends ContactSelectionGrid {

	public ContactCaseConversionSelectionGrid(List<SimilarContactDto> contacts) {
		super(contacts);
	}

	@Override
	protected void setColumns() {
		setColumns(
			SimilarContactDto.UUID,
			SimilarContactDto.CAZE,
			SimilarContactDto.CASE_ID_EXTERNAL_SYSTEM,
			SimilarContactDto.LAST_CONTACT_DATE,
			SimilarContactDto.CONTACT_PROXIMITY,
			SimilarContactDto.CONTACT_CLASSIFICATION,
			SimilarContactDto.CONTACT_STATUS,
			SimilarContactDto.FOLLOW_UP_STATUS);

		for (Column column : getColumns()) {
			column.setHeaderCaption(
				I18nProperties.findPrefixCaption(
					column.getPropertyId().toString(),
					SimilarContactDto.I18N_PREFIX,
					ContactIndexDto.I18N_PREFIX,
					ContactDto.I18N_PREFIX));
		}

		getColumn(SimilarContactDto.UUID).setRenderer(new V7UuidRenderer());
	}
}
