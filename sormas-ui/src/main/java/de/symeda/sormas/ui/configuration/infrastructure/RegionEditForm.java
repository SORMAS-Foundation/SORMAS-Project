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

import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.utils.AbstractEditForm;

public class RegionEditForm extends AbstractEditForm<RegionDto> {

	private static final long serialVersionUID = -1;
	
	private static final String HTML_LAYOUT = 
			fluidRowLocs(RegionDto.NAME, RegionDto.EPID_CODE) +
			fluidRowLocs(RegionDto.EXTERNAL_ID);
			//+ fluidRowLocs(RegionDto.GROWTH_RATE);

	public RegionEditForm(UserRight editOrCreateUserRight, boolean create) {
		super(RegionDto.class, RegionDto.I18N_PREFIX, editOrCreateUserRight);
		
		setWidth(540, Unit.PIXELS);
		
		if (create) {
			hideValidationUntilNextCommit();
		}
	}
	
	@Override
	protected void addFields() {
		addField(RegionDto.NAME, TextField.class);
		addField(RegionDto.EPID_CODE, TextField.class);
		addField(RegionDto.EXTERNAL_ID, TextField.class);
//		TextField growthRate = addField(RegionDto.GROWTH_RATE, TextField.class);
//		growthRate.setConverter(new StringToFloatConverter());
//		growthRate.setConversionError(I18nProperties.getValidationError(Validations.onlyDecimalNumbersAllowed, growthRate.getCaption()));
		
		setRequired(true, RegionDto.NAME, RegionDto.EPID_CODE);
	}
	

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

}
