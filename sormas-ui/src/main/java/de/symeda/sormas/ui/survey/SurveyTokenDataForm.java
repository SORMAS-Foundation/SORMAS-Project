/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2025 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.survey;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import de.symeda.sormas.api.survey.SurveyTokenDto;
import de.symeda.sormas.ui.utils.AbstractEditForm;

public class SurveyTokenDataForm extends AbstractEditForm<SurveyTokenDto> {

	private static final String HTML_LAYOUT = fluidRowLocs(SurveyTokenDto.UUID, SurveyTokenDto.TOKEN)
		+ fluidRowLocs(SurveyTokenDto.SURVEY, "")
		+ fluidRowLocs(SurveyTokenDto.ASSIGNMENT_DATE, SurveyTokenDto.RECIPIENT_EMAIL)
		+ fluidRowLocs(SurveyTokenDto.RESPONSE_RECEIVED);

	protected SurveyTokenDataForm() {
		super(SurveyTokenDto.class, SurveyTokenDto.I18N_PREFIX);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	protected void addFields() {
		addField(SurveyTokenDto.UUID).setReadOnly(true);
		addField(SurveyTokenDto.TOKEN).setReadOnly(true);
		addField(SurveyTokenDto.SURVEY).setReadOnly(true);
		addField(SurveyTokenDto.ASSIGNMENT_DATE).setReadOnly(true);
		addField(SurveyTokenDto.RECIPIENT_EMAIL).setReadOnly(true);
		addField(SurveyTokenDto.RESPONSE_RECEIVED);
	}
}
