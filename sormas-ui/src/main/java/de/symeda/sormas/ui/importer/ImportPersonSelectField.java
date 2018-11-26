package de.symeda.sormas.ui.importer;

import java.util.List;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
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

	public static final String I18N_PREFIX = "CaseImport";
	
	public static final String CREATE_PERSON = "createPerson";
	public static final String SELECT_PERSON = "selectPerson";

	private List<PersonNameDto> persons;
	private CaseDataDto importedCase;
	private PersonDto importedPerson;
	private UserReferenceDto currentUser;

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
		layout.setSpacing(true);
		layout.setSizeUndefined();
		layout.setWidth(100, Unit.PERCENTAGE);

		// Info label
		
		Label infoLabel = new Label(I18nProperties.getText("importSimilarityInfo"));
		layout.addComponentAsFirst(infoLabel);

		// Imported case info
		
		HorizontalLayout caseInfoLayout = new HorizontalLayout();
		caseInfoLayout.setSpacing(true);
		{
			TextField firstNameField = new TextField();
			firstNameField.setCaption(I18nProperties.getPrefixFieldCaption(PersonDto.I18N_PREFIX, PersonDto.FIRST_NAME));
			firstNameField.setValue(importedPerson.getFirstName());
			firstNameField.setReadOnly(true);
			caseInfoLayout.addComponent(firstNameField);

			TextField lastNameField = new TextField();
			lastNameField.setCaption(I18nProperties.getPrefixFieldCaption(PersonDto.I18N_PREFIX, PersonDto.LAST_NAME));
			lastNameField.setValue(importedPerson.getLastName());
			lastNameField.setReadOnly(true);
			caseInfoLayout.addComponent(lastNameField);

			TextField nicknameField = new TextField();
			nicknameField.setCaption(I18nProperties.getPrefixFieldCaption(PersonDto.I18N_PREFIX, PersonDto.NICKNAME));
			nicknameField.setValue(importedPerson.getNickname());
			nicknameField.setReadOnly(true);
			caseInfoLayout.addComponent(nicknameField);

			TextField ageField = new TextField();
			ageField.setCaption(I18nProperties.getPrefixFieldCaption(PersonDto.I18N_PREFIX, PersonDto.APPROXIMATE_AGE));
			ageField.setValue(PersonHelper.buildAgeString(importedPerson.getApproximateAge(), importedPerson.getApproximateAgeType()));
			ageField.setReadOnly(true);
			caseInfoLayout.addComponent(ageField);

			TextField sexField = new TextField();
			sexField.setCaption(I18nProperties.getPrefixFieldCaption(PersonDto.I18N_PREFIX, PersonDto.SEX));
			sexField.setValue(importedPerson.getSex() != null ? importedPerson.getSex().toString() : "");
			sexField.setReadOnly(true);
			caseInfoLayout.addComponent(sexField);

			TextField presentConditionField = new TextField();
			presentConditionField.setCaption(I18nProperties.getPrefixFieldCaption(PersonDto.I18N_PREFIX, PersonDto.PRESENT_CONDITION));
			presentConditionField.setValue(importedPerson.getPresentCondition() != null ? importedPerson.getPresentCondition().toString() : null);
			presentConditionField.setReadOnly(true);
			caseInfoLayout.addComponent(presentConditionField);

			TextField regionField = new TextField();
			regionField.setCaption(I18nProperties.getPrefixFieldCaption(LocationDto.I18N_PREFIX, LocationDto.REGION));
			regionField.setValue(importedPerson.getAddress().getRegion() != null ? importedPerson.getAddress().getRegion().toString() : "");
			regionField.setReadOnly(true);
			caseInfoLayout.addComponent(regionField);

			TextField districtField = new TextField();
			districtField.setCaption(I18nProperties.getPrefixFieldCaption(LocationDto.I18N_PREFIX, LocationDto.DISTRICT));
			districtField.setValue(importedPerson.getAddress().getDistrict() != null ? importedPerson.getAddress().getDistrict().toString() : "");
			districtField.setReadOnly(true);
			caseInfoLayout.addComponent(districtField);

			TextField communityField = new TextField();
			communityField.setCaption(I18nProperties.getPrefixFieldCaption(LocationDto.I18N_PREFIX, LocationDto.COMMUNITY));
			communityField.setValue(importedPerson.getAddress().getCommunity() != null ? importedPerson.getAddress().getCommunity().toString() : "");
			communityField.setReadOnly(true);
			caseInfoLayout.addComponent(communityField);

			TextField cityField = new TextField();
			cityField.setCaption(I18nProperties.getPrefixFieldCaption(LocationDto.I18N_PREFIX, LocationDto.CITY));
			cityField.setValue(importedPerson.getAddress().getCity());
			cityField.setReadOnly(true);
			caseInfoLayout.addComponent(cityField);

			TextField diseaseField = new TextField();
			diseaseField.setCaption(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.DISEASE));
			diseaseField.setValue(importedCase.getDisease().toString());
			diseaseField.setReadOnly(true);
			caseInfoLayout.addComponent(diseaseField);

			TextField caseDateField = new TextField();

			if (importedCase.getSymptoms().getOnsetDate() != null) {
				caseDateField.setCaption(I18nProperties.getPrefixFieldCaption(SymptomsDto.I18N_PREFIX, SymptomsDto.ONSET_DATE));
				caseDateField.setValue(DateHelper.formatLocalShortDate(importedCase.getSymptoms().getOnsetDate()));
			} else if (importedCase.getReceptionDate() != null) {
				caseDateField.setCaption(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.RECEPTION_DATE));
				caseDateField.setValue(DateHelper.formatLocalShortDate(importedCase.getReceptionDate()));
			} else {
				caseDateField.setCaption(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.REPORT_DATE));
				caseDateField.setValue(DateHelper.formatLocalShortDate(importedCase.getReportDate()));
			}
			caseDateField.setReadOnly(true);
			caseInfoLayout.addComponent(caseDateField);
		}
		layout.addComponent(caseInfoLayout);
		
		// Person selection/creation

		selectPerson = new OptionGroup(null);
		selectPerson.addItem(SELECT_PERSON);
		selectPerson.setItemCaption(SELECT_PERSON, I18nProperties.getFragment("Person.select"));
		CssStyles.style(selectPerson, CssStyles.VSPACE_NONE);
		selectPerson.addValueChangeListener(e -> {
			if (e.getProperty().getValue() != null) {
				createNewPerson.setValue(null);
				personGrid.setEnabled(true);
				mergeCheckBox.setEnabled(true);
			}
		});
		layout.addComponent(selectPerson);
		
		mergeCheckBox = new CheckBox();
		mergeCheckBox.setCaption(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, "mergeCase"));
		layout.addComponent(mergeCheckBox);

		initPersonGrid();
		// Deselect "create new" when person is selected
		personGrid.addSelectionListener(e -> {
			if (e.getSelected().size() > 0) {
				createNewPerson.setValue(null);
			}
		});
		layout.addComponent(personGrid);

		createNewPerson = new OptionGroup(null);
		createNewPerson.addItem(CREATE_PERSON);
		createNewPerson.setItemCaption(CREATE_PERSON, I18nProperties.getFragment("Person.createNew"));
		// Deselect grid when "create new" is selected
		createNewPerson.addValueChangeListener(e -> {
			if (e.getProperty().getValue() != null) {
				selectPerson.setValue(null);
				personGrid.select(null);
				personGrid.setEnabled(false);
				mergeCheckBox.setEnabled(false);
			}
		});
		layout.addComponent(createNewPerson);

		// Set field values based on internal value
		setInternalValue(super.getInternalValue());

		return layout;
	}

	private void initPersonGrid() {
		if (personGrid == null) {
			personGrid = new PersonGrid(persons, importedCase, importedPerson, currentUser);
		}
	}

	public void selectBestMatch() {
		if (personGrid.getContainerDataSource().size() == 1) {
			setInternalValue((PersonIndexDto) personGrid.getContainerDataSource().firstItemId());
		}
		else {
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
		return selectPerson.getValue().equals(SELECT_PERSON);
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

}
