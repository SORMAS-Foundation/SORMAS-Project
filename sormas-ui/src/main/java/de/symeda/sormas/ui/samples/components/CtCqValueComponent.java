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

import com.vaadin.data.ValueProvider;
import com.vaadin.server.Setter;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.ui.utils.FormComponent;
import de.symeda.sormas.ui.utils.StringToFloatNullableConverter;

/**
 * CQ value and CT values (E, N, RdRp, S, ORF1, RdRp/S) as a standalone composable component.
 */
public class CtCqValueComponent extends FormComponent<PathogenTestDto> {

	private static final long serialVersionUID = 1L;

	private final boolean isLuxembourg;

	private TextField cqValueField;
	private TextField ctValueE;
	private TextField ctValueN;
	private TextField ctValueRdrp;
	private TextField ctValueS;
	private TextField ctValueOrf1;
	private TextField ctValueRdrpS;

	private HorizontalLayout cqRow;

	public CtCqValueComponent(boolean isLuxembourg) {
		super(PathogenTestDto.class);
		this.isLuxembourg = isLuxembourg;
		buildLayout();
		bindFields();
	}

	private void buildLayout() {
		// CQ value
		cqValueField = createTextField(PathogenTestDto.CQ_VALUE, PathogenTestDto.I18N_PREFIX, ValueChangeMode.BLUR);
		if (!isLuxembourg) {
			cqValueField.setVisible(false);
		}
		cqRow = addRow(cqValueField);

		// CT values
		ctValueE = createTextField(PathogenTestDto.CT_VALUE_E, PathogenTestDto.I18N_PREFIX, ValueChangeMode.BLUR);
		ctValueN = createTextField(PathogenTestDto.CT_VALUE_N, PathogenTestDto.I18N_PREFIX, ValueChangeMode.BLUR);
		addRow(ctValueE, ctValueN);

		ctValueRdrp = createTextField(PathogenTestDto.CT_VALUE_RDRP, PathogenTestDto.I18N_PREFIX, ValueChangeMode.BLUR);
		ctValueS = createTextField(PathogenTestDto.CT_VALUE_S, PathogenTestDto.I18N_PREFIX, ValueChangeMode.BLUR);
		addRow(ctValueRdrp, ctValueS);

		ctValueOrf1 = createTextField(PathogenTestDto.CT_VALUE_ORF_1, PathogenTestDto.I18N_PREFIX, ValueChangeMode.BLUR);
		ctValueRdrpS = createTextField(PathogenTestDto.CT_VALUE_RDRP_S, PathogenTestDto.I18N_PREFIX, ValueChangeMode.BLUR);
		addRow(ctValueOrf1, ctValueRdrpS);

		// Initially hide CT fields
		setCtFieldsVisible(false);
	}

	private void bindFields() {
		bindFloatField(cqValueField, PathogenTestDto::getCqValue, PathogenTestDto::setCqValue);
		bindFloatField(ctValueE, PathogenTestDto::getCtValueE, PathogenTestDto::setCtValueE);
		bindFloatField(ctValueN, PathogenTestDto::getCtValueN, PathogenTestDto::setCtValueN);
		bindFloatField(ctValueRdrp, PathogenTestDto::getCtValueRdrp, PathogenTestDto::setCtValueRdrp);
		bindFloatField(ctValueS, PathogenTestDto::getCtValueS, PathogenTestDto::setCtValueS);
		bindFloatField(ctValueOrf1, PathogenTestDto::getCtValueOrf1, PathogenTestDto::setCtValueOrf1);
		bindFloatField(ctValueRdrpS, PathogenTestDto::getCtValueRdrpS, PathogenTestDto::setCtValueRdrpS);
	}

	private void bindFloatField(TextField field, ValueProvider<PathogenTestDto, Float> getter, Setter<PathogenTestDto, Float> setter) {
		binder.forField(field).withConverter(new StringToFloatNullableConverter(field.getCaption())).bind(getter, setter);
	}

	public void updateCtVisibility(Disease disease, PathogenTestType testType) {
		if (disease == null || !java.util.Arrays.asList(Disease.TUBERCULOSIS).contains(disease)) {
			setCtFieldsVisible(PathogenTestType.PCR_RT_PCR == testType);
		} else {
			setCtFieldsVisible(false);
		}
	}

	public void updateCqVisibility(Disease disease, PathogenTestType testType, PathogenTestResultType testResult) {
		if (disease == null || !java.util.Arrays.asList(Disease.TUBERCULOSIS).contains(disease)) {
			if ((testType == PathogenTestType.PCR_RT_PCR && testResult == PathogenTestResultType.POSITIVE)
				|| testType == PathogenTestType.CQ_VALUE_DETECTION
				|| testType == PathogenTestType.Q_PCR) {
				cqValueField.setVisible(true);
			} else {
				cqValueField.setVisible(false);
				cqValueField.clear();
			}
		} else {
			cqValueField.setVisible(false);
			cqValueField.clear();
		}
		updateRowVisibility(cqRow);
		updateRowAndSelfVisibility();
	}

	private void setCtFieldsVisible(boolean visible) {
		TextField[] ctFields = {
			ctValueE,
			ctValueN,
			ctValueRdrp,
			ctValueS,
			ctValueOrf1,
			ctValueRdrpS };
		for (TextField f : ctFields) {
			if (!visible) {
				f.clear();
			}
			f.setVisible(visible);
		}
		updateRowAndSelfVisibility();
	}

}
