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
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.facility.FacilityHelper;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.caze.CasePickOrCreateField;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;

@SuppressWarnings("serial")
public class CasePickOrImportField extends CasePickOrCreateField {

	private CaseDataDto importedCase;
	private PersonDto importedPerson;
	private CheckBox overrideCheckBox;

	public CasePickOrImportField(CaseDataDto importedCase, PersonDto importedPerson, List<CaseIndexDto> similarCases) {
		super(similarCases);
		this.importedCase = importedCase;
		this.importedPerson = importedPerson;
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
		CssStyles.style(caseInfoContainer, CssStyles.BACKGROUND_ROUNDED_CORNERS, CssStyles.BACKGROUND_SUB_CRITERIA, CssStyles.VSPACE_3, "v-scrollable");

		Label importedCaseLabel = new Label(I18nProperties.getString(Strings.headingImportedCaseInfo));
		CssStyles.style(importedCaseLabel, CssStyles.LABEL_BOLD, CssStyles.VSPACE_4);
		caseInfoContainer.addComponent(importedCaseLabel);

		HorizontalLayout caseInfoLayout = new HorizontalLayout();
		caseInfoLayout.setSpacing(true);
		caseInfoLayout.setSizeUndefined();
		{
			Label diseaseField = new Label();
			diseaseField.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.DISEASE));
			diseaseField.setValue(DiseaseHelper.toString(importedCase.getDisease(), importedCase.getDiseaseDetails()));
			diseaseField.setWidthUndefined();
			caseInfoLayout.addComponent(diseaseField);

			Label reportDateField = new Label();
			reportDateField.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.REPORT_DATE));
			reportDateField.setValue(DateFormatHelper.formatDate(importedCase.getReportDate()));
			reportDateField.setWidthUndefined();
			caseInfoLayout.addComponent(reportDateField);

			Label regionField = new Label();
			regionField.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.REGION));
			regionField.setValue(importedCase.getRegion().toString());
			regionField.setWidthUndefined();
			caseInfoLayout.addComponent(regionField);

			Label districtField = new Label();
			districtField.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.DISTRICT));
			districtField.setValue(importedCase.getDistrict().toString());
			districtField.setWidthUndefined();
			caseInfoLayout.addComponent(districtField);

			Label facilityField = new Label();
			facilityField.setValue(FacilityHelper.buildFacilityString(null,
					importedCase.getHealthFacility() != null ? importedCase.getHealthFacility().toString() : "",
					importedCase.getHealthFacilityDetails()));
			facilityField.setWidthUndefined();
			caseInfoLayout.addComponent(facilityField);

			Label firstNameField = new Label();
			firstNameField.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.FIRST_NAME));
			firstNameField.setValue(importedPerson.getFirstName());
			firstNameField.setWidthUndefined();
			caseInfoLayout.addComponent(firstNameField);

			Label lastNameField = new Label();
			lastNameField.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.LAST_NAME));
			lastNameField.setValue(importedPerson.getLastName());
			lastNameField.setWidthUndefined();
			caseInfoLayout.addComponent(lastNameField);

			Label ageAndBirthDateField = new Label();
			ageAndBirthDateField.setCaption(I18nProperties.getCaption(Captions.personAgeAndBirthdate));
			ageAndBirthDateField.setValue(PersonHelper.getAgeAndBirthdateString(importedPerson.getApproximateAge(), importedPerson.getApproximateAgeType(),
					importedPerson.getBirthdateDD(), importedPerson.getBirthdateMM(), importedPerson.getBirthdateYYYY(), FacadeProvider.getUserFacade().getCurrentUser().getLanguage()));
			ageAndBirthDateField.setWidthUndefined();
			caseInfoLayout.addComponent(ageAndBirthDateField);

			Label sexField = new Label();
			sexField.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.SEX));
			sexField.setValue(importedPerson.getSex() != null ? importedPerson.getSex().toString() : "");
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
