/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.sormastosormas;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import java.util.List;

import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextArea;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.sormastosormas.HealthDepartmentServerAccessData;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOptionsDto;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;

public class SormasToSormasOptionsForm extends AbstractEditForm<SormasToSormasOptionsDto> {

	private static final String HTML_LAYOUT = fluidRowLocs(SormasToSormasOptionsDto.HEALTH_DEPARTMENT)
		+ fluidRowLocs(SormasToSormasOptionsDto.PSEUDONYMIZE_PERSONAL_DATA)
		+ fluidRowLocs(SormasToSormasOptionsDto.PSEUDONYMIZE_SENSITIVE_DATA)
		+ fluidRowLocs(SormasToSormasOptionsDto.COMMENT);

	public SormasToSormasOptionsForm() {
		super(SormasToSormasOptionsDto.class, SormasToSormasOptionsDto.I18N_PREFIX);

		setWidthUndefined();
		hideValidationUntilNextCommit();
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	protected void addFields() {
		ComboBox healthDepartmentField = addField(SormasToSormasOptionsDto.HEALTH_DEPARTMENT, ComboBox.class);
		healthDepartmentField.setRequired(true);
		List<HealthDepartmentServerAccessData> healthDepartments = FacadeProvider.getSormasToSormasFacade().getAvailableHealthDepartments();
		healthDepartmentField.addItems(healthDepartments);
		healthDepartments.forEach(hd -> {
			healthDepartmentField.setItemCaption(hd, hd.getName());
		});

		addField(SormasToSormasOptionsDto.PSEUDONYMIZE_PERSONAL_DATA);

		CheckBox pseudonymizeSensitiveData = addField(SormasToSormasOptionsDto.PSEUDONYMIZE_SENSITIVE_DATA);
		pseudonymizeSensitiveData.addStyleNames(CssStyles.FORCE_CAPTION, CssStyles.VSPACE_2);

		TextArea comment = addField(SormasToSormasOptionsDto.COMMENT, TextArea.class);
		comment.setRows(3);
	}
}
