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

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.CustomField;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonIndexDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class PersonSelectField extends CustomField<PersonIndexDto> {

	public static final String CREATE_PERSON = "createPerson";
	public static final String SELECT_PERSON = "selectPerson";
	
	private final TextField firstNameField = new TextField();
	private final TextField lastNameField = new TextField();
	private final Label nicknameLabel = new Label();
	private final Label approximateAgeLabel = new Label();
	private final Label sexLabel = new Label();
	private final Label presentConditionLabel = new Label();
	private final Label districtLabel = new Label();
	private final Label communityLabel = new Label();
	private final Label cityLabel = new Label();
	private final Button searchMatchesButton = new Button(I18nProperties.getCaption(Captions.personFindMatching));
	private PersonGrid personGrid;
	private OptionGroup selectPerson;
	private OptionGroup createNewPerson;
	private Consumer<Boolean> selectionChangeCallback;
	private boolean importMode;
	
	public PersonSelectField(boolean importMode) {
		this.importMode = importMode;
	}

	@Override
	protected Component initContent() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setSizeUndefined();
		layout.setWidth(100, Unit.PERCENTAGE);
		
		HorizontalLayout nameLayout = new HorizontalLayout();
		nameLayout.setSpacing(true);
		nameLayout.setWidth(100, Unit.PERCENTAGE);
		
		firstNameField.setCaption(
				I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.FIRST_NAME));
		firstNameField.setWidth(100, Unit.PERCENTAGE);
		firstNameField.setRequired(true);
		nameLayout.addComponent(firstNameField);
		
		lastNameField.setCaption(
				I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.LAST_NAME));
		lastNameField.setWidth(100, Unit.PERCENTAGE);
		lastNameField.setRequired(true);
		nameLayout.addComponent(lastNameField);
		
		CssStyles.style(searchMatchesButton, CssStyles.FORCE_CAPTION, ValoTheme.BUTTON_PRIMARY);
		searchMatchesButton.addClickListener(e -> {
			personGrid.reload(firstNameField.getValue(), lastNameField.getValue());
			selectBestMatch();
		});

		if (!importMode) {
			nameLayout.addComponent(searchMatchesButton);
		}

		layout.addComponent(nameLayout);

		if (importMode) {
			HorizontalLayout infoLayout = new HorizontalLayout();
			infoLayout.setSpacing(true);
			
			nicknameLabel.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.NICKNAME));
			nicknameLabel.setWidthUndefined();
			infoLayout.addComponent(nicknameLabel);

			approximateAgeLabel
					.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.APPROXIMATE_AGE));
			approximateAgeLabel.setWidthUndefined();
			infoLayout.addComponent(approximateAgeLabel);

			sexLabel.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.SEX));
			sexLabel.setWidthUndefined();
			infoLayout.addComponent(sexLabel);

			presentConditionLabel
					.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.PRESENT_CONDITION));
			presentConditionLabel.setWidthUndefined();
			infoLayout.addComponent(presentConditionLabel);

			districtLabel.setCaption(I18nProperties.getPrefixCaption(LocationDto.I18N_PREFIX, LocationDto.DISTRICT));
			districtLabel.setWidthUndefined();
			infoLayout.addComponent(districtLabel);

			communityLabel.setCaption(I18nProperties.getPrefixCaption(LocationDto.I18N_PREFIX, LocationDto.COMMUNITY));
			communityLabel.setWidthUndefined();
			infoLayout.addComponent(communityLabel);

			cityLabel.setCaption(I18nProperties.getPrefixCaption(LocationDto.I18N_PREFIX, LocationDto.CITY));
			cityLabel.setWidthUndefined();
			infoLayout.addComponent(cityLabel);

			layout.addComponent(infoLayout);
		}

		selectPerson = new OptionGroup(null);
		selectPerson.addItem(SELECT_PERSON);
		selectPerson.setItemCaption(SELECT_PERSON, I18nProperties.getCaption(Captions.personSelect));
		CssStyles.style(selectPerson, CssStyles.VSPACE_NONE);
		selectPerson.addValueChangeListener(e -> {
			if (e.getProperty().getValue() != null) {
				createNewPerson.setValue(null);
				personGrid.setEnabled(true);
				if (selectionChangeCallback != null) {
					selectionChangeCallback.accept(personGrid.getSelectedRow() != null);
				}
			}
		});

		layout.addComponent(selectPerson);

		initPersonGrid();
		// unselect "create new" when person is selected
		personGrid.addSelectionListener(e -> {
			if (e.getSelected().size() > 0) {
				createNewPerson.setValue(null);
			}
		});
		layout.addComponent(personGrid);

		personGrid.addSelectionListener(e -> {
			if (selectionChangeCallback != null) {
				selectionChangeCallback.accept(!e.getSelected().isEmpty());
			}
		});
		
		createNewPerson = new OptionGroup(null);
		createNewPerson.addItem(CREATE_PERSON);
		createNewPerson.setItemCaption(CREATE_PERSON, I18nProperties.getCaption(Captions.personCreateNew));
		// unselect grid when "create new" is selected
		createNewPerson.addValueChangeListener(e -> {
			if (e.getProperty().getValue() != null) {
				selectPerson.setValue(null);
				personGrid.select(null);
				personGrid.setEnabled(false);
				if (selectionChangeCallback != null) {
					selectionChangeCallback.accept(true);
				}
			}
		});
		layout.addComponent(createNewPerson);
		
		// set field values based on internal value
		setInternalValue(super.getInternalValue());
		
		return layout;
	}
	
	private void initPersonGrid() {
		if (personGrid == null) {
			personGrid = new PersonGrid(firstNameField.getValue(), lastNameField.getValue());
		}
	}
	
	public void selectBestMatch() {
		if (personGrid.getContainerDataSource().size() == 1) {
			setInternalValue((PersonIndexDto)personGrid.getContainerDataSource().firstItemId());
		}
		else {
			setInternalValue(null);
		}
	}
	
	@Override
	public Class<? extends PersonIndexDto> getType() {
		return PersonIndexDto.class;
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
			PersonIndexDto value = (PersonIndexDto)personGrid.getSelectedRow();
			return value;
		}
		
		return super.getInternalValue();
	}

	public String getFirstName() {
		return firstNameField.getValue();
	}
	
	public void setFirstName(String firstName) {
		firstNameField.setValue(firstName);
	}

	public String getLastName() {
		return lastNameField.getValue();
	}
	
	public void setLastName(String lastName) {
		lastNameField.setValue(lastName);
	}
	
	public TextField getFirstNameField() {
		return firstNameField;
	}

	public TextField getLastNameField() {
		return lastNameField;
	}

	public void setNickname(String nickname) {
		nicknameLabel.setValue(nickname);
	}

	public void setApproximateAge(Integer approximateAge) {
		if (approximateAge != null) {
			approximateAgeLabel.setValue(approximateAge.toString());
		}
	}

	public void setSex(Sex sex) {
		if (sex != null) {
			sexLabel.setValue(sex.toString());
		}
	}

	public void setPresentCondition(PresentCondition presentCondition) {
		if (presentCondition != null) {
			presentConditionLabel.setValue(presentCondition.toString());
		}
	}

	public void setDistrict(DistrictReferenceDto districtReferenceDto) {
		if (districtReferenceDto != null) {
			districtLabel.setValue(districtReferenceDto.getCaption());
		}
	}

	public void setCommunity(CommunityReferenceDto communityReferenceDto) {
		if (communityReferenceDto != null) {
			communityLabel.setValue(communityReferenceDto.getCaption());
		}
	}

	public void setCity(String city) {
		cityLabel.setValue(city);
	}

	public boolean hasMatches() {
		if (personGrid == null) {
			initPersonGrid();
		}
		return personGrid.getContainerDataSource().size() > 0;
	}
	
	/**
	 * Callback is executed with 'true' when a grid entry or "Create new person" is selected.
	 */
	public void setSelectionChangeCallback(Consumer<Boolean> callback) {
		this.selectionChangeCallback = callback;
	}
}