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

import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.Validator;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseLogic;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactLogic;
import de.symeda.sormas.api.followup.FollowUpLogic;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.symptoms.SymptomsContext;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.symptoms.SymptomsForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.DateTimeField;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.NullableOptionGroup;

public class VisitEditForm extends AbstractEditForm<VisitDto> {

	private static final long serialVersionUID = 4265377973842591202L;
	private static final String CONTACT_PERSON_PHONE_NUMBER_LOC = "contactPersonPhoneNumberLoc";
	private static final String PHONE_LINK_PREFIX = "tel: ";

	private static final String HTML_LAYOUT = fluidRowLocs(VisitDto.VISIT_STATUS, CONTACT_PERSON_PHONE_NUMBER_LOC)
		+ fluidRowLocs(VisitDto.VISIT_DATE_TIME, VisitDto.VISIT_REMARKS)
		+ fluidRowLocs(VisitDto.SYMPTOMS);

	private final Disease disease;
	private final ContactDto contact;
	private final CaseDataDto caze;
	private final PersonDto person;
	private SymptomsForm symptomsForm;

	public VisitEditForm(
		Disease disease,
		ContactDto contact,
		CaseDataDto caze,
		PersonDto person,
		boolean create,
		boolean isPseudonymized,
		boolean inJurisdiction) {

		super(
			VisitDto.class,
			VisitDto.I18N_PREFIX,
			false,
			null,
			UiFieldAccessCheckers.forDataAccessLevel(UiUtil.getPseudonymizableDataAccessLevel(create || inJurisdiction), !create && isPseudonymized));
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

	public VisitEditForm(Disease disease, ContactDto contact, PersonDto person, boolean create, boolean inJurisdiction) {
		this(disease, contact, null, person, create, contact.isPseudonymized(), contact.isInJurisdiction());
	}

	public VisitEditForm(Disease disease, CaseDataDto caze, PersonDto person, boolean create, boolean inJurisdiction) {
		this(disease, null, caze, person, create, caze.isPseudonymized(), caze.isInJurisdiction());
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

		VerticalLayout layoutPhoneLink = new VerticalLayout();
		layoutPhoneLink.setSpacing(false);
		layoutPhoneLink.setMargin(false);
		layoutPhoneLink.setWidth(100, Unit.PERCENTAGE);
		Label labelPhoneLink = new Label((I18nProperties.getCaption(Captions.contactPersonPhoneNumber)));
		labelPhoneLink.setPrimaryStyleName("v-caption");
		layoutPhoneLink.addComponent(labelPhoneLink);
		Link linkPhone = new Link(this.person.getPhone(), new ExternalResource(PHONE_LINK_PREFIX + this.person.getPhone()));
		linkPhone.setWidth(100, Unit.PERCENTAGE);
		layoutPhoneLink.addComponent(linkPhone);
		getContent().addComponent(layoutPhoneLink, CONTACT_PERSON_PHONE_NUMBER_LOC);

		addField(VisitDto.VISIT_DATE_TIME, DateTimeField.class);
		NullableOptionGroup visitStatus = addField(VisitDto.VISIT_STATUS, NullableOptionGroup.class);
		addField(VisitDto.VISIT_REMARKS, TextField.class).setDescription(
			I18nProperties.getPrefixDescription(VisitDto.I18N_PREFIX, VisitDto.VISIT_REMARKS) + "\n"
				+ I18nProperties.getDescription(Descriptions.descGdpr));

		symptomsForm = new SymptomsForm(null, disease, person, SymptomsContext.VISIT, null, fieldAccessCheckers);
		getFieldGroup().bind(symptomsForm, VisitDto.SYMPTOMS);
		getContent().addComponent(symptomsForm, VisitDto.SYMPTOMS);

		setRequired(true, VisitDto.VISIT_DATE_TIME, VisitDto.VISIT_STATUS);

		initializeAccessAndAllowedAccesses();

		if (contact != null) {
			Date startDate = ContactLogic.getStartDate(contact);

			addDateValidation(
				startDate,
				startDate,
				contact.getLastContactDate() != null ? Validations.visitBeforeLastContactDate : Validations.visitBeforeContactReport,
				ContactLogic.getEndDate(contact));
		}

		if (caze != null) {
			addDateValidation(
				CaseLogic.getStartDate(caze),
				caze.getReportDate(),
				caze.getSymptoms().getOnsetDate() != null ? Validations.visitBeforeSymptomsOnSet : Validations.visitBeforeCaseReport,
				CaseLogic.getEndDate(caze.getSymptoms().getOnsetDate(), caze.getReportDate(), caze.getFollowUpUntil()));
		}

		symptomsForm.initializeSymptomRequirementsForVisit((NullableOptionGroup) getFieldGroup().getField(VisitDto.VISIT_STATUS));

		FieldHelper.setEnabledWhen(getFieldGroup(), visitStatus, Arrays.asList(VisitStatus.COOPERATIVE), Arrays.asList(VisitDto.SYMPTOMS), true);
	}

	private void addDateValidation(Date startDate, Date referenceStartDate, String errorMessageDateTooEarly, Date endDate) {

		getField(VisitDto.VISIT_DATE_TIME).addValidator((Validator) value -> {
			Date visitDateTime = (Date) getFieldGroup().getField(VisitDto.VISIT_DATE_TIME).getValue();
			if (visitDateTime.before(startDate) && DateHelper.getDaysBetween(visitDateTime, referenceStartDate) > FollowUpLogic.ALLOWED_DATE_OFFSET) {
				throw new Validator.InvalidValueException(
					I18nProperties.getValidationError(errorMessageDateTooEarly, FollowUpLogic.ALLOWED_DATE_OFFSET));
			}
			if (endDate != null
				&& visitDateTime.after(endDate)
				&& DateHelper.getDaysBetween(endDate, visitDateTime) > FollowUpLogic.ALLOWED_DATE_OFFSET) {
				throw new Validator.InvalidValueException(
					I18nProperties.getValidationError(Validations.visitAfterFollowUp, FollowUpLogic.ALLOWED_DATE_OFFSET));
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
