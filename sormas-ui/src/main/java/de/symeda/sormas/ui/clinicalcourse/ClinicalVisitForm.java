package de.symeda.sormas.ui.clinicalcourse;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitDto;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.symptoms.SymptomsContext;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.symptoms.SymptomsForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.DateTimeField;

public class ClinicalVisitForm extends AbstractEditForm<ClinicalVisitDto> {

	private static final long serialVersionUID = 1L;

	private static final String HTML_LAYOUT = fluidRowLocs(ClinicalVisitDto.VISIT_DATE_TIME, ClinicalVisitDto.VISITING_PERSON)
		+ loc(ClinicalVisitDto.VISIT_REMARKS)
		+ fluidRowLocs(ClinicalVisitDto.SYMPTOMS);

	private final Disease disease;
	private final PersonDto person;
	private SymptomsForm symptomsForm;

	public ClinicalVisitForm(boolean create, Disease disease, PersonDto person, boolean isPseudonymized) {
		super(
			ClinicalVisitDto.class,
			ClinicalVisitDto.I18N_PREFIX,
			false,
			new FieldVisibilityCheckers(),
			UiFieldAccessCheckers.forSensitiveData(isPseudonymized));
		if (create) {
			hideValidationUntilNextCommit();
		}
		if (disease == null) {
			throw new IllegalArgumentException("Disease cannot be null");
		}
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

		symptomsForm = new SymptomsForm(null, disease, person, SymptomsContext.CLINICAL_VISIT, null, fieldAccessCheckers);
		getFieldGroup().bind(symptomsForm, ClinicalVisitDto.SYMPTOMS);
		getContent().addComponent(symptomsForm, ClinicalVisitDto.SYMPTOMS);

		setRequired(true, ClinicalVisitDto.VISIT_DATE_TIME);

		initializeAccessAndAllowedAccesses();
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
