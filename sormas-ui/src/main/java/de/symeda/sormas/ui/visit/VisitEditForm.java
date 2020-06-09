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
package de.symeda.sormas.ui.visit;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import java.util.Arrays;
import java.util.Date;

import com.vaadin.v7.data.Validator;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactLogic;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.symptoms.SymptomsContext;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.fieldaccess.FieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldaccess.checkers.SensitiveDataFieldAccessChecker;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.symptoms.SymptomsForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.DateTimeField;
import de.symeda.sormas.ui.utils.FieldHelper;

public class VisitEditForm extends AbstractEditForm<VisitDto> {

	private static final long serialVersionUID = 1L;

	private static final String HTML_LAYOUT =
		fluidRowLocs(VisitDto.VISIT_STATUS) + fluidRowLocs(VisitDto.VISIT_DATE_TIME, VisitDto.VISIT_REMARKS) + fluidRowLocs(VisitDto.SYMPTOMS);

	private final Disease disease;
	private final ContactDto contact;
	private final PersonDto person;
	private SymptomsForm symptomsForm;

	public VisitEditForm(Disease disease, ContactDto contact, PersonDto person, boolean create, boolean isInJurisdiction) {

		super(
			VisitDto.class,
			VisitDto.I18N_PREFIX,
			false,
			null,
			new FieldAccessCheckers()
				.add(new SensitiveDataFieldAccessChecker(r -> UserProvider.getCurrent().hasUserRight(r), create || isInJurisdiction)));
		if (create) {
			hideValidationUntilNextCommit();
		}
		this.disease = disease;
		this.contact = contact;
		this.person = person;
		if (disease == null) {
			throw new IllegalArgumentException("disease cannot be null");
		}
		addFields();
	}

	@Override
	protected void setInternalValue(VisitDto newValue) {

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

		addField(VisitDto.VISIT_DATE_TIME, DateTimeField.class);
		OptionGroup visitStatus = addField(VisitDto.VISIT_STATUS, OptionGroup.class);
		addField(VisitDto.VISIT_REMARKS, TextField.class);

		symptomsForm = new SymptomsForm(null, disease, person, SymptomsContext.VISIT, null);
		getFieldGroup().bind(symptomsForm, VisitDto.SYMPTOMS);
		getContent().addComponent(symptomsForm, VisitDto.SYMPTOMS);

		setRequired(true, VisitDto.VISIT_DATE_TIME, VisitDto.VISIT_STATUS);

		initializeAccessAndAllowedAccesses();

		if (contact != null) {
			getField(VisitDto.VISIT_DATE_TIME).addValidator(new Validator() {

				private static final long serialVersionUID = -7857409200401637094L;

				@Override
				public void validate(Object value) throws InvalidValueException {
					Date visitDateTime = (Date) getFieldGroup().getField(VisitDto.VISIT_DATE_TIME).getValue();
					Date contactReferenceDate = ContactLogic.getStartDate(contact.getLastContactDate(), contact.getReportDateTime());
					if (visitDateTime.before(contactReferenceDate)
						&& DateHelper.getDaysBetween(visitDateTime, contactReferenceDate) > VisitDto.ALLOWED_CONTACT_DATE_OFFSET) {
						if (contact.getLastContactDate() != null) {
							throw new InvalidValueException(
								I18nProperties.getValidationError(Validations.visitBeforeLastContactDate, VisitDto.ALLOWED_CONTACT_DATE_OFFSET));
						} else {
							throw new InvalidValueException(
								I18nProperties.getValidationError(Validations.visitBeforeContactReport, VisitDto.ALLOWED_CONTACT_DATE_OFFSET));
						}
					}
					if (contact.getFollowUpUntil() != null
						&& visitDateTime.after(contact.getFollowUpUntil())
						&& DateHelper.getDaysBetween(contact.getFollowUpUntil(), visitDateTime) > VisitDto.ALLOWED_CONTACT_DATE_OFFSET) {
						throw new InvalidValueException(
							I18nProperties.getValidationError(Validations.visitAfterFollowUp, VisitDto.ALLOWED_CONTACT_DATE_OFFSET));
					}
				}
			});
		}

		symptomsForm.initializeSymptomRequirementsForVisit((OptionGroup) getFieldGroup().getField(VisitDto.VISIT_STATUS));

		FieldHelper.setEnabledWhen(getFieldGroup(), visitStatus, Arrays.asList(VisitStatus.COOPERATIVE), Arrays.asList(VisitDto.SYMPTOMS), true);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	public SymptomsForm getSymptomsForm() {
		return symptomsForm;
	}
}
