/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.ui.configuration.infrastructure;

import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import com.vaadin.ui.Label;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.country.CountryDto;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.utils.AbstractEditForm;

public class CountryEditForm extends AbstractEditForm<CountryDto> {

	private static final long serialVersionUID = 5397257740478821166L;

	private static final String GENERAL_INFORMATION_HEADING_LOC = "generalInformationHeadingLoc";
	private static final String COUNTRY_CODES_HEADING_LOC = "countryCodesHeadingLoc";

	//@formatter:off
	private static final String HTML_LAYOUT =
			loc(GENERAL_INFORMATION_HEADING_LOC) +
			fluidRowLocs(CountryDto.DEFAULT_NAME, CountryDto.SUBCONTINENT) +
			loc(COUNTRY_CODES_HEADING_LOC) +
			fluidRowLocs(CountryDto.ISO_CODE, CountryDto.ISO2_CODE, CountryDto.ISO_NUMERIC_CODE) +
			fluidRowLocs(CountryDto.NUTS_CODE, CountryDto.UNO_CODE, CountryDto.EPIPULSE_LOCATION_CODE) +
			fluidRowLocs(CountryDto.EXTERNAL_ID, "", "");
	//@formatter:on

	private final Boolean create;

	public CountryEditForm(boolean create) {

		super(
			CountryDto.class,
			CountryDto.I18N_PREFIX,
			false,
			FieldVisibilityCheckers.withFeatureTypes(FacadeProvider.getFeatureConfigurationFacade().getActiveServerFeatureConfigurations()),
			UiFieldAccessCheckers.getNoop());
		this.create = create;

		setWidth(840, Unit.PIXELS);

		if (create) {
			hideValidationUntilNextCommit();
		}

		addFields();
	}

	@Override
	protected void addFields() {
		if (create == null) {
			return;
		}

		Label generalInformationHeading = new Label(I18nProperties.getString(Strings.headingCountryGeneralInformation));
		generalInformationHeading.addStyleName(H3);
		getContent().addComponent(generalInformationHeading, GENERAL_INFORMATION_HEADING_LOC);

		addField(CountryDto.DEFAULT_NAME, TextField.class);
		ComboBox subcontinent = addInfrastructureField(CountryDto.SUBCONTINENT);
		subcontinent.addItems(FacadeProvider.getSubcontinentFacade().getAllActiveAsReference());

		Label countryCodesHeading = new Label(I18nProperties.getString(Strings.headingCountryCodes));
		countryCodesHeading.addStyleName(H3);
		getContent().addComponent(countryCodesHeading, COUNTRY_CODES_HEADING_LOC);

		addField(CountryDto.ISO_CODE, TextField.class).setMaxLength(3);
		addField(CountryDto.ISO2_CODE, TextField.class).setMaxLength(2);
		addField(CountryDto.ISO_NUMERIC_CODE, TextField.class).setMaxLength(3);
		addField(CountryDto.NUTS_CODE, TextField.class);
		addField(CountryDto.UNO_CODE, TextField.class).setMaxLength(3);
		addField(CountryDto.EPIPULSE_LOCATION_CODE, TextField.class).setMaxLength(10);
		addField(CountryDto.EXTERNAL_ID, TextField.class);

		initializeVisibilitiesAndAllowedVisibilities();

		setRequired(true, CountryDto.DEFAULT_NAME, CountryDto.ISO_CODE);

		if (!create) {
			subcontinent.setEnabled(false);
		}
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
