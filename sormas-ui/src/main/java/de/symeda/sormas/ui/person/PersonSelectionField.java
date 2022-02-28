/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.person;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.person.PersonSimilarityCriteria;
import de.symeda.sormas.api.person.SimilarPersonDto;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
public class PersonSelectionField extends CustomField<SimilarPersonDto> {

	public static final String CREATE_PERSON = "createPerson";
	public static final String SELECT_PERSON = Captions.personSelect;
	public static final String SEARCH_AND_SELECT_PERSON = Captions.personSearchAndSelect;

	protected VerticalLayout mainLayout;
	protected PersonSelectionGrid personGrid;

	private String infoText;
	protected Consumer<Boolean> selectionChangeCallback;
	private PersonSimilarityCriteria defaultCriteria;
	private RadioButtonGroup<String> rbSelectPerson;
	private RadioButtonGroup<String> rbCreatePerson;
	protected PersonSelectionFilterForm filterForm;
	private PersonDto referencePerson;
	private boolean hasMatches;

	public PersonSelectionField(PersonDto referencePerson, String infoText) {
		this(referencePerson, infoText, null);
	}

	/**
	 * Generate a selection field which contains a grid containing all similar persons to the `referencePerson`.
	 * 
	 * @param referencePerson
	 *            The person for which similar persons are searched and displayed.
	 * @param infoText
	 *            Information displayed to the user.
	 * @param infoTextWithoutMatches
	 *            Information displayed to the user if the field is shown without matches.
	 */
	public PersonSelectionField(PersonDto referencePerson, String infoText, String infoTextWithoutMatches) {
		this.referencePerson = referencePerson;

		initializeGrid();

		this.hasMatches = FacadeProvider.getPersonFacade().checkMatchingNameInDatabase(UserProvider.getCurrent().getUserReference(), defaultCriteria);
		if (infoTextWithoutMatches == null) {
			this.infoText = infoText;
		} else {
			this.infoText = hasMatches ? infoText : infoTextWithoutMatches;
		}
	}

	protected void addInfoComponent() {
		mainLayout.addComponent(VaadinUiUtil.createInfoComponent(infoText));
	}

