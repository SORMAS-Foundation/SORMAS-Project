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
package de.symeda.sormas.ui.configuration.infrastructure;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import java.util.Arrays;
import java.util.Collections;

import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldHelper;

public class RegionEditForm extends AbstractEditForm<RegionDto> {

	private static final long serialVersionUID = -1;

	//@formatter:off
	private static final String HTML_LAYOUT = 
			fluidRowLocs(RegionDto.NAME, RegionDto.EPID_CODE) +
					fluidRowLocs(RegionDto.COUNTRY) +
					fluidRowLocs(RegionDto.AREA) +
					fluidRowLocs(RegionDto.EXTERNAL_ID);
			//+ fluidRowLocs(RegionDto.GROWTH_RATE);
	//@formatter:on

	private final Boolean create;

	public RegionEditForm(boolean create) {

		super(
			RegionDto.class,
			RegionDto.I18N_PREFIX,
			false,
			FieldVisibilityCheckers.withFeatureTypes(FacadeProvider.getFeatureConfigurationFacade().getActiveServerFeatureTypes()),
			UiFieldAccessCheckers.getNoop());
		this.create = create;

		setWidth(540, Unit.PIXELS);

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

		addField(RegionDto.NAME, TextField.class);
		addField(RegionDto.EPID_CODE, TextField.class);
		ComboBox country = addInfrastructureField(RegionDto.COUNTRY);
		ComboBox area = addInfrastructureField(RegionDto.AREA);
		addField(RegionDto.EXTERNAL_ID, TextField.class);
//		TextField growthRate = addField(RegionDto.GROWTH_RATE, TextField.class);
//		growthRate.setConverter(new StringToFloatConverter());
//		growthRate.setConversionError(I18nProperties.getValidationError(Validations.onlyDecimalNumbersAllowed, growthRate.getCaption()));

		initializeVisibilitiesAndAllowedVisibilities();

		setRequired(true, RegionDto.NAME, RegionDto.EPID_CODE);

		country.addItems(FacadeProvider.getCountryFacade().getAllActiveAsReference());

		area.addItems(FacadeProvider.getAreaFacade().getAllActiveAsReference());
		FieldHelper.setVisibleWhen(
			country,
			Collections.singletonList(area),
			Arrays.asList(null, FacadeProvider.getCountryFacade().getServerCountry()),
			true);
	}

	@Override
	public void setValue(RegionDto newFieldValue) throws ReadOnlyException, Converter.ConversionException {
		super.setValue(newFieldValue);

		getField(RegionDto.COUNTRY).setReadOnly(newFieldValue.getCountry() != null);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
