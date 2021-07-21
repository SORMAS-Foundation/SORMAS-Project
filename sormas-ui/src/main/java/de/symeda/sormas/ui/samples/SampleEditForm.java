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
package de.symeda.sormas.ui.samples;

import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import com.vaadin.ui.Label;
import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.FieldHelper;

public class SampleEditForm extends AbstractSampleForm {

	private static final long serialVersionUID = 1L;

	private static final String LABORATORY_SAMPLE_HEADING_LOC = "laboratorySampleHeadingLoc";

	private static final String HTML_LAYOUT = loc(LABORATORY_SAMPLE_HEADING_LOC) + SAMPLE_COMMON_HTML_LAYOUT;

	public SampleEditForm(boolean isPseudonymized) {
		super(
			SampleDto.class,
			SampleDto.I18N_PREFIX,
			UiFieldAccessCheckers.forSensitiveData(isPseudonymized));
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void addFields() {
		Label laboratorySampleHeadingLabel = new Label(I18nProperties.getString(Strings.headingLaboratorySample));
		laboratorySampleHeadingLabel.addStyleName(H3);
		getContent().addComponent(laboratorySampleHeadingLabel, LABORATORY_SAMPLE_HEADING_LOC);

		addCommonFields();

		initializeRequestedTestFields();

		addValidators();

		initializeAccessAndAllowedAccesses();

		setVisibilities();

		addValueChangeListener(e -> {
			defaultValueChangeListener();
			fillPathogenTestResult();
			UserReferenceDto reportingUser = getValue().getReportingUser();
			if (!(UserProvider.getCurrent().hasUserRight(UserRight.SAMPLE_EDIT_NOT_OWNED)
					|| (reportingUser != null && UserProvider.getCurrent().getUuid().equals(reportingUser.getUuid())))) {
				getField(SampleDto.SAMPLE_PURPOSE).setEnabled(false);
				getField(SampleDto.SAMPLING_REASON).setEnabled(false);
				getField(SampleDto.SAMPLING_REASON_DETAILS).setEnabled(false);
				getField(SampleDto.FIELD_SAMPLE_ID).setEnabled(false);
			}
		});
	}

	public void fillPathogenTestResult() {
		ComboBox pathogenTestResultField = (ComboBox) getFieldGroup().getField(SampleDto.PATHOGEN_TEST_RESULT);

		boolean hasOnlyPendingPathogenTests = FacadeProvider.getPathogenTestFacade()
			.getBySampleUuids(Collections.singletonList(getValue().getUuid()))
			.stream()
			.allMatch(pathogenTest -> pathogenTest.getTestResult() == PathogenTestResultType.PENDING);

		Collection<PathogenTestResultType> pathogenTestResultTypes;

		if (hasOnlyPendingPathogenTests) {
			pathogenTestResultTypes = Arrays.asList(PathogenTestResultType.values());
		} else {
			pathogenTestResultTypes =
				Arrays.stream(PathogenTestResultType.values()).filter(type -> type != PathogenTestResultType.NOT_DONE).collect(Collectors.toList());
		}

		FieldHelper.updateEnumData(pathogenTestResultField, pathogenTestResultTypes);

		if (pathogenTestResultField.getValue() == null) {
			pathogenTestResultField.setValue(PathogenTestResultType.PENDING);
		}
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
