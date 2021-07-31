package de.symeda.sormas.ui.immunization.components.form;

import static de.symeda.sormas.ui.utils.CssStyles.ERROR_COLOR_PRIMARY;
import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.CssStyles.SOFT_REQUIRED;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_3;
import static de.symeda.sormas.ui.utils.CssStyles.style;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumnLoc;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import java.util.Collections;

import com.vaadin.ui.Label;
import com.vaadin.v7.data.util.converter.StringToIntegerConverter;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.immunization.ImmunizationManagementStatus;
import de.symeda.sormas.api.immunization.ImmunizationStatus;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.InfrastructureFieldsHelper;
import de.symeda.sormas.ui.utils.NullableOptionGroup;
import de.symeda.sormas.ui.utils.ResizableTextAreaWrapper;

public class ImmunizationDataForm extends AbstractEditForm<ImmunizationDto> {

	private static final String OVERWRITE_IMMUNIZATION_MANAGEMENT_STATUS = "overwriteImmunizationManagementStatus";
	private static final String RESPONSIBLE_JURISDICTION_HEADING_LOC = "responsibleJurisdictionHeadingLoc";
	private static final String VACCINATION_HEADING_LOC = "vaccinationHeadingLoc";
	private static final String RECOVERY_HEADING_LOC = "recoveryHeadingLoc";

	//@formatter:off
	private static final String HTML_LAYOUT = fluidRowLocs(ImmunizationDto.REPORT_DATE, ImmunizationDto.EXTERNAL_ID)
		+ fluidRow(fluidColumnLoc(6, 0, ImmunizationDto.DISEASE))
		+ fluidRowLocs(ImmunizationDto.MEANS_OF_IMMUNIZATION, ImmunizationDto.MEANS_OF_IMMUNIZATION_DETAILS)
		+ fluidRowLocs(OVERWRITE_IMMUNIZATION_MANAGEMENT_STATUS)
		+ fluidRowLocs(ImmunizationDto.MANAGEMENT_STATUS, ImmunizationDto.IMMUNIZATION_STATUS)
		+ fluidRowLocs(RESPONSIBLE_JURISDICTION_HEADING_LOC)
		+ fluidRowLocs(ImmunizationDto.RESPONSIBLE_REGION, ImmunizationDto.RESPONSIBLE_DISTRICT, ImmunizationDto.RESPONSIBLE_COMMUNITY)
		+ fluidRowLocs(ImmunizationDto.START_DATE, ImmunizationDto.END_DATE)
		+ fluidRowLocs(ImmunizationDto.REPORTING_USER, ImmunizationDto.PREVIOUS_INFECTION, ImmunizationDto.LAST_INFECTION_DATE)
		+ fluidRowLocs(ImmunizationDto.ADDITIONAL_DETAILS)
		+ fluidRowLocs(VACCINATION_HEADING_LOC)
		+ fluidRow(fluidColumnLoc(6, 0, ImmunizationDto.NUMBER_OF_DOSES))
		+ fluidRowLocs(RECOVERY_HEADING_LOC)
		+ fluidRowLocs(ImmunizationDto.POSITIVE_TEST_RESULT_DATE, ImmunizationDto.RECOVERY_DATE)
		+ fluidRow(fluidColumnLoc(6, 0, ImmunizationDto.COUNTRY));
	//@formatter:on

	private final String immunizationUuid;

