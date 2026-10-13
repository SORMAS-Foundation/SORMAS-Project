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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.samples.components;

import static de.symeda.sormas.ui.utils.CssStyles.H3;

import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.utils.FormComponent;

/**
 * Prescriber fields section using Vaadin 8 components with own Binder.
 * as a standalone composable component
 */
public class PrescriberComponent extends FormComponent<PathogenTestDto> {

	private static final long serialVersionUID = 1L;

	private Label heading;
	private TextField physicianCode;
	private TextField firstName;
	private TextField lastName;
	private TextField phone;
	private TextField address;
	private TextField postalCode;
	private TextField city;
	private ComboBox<CountryReferenceDto> country;

	public PrescriberComponent() {
		super(PathogenTestDto.class);
		buildLayout();
		bindFields();
	}

	private void buildLayout() {
		heading = new Label(I18nProperties.getCaption(Captions.PathogenTest_prescriber));
		heading.addStyleName(H3);
		addComponent(heading);

		physicianCode = createTextField(PathogenTestDto.PRESCRIBER_PHYSICIAN_CODE, PathogenTestDto.I18N_PREFIX, ValueChangeMode.BLUR);
		addRow(physicianCode);

		firstName = createTextField(PathogenTestDto.PRESCRIBER_FIRST_NAME, PathogenTestDto.I18N_PREFIX, ValueChangeMode.BLUR);
		lastName = createTextField(PathogenTestDto.PRESCRIBER_LAST_NAME, PathogenTestDto.I18N_PREFIX, ValueChangeMode.BLUR);
		addRow(firstName, lastName);

		phone = createTextField(PathogenTestDto.PRESCRIBER_PHONE_NUMBER, PathogenTestDto.I18N_PREFIX, ValueChangeMode.BLUR);
		addRow(phone);

		address = createTextField(PathogenTestDto.PRESCRIBER_ADDRESS, PathogenTestDto.I18N_PREFIX, ValueChangeMode.BLUR);
		postalCode = createTextField(PathogenTestDto.PRESCRIBER_POSTAL_CODE, PathogenTestDto.I18N_PREFIX, ValueChangeMode.BLUR);
		addRow(address, postalCode);

		city = createTextField(PathogenTestDto.PRESCRIBER_CITY, PathogenTestDto.I18N_PREFIX, ValueChangeMode.BLUR);
		country = createComboBox(PathogenTestDto.PRESCRIBER_COUNTRY, PathogenTestDto.I18N_PREFIX);
		country.setItems(FacadeProvider.getCountryFacade().getAllActiveAsReference());
		country.setItemCaptionGenerator(CountryReferenceDto::getCaption);
		addRow(city, country);
	}

	private void bindFields() {
		binder.forField(physicianCode).bind(PathogenTestDto::getPrescriberPhysicianCode, PathogenTestDto::setPrescriberPhysicianCode);
		binder.forField(firstName).bind(PathogenTestDto::getPrescriberFirstName, PathogenTestDto::setPrescriberFirstName);
		binder.forField(lastName).bind(PathogenTestDto::getPrescriberLastName, PathogenTestDto::setPrescriberLastName);
		binder.forField(phone)
			.withValidator(DataHelper::isValidPhoneNumber, I18nProperties.getValidationError(Validations.validPhoneNumber, phone.getCaption()))
			.bind(PathogenTestDto::getPrescriberPhoneNumber, PathogenTestDto::setPrescriberPhoneNumber);
		binder.forField(address).bind(PathogenTestDto::getPrescriberAddress, PathogenTestDto::setPrescriberAddress);
		binder.forField(postalCode).bind(PathogenTestDto::getPrescriberPostalCode, PathogenTestDto::setPrescriberPostalCode);
		binder.forField(city).bind(PathogenTestDto::getPrescriberCity, PathogenTestDto::setPrescriberCity);
		binder.forField(country).bind(PathogenTestDto::getPrescriberCountry, PathogenTestDto::setPrescriberCountry);
	}

	@Override
	protected void updateRowAndSelfVisibility() {
		super.updateRowAndSelfVisibility();
		heading.setVisible(this.isVisible());
	}

	public boolean isHeadingVisible() {
		return heading.isVisible();
	}
}
