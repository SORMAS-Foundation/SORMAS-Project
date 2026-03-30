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
package de.symeda.sormas.ui.samples.components;

import com.vaadin.ui.TextArea;

import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.ui.utils.FormComponent;

/**
 * Test result free-text field.
 * Vaadin 8 components with own Binder, self-managed visibility.
 */
public class ResultTextComponent extends FormComponent<PathogenTestDto> {

	private static final long serialVersionUID = 1L;

	private TextArea resultText;

	public ResultTextComponent() {
		super(PathogenTestDto.class);
		buildLayout();
		bindFields();
	}

	private void buildLayout() {
		resultText = createTextArea(PathogenTestDto.TEST_RESULT_TEXT, PathogenTestDto.I18N_PREFIX);
		resultText.setRows(6);
		addFullWidthRow(resultText);
	}

	private void bindFields() {
		binder.forField(resultText).bind(PathogenTestDto::getTestResultText, PathogenTestDto::setTestResultText);
	}
}