	private void addPersonDetailsComponent() {
		HorizontalLayout personDetailsLayout1 = new HorizontalLayout();
		personDetailsLayout1.setSpacing(true);

		Label lblFirstName = new Label(referencePerson.getFirstName());
		lblFirstName.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.FIRST_NAME));
		lblFirstName.setWidthUndefined();
		personDetailsLayout1.addComponent(lblFirstName);

		Label lblLastName = new Label(referencePerson.getLastName());
		lblLastName.setWidthUndefined();
		lblLastName.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.LAST_NAME));
		personDetailsLayout1.addComponent(lblLastName);

		Label lblNickname = new Label(referencePerson.getNickname());
		lblNickname.setWidthUndefined();
		lblNickname.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.NICKNAME));
		personDetailsLayout1.addComponent(lblNickname);

		Label lblBirthDateAndAge = new Label(
			PersonHelper.getAgeAndBirthdateString(
				referencePerson.getApproximateAge(),
				referencePerson.getApproximateAgeType(),
				referencePerson.getBirthdateDD(),
				referencePerson.getBirthdateMM(),
				referencePerson.getBirthdateYYYY()));
		lblBirthDateAndAge.setWidthUndefined();
		lblBirthDateAndAge.setCaption(I18nProperties.getCaption(Captions.personAgeAndBirthdate));
		personDetailsLayout1.addComponent(lblBirthDateAndAge);

		Label lblSex = new Label(referencePerson.getSex() != null ? referencePerson.getSex().toString() : "");
		lblSex.setWidthUndefined();
		lblSex.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.SEX));
		personDetailsLayout1.addComponent(lblSex);

		Label lblPresentCondition = new Label(referencePerson.getPresentCondition() != null ? referencePerson.getPresentCondition().toString() : "");
		lblPresentCondition.setWidthUndefined();
		lblPresentCondition.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.PRESENT_CONDITION));
		personDetailsLayout1.addComponent(lblPresentCondition);

		Label lblPhone = new Label(referencePerson.getPhone());
		lblPhone.setWidthUndefined();
		lblPhone.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.PHONE));
		personDetailsLayout1.addComponent(lblPhone);

		mainLayout.addComponent(personDetailsLayout1);

		HorizontalLayout personDetailsLayout2 = new HorizontalLayout();
		personDetailsLayout2.setSpacing(true);

		Label lblDistrict =
			new Label(referencePerson.getAddress().getDistrict() != null ? referencePerson.getAddress().getDistrict().toString() : "");
		lblDistrict.setWidthUndefined();
		lblDistrict.setCaption(I18nProperties.getPrefixCaption(LocationDto.I18N_PREFIX, LocationDto.DISTRICT));
		personDetailsLayout2.addComponent(lblDistrict);

		Label lblCommunity =
			new Label(referencePerson.getAddress().getCommunity() != null ? referencePerson.getAddress().getCommunity().toString() : "");
		lblCommunity.setWidthUndefined();
		lblCommunity.setCaption(I18nProperties.getPrefixCaption(LocationDto.I18N_PREFIX, LocationDto.COMMUNITY));
		personDetailsLayout2.addComponent(lblCommunity);

		Label lblPostalCode = new Label(referencePerson.getAddress().getPostalCode());
		lblPostalCode.setWidthUndefined();
		lblPostalCode.setCaption(I18nProperties.getPrefixCaption(LocationDto.I18N_PREFIX, LocationDto.POSTAL_CODE));
		personDetailsLayout2.addComponent(lblPostalCode);

		Label lblCity = new Label(referencePerson.getAddress().getCity());
		lblCity.setWidthUndefined();
		lblCity.setCaption(I18nProperties.getPrefixCaption(LocationDto.I18N_PREFIX, LocationDto.CITY));
		personDetailsLayout2.addComponent(lblCity);

		Label lblStreet = new Label(referencePerson.getAddress().getStreet());
		lblStreet.setWidthUndefined();
		lblStreet.setCaption(I18nProperties.getPrefixCaption(LocationDto.I18N_PREFIX, LocationDto.STREET));
		personDetailsLayout2.addComponent(lblStreet);

		Label lblHouseNumber = new Label(referencePerson.getAddress().getHouseNumber());
		lblHouseNumber.setWidthUndefined();
		lblHouseNumber.setCaption(I18nProperties.getPrefixCaption(LocationDto.I18N_PREFIX, LocationDto.HOUSE_NUMBER));
		personDetailsLayout2.addComponent(lblHouseNumber);

		mainLayout.addComponent(personDetailsLayout2);

		HorizontalLayout personDetailsLayout3 = new HorizontalLayout();
		personDetailsLayout3.setSpacing(true);

		Label lblNationalHealthId = new Label(referencePerson.getNationalHealthId());
		lblNationalHealthId.setWidthUndefined();
		lblNationalHealthId.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.NATIONAL_HEALTH_ID));
		personDetailsLayout3.addComponent(lblNationalHealthId);

		Label lblPassportNumber = new Label(referencePerson.getPassportNumber());
		lblPassportNumber.setWidthUndefined();
		lblPassportNumber.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.PASSPORT_NUMBER));
		personDetailsLayout3.addComponent(lblPassportNumber);

		mainLayout.addComponent(personDetailsLayout3);
	}

	private void addSelectPersonRadioGroup() {
		List<String> selectPersonOption = new ArrayList<>();
		if (hasMatches) {
			selectPersonOption.add(SELECT_PERSON);
		}
		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.PERSON_DUPLICATE_CUSTOM_SEARCH)) {
			selectPersonOption.add(SEARCH_AND_SELECT_PERSON);
		}

		rbSelectPerson = new RadioButtonGroup<>();
		rbSelectPerson.setItems(selectPersonOption);
		rbSelectPerson.setItemCaptionGenerator(I18nProperties::getCaption);
		CssStyles.style(rbSelectPerson, CssStyles.VSPACE_NONE);

		rbSelectPerson.addValueChangeListener(e -> {
			String value = e.getValue();
			if (value != null) {
				rbCreatePerson.setValue(null);
				personGrid.setEnabled(true);
				if (selectionChangeCallback != null) {
					selectionChangeCallback.accept(personGrid.getSelectedRow() != null);
				}

				if (SELECT_PERSON.equals(value)) {
					filterForm.setVisible(false);
					personGrid.loadData(defaultCriteria);
					selectBestMatch();
				} else {
					filterForm.setVisible(true);
					personGrid.loadData(filterForm.getValue());
					setValue(null);
				}
			}
		});

		mainLayout.addComponent(rbSelectPerson);
	}

	protected void addFilterForm() {
		filterForm = new PersonSelectionFilterForm();

		final PersonSimilarityCriteria searchCriteria = new PersonSimilarityCriteria();
		if (referencePerson != null) {
			searchCriteria.setFirstName(referencePerson.getFirstName());
			searchCriteria.setLastName(referencePerson.getLastName());
		}
		filterForm.setValue(searchCriteria);
		filterForm.addApplyHandler((e) -> {
			if (filterForm.validateFields()) {
				personGrid.loadData(filterForm.getValue());
			}
		});
		filterForm.addResetHandler((e) -> filterForm.setValue(new PersonSimilarityCriteria()));

		mainLayout.addComponent(filterForm);
	}

	/**
	 * Load a grid of all persons similar to the given reference person.
	 */
	protected void initializeGrid() {
		defaultCriteria = new PersonSimilarityCriteria().firstName(referencePerson.getFirstName())
			.lastName(referencePerson.getLastName())
			.sex(referencePerson.getSex())
			.birthdateDD(referencePerson.getBirthdateDD())
			.birthdateMM(referencePerson.getBirthdateMM())
			.birthdateYYYY(referencePerson.getBirthdateYYYY())
			.passportNumber(referencePerson.getPassportNumber())
			.nationalHealthId(referencePerson.getNationalHealthId());
		personGrid = new PersonSelectionGrid();

		personGrid.addSelectionListener(e -> {
			if (e.getSelected().size() > 0) {
				rbCreatePerson.setValue(null);
			}

			if (selectionChangeCallback != null) {
				selectionChangeCallback.accept(!e.getSelected().isEmpty());
			}
		});
	}

	private void addCreatePersonRadioGroup() {
		rbCreatePerson = new RadioButtonGroup<>();
		rbCreatePerson.setItems(CREATE_PERSON);
		rbCreatePerson.setItemCaptionGenerator((item) -> I18nProperties.getCaption(Captions.personCreateNew));
		rbCreatePerson.addValueChangeListener(e -> {
			if (e.getValue() != null) {
				rbSelectPerson.setValue(null);
				personGrid.deselectAll();
				personGrid.setEnabled(false);
				personGrid.setHeightByRows(1);
				if (selectionChangeCallback != null) {
					selectionChangeCallback.accept(true);
				}
			}
		});

		mainLayout.addComponent(rbCreatePerson);
	}

	@Override
	protected Component initContent() {
		// Main layout
		mainLayout = new VerticalLayout();
		mainLayout.setSpacing(true);
		mainLayout.setMargin(false);
		mainLayout.setSizeUndefined();
		mainLayout.setWidth(100, Unit.PERCENTAGE);

		addInfoComponent();
		addPersonDetailsComponent();
		addSelectPersonRadioGroup();
		addFilterForm();
		mainLayout.addComponent(personGrid);
		addCreatePersonRadioGroup();

		if (hasMatches) {
			rbSelectPerson.setValue(SELECT_PERSON);
		} else {
			rbCreatePerson.setValue(CREATE_PERSON);
		}

		return mainLayout;
	}

	public void selectBestMatch() {
		if (personGrid.getContainerDataSource().size() == 1) {
			setValue((SimilarPersonDto) personGrid.getContainerDataSource().firstItemId());
		} else {
			setValue(null);
		}
	}

	@Override
	public SimilarPersonDto getValue() {
		if (personGrid != null) {
			return (SimilarPersonDto) personGrid.getSelectedRow();
		}

		return null;
	}

	@Override
	protected void doSetValue(SimilarPersonDto newValue) {
		if (rbSelectPerson.getValue() == null) {
			rbSelectPerson.setValue(SELECT_PERSON);
		}

		personGrid.select(newValue);
	}

	public boolean hasMatches() {
		return hasMatches;
	}

	/**
	 * Callback is executed with 'true' when a grid entry or "Create new person" is selected.
	 */
	public void setSelectionChangeCallback(Consumer<Boolean> callback) {
		this.selectionChangeCallback = callback;
	}
}
