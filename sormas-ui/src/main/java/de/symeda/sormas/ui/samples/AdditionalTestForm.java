package de.symeda.sormas.ui.samples;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.sample.AdditionalTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.DateTimeField;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class AdditionalTestForm extends AbstractEditForm<AdditionalTestDto> {

	private static final String HTML_LAYOUT =
			LayoutUtil.fluidRowLocs(AdditionalTestDto.TEST_DATE_TIME, "") +
			LayoutUtil.fluidRowLocs(AdditionalTestDto.HAEMOGLOBINURIA, AdditionalTestDto.PROTEINURIA) +
			LayoutUtil.fluidRowLocs(AdditionalTestDto.HEMATURIA, "") +
			LayoutUtil.h4(I18nProperties.getPrefixCaption(AdditionalTestDto.I18N_PREFIX, AdditionalTestDto.ARTERIAL_VENOUS_BLOOD_GAS)) +
			LayoutUtil.fluidRowLocs(AdditionalTestDto.ARTERIAL_VENOUS_GAS_PH, AdditionalTestDto.ARTERIAL_VENOUS_GAS_PCO2,
					AdditionalTestDto.ARTERIAL_VENOUS_GAS_PAO2, AdditionalTestDto.ARTERIAL_VENOUS_GAS_HCO3) +
			LayoutUtil.fluidRowLocs(AdditionalTestDto.GAS_OXYGEN_THERAPY, "") +
			LayoutUtil.fluidRowLocsCss(CssStyles.VSPACE_TOP_3, AdditionalTestDto.ALT_SGPT, AdditionalTestDto.TOTAL_BILIRUBIN) +
			LayoutUtil.fluidRowLocs(AdditionalTestDto.AST_SGOT, AdditionalTestDto.CONJ_BILIRUBIN) +
			LayoutUtil.fluidRowLocs(AdditionalTestDto.CREATININE, AdditionalTestDto.WBC_COUNT) +
			LayoutUtil.fluidRowLocs(AdditionalTestDto.POTASSIUM, AdditionalTestDto.PLATELETS) +
			LayoutUtil.fluidRowLocs(AdditionalTestDto.UREA, AdditionalTestDto.PROTHROMBIN_TIME) +
			LayoutUtil.fluidRowLocs(AdditionalTestDto.HAEMOGLOBIN, "") +
			LayoutUtil.loc(AdditionalTestDto.OTHER_TEST_RESULTS);

	private final SampleDto sample;

	public AdditionalTestForm(SampleDto sample, boolean create, UserRight editOrCreateUserRight) {
		super(AdditionalTestDto.class, AdditionalTestDto.I18N_PREFIX, editOrCreateUserRight);

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
		testDateTimeField.addValidator(new DateComparisonValidator(testDateTimeField, sample.getSampleDateTime(), false, false,
				I18nProperties.getValidationError(Validations.afterDate, testDateTimeField.getCaption(), I18nProperties.getPrefixCaption(SampleDto.I18N_PREFIX, SampleDto.SAMPLE_DATE_TIME))));
		
		addField(AdditionalTestDto.HAEMOGLOBINURIA, ComboBox.class);
		addField(AdditionalTestDto.PROTEINURIA, ComboBox.class);
		addField(AdditionalTestDto.HEMATURIA, ComboBox.class);

		TextField bloodGasPHField = addField(AdditionalTestDto.ARTERIAL_VENOUS_GAS_PH, TextField.class);
		bloodGasPHField.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, bloodGasPHField.getCaption()));
		TextField bloodGasPco2Field = addField(AdditionalTestDto.ARTERIAL_VENOUS_GAS_PCO2, TextField.class);
		bloodGasPco2Field.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, bloodGasPco2Field.getCaption()));
		TextField bloodGasPao2Field = addField(AdditionalTestDto.ARTERIAL_VENOUS_GAS_PAO2, TextField.class);
		bloodGasPao2Field.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, bloodGasPao2Field.getCaption()));
		TextField bloodGasHco3Field = addField(AdditionalTestDto.ARTERIAL_VENOUS_GAS_HCO3, TextField.class);
		bloodGasHco3Field.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, bloodGasHco3Field.getCaption()));
		TextField gasOxygenTherapyField = addField(AdditionalTestDto.GAS_OXYGEN_THERAPY, TextField.class);
		gasOxygenTherapyField.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, gasOxygenTherapyField.getCaption()));
		TextField altSgptField = addField(AdditionalTestDto.ALT_SGPT, TextField.class);
		altSgptField.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, altSgptField.getCaption()));
		TextField astSgotField = addField(AdditionalTestDto.AST_SGOT, TextField.class);
		astSgotField.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, astSgotField.getCaption()));
		TextField creatinineField = addField(AdditionalTestDto.CREATININE, TextField.class);
		creatinineField.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, creatinineField.getCaption()));
		TextField potassiumField = addField(AdditionalTestDto.POTASSIUM, TextField.class);
		potassiumField.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, potassiumField.getCaption()));
		TextField ureaField = addField(AdditionalTestDto.UREA, TextField.class);
		ureaField.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, ureaField.getCaption()));
		TextField haemoglobinField = addField(AdditionalTestDto.HAEMOGLOBIN, TextField.class);
		haemoglobinField.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, haemoglobinField.getCaption()));
		TextField totalBilirubinField = addField(AdditionalTestDto.TOTAL_BILIRUBIN, TextField.class);
		totalBilirubinField.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, totalBilirubinField.getCaption()));
		TextField conjBilirubinField = addField(AdditionalTestDto.CONJ_BILIRUBIN, TextField.class);
		conjBilirubinField.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, conjBilirubinField.getCaption()));
		TextField wbcCountField = addField(AdditionalTestDto.WBC_COUNT, TextField.class);
		wbcCountField.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, wbcCountField.getCaption()));
		TextField plateletsField = addField(AdditionalTestDto.PLATELETS, TextField.class);
		plateletsField.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, plateletsField.getCaption()));
		TextField prothrombinTimeField = addField(AdditionalTestDto.PROTHROMBIN_TIME, TextField.class);
		prothrombinTimeField.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, prothrombinTimeField.getCaption()));
		addField(AdditionalTestDto.OTHER_TEST_RESULTS, TextArea.class).setRows(3);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

}
