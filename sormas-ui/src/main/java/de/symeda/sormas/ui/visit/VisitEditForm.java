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
import java.util.function.Supplier;

import com.vaadin.v7.data.Validator;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseLogic;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactLogic;
import de.symeda.sormas.api.followup.FollowUpLogic;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.symptoms.SymptomsContext;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.ui.symptoms.SymptomsForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.DateTimeField;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.UiFieldAccessCheckers;

public class VisitEditForm extends AbstractEditForm<VisitDto> {

	private static final long serialVersionUID = 4265377973842591202L;

	private static final String HTML_LAYOUT =
		fluidRowLocs(VisitDto.VISIT_STATUS) + fluidRowLocs(VisitDto.VISIT_DATE_TIME, VisitDto.VISIT_REMARKS) + fluidRowLocs(VisitDto.SYMPTOMS);

	private final Disease disease;
	private final ContactDto contact;
	private final CaseDataDto caze;
	private final PersonDto person;
	private SymptomsForm symptomsForm;

	public VisitEditForm(Disease disease, ContactDto contact, CaseDataDto caze, PersonDto person, boolean create, boolean isInJurisdiction) {

		super(
			VisitDto.class,
			VisitDto.I18N_PREFIX,
			false,
			null,
			UiFieldAccessCheckers.withCheckers(create || isInJurisdiction, FieldHelper.createSensitiveDataFieldAccessChecker()));
		if (create) {
			hideValidationUntilNextCommit();
		}
		this.disease = disease;
		this.contact = contact;
		this.caze = caze;
		this.person = person;
		if (disease == null) {
			throw new IllegalArgumentException("disease cannot be null");
		}
		if (caze != null && contact != null) {
			throw new IllegalArgumentException("case and contact cannot be both defined");
		}

		addFields();

	}

	public VisitEditForm(Disease disease, ContactDto contact, PersonDto person, boolean create, boolean isInJurisdiction) {
		this(disease, contact, null, person, create, isInJurisdiction);
	}

	public VisitEditForm(Disease disease, CaseDataDto caze, PersonDto person, boolean create, boolean isInJurisdiction) {
		this(disease, null, caze, person, create, isInJurisdiction);
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

		symptomsForm = new SymptomsForm(null, disease, person, SymptomsContext.VISIT, null, fieldAccessCheckers);
		getFieldGroup().bind(symptomsForm, VisitDto.SYMPTOMS);
		getContent().addComponent(symptomsForm, VisitDto.SYMPTOMS);

		setRequired(true, VisitDto.VISIT_DATE_TIME, VisitDto.VISIT_STATUS);

		initializeAccessAndAllowedAccesses();

		if (contact != null) {
			addDateValidation(
				() -> ContactLogic.getStartDate(contact.getLastContactDate(), contact.getReportDateTime()),
				() -> contact.getLastContactDate(),
				() -> contact.getFollowUpUntil(),
				Validations.visitBeforeLastContactDate,
				Validations.visitBeforeContactReport,
				Validations.visitAfterFollowUp);
		}

		if (caze != null) {
			addDateValidation(
				() -> CaseLogic.getStartDate(caze.getSymptoms().getOnsetDate(), caze.getReportDate()),
				() -> caze.getSymptoms().getOnsetDate(),
				() -> CaseLogic.getEndDate(caze.getSymptoms().getOnsetDate(), caze.getReportDate(), caze.getFollowUpUntil()),
				Validations.visitBeforeSymptomsOnSet,
				Validations.visitBeforeCaseReport,
				Validations.visitAfterFollowUp);
		}

		symptomsForm.initializeSymptomRequirementsForVisit((OptionGroup) getFieldGroup().getField(VisitDto.VISIT_STATUS));

		FieldHelper.setEnabledWhen(getFieldGroup(), visitStatus, Arrays.asList(VisitStatus.COOPERATIVE), Arrays.asList(VisitDto.SYMPTOMS), true);
	}

	private void addDateValidation(
		Supplier<Date> startDateSupplier,
		Supplier<Date> firstStartDatePart,
		Supplier<Date> endDateSupplier,
		String errorMessageDateTooEarly,
		String errorMessageDateTooEarlyFirstPart,
		String errorMessageDateTooLate) {

		getField(VisitDto.VISIT_DATE_TIME).addValidator((Validator) value -> {
			Date visitDateTime = (Date) getFieldGroup().getField(VisitDto.VISIT_DATE_TIME).getValue();
			Date startDate = startDateSupplier.get();
			if (visitDateTime.before(startDate)
				&& DateHelper.getDaysBetween(visitDateTime, caze.getReportDate()) > FollowUpLogic.ALLOWED_DATE_OFFSET) {
				if (firstStartDatePart.get() != null) {
					throw new Validator.InvalidValueException(
						I18nProperties.getValidationError(errorMessageDateTooEarlyFirstPart, FollowUpLogic.ALLOWED_DATE_OFFSET));
				} else {
					throw new Validator.InvalidValueException(
						I18nProperties.getValidationError(errorMessageDateTooEarly, FollowUpLogic.ALLOWED_DATE_OFFSET));
				}
			}
			Date endDate = endDateSupplier.get();
			if (endDate != null
				&& visitDateTime.after(endDate)
				&& DateHelper.getDaysBetween(endDate, visitDateTime) > FollowUpLogic.ALLOWED_DATE_OFFSET) {
				throw new Validator.InvalidValueException(
					I18nProperties.getValidationError(errorMessageDateTooLate, FollowUpLogic.ALLOWED_DATE_OFFSET));
			}
		});

	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	public SymptomsForm getSymptomsForm() {
		return symptomsForm;
	}
}
