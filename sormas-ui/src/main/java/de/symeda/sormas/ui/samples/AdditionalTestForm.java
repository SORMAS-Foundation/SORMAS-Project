package de.symeda.sormas.ui.samples;

import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_TOP_3;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocsCss;
import static de.symeda.sormas.ui.utils.LayoutUtil.h4;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.sample.AdditionalTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.DateTimeField;

public class AdditionalTestForm extends AbstractEditForm<AdditionalTestDto> {

	private static final long serialVersionUID = 7231529309871021783L;

	//@formatter:off
	private static final String HTML_LAYOUT =
			fluidRowLocs(AdditionalTestDto.TEST_DATE_TIME, "") +
			fluidRowLocs(AdditionalTestDto.HAEMOGLOBINURIA, AdditionalTestDto.PROTEINURIA) +
			fluidRowLocs(AdditionalTestDto.HEMATURIA, "") +
			
			h4(I18nProperties.getPrefixCaption(AdditionalTestDto.I18N_PREFIX, AdditionalTestDto.ARTERIAL_VENOUS_BLOOD_GAS)) +
			fluidRowLocs(AdditionalTestDto.ARTERIAL_VENOUS_GAS_PH, AdditionalTestDto.ARTERIAL_VENOUS_GAS_PCO2,
					AdditionalTestDto.ARTERIAL_VENOUS_GAS_PAO2, AdditionalTestDto.ARTERIAL_VENOUS_GAS_HCO3) +
			fluidRowLocs(AdditionalTestDto.GAS_OXYGEN_THERAPY, "") +
			fluidRowLocsCss(VSPACE_TOP_3, AdditionalTestDto.ALT_SGPT, AdditionalTestDto.TOTAL_BILIRUBIN) +
			fluidRowLocs(AdditionalTestDto.AST_SGOT, AdditionalTestDto.CONJ_BILIRUBIN) +
			fluidRowLocs(AdditionalTestDto.CREATININE, AdditionalTestDto.WBC_COUNT) +
			fluidRowLocs(AdditionalTestDto.POTASSIUM, AdditionalTestDto.PLATELETS) +
			fluidRowLocs(AdditionalTestDto.UREA, AdditionalTestDto.PROTHROMBIN_TIME) +
			fluidRowLocs(AdditionalTestDto.HAEMOGLOBIN, "") +
			loc(AdditionalTestDto.OTHER_TEST_RESULTS);
	//@formatter:on

	private final SampleDto sample;

	public AdditionalTestForm(SampleDto sample, boolean create) {
		super(AdditionalTestDto.class, AdditionalTestDto.I18N_PREFIX);

		this.sample = sample;
		setWidth(600, Unit.PIXELS);

		addFields();
		if (create) {
			hideValidationUntilNextCommit();
		}
	}

	@Override
	protected void addFields() {
		if (sample == null) {
			return;
		}

		DateTimeField testDateTimeField = addField(AdditionalTestDto.TEST_DATE_TIME, DateTimeField.class);
		testDateTimeField.setRequired(true);
		testDateTimeField.addValidator(
			new DateComparisonValidator(
				testDateTimeField,
				sample.getSampleDateTime(),
				false,
				false,
				I18nProperties.getValidationError(
					Validations.afterDate,
					testDateTimeField.getCaption(),
					I18nProperties.getPrefixCaption(SampleDto.I18N_PREFIX, SampleDto.SAMPLE_DATE_TIME))));

		addField(AdditionalTestDto.HAEMOGLOBINURIA, ComboBox.class);
		addField(AdditionalTestDto.PROTEINURIA, ComboBox.class);
		addField(AdditionalTestDto.HEMATURIA, ComboBox.class);

		TextField bloodGasPHField = addField(AdditionalTestDto.ARTERIAL_VENOUS_GAS_PH, TextField.class);
		bloodGasPHField.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, bloodGasPHField.getCaption()));
		removeMaxLengthValidators(bloodGasPHField);
		TextField bloodGasPco2Field = addField(AdditionalTestDto.ARTERIAL_VENOUS_GAS_PCO2, TextField.class);
		bloodGasPco2Field.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, bloodGasPco2Field.getCaption()));
		removeMaxLengthValidators(bloodGasPco2Field);
		TextField bloodGasPao2Field = addField(AdditionalTestDto.ARTERIAL_VENOUS_GAS_PAO2, TextField.class);
		bloodGasPao2Field.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, bloodGasPao2Field.getCaption()));
		removeMaxLengthValidators(bloodGasPao2Field);
		TextField bloodGasHco3Field = addField(AdditionalTestDto.ARTERIAL_VENOUS_GAS_HCO3, TextField.class);
		bloodGasHco3Field.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, bloodGasHco3Field.getCaption()));
		removeMaxLengthValidators(bloodGasHco3Field);
		TextField gasOxygenTherapyField = addField(AdditionalTestDto.GAS_OXYGEN_THERAPY, TextField.class);
		gasOxygenTherapyField
			.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, gasOxygenTherapyField.getCaption()));
		removeMaxLengthValidators(gasOxygenTherapyField);
		TextField altSgptField = addField(AdditionalTestDto.ALT_SGPT, TextField.class);
		altSgptField.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, altSgptField.getCaption()));
		removeMaxLengthValidators(altSgptField);
		TextField astSgotField = addField(AdditionalTestDto.AST_SGOT, TextField.class);
		astSgotField.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, astSgotField.getCaption()));
		removeMaxLengthValidators(astSgotField);
		TextField creatinineField = addField(AdditionalTestDto.CREATININE, TextField.class);
		creatinineField.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, creatinineField.getCaption()));
		removeMaxLengthValidators(creatinineField);
		TextField potassiumField = addField(AdditionalTestDto.POTASSIUM, TextField.class);
		potassiumField.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, potassiumField.getCaption()));
		removeMaxLengthValidators(potassiumField);
		TextField ureaField = addField(AdditionalTestDto.UREA, TextField.class);
		ureaField.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, ureaField.getCaption()));
		removeMaxLengthValidators(ureaField);
		TextField haemoglobinField = addField(AdditionalTestDto.HAEMOGLOBIN, TextField.class);
		haemoglobinField.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, haemoglobinField.getCaption()));
		removeMaxLengthValidators(haemoglobinField);
		TextField totalBilirubinField = addField(AdditionalTestDto.TOTAL_BILIRUBIN, TextField.class);
		totalBilirubinField.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, totalBilirubinField.getCaption()));
		removeMaxLengthValidators(totalBilirubinField);
		TextField conjBilirubinField = addField(AdditionalTestDto.CONJ_BILIRUBIN, TextField.class);
		conjBilirubinField.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, conjBilirubinField.getCaption()));
		removeMaxLengthValidators(conjBilirubinField);
		TextField wbcCountField = addField(AdditionalTestDto.WBC_COUNT, TextField.class);
		wbcCountField.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, wbcCountField.getCaption()));
		removeMaxLengthValidators(wbcCountField);
		TextField plateletsField = addField(AdditionalTestDto.PLATELETS, TextField.class);
		plateletsField.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, plateletsField.getCaption()));
		removeMaxLengthValidators(plateletsField);
		TextField prothrombinTimeField = addField(AdditionalTestDto.PROTHROMBIN_TIME, TextField.class);
		prothrombinTimeField.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, prothrombinTimeField.getCaption()));
		removeMaxLengthValidators(prothrombinTimeField);
		addField(AdditionalTestDto.OTHER_TEST_RESULTS, TextArea.class).setRows(3);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