	public ImmunizationDataForm(String immunizationUuid, boolean isPseudonymized) {
		super(
			ImmunizationDto.class,
			ImmunizationDto.I18N_PREFIX,
			false,
			FieldVisibilityCheckers.withCountry(FacadeProvider.getConfigFacade().getCountryLocale()),
			createFieldAccessCheckers(isPseudonymized, true));
		this.immunizationUuid = immunizationUuid;
		addFields();
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	protected void addFields() {
		addField(ImmunizationDto.REPORT_DATE, DateField.class);

		TextField externalIdField = addField(ImmunizationDto.EXTERNAL_ID, TextField.class);
		style(externalIdField, ERROR_COLOR_PRIMARY);

		addDiseaseField(ImmunizationDto.DISEASE, false);

		ComboBox meansOfImmunizationField = addField(ImmunizationDto.MEANS_OF_IMMUNIZATION, ComboBox.class);
		addField(ImmunizationDto.MEANS_OF_IMMUNIZATION_DETAILS, TextField.class);

		CheckBox overwriteImmunizationManagementStatus = addCustomField(OVERWRITE_IMMUNIZATION_MANAGEMENT_STATUS, Boolean.class, CheckBox.class);
		overwriteImmunizationManagementStatus.addStyleName(VSPACE_3);

		ComboBox managementStatusField = addCustomField(ImmunizationDto.MANAGEMENT_STATUS, ImmunizationManagementStatus.class, ComboBox.class);
		managementStatusField.setValue(ImmunizationManagementStatus.SCHEDULED);
		managementStatusField.setEnabled(false);

		ComboBox immunizationStatusField = addCustomField(ImmunizationDto.IMMUNIZATION_STATUS, ImmunizationStatus.class, ComboBox.class);
		immunizationStatusField.setValue(ImmunizationStatus.PENDING);
		immunizationStatusField.setEnabled(false);

		Label jurisdictionHeadingLabel = new Label(I18nProperties.getString(Strings.headingResponsibleJurisdiction));
		jurisdictionHeadingLabel.addStyleName(H3);
		getContent().addComponent(jurisdictionHeadingLabel, RESPONSIBLE_JURISDICTION_HEADING_LOC);

		ComboBox responsibleRegion = addField(ImmunizationDto.RESPONSIBLE_REGION);
		responsibleRegion.setRequired(true);
		ComboBox responsibleDistrictCombo = addField(ImmunizationDto.RESPONSIBLE_DISTRICT);
		responsibleDistrictCombo.setRequired(true);
		ComboBox responsibleCommunityCombo = addField(ImmunizationDto.RESPONSIBLE_COMMUNITY);
		responsibleCommunityCombo.setNullSelectionAllowed(true);
		responsibleCommunityCombo.addStyleName(SOFT_REQUIRED);

		InfrastructureFieldsHelper.initInfrastructureFields(responsibleRegion, responsibleDistrictCombo, responsibleCommunityCombo);

		addField(ImmunizationDto.START_DATE, DateField.class);
		addField(ImmunizationDto.END_DATE, DateField.class);

		Label vaccinationHeadingLabel = new Label(I18nProperties.getString(Strings.headingVaccination));
		vaccinationHeadingLabel.addStyleName(H3);
		getContent().addComponent(vaccinationHeadingLabel, VACCINATION_HEADING_LOC);
		vaccinationHeadingLabel.setVisible(shouldShowVaccinationFields((MeansOfImmunization) meansOfImmunizationField.getValue()));

		TextField numberOfDosesField = addField(ImmunizationDto.NUMBER_OF_DOSES, TextField.class);
		numberOfDosesField.setConverter(new StringToIntegerConverter());
		numberOfDosesField.setVisible(shouldShowVaccinationFields((MeansOfImmunization) meansOfImmunizationField.getValue()));

		Label recoveryHeadingLabel = new Label(I18nProperties.getString(Strings.headingRecovery));
		recoveryHeadingLabel.addStyleName(H3);
		getContent().addComponent(recoveryHeadingLabel, RECOVERY_HEADING_LOC);
		recoveryHeadingLabel.setVisible(shouldShowVaccinationFields((MeansOfImmunization) meansOfImmunizationField.getValue()));

		DateField positiveTestResultDate = addField(ImmunizationDto.POSITIVE_TEST_RESULT_DATE, DateField.class);
		positiveTestResultDate.setVisible(shouldShowVaccinationFields((MeansOfImmunization) meansOfImmunizationField.getValue()));

		DateField recoveryDate = addField(ImmunizationDto.RECOVERY_DATE, DateField.class);
		recoveryDate.setVisible(shouldShowVaccinationFields((MeansOfImmunization) meansOfImmunizationField.getValue()));

		addField(ImmunizationDto.REPORTING_USER, ComboBox.class);
		addField(ImmunizationDto.PREVIOUS_INFECTION, NullableOptionGroup.class);
		addField(ImmunizationDto.LAST_INFECTION_DATE, DateField.class);

		TextArea descriptionField = addField(ImmunizationDto.ADDITIONAL_DETAILS, TextArea.class, new ResizableTextAreaWrapper<>());
		descriptionField.setRows(2);
		descriptionField.setDescription(
			I18nProperties.getPrefixDescription(ImmunizationDto.I18N_PREFIX, ImmunizationDto.ADDITIONAL_DETAILS, "") + "\n"
				+ I18nProperties.getDescription(Descriptions.descGdpr));

		ComboBox country = addInfrastructureField(ImmunizationDto.COUNTRY);
		country.addItems(FacadeProvider.getCountryFacade().getAllActiveAsReference());

		// Set initial visibilities & accesses
		initializeVisibilitiesAndAllowedVisibilities();

		setRequired(true, ImmunizationDto.REPORT_DATE, ImmunizationDto.DISEASE, ImmunizationDto.MEANS_OF_IMMUNIZATION, ImmunizationDto.START_DATE);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Collections.singletonList(ImmunizationDto.MEANS_OF_IMMUNIZATION_DETAILS),
			ImmunizationDto.MEANS_OF_IMMUNIZATION,
			Collections.singletonList(MeansOfImmunization.OTHER),
			true);

		overwriteImmunizationManagementStatus.addValueChangeListener(valueChangeEvent -> {
			boolean selectedValue = (boolean) valueChangeEvent.getProperty().getValue();
			managementStatusField.setEnabled(selectedValue);
		});

		meansOfImmunizationField.addValueChangeListener(valueChangeEvent -> {
			MeansOfImmunization meansOfImmunization = (MeansOfImmunization) valueChangeEvent.getProperty().getValue();
			if (MeansOfImmunization.RECOVERY.equals(meansOfImmunization) || MeansOfImmunization.OTHER.equals(meansOfImmunization)) {
				managementStatusField.setValue(ImmunizationManagementStatus.COMPLETED);
			} else {
				managementStatusField.setValue(ImmunizationManagementStatus.SCHEDULED);
			}
			boolean isVaccinationVisible = shouldShowVaccinationFields(meansOfImmunization);
			vaccinationHeadingLabel.setVisible(isVaccinationVisible);
			numberOfDosesField.setVisible(isVaccinationVisible);
			if (!isVaccinationVisible) {
				numberOfDosesField.setValue(null);
			}
			recoveryHeadingLabel.setVisible(isVaccinationVisible);
			positiveTestResultDate.setVisible(isVaccinationVisible);
			recoveryDate.setVisible(isVaccinationVisible);
		});

		managementStatusField.addValueChangeListener(valueChangeEvent -> {
			ImmunizationManagementStatus managementStatusValue = (ImmunizationManagementStatus) valueChangeEvent.getProperty().getValue();
			switch (managementStatusValue) {
			case SCHEDULED:
			case ONGOING:
				immunizationStatusField.setValue(ImmunizationStatus.PENDING);
				break;
			case COMPLETED:
				immunizationStatusField.setValue(ImmunizationStatus.ACQUIRED);
				break;
			case CANCELED:
				immunizationStatusField.setValue(ImmunizationStatus.NOT_ACQUIRED);
				break;
			default:
				break;
			}
		});

		setReadOnly(true, ImmunizationDto.REPORTING_USER);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			ImmunizationDto.LAST_INFECTION_DATE,
			ImmunizationDto.PREVIOUS_INFECTION,
			Collections.singletonList(YesNoUnknown.YES),
			true);
	}

	private static UiFieldAccessCheckers createFieldAccessCheckers(boolean isPseudonymized, boolean withPersonalAndSensitive) {
		if (withPersonalAndSensitive) {
			return UiFieldAccessCheckers.getDefault(isPseudonymized);
		}

		return UiFieldAccessCheckers.getNoop();
	}

	private boolean shouldShowVaccinationFields(MeansOfImmunization meansOfImmunization) {
		return MeansOfImmunization.VACCINATION.equals(meansOfImmunization) || MeansOfImmunization.VACCINATION_RECOVERY.equals(meansOfImmunization);
	}
}
