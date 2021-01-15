package de.symeda.sormas.ui.caze.importer;

import java.util.List;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.Label;

import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.facility.FacilityHelper;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.ui.caze.CasePickOrCreateField;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;

@SuppressWarnings("serial")
public class CasePickOrImportField extends CasePickOrCreateField {

	private CheckBox overrideCheckBox;

	public CasePickOrImportField(CaseDataDto newCase, PersonDto importedPerson, List<CaseIndexDto> similarCases) {
		super(newCase, importedPerson, similarCases);
	}

	@Override
	protected void addInfoComponent() {

		HorizontalLayout infoLayout = new HorizontalLayout();
		infoLayout.setWidth(100, Unit.PERCENTAGE);
		infoLayout.setSpacing(true);
		Image icon = new Image(null, new ThemeResource("img/info-icon.png"));
		icon.setHeight(35, Unit.PIXELS);
		icon.setWidth(35, Unit.PIXELS);
		infoLayout.addComponent(icon);
		Label infoLabel = new Label(I18nProperties.getString(Strings.infoImportSimilarity));
		infoLayout.addComponent(infoLabel);
		infoLayout.setExpandRatio(infoLabel, 1);
		mainLayout.addComponent(infoLayout);
		CssStyles.style(infoLayout, CssStyles.VSPACE_3);

		// Imported case info
		VerticalLayout caseInfoContainer = new VerticalLayout();
		caseInfoContainer.setWidth(100, Unit.PERCENTAGE);
		CssStyles
			.style(caseInfoContainer, CssStyles.BACKGROUND_ROUNDED_CORNERS, CssStyles.BACKGROUND_SUB_CRITERIA, CssStyles.VSPACE_3, "v-scrollable");

		Label newCaseLabel = new Label(I18nProperties.getString(Strings.headingImportedCaseInfo));
		CssStyles.style(newCaseLabel, CssStyles.LABEL_BOLD, CssStyles.VSPACE_4);
		caseInfoContainer.addComponent(newCaseLabel);

		HorizontalLayout caseInfoLayout = new HorizontalLayout();
		caseInfoLayout.setSpacing(true);
		caseInfoLayout.setSizeUndefined();
		{
			Label diseaseField = new Label();
			diseaseField.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.DISEASE));
			diseaseField.setValue(DiseaseHelper.toString(newCase.getDisease(), newCase.getDiseaseDetails()));
			diseaseField.setWidthUndefined();
			caseInfoLayout.addComponent(diseaseField);

			Label reportDateField = new Label();
			reportDateField.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.REPORT_DATE));
			reportDateField.setValue(DateFormatHelper.formatDate(newCase.getReportDate()));
			reportDateField.setWidthUndefined();
			caseInfoLayout.addComponent(reportDateField);

			Label regionField = new Label();
			regionField.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.REGION));
			regionField.setValue(newCase.getRegion().toString());
			regionField.setWidthUndefined();
			caseInfoLayout.addComponent(regionField);

			Label districtField = new Label();
			districtField.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.DISTRICT));
			districtField.setValue(newCase.getDistrict().toString());
			districtField.setWidthUndefined();
			caseInfoLayout.addComponent(districtField);

			Label facilityField = new Label();
			facilityField.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.HEALTH_FACILITY));
			facilityField.setValue(
				FacilityHelper.buildFacilityString(
					null,
					newCase.getHealthFacility() != null ? newCase.getHealthFacility().toString() : "",
					newCase.getHealthFacilityDetails()));
			facilityField.setWidthUndefined();
			caseInfoLayout.addComponent(facilityField);

			Label firstNameField = new Label();
			firstNameField.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.FIRST_NAME));
			firstNameField.setValue(newPerson.getFirstName());
			firstNameField.setWidthUndefined();
			caseInfoLayout.addComponent(firstNameField);

			Label lastNameField = new Label();
			lastNameField.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.LAST_NAME));
			lastNameField.setValue(newPerson.getLastName());
			lastNameField.setWidthUndefined();
			caseInfoLayout.addComponent(lastNameField);

			Label ageAndBirthDateField = new Label();
			ageAndBirthDateField.setCaption(I18nProperties.getCaption(Captions.personAgeAndBirthdate));
			ageAndBirthDateField.setValue(
				PersonHelper.getAgeAndBirthdateString(
					newPerson.getApproximateAge(),
					newPerson.getApproximateAgeType(),
					newPerson.getBirthdateDD(),
					newPerson.getBirthdateMM(),
					newPerson.getBirthdateYYYY(),
					I18nProperties.getUserLanguage()));
			ageAndBirthDateField.setWidthUndefined();
			caseInfoLayout.addComponent(ageAndBirthDateField);

			Label sexField = new Label();
			sexField.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.SEX));
			sexField.setValue(newPerson.getSex() != null ? newPerson.getSex().toString() : "");
			sexField.setWidthUndefined();
			caseInfoLayout.addComponent(sexField);
		}

		caseInfoContainer.addComponent(caseInfoLayout);
		mainLayout.addComponent(caseInfoContainer);
	}

	@Override
	protected Component initContent() {

		addInfoComponent();
		addPickCaseComponent();

		overrideCheckBox = new CheckBox();
		overrideCheckBox.setCaption(I18nProperties.getCaption(Captions.caseImportMergeCase));
		CssStyles.style(overrideCheckBox, CssStyles.VSPACE_3);
		mainLayout.addComponent(overrideCheckBox);

		addAndConfigureGrid();
		addCreateCaseComponent();

		pickCase.addValueChangeListener(e -> {
			if (e.getProperty().getValue() != null) {
				overrideCheckBox.setEnabled(true);
			}
		});

		createCase.addValueChangeListener(e -> {
			if (e.getProperty().getValue() != null) {
				overrideCheckBox.setEnabled(false);
			}
		});

		setInternalValue(super.getInternalValue());
		pickCase.setValue(PICK_CASE);

		return mainLayout;
	}

	public boolean isOverrideCase() {
		return overrideCheckBox.getValue();
	}
}
