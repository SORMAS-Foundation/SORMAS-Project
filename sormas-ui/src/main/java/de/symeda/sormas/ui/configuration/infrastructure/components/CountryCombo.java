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

package de.symeda.sormas.ui.configuration.infrastructure.components;

import java.util.function.BiConsumer;

import com.vaadin.v7.shared.ui.combobox.FilteringMode;
import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionDto;

public class CountryCombo extends ComboBox {

	public CountryCombo(BiConsumer<CountryReferenceDto, Boolean> changeHandler) {
		setId(RegionDto.COUNTRY);
		setWidth(140, Unit.PIXELS);
		setFilteringMode(FilteringMode.CONTAINS);
		setCaption(I18nProperties.getPrefixCaption(RegionDto.I18N_PREFIX, RegionDto.COUNTRY));
		addItems(FacadeProvider.getCountryFacade().getAllActiveAsReference());

		CountryReferenceDto serverCountry = FacadeProvider.getCountryFacade().getServerCountry();
		addValueChangeListener(e -> {
			CountryReferenceDto country = (CountryReferenceDto) e.getProperty().getValue();
			boolean isServerCountry = country == null || country.equals(serverCountry);

			changeHandler.accept(country, isServerCountry);
		});
	}
}
