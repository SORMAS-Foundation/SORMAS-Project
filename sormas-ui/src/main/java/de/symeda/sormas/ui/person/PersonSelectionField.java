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
package de.symeda.sormas.ui.person;

import java.util.function.Consumer;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.person.PersonIndexDto;
import de.symeda.sormas.api.person.PersonSimilarityCriteria;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
public class PersonSelectionField extends CustomField<PersonIndexDto> {

	public static final String CREATE_PERSON = "createPerson";
	public static final String SELECT_PERSON = "selectPerson";

	private PersonDto referencePerson;
	private String infoText;
	private VerticalLayout mainLayout;
	private PersonSelectionGrid personGrid;
	private RadioButtonGroup<String> rbSelectPerson;
	private RadioButtonGroup<String> rbCreatePerson;
	private Consumer<Boolean> selectionChangeCallback;

	public PersonSelectionField(PersonDto referencePerson, String infoText) {
		this.referencePerson = referencePerson;
		this.infoText = infoText;

		initializeGrid();
	}

	private void addInfoComponent() {
		mainLayout.addComponent(VaadinUiUtil.createInfoComponent(infoText));
	}

	private void addPersonDetailsComponent() {
		HorizontalLayout personDetailsLayout = new HorizontalLayout();
		personDetailsLayout.setSpacing(true);

		Label lblFirstName = new Label(referencePerson.getFirstName());
		lblFirstName.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.FIRST_NAME));
		lblFirstName.setWidthUndefined();
		personDetailsLayout.addComponent(lblFirstName);

		Label lblLastName = new Label(referencePerson.getLastName());
		lblLastName.setWidthUndefined();
		lblLastName.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.LAST_NAME));
		personDetailsLayout.addComponent(lblLastName);

		Label lblNickname = new Label(referencePerson.getNickname());
		lblNickname.setWidthUndefined();
		lblNickname.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.NICKNAME));
		personDetailsLayout.addComponent(lblNickname);

		Label lblBirthDateAndAge = new Label(PersonHelper.getAgeAndBirthdateString(
				referencePerson.getApproximateAge(), referencePerson.getApproximateAgeType(),
				referencePerson.getBirthdateDD(), referencePerson.getBirthdateMM(), referencePerson.getBirthdateYYYY(), FacadeProvider.getUserFacade().getCurrentUser().getLanguage()));
		lblBirthDateAndAge.setWidthUndefined();
		lblBirthDateAndAge.setCaption(I18nProperties.getCaption(Captions.personAgeAndBirthdate));
		personDetailsLayout.addComponent(lblBirthDateAndAge);

		Label lblSex = new Label(referencePerson.getSex() != null ? referencePerson.getSex().toString() : "");
		lblSex.setWidthUndefined();
		lblSex.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.SEX));
		personDetailsLayout.addComponent(lblSex);

		Label lblPresentCondition = new Label(referencePerson.getPresentCondition() != null ? referencePerson.getPresentCondition().toString() : "");
		lblPresentCondition.setWidthUndefined();
		lblPresentCondition.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.PRESENT_CONDITION));
		personDetailsLayout.addComponent(lblPresentCondition);

		Label lblDistrict = new Label(referencePerson.getAddress().getDistrict() != null ? referencePerson.getAddress().getDistrict().toString() : "");
		lblDistrict.setWidthUndefined();
		lblDistrict.setCaption(I18nProperties.getPrefixCaption(LocationDto.I18N_PREFIX, LocationDto.DISTRICT));
		personDetailsLayout.addComponent(lblDistrict);

		Label lblCommunity = new Label(referencePerson.getAddress().getCommunity() != null ? referencePerson.getAddress().getCommunity().toString() : "");
		lblCommunity.setWidthUndefined();
		lblCommunity.setCaption(I18nProperties.getPrefixCaption(LocationDto.I18N_PREFIX, LocationDto.COMMUNITY));
		personDetailsLayout.addComponent(lblCommunity);

		Label lblCity = new Label(referencePerson.getAddress().getCity());
		lblCity.setWidthUndefined();
		lblCity.setCaption(I18nProperties.getPrefixCaption(LocationDto.I18N_PREFIX, LocationDto.CITY));
		personDetailsLayout.addComponent(lblCity);

		mainLayout.addComponent(personDetailsLayout);
	}

	private void addSelectPersonRadioGroup() {
		rbSelectPerson = new RadioButtonGroup<>();
		rbSelectPerson.setItems(SELECT_PERSON);
		rbSelectPerson.setItemCaptionGenerator((item) -> {
			return I18nProperties.getCaption(Captions.personSelect);
		});
		CssStyles.style(rbSelectPerson, CssStyles.VSPACE_NONE);
		rbSelectPerson.addValueChangeListener(e -> {
			if (e.getValue() != null) {
				rbCreatePerson.setValue(null);
				personGrid.setEnabled(true);
				if (selectionChangeCallback != null) {
					selectionChangeCallback.accept(personGrid.getSelectedRow() != null);
				}
			}
		});

		mainLayout.addComponent(rbSelectPerson);
	}

	private void initializeGrid() {
		PersonSimilarityCriteria criteria = new PersonSimilarityCriteria()
				.firstName(referencePerson.getFirstName())
				.lastName(referencePerson.getLastName())
				.sex(referencePerson.getSex())
				.birthdateDD(referencePerson.getBirthdateDD())
				.birthdateMM(referencePerson.getBirthdateMM())
				.birthdateYYYY(referencePerson.getBirthdateYYYY());
		personGrid = new PersonSelectionGrid(criteria);

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
		rbCreatePerson.setItemCaptionGenerator((item) -> {
			return I18nProperties.getCaption(Captions.personCreateNew);
		});
		rbCreatePerson.addValueChangeListener(e -> {
			if (e.getValue() != null) {
				rbSelectPerson.setValue(null);
				personGrid.deselectAll();
				personGrid.setEnabled(false);
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
		mainLayout.addComponent(personGrid);
		addCreatePersonRadioGroup();

		rbSelectPerson.setValue(SELECT_PERSON);

		//		setInternalValue(super.getInternalValue());

		return mainLayout;
	}

	public void selectBestMatch() {
		if (personGrid.getContainerDataSource().size() == 1) {
			setValue((PersonIndexDto) personGrid.getContainerDataSource().firstItemId());
		} else {
			setValue(null);
		}
	}

	@Override
	public PersonIndexDto getValue() {
		if (personGrid != null) {
			PersonIndexDto value = (PersonIndexDto) personGrid.getSelectedRow();
			return value;
		}

		return null;
	}

	@Override
	protected void doSetValue(PersonIndexDto newValue) {
		rbSelectPerson.setValue(SELECT_PERSON);

		if (newValue != null) {
			personGrid.select(newValue);
		}
	}

	public boolean hasMatches() {
		return personGrid.getContainerDataSource().size() > 0;
	}

	/**
	 * Callback is executed with 'true' when a grid entry or "Create new person" is selected.
	 */
	public void setSelectionChangeCallback(Consumer<Boolean> callback) {
		this.selectionChangeCallback = callback;
	}

}