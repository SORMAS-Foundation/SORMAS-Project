package de.symeda.sormas.ui.therapy;

import java.util.Arrays;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.therapy.TreatmentDto;
import de.symeda.sormas.api.therapy.TreatmentRoute;
import de.symeda.sormas.api.therapy.TreatmentType;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.DateTimeField;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class TreatmentForm extends AbstractEditForm<TreatmentDto> {

	private static final String HTML_LAYOUT =
			LayoutUtil.fluidRowLocs(TreatmentDto.TREATMENT_TYPE, TreatmentDto.TREATMENT_DETAILS) +
			LayoutUtil.loc(TreatmentDto.TYPE_OF_DRUG) +
			LayoutUtil.fluidRowLocs(TreatmentDto.TREATMENT_DATE_TIME, TreatmentDto.EXECUTING_CLINICIAN) +
			LayoutUtil.fluidRowLocs(TreatmentDto.DOSE, TreatmentDto.ROUTE) +
			LayoutUtil.loc(TreatmentDto.ROUTE_DETAILS) +
			LayoutUtil.loc(TreatmentDto.ADDITIONAL_NOTES);
	
	public TreatmentForm(boolean create, UserRight editOrCreateUserRight) {
		super(TreatmentDto.class, TreatmentDto.I18N_PREFIX, editOrCreateUserRight);
		
		setWidth(680, Unit.PIXELS);
		
		if (create) {
			hideValidationUntilNextCommit();
		}
	}
	
	@Override
	protected void addFields() {
		ComboBox treatmentTypeField = addField(TreatmentDto.TREATMENT_TYPE, ComboBox.class);
		treatmentTypeField.setImmediate(true);
		TextField treatmentDetailsField = addField(TreatmentDto.TREATMENT_DETAILS, TextField.class);
		addField(TreatmentDto.TYPE_OF_DRUG, OptionGroup.class);
		addField(TreatmentDto.TREATMENT_DATE_TIME, DateTimeField.class);
		addField(TreatmentDto.EXECUTING_CLINICIAN, TextField.class);
		addField(TreatmentDto.DOSE, TextField.class);
		ComboBox routeField = addField(TreatmentDto.ROUTE, ComboBox.class);
		addField(TreatmentDto.ROUTE_DETAILS, TextField.class);
		addField(TreatmentDto.ADDITIONAL_NOTES, TextArea.class).setRows(3);
		
		setRequired(true, TreatmentDto.TREATMENT_TYPE, TreatmentDto.TREATMENT_DATE_TIME);
		FieldHelper.setRequiredWhen(getFieldGroup(), treatmentTypeField, Arrays.asList(TreatmentDto.TREATMENT_DETAILS), Arrays.asList(TreatmentType.OTHER, TreatmentType.DRUG_INTAKE));
		FieldHelper.setRequiredWhen(getFieldGroup(), routeField, Arrays.asList(TreatmentDto.ROUTE_DETAILS), Arrays.asList(TreatmentRoute.OTHER));
		FieldHelper.setVisibleWhen(getFieldGroup(), TreatmentDto.ROUTE_DETAILS, TreatmentDto.ROUTE, Arrays.asList(TreatmentRoute.OTHER), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), TreatmentDto.TYPE_OF_DRUG, TreatmentDto.TREATMENT_TYPE, Arrays.asList(TreatmentType.DRUG_INTAKE), true);

		treatmentTypeField.addValueChangeListener(e -> {
			if (e.getProperty().getValue() == TreatmentType.DRUG_INTAKE) {
				treatmentDetailsField.setCaption(I18nProperties.getPrefixCaption(TreatmentDto.I18N_PREFIX, "drugIntakeDetails"));
			} else {
				treatmentDetailsField.setCaption(I18nProperties.getPrefixCaption(TreatmentDto.I18N_PREFIX, TreatmentDto.TREATMENT_DETAILS));
			}
		});
	}

	@Override
	protected String createHtmlLayout() {
		 return HTML_LAYOUT;
	}
	
}
