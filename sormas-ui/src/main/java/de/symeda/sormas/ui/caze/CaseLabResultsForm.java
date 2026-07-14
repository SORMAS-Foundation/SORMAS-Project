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

package de.symeda.sormas.ui.caze;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import java.util.Date;

import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldAccessHelper;

/**
 * Header form of the case "Laboratory results" tab (#13948, issue #13955). The symptom-onset date is shown
 * read-only (sourced from the case's symptoms, not bound to the field group). Date other, its detail and
 * the external comments are editable and persisted on the case.
 */
public class CaseLabResultsForm extends AbstractEditForm<CaseDataDto> {

	private static final long serialVersionUID = 1L;

	private static final String ONSET_DATE_LOC = "onsetDateLoc";

	//@formatter:off
	private static final String HTML_LAYOUT =
		fluidRowLocs(4, ONSET_DATE_LOC) +
		fluidRowLocs(CaseDataDto.DATE_OTHER, CaseDataDto.DATE_OTHER_DETAILS) +
		fluidRowLocs(CaseDataDto.EXTERNAL_COMMENTS);
	//@formatter:on

	private DateField onsetDateField;

	public CaseLabResultsForm(boolean isPseudonymized, boolean inJurisdiction, boolean isEditAllowed) {
		super(
			CaseDataDto.class,
			CaseDataDto.I18N_PREFIX,
			false,
			FieldVisibilityCheckers.withCountry(FacadeProvider.getConfigFacade().getCountryLocale()),
			FieldAccessHelper.getFieldAccessCheckers(inJurisdiction, isPseudonymized),
			isEditAllowed);
		addFields();
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	protected void addFields() {
		// Symptom-onset date: display-only, value injected by the controller from the case's symptoms. It is
		// not bound to the field group (never committed). Onset date is plain clinical data (no @SensitiveData),
		// so showing it here is consistent with the Symptoms tab and not subject to field-access masking.
		onsetDateField = new DateField();
		onsetDateField.setId(ONSET_DATE_LOC);
		onsetDateField.setCaption(I18nProperties.getPrefixCaption(SymptomsDto.I18N_PREFIX, SymptomsDto.ONSET_DATE));
		onsetDateField.setReadOnly(true);
		getContent().addComponent(onsetDateField, ONSET_DATE_LOC);

		addField(CaseDataDto.DATE_OTHER, DateField.class);
		addField(CaseDataDto.DATE_OTHER_DETAILS, TextField.class);
		TextArea externalComments = addField(CaseDataDto.EXTERNAL_COMMENTS, TextArea.class);
		externalComments.setRows(3);
	}

	public void setOnsetDate(Date onsetDate) {
		// Set the value before marking the field read-only, since a read-only Vaadin field rejects setValue.
		onsetDateField.setReadOnly(false);
		onsetDateField.setValue(onsetDate);
		onsetDateField.setReadOnly(true);
	}
}
