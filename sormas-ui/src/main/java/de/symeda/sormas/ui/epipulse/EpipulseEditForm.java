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

package de.symeda.sormas.ui.epipulse;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import java.util.List;

import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.epipulse.EpipulseExportDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.DateComparisonValidator;

public class EpipulseEditForm extends AbstractEditForm<EpipulseExportDto> {

	private static final long serialVersionUID = 1L;

	private static final String SAVE_INFO = "saveInfo";
	private static final String ASSIGNEE_MISSING_INFO = "assigneeMissingInfo";
	private static final String OBSERVER_MISSING_INFO = "observerMissingInfo";

	//@formatter:off
    private static final String HTML_LAYOUT =
                    fluidRowLocs(EpipulseExportDto.DISEASE, "") +
                    fluidRowLocs(EpipulseExportDto.START_DATE, EpipulseExportDto.END_DATE);
    //@formatter:on

	private UserRight editOrCreateUserRight;
	private boolean editedFromTaskGrid;
	private Disease disease;
	private List<UserReferenceDto> availableUsers;

	public EpipulseEditForm(boolean create) {

		super(EpipulseExportDto.class, EpipulseExportDto.I18N_PREFIX, false, null);

		this.editOrCreateUserRight = UserRight.EPIPULSE_EXPORT_CREATE;

		setWidth(680, Unit.PIXELS);

		if (create) {
			hideValidationUntilNextCommit();
		}

		addFields();
	}

	@Override
	protected void addFields() {

		ComboBox diseaseField = addDiseaseField(CaseDataDto.DISEASE, false, false);
		DateField reportStartDate = addField(EpipulseExportDto.START_DATE, DateField.class);
		DateField reportEndDate = addField(EpipulseExportDto.END_DATE, DateField.class);

		setRequired(true, EpipulseExportDto.DISEASE, EpipulseExportDto.START_DATE, EpipulseExportDto.END_DATE);

		reportStartDate.addValidator(
			new DateComparisonValidator(
				reportStartDate,
				reportEndDate,
				true,
				false,
				I18nProperties.getValidationError(Validations.beforeDate, reportStartDate.getCaption(), reportEndDate.getCaption())));
		reportEndDate.addValidator(
			new DateComparisonValidator(
				reportEndDate,
				reportStartDate,
				false,
				false,
				I18nProperties.getValidationError(Validations.afterDate, reportEndDate.getCaption(), reportStartDate.getCaption())));
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
