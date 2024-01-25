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

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.specialcaseaccess.SpecialCaseAccessDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.DateTimeField;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.PastDateValidator;

public class SpecialCaseAccessForm extends AbstractEditForm<SpecialCaseAccessDto> {

	private static final long serialVersionUID = -945936537515438396L;
	private static final String HTML_LAYOUT = fluidRowLocs(SpecialCaseAccessDto.CAZE)
		+ fluidRowLocs(SpecialCaseAccessDto.ASSIGNED_TO)
		+ fluidRowLocs(SpecialCaseAccessDto.END_DATE_TIME)
		+ fluidRowLocs(SpecialCaseAccessDto.ASSIGNED_BY, SpecialCaseAccessDto.ASSIGNMENT_DATE);

	private final boolean isCreate;

	protected SpecialCaseAccessForm(boolean isCreate) {
		super(SpecialCaseAccessDto.class, SpecialCaseAccessDto.I18N_PREFIX, false);
		this.isCreate = isCreate;

		if (isCreate) {
			hideValidationUntilNextCommit();
		}

		addFields();
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	protected void addFields() {
		addField(SpecialCaseAccessDto.CAZE).setReadOnly(true);

		ComboBox assignedToField = addField(SpecialCaseAccessDto.ASSIGNED_TO);
		FieldHelper.updateItems(assignedToField, FacadeProvider.getUserFacade().getUsersByRegionAndRights(null, null, UserRight.CASE_VIEW));
		assignedToField.setRequired(true);

		DateTimeField endDateTime = addDateField(SpecialCaseAccessDto.END_DATE_TIME, DateTimeField.class, -1);
		endDateTime.addValidator(new PastDateValidator(endDateTime.getCaption()));
		endDateTime.setRequired(true);

		if (!isCreate) {
			addField(SpecialCaseAccessDto.ASSIGNED_BY).setReadOnly(true);
			addField(SpecialCaseAccessDto.ASSIGNMENT_DATE).setReadOnly(true);
		}
	}
}
