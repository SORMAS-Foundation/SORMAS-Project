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
package de.symeda.sormas.ui.configuration.infrastructure;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.ui.utils.AbstractEditForm;

public class DistrictEditForm extends AbstractEditForm<DistrictDto> {

	private static final long serialVersionUID = 7573666294384000190L;

	private static final String HTML_LAYOUT =
			fluidRowLocs(DistrictDto.NAME, DistrictDto.EPID_CODE) +
			fluidRowLocs(DistrictDto.REGION) + 
			fluidRowLocs(RegionDto.EXTERNAL_ID); // ,DistrictDto.GROWTH_RATE);

	private boolean create;
	
	public DistrictEditForm(boolean create) {
		super(DistrictDto.class, DistrictDto.I18N_PREFIX, false);
		this.create = create;
		
		setWidth(540, Unit.PIXELS);
		
		if (create) {
			hideValidationUntilNextCommit();
		}
		addFields();
	}
	
	@Override
	protected void addFields() {
		addField(DistrictDto.NAME, TextField.class);
		addField(DistrictDto.EPID_CODE, TextField.class);
		ComboBox region = addInfrastructureField(DistrictDto.REGION);
		addField(RegionDto.EXTERNAL_ID, TextField.class);
//		TextField growthRate = addField(DistrictDto.GROWTH_RATE, TextField.class);
//		growthRate.setConverter(new StringToFloatConverter());
//		growthRate.setConversionError(I18nProperties.getValidationError(Validations.onlyDecimalNumbersAllowed, growthRate.getCaption()));

		setRequired(true, DistrictDto.NAME, DistrictDto.EPID_CODE, DistrictDto.REGION);
		
		region.addItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());		
		
		// TODO: Workaround until cases and other data is properly transfered when infrastructure data changes
		if (!create) {
			region.setEnabled(false);
		}
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

}
