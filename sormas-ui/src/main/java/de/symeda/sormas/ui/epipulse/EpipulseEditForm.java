/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.epipulse.EpipulseExportDto;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;
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
                    fluidRowLocs(EpipulseExportDto.SUBJECT_CODE, "") +
                    fluidRowLocs(EpipulseExportDto.EXPORT_SUBJECT_CODE, EpipulseExportDto.EXPORT_DATASOURCE) +
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

		ComboBox diseaseField = addDiseaseField(EpipulseExportDto.SUBJECT_CODE, false, false);
		sortItemsByCaption(diseaseField);
		TextField exportSubjectCodeField = addField(EpipulseExportDto.EXPORT_SUBJECT_CODE, TextField.class);
		TextField exportDatasourceField = addField(EpipulseExportDto.EXPORT_DATASOURCE, TextField.class);
		DateField reportStartDate = addField(EpipulseExportDto.START_DATE, DateField.class);
		DateField reportEndDate = addField(EpipulseExportDto.END_DATE, DateField.class);

		setRequired(
			true,
			EpipulseExportDto.SUBJECT_CODE,
			EpipulseExportDto.EXPORT_SUBJECT_CODE,
			EpipulseExportDto.EXPORT_DATASOURCE,
			EpipulseExportDto.START_DATE,
			EpipulseExportDto.END_DATE);

		// #14472 - one way only: the disease configuration prefills the codes, what the user enters stays on this export
		diseaseField.addValueChangeListener(e -> prefillExportCodes(diseaseField.getValue(), exportSubjectCodeField, exportDatasourceField));

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

	/**
	 * Fills the subject code and data source of the export with the values configured for the selected disease (#14472).
	 * Called whenever the disease changes, so the fields always start from the configuration of the selected disease; a
	 * value the user then enters is stored on this export only and never changes the disease configuration. Without a
	 * configured value the field is cleared, and as it is required the export can't be created until the user enters one.
	 * <p>
	 * Example: select Congenital syphilis while Syphilis is configured with {@code "SYPH"} and the system data source is
	 * {@code "LU-SYSTEM1"} → subject code {@code "CONSYPH"}, data source {@code "LU-SYSTEM1"}; select Diphtheria without a
	 * configured subject code → subject code empty; clear the disease → both empty.
	 *
	 * @param selectedValue
	 *            the value of the disease dropdown, expected to be an {@link EpipulseSubjectCode} or {@code null}
	 * @param exportSubjectCodeField
	 *            the subject code field of the export
	 * @param exportDatasourceField
	 *            the data source field of the export
	 */
	private void prefillExportCodes(Object selectedValue, TextField exportSubjectCodeField, TextField exportDatasourceField) {

		if (exportSubjectCodeField == null || exportDatasourceField == null) {
			return;
		}

		String subjectCode = null;
		String datasource = null;
		if (selectedValue instanceof EpipulseSubjectCode) {
			EpipulseSubjectCode selectedSubjectCode = (EpipulseSubjectCode) selectedValue;
			subjectCode = FacadeProvider.getEpipulseExportFacade().getConfiguredSubjectCode(selectedSubjectCode);
			datasource = FacadeProvider.getEpipulseExportFacade().getConfiguredDatasource(selectedSubjectCode);
		}

		setPrefillValue(exportSubjectCodeField, subjectCode);
		setPrefillValue(exportDatasourceField, datasource);
	}

	/**
	 * Sets a prefilled value unless the field is read-only; {@code null} clears the field.
	 * <p>
	 * Example: {@code "SYPH"} → the field shows SYPH; {@code null} → the field is empty; read-only field → unchanged.
	 */
	private void setPrefillValue(TextField field, String value) {

		if (field.isReadOnly()) {
			return;
		}
		field.setValue(value);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
