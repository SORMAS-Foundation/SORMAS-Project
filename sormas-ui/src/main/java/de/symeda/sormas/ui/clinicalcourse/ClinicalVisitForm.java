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

package de.symeda.sormas.ui.clinicalcourse;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.SyphilisPresentation;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitDto;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.symptoms.SymptomsContext;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.symptoms.SymptomsForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.DateTimeField;

public class ClinicalVisitForm extends AbstractEditForm<ClinicalVisitDto> {

	private static final long serialVersionUID = 1L;

	private static final String HTML_LAYOUT = fluidRowLocs(ClinicalVisitDto.VISIT_DATE_TIME, ClinicalVisitDto.VISITING_PERSON)
		+ loc(ClinicalVisitDto.VISIT_REMARKS)
		+ fluidRowLocs(ClinicalVisitDto.SYMPTOMS);

	private final Disease disease;
	private final SyphilisPresentation syphilisPresentation;
	private final PersonDto person;
	private SymptomsForm symptomsForm;

	public ClinicalVisitForm(
		boolean create,
		Disease disease,
		SyphilisPresentation syphilisPresentation,
		PersonDto person,
		boolean isPseudonymized,
		boolean inJurisdiction) {
		super(
			ClinicalVisitDto.class,
			ClinicalVisitDto.I18N_PREFIX,
			false,
			new FieldVisibilityCheckers(),
			UiFieldAccessCheckers.forDataAccessLevel(
				UiUtil.getPseudonymizableDataAccessLevel(inJurisdiction),
				isPseudonymized,
				FacadeProvider.getConfigFacade().getCountryLocale()));
		if (create) {
			hideValidationUntilNextCommit();
		}
		if (disease == null) {
			throw new IllegalArgumentException("Disease cannot be null");
		}
		this.syphilisPresentation = syphilisPresentation;
		this.disease = disease;
		this.person = person;
		addFields();
	}

	@Override
	protected void setInternalValue(ClinicalVisitDto newValue) {

		if (!disease.equals(newValue.getDisease())) {
			throw new IllegalArgumentException("Visit's disease doesn't match the form configuration");
		}
		super.setInternalValue(newValue);
	}

	@Override
	protected void addFields() {

		if (disease == null) {
			// workaround to stop initialization until disease is set 
			return;
		}

		addField(ClinicalVisitDto.VISIT_DATE_TIME, DateTimeField.class);
		addField(ClinicalVisitDto.VISITING_PERSON, TextField.class);
		addField(ClinicalVisitDto.VISIT_REMARKS, TextField.class).setDescription(
			I18nProperties.getPrefixDescription(ClinicalVisitDto.I18N_PREFIX, ClinicalVisitDto.VISIT_REMARKS, "") + "\n"
				+ I18nProperties.getDescription(Descriptions.descGdpr));;

		symptomsForm = new SymptomsForm(null, disease, syphilisPresentation, person, SymptomsContext.CLINICAL_VISIT, null, fieldAccessCheckers);
		getFieldGroup().bind(symptomsForm, ClinicalVisitDto.SYMPTOMS);
		getContent().addComponent(symptomsForm, ClinicalVisitDto.SYMPTOMS);

		setRequired(true, ClinicalVisitDto.VISIT_DATE_TIME);

		initializeAccessAndAllowedAccesses();
	}

	/**
	 * Returns true if the symptoms of the visit have "No complications" Yes together
	 * with a complication set to Yes (#14457), see
	 * {@link SymptomsForm#hasContradictingComplications()}.
	 * <p>
	 * Example (Mumps): No complications Yes, Orchitis Yes -> {@code true}; symptoms
	 * form not created yet -> {@code false}.
	 *
	 * @return true if "No complications" contradicts a complication
	 */
	public boolean hasContradictingComplications() {
		return symptomsForm != null && symptomsForm.hasContradictingComplications();
	}

	/**
	 * Returns the warning text for the contradiction, see
	 * {@link SymptomsForm#getContradictingComplicationsMessage()}.
	 * <p>
	 * Example (Mumps): No complications Yes, Orchitis Yes ->
	 * {@code No complications is set to "Yes", but these complications are also set to "Yes": Orchitis. ...};
	 * no contradiction or no symptoms form -> {@code ""}.
	 *
	 * @return the warning text, never {@code null}
	 */
	public String getContradictingComplicationsMessage() {
		return symptomsForm != null ? symptomsForm.getContradictingComplicationsMessage() : "";
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
