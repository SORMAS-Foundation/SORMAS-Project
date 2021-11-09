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

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.components.JsonForm;

public class CaseExternalDataForm extends AbstractEditForm<CaseDataDto> {

	private static final String MAIN_HTML_LAYOUT = fluidRowLocs(CaseDataDto.EXTERNAL_DATA);

	protected CaseExternalDataForm(UiFieldAccessCheckers fieldAccessCheckers) {
		super(CaseDataDto.class, CaseDataDto.I18N_PREFIX, true, new FieldVisibilityCheckers(), fieldAccessCheckers);
	}

	@Override
	protected String createHtmlLayout() {
		return MAIN_HTML_LAYOUT;
	}

	@Override
	protected void addFields() {
		addField(CaseDataDto.EXTERNAL_DATA, JsonForm.class).setCaption(null);
	}
}
