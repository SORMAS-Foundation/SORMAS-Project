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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.importer;

import java.util.List;
import java.util.function.Consumer;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.facility.FacilityHelper;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.person.PersonIndexDto;
import de.symeda.sormas.api.person.PersonNameDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.person.PersonGrid;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class ImportPersonSelectField extends CustomField<PersonIndexDto> {

	public static final String CREATE_PERSON = "createPerson";
	public static final String SELECT_PERSON = "selectPerson";

	private List<PersonNameDto> persons;
	private CaseDataDto importedCase;
	private PersonDto importedPerson;
	private UserReferenceDto currentUser;

	private Consumer<Boolean> selectionChangeCallback;

	// Components
	private PersonGrid personGrid;
	private OptionGroup selectPerson;
	private OptionGroup createNewPerson;
	private CheckBox mergeCheckBox;

	public ImportPersonSelectField(List<PersonNameDto> persons, CaseDataDto importedCase, PersonDto importedPerson, UserReferenceDto currentUser) {
		this.persons = persons;
		this.importedCase = importedCase;
		this.importedPerson = importedPerson;
		this.currentUser = currentUser;
		initContent();
	}

	@Override
	protected Component initContent() {
		if (importedCase == null || importedPerson == null) {
			return null;
		}

		VerticalLayout layout = new VerticalLayout();
		layout.setSizeUndefined();
		layout.setWidth(100, Unit.PERCENTAGE);

		// Info label

		Label infoLabel = new Label(I18nProperties.getString(Strings.infoImportSimilarity));
		CssStyles.style(infoLabel, CssStyles.VSPACE_3);
		layout.addComponent(infoLabel);

		// Imported case info
		VerticalLayout outerCaseInfoLayout = new VerticalLayout();
		outerCaseInfoLayout.setWidth(100, Unit.PERCENTAGE);
		CssStyles.style(outerCaseInfoLayout, CssStyles.BACKGROUND_ROUNDED_CORNERS, CssStyles.BACKGROUND_SUB_CRITERIA, CssStyles.VSPACE_3, "v-scrollable");

		Label importedCaseLabel = new Label(I18nProperties.getString(Strings.headingImportedCaseInfo));
		CssStyles.style(importedCaseLabel, CssStyles.LABEL_BOLD, CssStyles.VSPACE_4);
		outerCaseInfoLayout.addComponent(importedCaseLabel);

		HorizontalLayout caseInfoLayout = new HorizontalLayout();
		caseInfoLayout.setSpacing(true);
		caseInfoLayout.setSizeUndefined();
		{
			Label diseaseField = new Label();
			diseaseField.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.DISEASE));
			diseaseField.setValue(DiseaseHelper.toString(importedCase.getDisease(), importedCase.getDiseaseDetails()));
			diseaseField.setWidthUndefined();
			caseInfoLayout.addComponent(diseaseField);

			Label caseDateField = new Label();
			if (importedCase.getSymptoms().getOnsetDate() != null) {
				caseDateField.setCaption(I18nProperties.getPrefixCaption(SymptomsDto.I18N_PREFIX, SymptomsDto.ONSET_DATE));
				caseDateField.setValue(DateHelper.formatLocalShortDate(importedCase.getSymptoms().getOnsetDate()));
			} else if (importedCase.getReceptionDate() != null) {
				caseDateField.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.RECEPTION_DATE));
				caseDateField.setValue(DateHelper.formatLocalShortDate(importedCase.getReceptionDate()));
			} else {
				caseDateField.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.REPORT_DATE));
				caseDateField.setValue(DateHelper.formatLocalShortDate(importedCase.getReportDate()));
			}
			caseDateField.setWidthUndefined();
			caseInfoLayout.addComponent(caseDateField);

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

			Label communityField = new Label();
			communityField.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.COMMUNITY));
			communityField.setValue(importedCase.getCommunity().toString());
			communityField.setWidthUndefined();
			caseInfoLayout.addComponent(communityField);

			Label facilityField = new Label();
			facilityField.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.HEALTH_FACILITY));
			facilityField.setValue(FacilityHelper.buildFacilityString(null, 
					importedCase.getHealthFacility() != null ? importedCase.getHealthFacility().toString() : "", 
							importedCase.getHealthFacilityDetails()));
			facilityField.setWidthUndefined();
			caseInfoLayout.addComponent(facilityField);
		}

		outerCaseInfoLayout.addComponent(caseInfoLayout);
		layout.addComponent(outerCaseInfoLayout);

		// Imported person info
		VerticalLayout outerPersonInfoLayout = new VerticalLayout();
		outerPersonInfoLayout.setWidth(100, Unit.PERCENTAGE);
		CssStyles.style(outerPersonInfoLayout, CssStyles.BACKGROUND_ROUNDED_CORNERS, CssStyles.BACKGROUND_SUB_CRITERIA, CssStyles.VSPACE_3, "v-scrollable");

		Label importedPersonLabel = new Label(I18nProperties.getString(Strings.headingImportedPersonInfo));
		CssStyles.style(importedPersonLabel, CssStyles.LABEL_BOLD, CssStyles.VSPACE_4);
		outerPersonInfoLayout.addComponent(importedPersonLabel);

		HorizontalLayout personInfoLayout = new HorizontalLayout();
		personInfoLayout.setSpacing(true);
		personInfoLayout.setSizeUndefined();
		{
			Label firstNameField = new Label();
			firstNameField.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.FIRST_NAME));
			firstNameField.setValue(importedPerson.getFirstName());
			firstNameField.setWidthUndefined();
			personInfoLayout.addComponent(firstNameField);

			Label lastNameField = new Label();
			lastNameField.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.LAST_NAME));
			lastNameField.setValue(importedPerson.getLastName());
			lastNameField.setWidthUndefined();
			personInfoLayout.addComponent(lastNameField);

			Label nicknameField = new Label();
			nicknameField.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.NICKNAME));
			nicknameField.setValue(importedPerson.getNickname());
			nicknameField.setWidthUndefined();
			personInfoLayout.addComponent(nicknameField);

			Label ageField = new Label();
			ageField.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.APPROXIMATE_AGE));
			ageField.setValue(PersonHelper.buildAgeString(importedPerson.getApproximateAge(), importedPerson.getApproximateAgeType()));
			ageField.setWidthUndefined();
			personInfoLayout.addComponent(ageField);

			Label sexField = new Label();
			sexField.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.SEX));
			sexField.setValue(importedPerson.getSex() != null ? importedPerson.getSex().toString() : "");
			sexField.setWidthUndefined();
			personInfoLayout.addComponent(sexField);

			Label presentConditionField = new Label();
			presentConditionField.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.PRESENT_CONDITION));
			presentConditionField.setValue(importedPerson.getPresentCondition() != null ? importedPerson.getPresentCondition().toString() : null);
			presentConditionField.setWidthUndefined();
			personInfoLayout.addComponent(presentConditionField);

			Label regionField = new Label();
			regionField.setCaption(I18nProperties.getPrefixCaption(LocationDto.I18N_PREFIX, LocationDto.REGION));
			regionField.setValue(importedPerson.getAddress().getRegion() != null ? importedPerson.getAddress().getRegion().toString() : "");
			regionField.setWidthUndefined();
			personInfoLayout.addComponent(regionField);

			Label districtField = new Label();
			districtField.setCaption(I18nProperties.getPrefixCaption(LocationDto.I18N_PREFIX, LocationDto.DISTRICT));
			districtField.setValue(importedPerson.getAddress().getDistrict() != null ? importedPerson.getAddress().getDistrict().toString() : "");
			districtField.setWidthUndefined();
			personInfoLayout.addComponent(districtField);

			Label communityField = new Label();
			communityField.setCaption(I18nProperties.getPrefixCaption(LocationDto.I18N_PREFIX, LocationDto.COMMUNITY));
			communityField.setValue(importedPerson.getAddress().getCommunity() != null ? importedPerson.getAddress().getCommunity().toString() : "");
			communityField.setWidthUndefined();
			personInfoLayout.addComponent(communityField);

			Label cityField = new Label();
			cityField.setCaption(I18nProperties.getPrefixCaption(LocationDto.I18N_PREFIX, LocationDto.CITY));
			cityField.setValue(importedPerson.getAddress().getCity());
			cityField.setWidthUndefined();
			personInfoLayout.addComponent(cityField);
		}

		outerPersonInfoLayout.addComponent(personInfoLayout);
		layout.addComponent(outerPersonInfoLayout);

		// Person selection/creation
		selectPerson = new OptionGroup(null);
		selectPerson.addItem(SELECT_PERSON);
		selectPerson.setItemCaption(SELECT_PERSON, I18nProperties.getCaption(Captions.personSelect));
		CssStyles.style(selectPerson, CssStyles.VSPACE_NONE);
		selectPerson.addValueChangeListener(e -> {
			if (e.getProperty().getValue() != null) {
				createNewPerson.setValue(null);
				personGrid.setEnabled(true);
				mergeCheckBox.setEnabled(true);
				if (selectionChangeCallback != null) {
					selectionChangeCallback.accept(personGrid.getSelectedRow() != null);
				}
			}
		});
		layout.addComponent(selectPerson);

		mergeCheckBox = new CheckBox();
		mergeCheckBox.setCaption(I18nProperties.getCaption(Captions.caseImportMergeCase));
		CssStyles.style(mergeCheckBox, CssStyles.VSPACE_3);
		layout.addComponent(mergeCheckBox);

		initPersonGrid();
		// Deselect "create new" when person is selected
		personGrid.addSelectionListener(e -> {
			if (e.getSelected().size() > 0) {
				createNewPerson.setValue(null);
			}
		});
		CssStyles.style(personGrid, CssStyles.VSPACE_3);
		layout.addComponent(personGrid);

		personGrid.addSelectionListener(e -> {
			if (selectionChangeCallback != null) {
				selectionChangeCallback.accept(!e.getSelected().isEmpty());
			}
		});

		createNewPerson = new OptionGroup(null);
		createNewPerson.addItem(CREATE_PERSON);
		createNewPerson.setItemCaption(CREATE_PERSON, I18nProperties.getCaption(Captions.personCreateNew));
		// Deselect grid when "create new" is selected
		createNewPerson.addValueChangeListener(e -> {
			if (e.getProperty().getValue() != null) {
				selectPerson.setValue(null);
				personGrid.select(null);
				personGrid.setEnabled(false);
				mergeCheckBox.setEnabled(false);
				if (selectionChangeCallback != null) {
					selectionChangeCallback.accept(true);
				}
			}
		});
		layout.addComponent(createNewPerson);

		// Set field values based on internal value
		setInternalValue(super.getInternalValue());


		return layout;
	}

	private void initPersonGrid() {
		if (personGrid == null) {
			personGrid = new PersonGrid(persons, importedPerson, importedCase, currentUser);
		}
	}

	public void selectBestMatch() {
		if (personGrid.getContainerDataSource().size() == 1) {
			setInternalValue((PersonIndexDto) personGrid.getContainerDataSource().firstItemId());
		} else {
			setInternalValue(null);
		}
	}

	@Override
	public Class<? extends PersonIndexDto> getType() {
		return PersonIndexDto.class;
	}

	public CaseDataDto getSelectedMatchingCase() {
		if (selectPerson == null || mergeCheckBox.getValue() == null || getValue().getCaseUuid() == null) {
			return null;
		}

		return FacadeProvider.getCaseFacade().getCaseDataByUuid(getInternalValue().getCaseUuid());
	}

	public boolean isUsePerson() {
		return SELECT_PERSON.equals(selectPerson.getValue());
	}

	public boolean isMergeCase() {
		return mergeCheckBox.getValue();
	}

	@Override
	protected void setInternalValue(PersonIndexDto newValue) {
		super.setInternalValue(newValue);

		if (selectPerson != null) {
			selectPerson.setValue(SELECT_PERSON);
		}

		if (newValue != null) {
			personGrid.select(newValue);
		}
	}

	@Override
	protected PersonIndexDto getInternalValue() {
		if (personGrid != null) {
			PersonIndexDto value = (PersonIndexDto) personGrid.getSelectedRow();
			return value;
		}

		return super.getInternalValue();
	}

	/**
	 * Callback is executed with 'true' when a grid entry or "Create new person" is selected.
	 */
	public void setSelectionChangeCallback(Consumer<Boolean> callback) {
		this.selectionChangeCallback = callback;
	}

}
