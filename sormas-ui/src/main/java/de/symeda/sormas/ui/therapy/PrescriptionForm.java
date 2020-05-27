package de.symeda.sormas.ui.therapy;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.Arrays;

import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.therapy.PrescriptionDto;
import de.symeda.sormas.api.therapy.TreatmentRoute;
import de.symeda.sormas.api.therapy.TreatmentType;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.FieldHelper;

public class PrescriptionForm extends AbstractEditForm<PrescriptionDto> {

	private static final long serialVersionUID = 1L;
	
	private static final String HTML_LAYOUT =
			fluidRowLocs(PrescriptionDto.PRESCRIPTION_TYPE, PrescriptionDto.PRESCRIPTION_DETAILS) +
			loc(PrescriptionDto.TYPE_OF_DRUG) +
			fluidRowLocs(PrescriptionDto.PRESCRIPTION_DATE, PrescriptionDto.PRESCRIBING_CLINICIAN) +
			fluidRowLocs(PrescriptionDto.PRESCRIPTION_START, PrescriptionDto.PRESCRIPTION_END) +
			fluidRowLocs(PrescriptionDto.FREQUENCY, PrescriptionDto.DOSE) +
			fluidRowLocs(PrescriptionDto.ROUTE, PrescriptionDto.ROUTE_DETAILS) +
			loc(PrescriptionDto.ADDITIONAL_NOTES);

	public PrescriptionForm(boolean create, boolean readOnly) {
		super(PrescriptionDto.class, PrescriptionDto.I18N_PREFIX);

		getFieldGroup().setReadOnly(readOnly);

		setWidth(680, Unit.PIXELS);

		if (create) {
			hideValidationUntilNextCommit();
		}
	}

	@Override
	protected void addFields() {
		ComboBox prescriptionTypeField = addField(PrescriptionDto.PRESCRIPTION_TYPE, ComboBox.class);
		prescriptionTypeField.setImmediate(true);
		TextField prescriptionDetailsField = addField(PrescriptionDto.PRESCRIPTION_DETAILS, TextField.class);
		addField(PrescriptionDto.TYPE_OF_DRUG, OptionGroup.class);
		addField(PrescriptionDto.PRESCRIPTION_DATE, DateField.class);
		addField(PrescriptionDto.PRESCRIBING_CLINICIAN, TextField.class);
		DateField prescriptionStartField = addDateField(PrescriptionDto.PRESCRIPTION_START, DateField.class, -1);
		DateField prescriptionEndField = addDateField(PrescriptionDto.PRESCRIPTION_END, DateField.class, -1);
		prescriptionEndField.setImmediate(true);
		addField(PrescriptionDto.FREQUENCY, TextField.class);
		addField(PrescriptionDto.DOSE, TextField.class);
		ComboBox routeField = addField(PrescriptionDto.ROUTE, ComboBox.class);
		addField(PrescriptionDto.ROUTE_DETAILS, TextField.class);
		addField(PrescriptionDto.ADDITIONAL_NOTES, TextArea.class).setRows(3);

		setRequired(true, PrescriptionDto.PRESCRIPTION_TYPE, PrescriptionDto.PRESCRIPTION_DATE);
		FieldHelper.setRequiredWhen(getFieldGroup(), prescriptionTypeField, Arrays.asList(PrescriptionDto.PRESCRIPTION_DETAILS), Arrays.asList(TreatmentType.OTHER, TreatmentType.DRUG_INTAKE));
		FieldHelper.setRequiredWhen(getFieldGroup(), routeField, Arrays.asList(PrescriptionDto.ROUTE_DETAILS), Arrays.asList(TreatmentRoute.OTHER));
		FieldHelper.setVisibleWhen(getFieldGroup(), PrescriptionDto.ROUTE_DETAILS, PrescriptionDto.ROUTE, Arrays.asList(TreatmentRoute.OTHER), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), PrescriptionDto.TYPE_OF_DRUG, PrescriptionDto.PRESCRIPTION_TYPE, Arrays.asList(TreatmentType.DRUG_INTAKE), true);

		prescriptionTypeField.addValueChangeListener(e -> {
			if (e.getProperty().getValue() == TreatmentType.DRUG_INTAKE) {
				prescriptionDetailsField.setCaption(I18nProperties.getPrefixCaption(PrescriptionDto.I18N_PREFIX, PrescriptionDto.DRUG_INTAKE_DETAILS));
			} else {
				prescriptionDetailsField.setCaption(I18nProperties.getPrefixCaption(PrescriptionDto.I18N_PREFIX, PrescriptionDto.PRESCRIPTION_DETAILS));
			}
		});

		prescriptionStartField.addValidator(new DateComparisonValidator(prescriptionStartField, prescriptionEndField, true, false,
				I18nProperties.getValidationError(Validations.beforeDate, prescriptionStartField.getCaption(), prescriptionEndField.getCaption())));
		prescriptionEndField.addValidator(new DateComparisonValidator(prescriptionEndField, prescriptionStartField, false, false, 
				I18nProperties.getValidationError(Validations.afterDate, prescriptionEndField.getCaption(), prescriptionStartField.getCaption())));
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

}
